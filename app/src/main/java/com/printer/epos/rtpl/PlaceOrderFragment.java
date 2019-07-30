package com.printer.epos.rtpl;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;
import com.printer.epos.rtpl.wrapper.CouponCodeWrapper;
import com.printer.epos.rtpl.wrapper.CustomerWrapper;
import com.printer.epos.rtpl.wrapper.OrderPreviewWrapper;
import com.printer.epos.rtpl.wrapper.OrderWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceOrderFragment extends NewOrderFragment implements View.OnClickListener, CalculationListener, AdapterView.OnItemSelectedListener {


    private View rootView;
    private EditText mSearchEt, mMemberShipIDET, mCustomerNameET, mRedeemPointsET;
    private Spinner mCouponCodeET;
    private Button mCouponCodeApplyButton, mRedeemPointsApplyButton, mPutOnHoldButton, mCompleteButton,
            mRemoveRedeemPoints, mRemoveCouponCode, mPayButton, mCancelOrderButton, mOnHoldButton;
    private ProgressDialog mDialog;
    private String paymentType = "";

    protected List<CouponCodeWrapper.CouponCodeData> couponCodedList;
    double gstAmount = 0.0;
    double amountBeforeGST = 0.0;
    double discountAmount = 0.0;
    double totalDiscountAmount = 0.0;
    double amountAfterDiscount = 0.0;
    double amountIncludesGST = 0.0;
    double gstInclusive = 0.0;


    private TextView mGrossAmountValueTV, mSubTotalTV, mSubTotalValueTV, mAmountBeforeGSTTV, mAmountBeforeGSTValueTV, mDiscountAmountTV, mDiscountAmountValueTV,mCouponAmountTV, mCouponAmountValueTV,
            mRedeemAmountValueTV,mRedeemAmountTV, mTaxTV, mTaxValueTV, mNetAMountValeTV, mPointsTV, mReedeemAmount, mAmountAfterDiscountTV,mAmountAfterDiscountValueTV,
            mGstInclusiveTV,mGstInclusiveValueTV, mAmountIncludesGSTTV,mPaymentTypeValueTV, mAmountIncludesGSTValueTV;

    public PlaceOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_place_order, container, false);
        setupUI(rootView);

        initializeAllViews();

        mCompleteButton.setOnClickListener(this);
        mPutOnHoldButton.setOnClickListener(this);
        mRedeemPointsApplyButton.setOnClickListener(this);
        mCouponCodeApplyButton.setOnClickListener(this);
        mRemoveRedeemPoints.setOnClickListener(this);
        mRemoveCouponCode.setOnClickListener(this);
        mPayButton.setOnClickListener(this);
        mCancelOrderButton.setOnClickListener(this);
        mOnHoldButton.setOnClickListener(this);

        setCalculationListener(this);

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });


        if (orderType != null && orderType.equals("OnHold")) {
            mPayButton.setVisibility(View.VISIBLE);
            mCancelOrderButton.setVisibility(View.VISIBLE);
            mOnHoldButton.setVisibility(View.VISIBLE);

            mPutOnHoldButton.setVisibility(View.INVISIBLE);
            mCompleteButton.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        couponCodedList = new ArrayList<>();
        getCouponCodes();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.completeButton: {
                if (peripheralManager != null){
                    //peripheralManager.displayAlignedText("NET AMOUNT:", NET_AMOUNT);
                    peripheralManager.displayAlignedText("NET AMOUNT:", amountIncludesGST);
                }

                String membershipId = mMemberShipIDET.getText().toString();

                if (!TextUtils.isEmpty(membershipId))
                    validateMembershipId(membershipId);
                else
                    completeOrder();

                break;
            }
            case R.id.putOnHoldButton: {
                addOrder("on hold");
                break;
            }
            case R.id.redeemApply: {
                Util.hideSoftKeypad(getActivity());
                applyLoyaltyPoints();
                break;
            }
            case R.id.couponApply: {
                Util.hideSoftKeypad(getActivity());
                applyCouponCode();
                break;
            }
            case R.id.couponRemove: {
                removeCoupon();
                break;
            }
            case R.id.redeemRemove: {
                removeRedeemPoints();
                break;
            }
            case R.id.payButton: {
                payDialog(getActivity());
                break;
            }
            case R.id.cancelOrderButton: {
                addOrder("cancelled");
                break;
            }
            case R.id.onHoldOrderButton: {
                addOrder("on hold");
                break;
            }

        }
    }

    private void validateMembershipId(String id) {

        new WebServiceCalling().callWS(getActivity(), UiController.appUrl + "membership/" + id, null, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    completeOrder();

                } catch (JSONException e) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), e);
                }
            }

            @Override
            public void onError(String error) {
                System.out.println();
            }
        });
    }

    private void removeCoupon() {
        mCouponCodeET.setSelection(0);
        mCouponCodeApplyButton.setVisibility(View.VISIBLE);
        mCouponCodeET.setVisibility(View.VISIBLE);
        mRemoveCouponCode.setVisibility(View.INVISIBLE);
        mCouponAmountTV.setText("Coupon Amount");
        mCouponCodeET.setSelection(0);
        COUPON_AMOUNT = 0.0;
        COUPON_CODE="";
        calculationSetting();


        mCouponAmountValueTV.setText("" + COUPON_AMOUNT);
    }

    private void removeRedeemPoints() {
        mRedeemPointsET.setVisibility(View.VISIBLE);
        mRedeemPointsApplyButton.setVisibility(View.VISIBLE);
        mRemoveRedeemPoints.setVisibility(View.INVISIBLE);
        REDEEMED_POINTS = 0;
        REDEEMED_AMOUNT = 0.0;
        calculationSetting();

        // mRedeemAmountTV.setText("Redemption Amount");
        // mRedeemAmountValueTV.setText("" + REDEEMED_AMOUNT);
    }

    private void applyLoyaltyPoints() {
        SavePreferences pref =UiController.getInstance().getSavePreferences();
        if(pref.getIsloyaltyOn().toString().trim().equals("1")){
            if (mRedeemPointsET.getText().toString().length() > 0) {

                REDEEMED_POINTS = Integer.parseInt(mRedeemPointsET.getText().toString());
                if (REDEEMED_POINTS <= EARNED_LOYALTY_POINTS) {
                    REDEEMED_AMOUNT = REDEEMED_POINTS;
                    if (REDEEMED_AMOUNT <= (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT)) {
                        calculationSetting();
                    } else {
                        REDEEMED_AMOUNT = 0;
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Redeemed amount is more than total amount.",
                                "Please enter loyalty points less than or equal to the Gross Amount.", "OK", android.R.drawable.ic_dialog_alert, null);
                    }
                } else {
                    REDEEMED_POINTS = 0;
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Entered loyalty points are wrong",
                            "Please enter less or equal points from the earned loyalty points", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            } else {
                new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid loyalty points",
                        "Please enter valid loyalty points", "OK", android.R.drawable.ic_dialog_alert, null);
            }
        }
        else{
            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid loyalty points",
                    "Please enable loyalty points from settings", "OK", android.R.drawable.ic_dialog_alert, null);
        }
    }

    private void completeOrder() {
        Util.hideSoftKeypad(this.getActivity());
        try {
            if (mOrderAdapter.getCount() > 0) {
                DateFormat df = new SimpleDateFormat("dd MMM, yyyy");
                String date = df.format(Calendar.getInstance()
                        .getTime());
                openFragment(setOrderPreviewArguments(date));
            } else {
                new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                        "Please add at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
            }
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
            ex.printStackTrace();
        }


    }

    private Bundle setOrderPreviewArguments(String date) {
        OrderPreviewWrapper wrapper = new OrderPreviewWrapper();
        customerName = mCustomerNameET.getText().toString();
        memberId = mMemberShipIDET.getText().toString();
        wrapper.setMembershipId(memberId);
        wrapper.setCustomerName(customerName);
        wrapper.setOrderDate(date);
        wrapper.setDiscountPercentage(DISCOUNT_PERCENTAGE);
        wrapper.setCouponCode(COUPON_CODE);
        //wrapper.setRedeemPoints(REDEEMED_POINTS);
        wrapper.setRedeemPoints(new Double(REDEEMED_AMOUNT).intValue());
        wrapper.setGrossAmount(GROSS_AMOUNT);
        wrapper.setDiscountAmount(DISCOUNT_AMOUNT);
        wrapper.setCouponAmount(COUPON_AMOUNT);
        wrapper.setRedeemedAmount(REDEEMED_AMOUNT);
        wrapper.setTaxPercentage(TAX_PERCENTAGE);
        wrapper.setTaxAmount(TAX_AMOUNT);
        wrapper.setNetAmount(NET_AMOUNT);
        wrapper.setOrderList(getOrderList());
        wrapper.setPaymentType(paymentType);

        Bundle arguments = new Bundle();
        bus.postSticky(wrapper);
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");

        return arguments;
    }

    private void addOrder(String status) {
        Util.hideSoftKeypad(this.getActivity());
        try {

            String product_id = "", quantity = "";
            for (int i = 0; i < mOrderAdapter.getCount(); i++) {
                if (i == 0) {
                    product_id = mOrderAdapter.getItem(i).getId();
                    quantity = "" + mOrderAdapter.getItem(i).getAddedQuantity();
                } else {
                    product_id += "," + mOrderAdapter.getItem(i).getId();
                    quantity += "," + mOrderAdapter.getItem(i).getAddedQuantity();
                }
            }
            if (Validation.isValidString(product_id)) {
                HashMap<String, Object> map = new HashMap<String, Object>();

                map.put("membership_id", mMemberShipIDET.getText().toString().trim());
                map.put("customer_name", mCustomerNameET.getText().toString());
                map.put("coupon_code", COUPON_CODE);
                map.put("redeemed_points", REDEEMED_POINTS);
                map.put("employee_id", mSavePreferences.get_id());
                map.put("status", status);
                map.put("payment_type", paymentType);

                if (wrapper != null && !TextUtils.isEmpty(wrapper.getId()))
                    map.put("order_id", wrapper.getId());

                map.put("product_id", product_id);
                map.put("qty", quantity);

                if (status.equals("on hold") || status.equals("cancelled"))
                    OrderWrapper.addOrderWithoutPrint(map, getActivity(), this);
                else
                    OrderWrapper.addOrder(map, getActivity(), this);


            } else {
                new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                        "Please add at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
            }

        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
            ex.printStackTrace();
        }

    }

    private void performSearch() {
        Util.hideSoftKeypad(getActivity());
        final String search = mSearchEt.getText().toString();
        if (TextUtils.isEmpty(search))
            mSearchEt.setError("Membership ID/ Phone number is required");
        else {
            new CustomerWrapper() {
                @Override
                public void getCustomerWrapper(Context context, String membershipId, String customerName, String earnedLoyaltyPoints) {

                    try {
                        EARNED_LOYALTY_POINTS = Integer.parseInt(earnedLoyaltyPoints);
                        mPointsTV.setText(EARNED_LOYALTY_POINTS+"\nPoints");
                    } catch (Exception ex) {
                        RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
                        ex.printStackTrace();
                    }
                    mSearchEt.setText("");
                    mSearchEt.clearFocus();
                    mCustomerNameET.setText(customerName);
                    mMemberShipIDET.setText(membershipId);

                }
            }.getCustomer(search, getActivity());
        }
    }

    @Override
    public void setGrossAndDiscountAmountValue() {
        reCalculatePrice();
    }

    private void reCalculatePrice() {
        try {
            mSubTotalValueTV.setText(Util.priceFormat(GROSS_AMOUNT));
            if (wrapper != null) {
                if (!TextUtils.isEmpty(wrapper.getTaxPercentage())) {
                    gstAmount = Util.roundUpToTwoDecimal(GROSS_AMOUNT - GROSS_AMOUNT / (1 + Double.parseDouble(wrapper.getTaxPercentage()) / 100));
                    amountBeforeGST = Util.roundUpToTwoDecimal(GROSS_AMOUNT - gstAmount);
                    if (!TextUtils.isEmpty(wrapper.getDiscountPercentage())) {
                        discountAmount = Util.roundUpToTwoDecimal((amountBeforeGST * Double.parseDouble(wrapper.getDiscountPercentage())) / 100);
                    }
                    totalDiscountAmount = Util.roundUpToTwoDecimal(discountAmount + COUPON_AMOUNT + REDEEMED_AMOUNT);
                    if(totalDiscountAmount > 0) {
                        amountAfterDiscount = Util.roundUpToTwoDecimal(amountBeforeGST - totalDiscountAmount);
                        gstInclusive = Util.roundUpToTwoDecimal((amountAfterDiscount * Double.parseDouble(wrapper.getTaxPercentage())) / 100);
                    }else{
                        amountAfterDiscount = amountBeforeGST - totalDiscountAmount;
                        gstInclusive = gstAmount;

                    }
                    amountIncludesGST = Util.roundUpToTwoDecimal(amountAfterDiscount + gstInclusive);
                }
            }else{
                gstAmount = Util.roundUpToTwoDecimal(GROSS_AMOUNT - GROSS_AMOUNT / (1 + TAX_PERCENTAGE / 100));
                amountBeforeGST = Util.roundUpToTwoDecimal(GROSS_AMOUNT - gstAmount);
                discountAmount = Util.roundUpToTwoDecimal((amountBeforeGST * DISCOUNT_PERCENTAGE) / 100);
                totalDiscountAmount = Util.roundUpToTwoDecimal(discountAmount + COUPON_AMOUNT + REDEEMED_AMOUNT);
                if(totalDiscountAmount > 0) {
                    amountAfterDiscount = Util.roundUpToTwoDecimal(amountBeforeGST - totalDiscountAmount);
                    gstInclusive = Util.roundUpToTwoDecimal((amountAfterDiscount * TAX_PERCENTAGE) / 100);
                }else{
                    amountAfterDiscount = amountBeforeGST - totalDiscountAmount;
                    gstInclusive = gstAmount;

                }
                amountIncludesGST = Util.roundUpToTwoDecimal(amountAfterDiscount + gstInclusive);
            }
            mAmountBeforeGSTValueTV.setText(Util.priceFormat(amountBeforeGST));
            if (discountAmount == 0) {
                mDiscountAmountTV.setText("Discount Amount");
                mDiscountAmountValueTV.setText("0.00");
            } else {
                mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(DISCOUNT_PERCENTAGE) + "%)");
                mDiscountAmountValueTV.setText(Util.priceFormat(discountAmount));
            }
            mGstInclusiveTV.setText("" + Util.priceFormat(TAX_PERCENTAGE) + "% GST inclusive");
            if (TextUtils.isEmpty(COUPON_CODE)) {
                mCouponAmountTV.setText("Coupon Amount");
            } else if(wrapper != null){
                mCouponAmountValueTV.setText(wrapper.getCouponCode());
                if(!TextUtils.isEmpty(wrapper.getCouponCode())) {
                    mCouponAmountTV.setText("Coupon Amount (" + wrapper.getCouponCode() + ")");
                }
            }
            if (REDEEMED_POINTS == 0) {
                mRedeemAmountTV.setText("Redemption Amount");
            } else {
                mRedeemAmountTV.setText("Redemption Amount (" + REDEEMED_POINTS + " Points)");
            }
            if (wrapper != null && !TextUtils.isEmpty(wrapper.getTaxPercentage())) {
                mDiscountAmountTV.setText(getString(R.string.discount_Amount) + "(" + wrapper.getDiscountPercentage() + "%)");
            }
            mCouponAmountValueTV.setText(Util.priceFormat(COUPON_AMOUNT));
            mRedeemAmountValueTV.setText(Util.priceFormat(REDEEMED_AMOUNT));
            mGstInclusiveValueTV.setText(Util.priceFormat(gstInclusive));
            mAmountIncludesGSTValueTV.setText(Util.priceFormat(amountIncludesGST));
            mAmountAfterDiscountValueTV.setText(Util.priceFormat(amountAfterDiscount));
        }catch (Exception e){

        }

    }
    @Override
    public void setCouponAndRedeemedAmount(String status) {
        if (REDEEMED_AMOUNT == 0.0) {
            // mRedeemAmountValueTV.setText("0.00");
            // mRedeemAmountTV.setText("Redemption Amount");
        } else {
            int redeem=(int)REDEEMED_AMOUNT;
            //  mRedeemAmountTV.setText("Redemption Amount ("+redeem+" Points)");
            // mRedeemAmountValueTV.setText(Util.priceFormat(REDEEMED_AMOUNT));

            mRedeemPointsApplyButton.setVisibility(View.INVISIBLE);
            mRemoveRedeemPoints.setVisibility(View.VISIBLE);
            mRedeemPointsET.setVisibility(View.GONE);
        }

        if (COUPON_AMOUNT == 0.0) {
            if (!TextUtils.isEmpty(status) && REDEEMED_AMOUNT == 0.0)
                Crouton.makeText(getActivity(), status, Style.INFO).show();
            //  mCouponAmountValueTV.setText("0.00");
            mCouponAmountTV.setText("Coupon Amount");
        } else {
            if (!TextUtils.isEmpty(status) && REDEEMED_AMOUNT == 0.0)
                Crouton.makeText(getActivity(), status, Style.INFO).show();
            //  mCouponAmountValueTV.setText(Util.priceFormat(COUPON_AMOUNT));
            //  mCouponAmountTV.setText("Coupon Amount "+"("+COUPON_CODE+")");
            mCouponCodeApplyButton.setVisibility(View.INVISIBLE);
            mCouponCodeET.setVisibility(View.GONE);
            mRemoveCouponCode.setVisibility(View.VISIBLE);
        }
        reCalculatePrice();
    }

    @Override
    public void setDefaultValues() {

        GROSS_AMOUNT = 0.0;
        DISCOUNT_AMOUNT = 0.0;
        COUPON_AMOUNT = 0.0;
        REDEEMED_AMOUNT = 0.0;
        TAX_AMOUNT = 0.0;
        NET_AMOUNT = 0.0;

        mDiscountAmountValueTV.setText("0.00");
        mCouponAmountTV.setText("Coupon Amount");
        mCouponCodeApplyButton.setVisibility(View.VISIBLE);
        mCouponCodeET.setVisibility(View.VISIBLE);
        mRemoveCouponCode.setVisibility(View.INVISIBLE);
        mCouponCodeET.setSelection(0);

        mDiscountAmountTV.setText("Discount Amount");

        peripheralManager.displayShopName(mSavePreferences.getReceiptName());
    }

    @Override
    public void setDiscountAndTaxPercentage() {
        reCalculatePrice();
    }

    @Override
    public void setTaxAndNetAmount() {
        if (peripheralManager != null)
            peripheralManager.displayAlignedText(recentlyAddedItemName,Util.priceFormat(recentlyAddedItemPrice));

    }


    protected void setMsrData(String data) {
        mSearchEt.setText(data);
        performSearch();
    }

    protected void initializeAllViews() {

        mSubTotalTV = (TextView) rootView.findViewById(R.id.subTotalTV);
        mSubTotalValueTV = (TextView) rootView.findViewById(R.id.subTotalValueTV);
        mSubTotalValueTV = (TextView) rootView.findViewById(R.id.subTotalValueTV);
        mAmountBeforeGSTValueTV = (TextView) rootView.findViewById(R.id.amountBeforeGSTValueTV);
        mAmountBeforeGSTTV = (TextView) rootView.findViewById(R.id.amountBeforeGSTTV);
        mDiscountAmountTV = (TextView) rootView.findViewById(R.id.discountAmountTV);
        mDiscountAmountValueTV = (TextView) rootView.findViewById(R.id.discountAmountValueTV);
        mAmountBeforeGSTTV= (TextView)rootView.findViewById(R.id.amountBeforeGSTTV);
        mAmountBeforeGSTValueTV= (TextView)rootView.findViewById(R.id.amountBeforeGSTValueTV);

        mAmountAfterDiscountTV = (TextView)rootView.findViewById(R.id.amountAfterDiscountTV);
        mAmountAfterDiscountValueTV = (TextView)rootView.findViewById(R.id.amountAfterDiscountValueTV);

        mGstInclusiveTV = (TextView)rootView.findViewById(R.id.gstInclusiveTV);
        mGstInclusiveValueTV = (TextView)rootView.findViewById(R.id.gstInclusiveValueTV);

        mAmountIncludesGSTTV = (TextView)rootView.findViewById(R.id.amountIncludeGSTTV);
        mAmountIncludesGSTValueTV = (TextView)rootView.findViewById(R.id.amountIncludeGSTValueTV);

        mAmountIncludesGSTValueTV = (TextView)rootView.findViewById(R.id.amountIncludeGSTValueTV);
        mAmountIncludesGSTValueTV = (TextView)rootView.findViewById(R.id.amountIncludeGSTValueTV);

        mCouponAmountValueTV = (TextView) rootView.findViewById(R.id.couponAmountValueTV);
        mCouponAmountTV = (TextView) rootView.findViewById(R.id.couponAmountTV);
        mRedeemAmountValueTV = (TextView) rootView.findViewById(R.id.redeemAmountValueTV);
        mRedeemAmountTV = (TextView) rootView.findViewById(R.id.redeemAmountTV);
        mPaymentTypeValueTV = (TextView) rootView.findViewById(R.id.paymentTypeValueTV);
        mPointsTV = (TextView) rootView.findViewById(R.id.pointsTV);

        mMemberShipIDET = (EditText) rootView.findViewById(R.id.membership_idET);
        mCustomerNameET = (EditText) rootView.findViewById(R.id.customerNameET);
        mCouponCodeET = (Spinner) rootView.findViewById(R.id.couponCodeEt);
        mRedeemPointsET = (EditText) rootView.findViewById(R.id.redeemAmountEt);
        mSearchEt = (EditText) rootView.findViewById(R.id.search);

        mCouponCodeApplyButton = (Button) rootView.findViewById(R.id.couponApply);
        mRedeemPointsApplyButton = (Button) rootView.findViewById(R.id.redeemApply);
        mPutOnHoldButton = (Button) rootView.findViewById(R.id.putOnHoldButton);
        mCompleteButton = (Button) rootView.findViewById(R.id.completeButton);
        mRemoveCouponCode = (Button) rootView.findViewById(R.id.couponRemove);
        mRemoveRedeemPoints = (Button) rootView.findViewById(R.id.redeemRemove);
        mPayButton = (Button) rootView.findViewById(R.id.payButton);
        mCancelOrderButton = (Button) rootView.findViewById(R.id.cancelOrderButton);
        mOnHoldButton = (Button) rootView.findViewById(R.id.onHoldOrderButton);

        if(DISCOUNT_AMOUNT==0){
            mDiscountAmountTV.setText("Discount Amount");
        }else{
            mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(DISCOUNT_PERCENTAGE) + "%)");
        }



        if (wrapper != null) {
            if (wrapper.getMembershipId() != null)
                mMemberShipIDET.setText("" + wrapper.getMembershipId());
            if (!TextUtils.isEmpty(wrapper.getCustomerName()))
                mCustomerNameET.setText(wrapper.getCustomerName());

            if (wrapper.getEarnedPoints() != null && !wrapper.getEarnedPoints().equals("0")) {
                EARNED_LOYALTY_POINTS = Integer.parseInt(wrapper.getEarnedPoints());
                mPointsTV.setText(EARNED_LOYALTY_POINTS+"\nPoints");
            }
        }


        if (!TextUtils.isEmpty(customerName))
            mCustomerNameET.setText(customerName);
        if (!TextUtils.isEmpty(memberId))
            mMemberShipIDET.setText(memberId);
        if (EARNED_LOYALTY_POINTS != 0)
            mPointsTV.setText(EARNED_LOYALTY_POINTS+"\nPoints");
    }

    private void applyCouponCode() {
        if (mCouponCodeET != null) {
            if (mCouponCodeET.getSelectedItemPosition() != 0) {
                try {
                    CouponCodeWrapper.CouponCodeData data = (CouponCodeWrapper.CouponCodeData) mCouponCodeET.getSelectedItem();
                    COUPON_CODE = data.getCouponCode().trim();
                    COUPON_AMOUNT = Double.parseDouble(data.getAmount());

                    calculationSetting();

                    if (GROSS_AMOUNT == 0) {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "No Item.",
                                "Please add at least one item to apply the coupon code.", "OK", android.R.drawable.ic_dialog_alert, null);
                    }

                } catch (Exception ex) {
                    RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
                    ex.printStackTrace();
                }
            } else {
                new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid coupon code.",
                        "Please enter a valid coupon code.", "OK", android.R.drawable.ic_dialog_alert, null);
            }
        }
    }

    private void setSpinnerAdapter() {

        final Context context = getActivity();
        ArrayAdapter<CouponCodeWrapper.CouponCodeData> couponCodeAdapter = new ArrayAdapter<CouponCodeWrapper.CouponCodeData>(getActivity(), android.R.layout.simple_spinner_item, couponCodedList) {
            private int boundRight,
                    padding,
                    deviceHeight,
                    boundbottom;
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                deviceHeight = displayMetrics.heightPixels;
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_view,
                            null);
                    holder = new ViewHolder();
                    holder.mTextView = (TextView) convertView;

                    int boundRight = (int) (deviceHeight * .025f);
                    int boundbottom = (int) (deviceHeight * .025f * 0.9f);

                    Drawable rightDrawable = context.getResources().getDrawable(
                            R.drawable.dropdown_arrow);
                    if (rightDrawable != null) {
                        rightDrawable.setBounds(0, 0, boundRight, boundbottom);
                    }
                    holder.mTextView.setCompoundDrawables(null, null, rightDrawable,
                            null);
                    holder.mTextView.setCompoundDrawablePadding(padding);
                    holder.mTextView.setPadding(padding, 0, padding, 0);
                    convertView.setTag(holder);
                } else
                    holder = (ViewHolder) convertView.getTag();

                holder.mTextView.setTextColor(context.getResources().getColor(
                        android.R.color.black));
                if (couponCodedList != null && couponCodedList.size() != 0)
                    holder.mTextView.setText("" + couponCodedList.get(position).getCouponCode());

                return convertView;
            }

            class ViewHolder {
                TextView mTextView;
            }
        };
        couponCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCouponCodeET.setAdapter(couponCodeAdapter);
    }

    private void getCouponCodes() {

        new CouponCodeWrapper() {
            @Override
            public void getCoupons(Context context, List<CouponCodeData> data) {

                boolean applyCoupon = false;
                CouponCodeData couponCodeData = new CouponCodeWrapper().new CouponCodeData();
                couponCodeData.setCouponCode("Select Coupon");
                couponCodedList.add(couponCodeData);
                for (int i = 0; i < data.size(); i++) {
                    CouponCodeData wrapper = data.get(i);

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        Date date1 = sdf.parse(wrapper.getValidityFromDate());
                        Date date2 = sdf.parse(sdf.format(new Date()));
                        Date date3 = sdf.parse(wrapper.getValidityToDate());

                        if (date1.compareTo(date2) > 0) {
                            System.out.println("Date1 is after Date2");
                            applyCoupon = false;
                        } else if (date3.compareTo(date2) < 0) {
                            System.out.println("Date3 is before Date2");
                            applyCoupon = false;
                        } else {
                            applyCoupon = true;
                        }

                        if (applyCoupon) {
                            try {
                                couponCodedList.add(wrapper);
                                Log.i("couponCodedList",""+wrapper.getCouponCode());
                                //calculationSetting();
                            } catch (Exception ex) {
                                RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
                                ex.printStackTrace();
                            }
                        }

                    } catch (Exception ex) {
                        RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
                        ex.printStackTrace();
                    }
                }
                setSpinnerAdapter();

                if (wrapper != null && couponCodedList != null) {
                    if (!TextUtils.isEmpty(wrapper.getCouponCode())) {
                        mCouponCodeET.setSelection(getCouponPosition(wrapper.getCouponCode()));
                        applyCouponCode();
                    }
                }

                if (wrapper != null) {
                    if(!TextUtils.isEmpty(wrapper.getEarnedPoints())) {
                        mRedeemPointsET.setText(wrapper.getRedeemedPoints());
                        applyLoyaltyPoints();
                    }
                }


            }
        }.getCouponCodeList(getActivity(), null);

        if (couponCodedList == null)
            new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                    "Valid Coupon not available", "OK", android.R.drawable.ic_dialog_alert, null);


    }

    public void payDialog(final Context context) {
        try {
            final Dialog dialog = new CustomDialog().getDialog(context, R.layout.pay_popup_dialog_layout);
            TextView accept_button = (Button) dialog.findViewById(R.id.accept_button);
            final EditText amount_paidET = (EditText) dialog.findViewById(R.id.amount_paidET);
            amount_paidET.addTextChangedListener(new TextWatcher(){
                @Override
                public void afterTextChanged(Editable s) {
                    String str = amount_paidET.getText().toString();
                    DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
                    DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
                    char sep = symbols.getDecimalSeparator();

                    int indexOFdec = str.indexOf(sep);

                    if (indexOFdec >= 0) {
                        if (str.substring(indexOFdec, str.length() - 1).length() > 2) {
                            s.replace(0, s.length(), str.substring(0, str.length() - 1));
                        }
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
            TextView cancel_button = (TextView) dialog.findViewById(R.id.cancelDialog);
            TextView currency = (TextView) dialog.findViewById(R.id.currency);
            TextView save_button = (TextView) dialog.findViewById(R.id.saveCategory);
            TextView totalAmount = (TextView) dialog.findViewById(R.id.totalAmount);
            final TextView amountPaid = (TextView) dialog.findViewById(R.id.amountPaid);
            final TextView cashDue = (TextView) dialog.findViewById(R.id.cashDue);

//            totalAmount.setText(Util.priceFormat(NET_AMOUNT));
            totalAmount.setText(Util.priceFormat(amountIncludesGST));
            currency.setText(UiController.sCurrency);
            Spinner paymentModeSpinner = (Spinner) dialog.findViewById(R.id.paymentSpinner);


            // Drop down layout style - list view with radio button

            // attaching data adapter to spinner
            //paymentModeSpinner.setAdapter(paymentModeAdapter);
            setPaymentSpinnerAdapter(paymentModeSpinner);
            paymentModeSpinner.setOnItemSelectedListener(this);

            accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    amount_paidET.setError(null);
                    if(!Util.isValidPrice(amount_paidET) && new Validation().checkValidation((ViewGroup) dialog.findViewById(R.id.container))){
                        return;
                    }
                    if (amount_paidET.getText().toString().length() > 0) {
                        double amount = Double.parseDouble(amount_paidET.getText().toString());
                        double netAmount = Double.parseDouble(Util.price(NET_AMOUNT));

                        if (amount >= amountIncludesGST) {
                            amountPaid.setText(Util.priceFormat(amount));
                            double due = amount - amountIncludesGST;
                            cashDue.setText(Util.priceFormat(due));
                            wrapper.setCashDue(Util.priceFormat(due));
                            confirmButtonClicked = false;

                            if (peripheralManager != null) {
                                blockUi();
                                    peripheralManager.openCashDrawer();
                                peripheralManager.resetDisplay();
                                peripheralManager.displayAlignedText("CHANGE DUE:", Util.priceFormat(due));
                            }


                        } else {
                            amount_paidET.setError("Paid amount should be equal or more than net amount.");
                        }
                    } else {
                        amount_paidET.setError("Paid amount should be equal or more than net amount.");
                    }
                    Util.hideSoftKeypad(context);
                }

            });
            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (new Validation().checkValidation((ViewGroup) dialog.findViewById(R.id.container))) {
                        if (!amountPaid.getText().toString().equals("0.00")) {
                            dialog.dismiss();
                            confirmButtonClicked = true;
                            wrapper.setPaymentType(paymentType);
                          if (isPrinterOk)
                                addOrder("completed");
                            else
                                Crouton.makeText(getActivity(), "Please fix printer error first.", Style.ALERT).show();
                        } else
                            amount_paidET.setError("Please fill and accept correct amount.");
                    }
                }
            });

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();

                }
            });
            dialog.show();
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), ex);
        }
    }

    private void setPaymentSpinnerAdapter(Spinner paymentModeSpinner) {

        final Context context = getActivity();
        final List<String> paymentModes = new ArrayList<String>();
        paymentModes.add(context.getString(R.string.title_select_payment_mode));
        paymentModes.add(context.getString(R.string.text_visa_master));
        paymentModes.add(context.getString(R.string.text_nets));
        paymentModes.add(context.getString(R.string.text_cash));
        paymentModeSpinner.setPrompt(context.getString(R.string.title_select_payment_mode));

        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, paymentModes) {
            int boundRight,
                    padding,
                    deviceHeight,
                    boundbottom;
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                deviceHeight = displayMetrics.heightPixels;
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_view,
                            null);
                    holder = new ViewHolder();
                    holder.mTextView = (TextView) convertView;

                    int boundRight = (int) (deviceHeight * .025f);
                    int boundbottom = (int) (deviceHeight * .025f * 0.9f);

                    Drawable rightDrawable = context.getResources().getDrawable(
                            R.drawable.dropdown_arrow);
                    if (rightDrawable != null) {
                        rightDrawable.setBounds(0, 0, boundRight, boundbottom);
                    }
                    holder.mTextView.setCompoundDrawables(null, null, rightDrawable,
                            null);
                    holder.mTextView.setCompoundDrawablePadding(padding);
                    holder.mTextView.setPadding(padding, 0, padding, 0);
                    convertView.setTag(holder);
                } else
                    holder = (ViewHolder) convertView.getTag();

                holder.mTextView.setTextColor(context.getResources().getColor(
                        android.R.color.black));
                if (paymentModes != null && paymentModes.size() != 0)
                    holder.mTextView.setText("" + paymentModes.get(position));

                return convertView;
            }

            class ViewHolder {
                TextView mTextView;
            }
        };
        paymentModeSpinner.setAdapter(paymentModeAdapter);
        paymentModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


    }


    private int getCouponPosition(String couponCode) {
        if (couponCodedList != null && couponCodedList.size() > 0) {
            for (int i = 0; i < couponCodedList.size(); i++) {
                if (couponCodedList.get(i).getCouponCode().equals(couponCode))
                    return i;
            }
        }
        return 0;
    }

    private void blockUi()
    {
        mDialog = new ProgressDialog(getActivity(), android.R.style.Theme_Holo_Dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(6 * 1000);
                    if(mDialog.isShowing())
                        mDialog.dismiss();
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                    RetailPosLoging.getInstance().registerLog(PlaceOrderFragment.class.getName(), e);
                }
            }
        };

        // start thread
        background.start();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position != 0) {
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
           // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            paymentType = item;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
