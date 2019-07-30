package com.printer.epos.rtpl;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.AddOrderListAdapter;
import com.printer.epos.rtpl.adapter.ProductListAdapter;
import com.printer.epos.rtpl.adapter.SelectedProductCache;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.CouponCodeWrapper;
import com.printer.epos.rtpl.wrapper.CustomerWrapper;
import com.printer.epos.rtpl.wrapper.OrderPreviewWrapper;
import com.printer.epos.rtpl.wrapper.OrderWrapper;
import com.printer.epos.rtpl.wrapper.ProductOrderWrapper;
import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-pc3 on 7/5/15.
 */
public class OrderOnHoldFragment extends BaseFragment implements View.OnClickListener {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";


    private int REDEEMED_POINTS = 0;
    private String COUPON_CODE = "";
    private int EARNED_LOYALTY_POINTS = 0;
    private double DISCOUNT_PERCENTAGE = 0.0;
    private double TAX_PERCENTAGE = 0.0;
    private double MINIMUM_SPEND_AMOUNT = 0.0;
    private double REDEEMED_POINT_PRICE = 0.0;

    private double GROSS_AMOUNT = 0.0;
    private double DISCOUNT_AMOUNT = 0.0;
    private double COUPON_AMOUNT = 0.0;
    private double REDEEMED_AMOUNT = 0.0;
    private double TAX_AMOUNT = 0.0;
    private double NET_AMOUNT = 0.0;

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderOnHoldFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
      /*  if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }*/
        orderList = new ArrayList<ProductOrderWrapper>();

    }

    private Home hContext;

    private int deviceWidth;
    private int deviceHeight;

    private SavePreferences mSavePreferences;

    private ScrollView formScroll;
    private RelativeLayout mAmountLayout, mPointsLayout;
    private TextView mMembershipIDTV, mCustomerNameTV, mUnitPriceTV, mQuantityTV, mTotalPriceTV, mCouponCodeTV, mRedeemPointsTV, mGotSGDTV,
            mGrossAmountTV, mGrossAmountValueTV, mDiscountAmountTV, mDiscountAmountValueTV, mCouponAmountTV, mCouponAmountValueTV, mRedeemAmountTV,
            mRedeemAmountValueTV, mTaxTV, mTaxValueTV, mNetAMountTV, mNetAMountValeTV, mPointsTV, mProductNameTV, mRegNoTV, mRegNoValueTV;

    private EditText mMemberShipIDET, mCustomerNameET, mCouponCodeET, mRedeemPointsET;
    private TextView mEmptyTextView;

    private Button mCouponCodeApplyButton, mRedeemPointsApplyButton, mAddItemButton, mPutOnHoldButton, mCompleteButton;
    private View listSepartor, listSepartor1;
    private ListView mOrderListView;
    private AddOrderListAdapter mOrderAdapter;
    private ProductListAdapter mProductAdapter;

    private View rootView;
    private OrderWrapper wrapper;
//    private boolean update = false;

    private static List<ProductWrapper> productListForPopup;
    private static List<ProductOrderWrapper> orderList;

    private final EventBus bus = EventBus.getDefault();

    private OrderWrapper.OrderInnerWrapper orderInnerWrapper;


    void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.requestFocus();
    }

    void setupUI(final View view) {
        view.setFocusableInTouchMode(true);

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_order, container, false);

        setupUI(rootView);


        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        ImageLoader mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(false).cacheInMemory(false)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();


        formScroll = (ScrollView) rootView.findViewById(R.id.formScroll);

        mAmountLayout = (RelativeLayout) rootView.findViewById(R.id.amountLayout);
        mPointsLayout = (RelativeLayout) rootView.findViewById(R.id.pointsLayout);

        mMembershipIDTV = (TextView) rootView.findViewById(R.id.membershipIdTV);
        mUnitPriceTV = (TextView) rootView.findViewById(R.id.unitPriceTV);
        mProductNameTV = (TextView) rootView.findViewById(R.id.productNameTV);
        mCustomerNameTV = (TextView) rootView.findViewById(R.id.customerNameTV);
        mQuantityTV = (TextView) rootView.findViewById(R.id.quantityTV);
        mTotalPriceTV = (TextView) rootView.findViewById(R.id.totalPriceTV);
        mCouponCodeTV = (TextView) rootView.findViewById(R.id.couponCodeTV);
        mRedeemPointsTV = (TextView) rootView.findViewById(R.id.redeemPointsTV);
        mGotSGDTV = (TextView) rootView.findViewById(R.id.gotSGDTV);
        mGrossAmountTV = (TextView) rootView.findViewById(R.id.grossAmountTV);
        mGrossAmountValueTV = (TextView) rootView.findViewById(R.id.grossAmountValueTV);
        mDiscountAmountTV = (TextView) rootView.findViewById(R.id.discountAmountTV);
        mDiscountAmountValueTV = (TextView) rootView.findViewById(R.id.discountAmountValueTV);
        mCouponAmountTV = (TextView) rootView.findViewById(R.id.couponAmountTV);
        mCouponAmountValueTV = (TextView) rootView.findViewById(R.id.couponAmountValueTV);
        mRedeemAmountTV = (TextView) rootView.findViewById(R.id.redeemeAmountTV);
        mRedeemAmountValueTV = (TextView) rootView.findViewById(R.id.redeemeAmountValueTV);
        mTaxTV = (TextView) rootView.findViewById(R.id.taxTV);
        mTaxValueTV = (TextView) rootView.findViewById(R.id.taxValueTV);
        mRegNoTV = (TextView) rootView.findViewById(R.id.gstRegNoTV);
        mRegNoValueTV = (TextView) rootView.findViewById(R.id.gstRegNoValueTV);
        mTaxValueTV = (TextView) rootView.findViewById(R.id.taxValueTV);
        mNetAMountTV = (TextView) rootView.findViewById(R.id.netAmountTV);
        mNetAMountValeTV = (TextView) rootView.findViewById(R.id.netAmountValueTV);
        mPointsTV = (TextView) rootView.findViewById(R.id.pointsTV);

        mMemberShipIDET = (EditText) rootView.findViewById(R.id.membershipID);
        mCustomerNameET = (EditText) rootView.findViewById(R.id.customerNameET);
        mCouponCodeET = (EditText) rootView.findViewById(R.id.couponCodeET);
        mRedeemPointsET = (EditText) rootView.findViewById(R.id.redeemPointsET);

        mCouponCodeApplyButton = (Button) rootView.findViewById(R.id.couponCodeApplyButton);
        mRedeemPointsApplyButton = (Button) rootView.findViewById(R.id.redeemPointsApplyButton);
        mAddItemButton = (Button) rootView.findViewById(R.id.addItemButton);
        mPutOnHoldButton = (Button) rootView.findViewById(R.id.putOnHoldButton);
        mCompleteButton = (Button) rootView.findViewById(R.id.completeButton);

        mOrderListView = (ListView) rootView.findViewById(R.id.orderListView);
        listSepartor = rootView.findViewById(R.id.listSeparator);
        listSepartor1 = rootView.findViewById(R.id.listSeparator1);

        setDimensionsToViews();


        mEmptyTextView = (TextView) rootView.findViewById(R.id.emptyTextView);
        mEmptyTextView.setText("No Item Added.");
        RelativeLayout.LayoutParams mEmptyTextViewParam = (RelativeLayout.LayoutParams) mEmptyTextView.getLayoutParams();
        mEmptyTextViewParam.height = (int) (deviceHeight * 0.3f);
        mEmptyTextViewParam.leftMargin = (int) (deviceWidth * 0.02f);
        mEmptyTextViewParam.rightMargin = (int) (deviceWidth * 0.01f);
        mEmptyTextView.setLayoutParams(mEmptyTextViewParam);

        mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(DISCOUNT_PERCENTAGE) + "%)");

        SelectedProductCache productCache = new SelectedProductCache();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);

        hContext.backButton.setOnClickListener(this);
        hContext.setTitleText(getString(R.string.new_order));

         /*
        *  Setting Item List Adapter.
        * */

        if (orderList != null) {

            double total = 0;
            for (ProductOrderWrapper aa : orderList) {
                total = total + aa.getTotalPrice();
            }
            grossAmountCalculation(total, false);
        } else {
            orderList = new ArrayList<ProductOrderWrapper>();
        }
        mOrderListView.setEmptyView(mEmptyTextView);
        mOrderAdapter = new AddOrderListAdapter(this, orderList);
        mOrderListView.setAdapter(mOrderAdapter);

        RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
        mOrderListViewParam.leftMargin = (int) (deviceWidth * 0.02f);
        mOrderListViewParam.rightMargin = (int) (deviceWidth * 0.01f);

        if (orderList.size() > 0) {
            View item = mOrderAdapter.getView(0, null, mOrderListView);
            item.measure(0, 0);
            //mOrderListViewParam.height = (int) (mOrderAdapter.getCount() * item.getMeasuredHeight());

            mOrderListViewParam.height = (int) (2.0 * item.getMeasuredHeight());
            if (mOrderAdapter.getCount() > 2) {
                mOrderListViewParam.height = (int) (2.5 * item.getMeasuredHeight());
            }
        }
        mOrderListView.setLayoutParams(mOrderListViewParam);

        setDiscountTaxPercentage();

        orderInnerWrapper = EventBus.getDefault().removeStickyEvent(OrderWrapper.OrderInnerWrapper.class);

        if (orderInnerWrapper != null) {

            if (orderInnerWrapper.getMembershipId() != null)
                mMemberShipIDET.setText("" + orderInnerWrapper.getMembershipId());
            if (Validation.isValidData(orderInnerWrapper.getCustomerName()))
                mCustomerNameET.setText(orderInnerWrapper.getCustomerName());

          /*  mDiscountPercentage = orderInnerWrapper.getDiscountPercentage();
            mCouponCode = wrapper.getCouponCode();
            mRedeemedPoints = wrapper.getRedeemedPoints();*/
            GROSS_AMOUNT = Double.valueOf(orderInnerWrapper.getGrossAmount());
            DISCOUNT_AMOUNT = Double.valueOf(orderInnerWrapper.getDiscountAmount());
            REDEEMED_AMOUNT = Double.valueOf(orderInnerWrapper.getRedeemedPoints());

            REDEEMED_POINT_PRICE = Double.valueOf(mSavePreferences.getEarnedAmount());
            REDEEMED_AMOUNT = REDEEMED_AMOUNT * REDEEMED_POINT_PRICE;

            if (orderInnerWrapper.getCouponDiscountAmount() != null)
                COUPON_AMOUNT = Double.valueOf(orderInnerWrapper.getCouponDiscountAmount().toString());
            else
                COUPON_AMOUNT = 0.0;

            TAX_AMOUNT = Double.valueOf(orderInnerWrapper.getTaxAmount());
            NET_AMOUNT = Double.valueOf(orderInnerWrapper.getFinalAmount());
            TAX_PERCENTAGE = Double.valueOf(orderInnerWrapper.getTaxPercentage());

            setOrderDetailValues();

        }

    }

    private void setOrderDetailValues() {

        if (!TextUtils.isEmpty(orderInnerWrapper.getTaxPercentage()))
            mTaxTV.setText("Tax(" + orderInnerWrapper.getTaxPercentage() + "%)");

     /*   if (!TextUtils.isEmpty(mDiscountPercentage))
            mDiscountPercentageValueTV.setText(mDiscountPercentage);

        if (TextUtils.isEmpty(mCouponCode))
            mCouponValueTV.setText("NA");
        else
            mCouponValueTV.setText(wrapper.getCouponCode());

        mRedeemPointsValueTV.setText(mRedeemedPoints);*/
        mGrossAmountValueTV.setText(Util.priceFormat(GROSS_AMOUNT));
        mDiscountAmountValueTV.setText(Util.priceFormat(DISCOUNT_AMOUNT));
        mCouponAmountValueTV.setText(Util.priceFormat(COUPON_AMOUNT));
        mRedeemAmountValueTV.setText(Util.priceFormat(REDEEMED_AMOUNT));

        TAX_AMOUNT = ((GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT - REDEEMED_AMOUNT) * TAX_PERCENTAGE) / 100;
        mTaxValueTV.setText(Util.priceFormat(TAX_AMOUNT));

        NET_AMOUNT = (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT - REDEEMED_AMOUNT) + TAX_AMOUNT;
        mNetAMountValeTV.setText(UiController.sCurrency + " " + Util.priceFormat(NET_AMOUNT));
        if(!TextUtils.isEmpty(mSavePreferences.getGSTRegNo())) {
            mRegNoValueTV.setText(mSavePreferences.getGSTRegNo());
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ProductWrapper.getProductList(null, getActivity(), this);
    }

    public void setProductList(List<ProductWrapper> data) {

        productListForPopup = data;

        createAddItemDialog();
    }


    private void setDiscountTaxPercentage() {
        mTaxTV.setText("Tax (" + mSavePreferences.getTaxPercentage() + "%)");

        try {
            TAX_PERCENTAGE = Double.parseDouble(mSavePreferences.getTaxPercentage());
            MINIMUM_SPEND_AMOUNT = Double.parseDouble(mSavePreferences.getDiscountMinSpend());

            REDEEMED_POINT_PRICE = Double.parseDouble(mSavePreferences.getEarnedAmount());
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
        }


        boolean applyDiscount = false;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(mSavePreferences.getDiscountFromDate());
            Date date2 = sdf.parse(sdf.format(new Date()));
            Date date3 = sdf.parse(mSavePreferences.getDiscountToDate());

            if (date1.compareTo(date2) > 0) {
                System.out.println("Date1 is after Date2");
                applyDiscount = false;
                return;
            } else if (date3.compareTo(date2) < 0) {
                System.out.println("Date3 is before Date2");
                applyDiscount = false;
                return;
            } else {
                applyDiscount = true;
            }


            if (applyDiscount) {
                try {
                    DISCOUNT_PERCENTAGE = Double.parseDouble(mSavePreferences.getDiscountPercentage());

                } catch (Exception ex) {
                    ex.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
                }
            } else {
                mDiscountAmountTV.setText("Discount Amount (0%)");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
            case R.id.completeButton: {
//                addOrder("completed");

                completeOrder();
                break;
            }
            case R.id.putOnHoldButton: {
                addOrder();
                break;
            }
            case R.id.redeemPointsApplyButton: {
                Util.hideSoftKeypad(getActivity());
                applyLoyaltyPoints();
                break;
            }
            case R.id.couponCodeApplyButton: {
                Util.hideSoftKeypad(getActivity());
                applyCouponCode();
                break;
            }
            case R.id.addItemButton: {
                addItemPopupShow();
                break;
            }
        }
    }

    private void applyCouponCode() {
        if (mMemberShipIDET.getText().toString().length() == 10) {
            new CouponCodeWrapper() {
                @Override
                public void getCoupons(Context context, List<CouponCodeData> data) {

                    boolean applyCoupon = false;

                    for (int i = 0; i < data.size(); i++) {
                        CouponCodeData wrapper = data.get(i);

                        if (mCouponCodeET.getText().toString().trim().equals(wrapper.getCouponCode().trim())) {
                            try {

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                                        COUPON_CODE = mCouponCodeET.getText().toString().trim();
                                        COUPON_AMOUNT = Double.parseDouble(wrapper.getAmount());

                                        calculationSetting();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
                            }
                        }
                    }

                    // If coupon code not matched from fetched list
                    if (!applyCoupon) {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid coupon code.",
                                "Please enter a valid coupon code.", "OK", android.R.drawable.ic_dialog_alert, null);
                    }
                }
            }.getCouponCodeList(getActivity(), null);
        } else {
            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid membership ID",
                    "Please enter a valid membership Id.", "OK", android.R.drawable.ic_dialog_alert, null);
        }
    }

    private void applyLoyaltyPoints() {
        if (mMemberShipIDET.getText().toString().length() == 10 && mRedeemPointsET.getText().toString().length() > 0) {

            REDEEMED_POINTS = Integer.parseInt(mRedeemPointsET.getText().toString());
            if (REDEEMED_POINTS <= EARNED_LOYALTY_POINTS) {


                REDEEMED_AMOUNT = REDEEMED_POINTS * REDEEMED_POINT_PRICE;
                if (REDEEMED_AMOUNT <= (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT)) {
                    calculationSetting();
                } else {
                    REDEEMED_AMOUNT = 0;
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Redeemed amount is more than total amount.",
                            "Please enter less loyalty points.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            } else {
                REDEEMED_POINTS = 0;
                new CustomDialog().showOneButtonAlertDialog(getActivity(), "Entered loyalty points are wrong",
                        "Please enter less or equal loyalty points to earned loyalty points", "OK", android.R.drawable.ic_dialog_alert, null);
            }
        } else {
            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid loyalty points",
                    "Please enter valid loyalty points", "OK", android.R.drawable.ic_dialog_alert, null);
        }
    }

    private void completeOrder() {
        Util.hideSoftKeypad(this.getActivity());
        try {

            if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.form))) {
                if (mOrderAdapter.getCount() > 0) {
//                    OrderPreviewFragment newFragment = new OrderPreviewFragment();
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    DateFormat df = new SimpleDateFormat("dd MMM, yyyy");
                    String date = df.format(Calendar.getInstance().getTime());

                    OrderPreviewWrapper wrapper = new OrderPreviewWrapper();
                    wrapper.setMembershipId(mMemberShipIDET.getText().toString());
                    wrapper.setCustomerName(mCustomerNameET.getText().toString());
                    wrapper.setOrderDate(date);
                    wrapper.setDiscountPercentage(DISCOUNT_PERCENTAGE);
                    wrapper.setCouponCode(COUPON_CODE);
                    wrapper.setRedeemPoints(REDEEMED_POINTS);
                    wrapper.setGrossAmount(GROSS_AMOUNT);
                    wrapper.setDiscountAmount(DISCOUNT_AMOUNT);
                    wrapper.setCouponAmount(COUPON_AMOUNT);
                    wrapper.setRedeemedAmount(REDEEMED_AMOUNT);
                    wrapper.setTaxPercentage(TAX_PERCENTAGE);
                    wrapper.setTaxAmount(TAX_AMOUNT);
                    wrapper.setNetAmount(NET_AMOUNT);
                    wrapper.setOrderList(orderList);

                    Bundle arguments = new Bundle();
                    bus.postSticky(wrapper);
                    arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");

//                    newFragment.setArguments(arguments);
//                    transaction.replace(R.id.item_detail_container, newFragment);
//                    transaction.addToBackStack(DashboardFragment.class.toString());

                    // Commit the transaction
//                    transaction.commit();

                    if(getActivity() instanceof Home){
                        ((Home) getActivity()).changeFragment(FragmentUtils.OrderPreviewFragment, arguments, true, false);
                    }
                } else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                            "Please add at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            }
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
            ex.printStackTrace();
        }


    }

    private void addOrder() {
        Util.hideSoftKeypad(this.getActivity());
        try {

            if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.form))) {

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
                    map.put("status", "on hold");

                    map.put("product_id", product_id);
                    map.put("qty", quantity);


                    if (wrapper == null) {
                        OrderWrapper.addOrder(map, getActivity(), this);
                    }
                } else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                            "Please add at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            }
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
            ex.printStackTrace();
        }

    }

    private Dialog addItemPopup;

    void createAddItemDialog() {

        addItemPopup = getDialog(getActivity());
        TextView cancelButton = (TextView) addItemPopup.findViewById(R.id.cancelDialog);
        TextView addItemTitle = (TextView) addItemPopup.findViewById(R.id.addItemTV);
        TextView productNameTV = (TextView) addItemPopup.findViewById(R.id.productNameTV);
        TextView productPriceTV = (TextView) addItemPopup.findViewById(R.id.productPriceHTV);
        LinearLayout ListHeading = (LinearLayout) addItemPopup.findViewById(R.id.ListHeading);
        final EditText searchET = (EditText) addItemPopup.findViewById(R.id.searchProductET);
        ListView mProductListView = (ListView) addItemPopup.findViewById(R.id.productList);

        int margin = (int) (deviceHeight * 0.03f);
        int topmargin = (int) (deviceHeight * 0.03f);

        RelativeLayout.LayoutParams addItemTitleParam = (RelativeLayout.LayoutParams) addItemTitle.getLayoutParams();
        addItemTitleParam.topMargin = topmargin;
        addItemTitleParam.bottomMargin = topmargin;
        addItemTitle.setLayoutParams(addItemTitleParam);

        RelativeLayout.LayoutParams cancelButtonParam = (RelativeLayout.LayoutParams) cancelButton.getLayoutParams();
        cancelButtonParam.rightMargin = margin;
        cancelButton.setLayoutParams(cancelButtonParam);

        RelativeLayout.LayoutParams productNameTVParam = (RelativeLayout.LayoutParams) productNameTV.getLayoutParams();
        productNameTVParam.rightMargin = margin;
        productNameTVParam.topMargin = topmargin;
        productNameTVParam.leftMargin = margin;
        productNameTV.setLayoutParams(productNameTVParam);

        RelativeLayout.LayoutParams searchETParam = (RelativeLayout.LayoutParams) searchET.getLayoutParams();
        searchETParam.height = (int) (deviceHeight * 0.07f);
        searchETParam.bottomMargin = margin;
        searchET.setLayoutParams(searchETParam);
        searchET.setPadding(margin / 2, 0, 0, 0);

        RelativeLayout.LayoutParams mProductListViewParam = (RelativeLayout.LayoutParams) mProductListView.getLayoutParams();
        mProductListViewParam.bottomMargin = margin;
        mProductListView.setLayoutParams(mProductListViewParam);

        RelativeLayout.LayoutParams ListHeadingParam = (RelativeLayout.LayoutParams) ListHeading.getLayoutParams();
        ListHeadingParam.leftMargin = margin;
        ListHeadingParam.rightMargin = margin;
        ListHeading.setLayoutParams(ListHeadingParam);

        productPriceTV.setText("Price ( " + UiController.sCurrency + " )");

        searchET.clearFocus();
        Util.hideSoftKeypad(getActivity());
        try {
            mProductAdapter = new ProductListAdapter(this, productListForPopup);
            mProductListView.setAdapter(mProductAdapter);

            mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (addItemPopup.isShowing()) {
                        addItemPopup.cancel();
                    }

                    mMemberShipIDET.clearFocus();
                    boolean added = false;

                    if (!mProductAdapter.getItem(position).getQuantity().trim().equals("0")) {
                        ProductOrderWrapper _wrapper = null;
                        _wrapper = mProductAdapter.getItem(position);
                        _wrapper.setAdded(true);
                        _wrapper.setAddedQuantity(1);
                        _wrapper.setTotalPrice(Double.parseDouble(_wrapper.getSellingPrice()));

                        for (ProductOrderWrapper model : orderList) {
                            if (_wrapper.getId().trim().equals(model.getId().trim())) {
                                added = true;
                            }
                        }
                        if (!added) {
                            orderList.add(_wrapper);
                            mOrderAdapter.notifyDataSetChanged();

                            SelectedProductCache.setProductCacheData(productListForPopup.get(position));

                            productListForPopup.remove(position);
                            mProductAdapter.notifyDataSetChanged();

                            try {
                                grossAmountCalculation(Double.parseDouble(_wrapper.getSellingPrice()), true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
                            }
                        } else {
                            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product already added",
                                    "Please select another product.", "OK", android.R.drawable.ic_dialog_alert, null);
                        }
                    } else {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product is no more available.",
                                "Available product quantity should be greater than zero.", "OK", android.R.drawable.ic_dialog_alert, null);
                    }
                    searchET.clearFocus();
                    searchET.setText("");

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    searchET.clearFocus();
                    searchET.setText("");
                    if (addItemPopup.isShowing()) {
                        addItemPopup.cancel();
                    }

                }
            });

            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    //   if(searchET.getText().toString().length()>0){
                    mProductAdapter.getFilter(searchET.getText().toString());
                    //   }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
        }
    }

    void addItemPopupShow() {
        if (addItemPopup != null && !addItemPopup.isShowing()) {
            addItemPopup.show();
            Util.hideSoftKeypad(getActivity());
        }
//        if (mMemberShipIDET.getText().toString().length() == 10) {
//        } else {
//            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid membership ID",
//                    "Please enter a valid membership Id.", "Ok", android.R.drawable.ic_dialog_alert, null);
//        }
    }

    void grossAmountCalculation(Double value, boolean flag) {
        try {

            if (!flag) {
                GROSS_AMOUNT = value;
            } else {
                GROSS_AMOUNT += value;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
        }
        mGrossAmountValueTV.setText(Util.priceFormat(GROSS_AMOUNT));

        calculationSetting();
    }

    void calculationSetting() {

        if (GROSS_AMOUNT != 0) {
            if (mSavePreferences.getDiscountMinRestiction().equals("0")) {
                DISCOUNT_AMOUNT = (GROSS_AMOUNT * DISCOUNT_PERCENTAGE) / 100;
                mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(DISCOUNT_PERCENTAGE) + "%)");
                mDiscountAmountValueTV.setText(Util.priceFormat(DISCOUNT_AMOUNT));
            } else if (GROSS_AMOUNT >= MINIMUM_SPEND_AMOUNT) {
                DISCOUNT_AMOUNT = (GROSS_AMOUNT * DISCOUNT_PERCENTAGE) / 100;
                mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(DISCOUNT_PERCENTAGE) + "%)");
                mDiscountAmountValueTV.setText(Util.priceFormat(DISCOUNT_AMOUNT));
            } else {
                DISCOUNT_AMOUNT = 0;
                mDiscountAmountTV.setText("Discount Amount (0.00%)");
                mDiscountAmountValueTV.setText("0.00");
            }

            if (COUPON_AMOUNT != 0) {
                if (GROSS_AMOUNT - DISCOUNT_AMOUNT >= COUPON_AMOUNT) {
                    mGotSGDTV.setText("(You got " + UiController.sCurrency + " " + Util.priceFormat(COUPON_AMOUNT) + " off)");
                    mGotSGDTV.setTextColor(getActivity().getResources().getColor((R.color.green_text_color)));
                    mCouponAmountValueTV.setText(Util.priceFormat(COUPON_AMOUNT));
                } else {
                    COUPON_AMOUNT = 0.0;
                    mGotSGDTV.setText("(You got " + UiController.sCurrency + " " + Util.priceFormat(COUPON_AMOUNT) + " off)");
                    mGotSGDTV.setTextColor(getActivity().getResources().getColor((R.color.green_text_color)));
                    mCouponAmountValueTV.setText("0.00");
                }
            }

            if (REDEEMED_AMOUNT != 0) {
                if (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT >= REDEEMED_AMOUNT) {
                    mRedeemAmountValueTV.setText(Util.priceFormat(REDEEMED_AMOUNT));
                } else {
                    REDEEMED_AMOUNT = 0.0;
                    mRedeemAmountValueTV.setText("0.00");
                }
            }


            TAX_AMOUNT = ((GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT - REDEEMED_AMOUNT) * TAX_PERCENTAGE) / 100;
            mTaxValueTV.setText(Util.priceFormat((TAX_AMOUNT)));
            NET_AMOUNT = (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT - REDEEMED_AMOUNT) + TAX_AMOUNT;
            mNetAMountValeTV.setText(UiController.sCurrency + " " + Util.priceFormat(NET_AMOUNT));
        } else {
            setDefaultValues();
        }
    }

    /*public void addToOriginalProductList(String id) {

        for (int i = 0; i < productListForPopup.size(); i++) {
            if (productListForPopup.get(i).getId().equalsIgnoreCase(id)) {
                productListForPopup.get(i).setAdded(false);

                RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
                mOrderListViewParam.leftMargin = (int) (deviceWidth * 0.02f);
                mOrderListViewParam.rightMargin = (int) (deviceWidth * 0.01f);
                if (mOrderAdapter.getCount() > 0) {
                    View item = mOrderAdapter.getView(0, null, mOrderListView);
                    item.measure(0, 0);
                    mOrderListViewParam.height = (int) (mOrderAdapter.getCount() * item.getMeasuredHeight());

                    if (mOrderAdapter.getCount() > 2) {

                        mOrderListViewParam.height = (int) (2.5 * item.getMeasuredHeight());
                    }
                } else
                    mOrderListViewParam.height = 0;

                mOrderListView.setLayoutParams(mOrderListViewParam);
                break;
            }

        }
    }*/

    public void addProductBackToList(String id) {
        for (int i = 0; i < SelectedProductCache.getProductCacheData().size(); i++) {
            ProductWrapper wrapper = SelectedProductCache.getProductCacheData().get(i);
            if (wrapper.getId().equals(id)) {
                productListForPopup.add(wrapper);
                mProductAdapter.addItem(wrapper);
                SelectedProductCache.getProductCacheData().remove(wrapper);
            }
        }
    }


    private void setDimensionsToViews() {

        int editTextHeight = (int) (deviceHeight * 0.08);
        int buttonHeight = (int) (deviceHeight * 0.08);
        int textViewHeight = (int) (deviceHeight * 0.05);
        int textViewTopMargin = (int) (deviceHeight * 0.008f);
        int textViewLeftMargin = (int) (deviceWidth * 0.08f);

        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        form_param.bottomMargin = (int) (deviceHeight * .03f);
        formScroll.setLayoutParams(form_param);


        RelativeLayout.LayoutParams mMembershipIDTVParam = (RelativeLayout.LayoutParams) mMembershipIDTV.getLayoutParams();
        mMembershipIDTVParam.width = (int) (deviceWidth * 0.25f);
        mMembershipIDTVParam.height = textViewHeight;
        mMembershipIDTVParam.leftMargin = (int) (deviceWidth * 0.03f);
        mMembershipIDTVParam.rightMargin = (int) (deviceWidth * 0.03f);
        mMembershipIDTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mMembershipIDTV.setLayoutParams(mMembershipIDTVParam);

        RelativeLayout.LayoutParams mMembershipIDETParam = (RelativeLayout.LayoutParams) mMemberShipIDET.getLayoutParams();
        mMembershipIDETParam.width = (int) (deviceWidth * 0.25f);
        mMembershipIDETParam.height = editTextHeight;
        mMembershipIDETParam.leftMargin = (int) (deviceWidth * 0.03f);
        mMembershipIDETParam.rightMargin = (int) (deviceWidth * 0.03f);
        mMemberShipIDET.setLayoutParams(mMembershipIDETParam);
        mMemberShipIDET.setPadding((int) (deviceWidth * 0.015f), 0, (int) (deviceWidth * 0.015f), 0);

        RelativeLayout.LayoutParams mCustomerNameTVParam = (RelativeLayout.LayoutParams) mCustomerNameTV.getLayoutParams();
        mCustomerNameTVParam.width = (int) (deviceWidth * 0.25f);
        mCustomerNameTVParam.height = textViewHeight;
        mCustomerNameTVParam.leftMargin = (int) (deviceWidth * 0.03f);
        mCustomerNameTVParam.rightMargin = (int) (deviceWidth * 0.03f);
        mCustomerNameTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mCustomerNameTV.setLayoutParams(mCustomerNameTVParam);

        RelativeLayout.LayoutParams mCustomerNameETParam = (RelativeLayout.LayoutParams) mCustomerNameET.getLayoutParams();
        mCustomerNameETParam.width = (int) (deviceWidth * 0.25f);
        mCustomerNameETParam.height = editTextHeight;
        mCustomerNameETParam.rightMargin = (int) (deviceWidth * 0.05f);
        mCustomerNameET.setLayoutParams(mCustomerNameETParam);
        mCustomerNameET.setPadding((int) (deviceWidth * 0.015f), 0, (int) (deviceWidth * 0.015f), 0);

        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.add_item);
        RelativeLayout.LayoutParams mAddItemButtonParam = (RelativeLayout.LayoutParams) mAddItemButton.getLayoutParams();
        mAddItemButtonParam.width = /*bd.getBitmap().getWidth()*/(int) (buttonHeight * 2.75f);
        mAddItemButtonParam.height = /*bd.getBitmap().getHeight()*/buttonHeight;
        mAddItemButtonParam.rightMargin = (int) (deviceWidth * 0.02f);
        mAddItemButton.setLayoutParams(mAddItemButtonParam);

        RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
        mOrderListViewParam.height = (int) (deviceHeight * 0.3f);
        mOrderListViewParam.leftMargin = (int) (deviceWidth * 0.02f);
        mOrderListViewParam.rightMargin = (int) (deviceWidth * 0.01f);
        mOrderListView.setLayoutParams(mOrderListViewParam);

        RelativeLayout.LayoutParams listSeparatorParam = (RelativeLayout.LayoutParams) listSepartor.getLayoutParams();
        listSeparatorParam.leftMargin = (int) (deviceWidth * 0.02f);
        listSeparatorParam.rightMargin = (int) (deviceWidth * 0.01f);
        listSeparatorParam.topMargin = (int) (deviceWidth * 0.01f);
        listSepartor.setLayoutParams(listSeparatorParam);

        RelativeLayout.LayoutParams listSeparatorParam1 = (RelativeLayout.LayoutParams) listSepartor1.getLayoutParams();
        listSeparatorParam1.leftMargin = (int) (deviceWidth * 0.02f);
        listSeparatorParam1.rightMargin = (int) (deviceWidth * 0.01f);
        listSeparatorParam1.topMargin = (int) (deviceWidth * 0.01f);
        listSepartor1.setLayoutParams(listSeparatorParam1);

        RelativeLayout.LayoutParams mAmountLayoutParams = (RelativeLayout.LayoutParams) mAmountLayout.getLayoutParams();
        mAmountLayoutParams.width = (int) (deviceWidth * 0.4f);
        mAmountLayout.setLayoutParams(mAmountLayoutParams);
        mAmountLayout.setPadding((int) (deviceWidth * 0.01f), (int) (deviceWidth * 0.01f), (int) (deviceWidth * 0.02f), (int) (deviceWidth * 0.02f));

        mPointsLayout.setPadding(0, (int) (deviceHeight * 0.1f), 0, 0);

        RelativeLayout.LayoutParams mPutOnHoldButtonParam = (RelativeLayout.LayoutParams) mPutOnHoldButton.getLayoutParams();
        mPutOnHoldButtonParam.height = buttonHeight;
        mPutOnHoldButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mPutOnHoldButton.setLayoutParams(mPutOnHoldButtonParam);
        mPutOnHoldButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        mPutOnHoldButton.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams mCompleteButtonParam = (RelativeLayout.LayoutParams) mCompleteButton.getLayoutParams();
        mCompleteButtonParam.height = buttonHeight;
        mCompleteButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mCompleteButtonParam.leftMargin = (int) (deviceWidth * 0.08f);
        mCompleteButton.setLayoutParams(mCompleteButtonParam);
        mCompleteButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mGrossAmountTVParam = (RelativeLayout.LayoutParams) mGrossAmountTV.getLayoutParams();
        //mGrossAmountTVParam.topMargin = (int) (deviceHeight * 0.02f);
        //mCompleteButtonParam.leftMargin = (int) (deviceWidth * 0.008f);
        mGrossAmountTV.setLayoutParams(mGrossAmountTVParam);

        RelativeLayout.LayoutParams mGrossAmountValueTVParam = (RelativeLayout.LayoutParams) mGrossAmountValueTV.getLayoutParams();
        //mGrossAmountTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mGrossAmountValueTVParam.leftMargin = textViewLeftMargin;
        mGrossAmountValueTV.setLayoutParams(mGrossAmountValueTVParam);

        RelativeLayout.LayoutParams mDiscountAmountTVParam = (RelativeLayout.LayoutParams) mDiscountAmountTV.getLayoutParams();
        mDiscountAmountTVParam.topMargin = textViewTopMargin;
        mDiscountAmountTV.setLayoutParams(mDiscountAmountTVParam);

        RelativeLayout.LayoutParams mDiscountAmountValueTVParam = (RelativeLayout.LayoutParams) mDiscountAmountValueTV.getLayoutParams();
        mDiscountAmountValueTVParam.topMargin = textViewTopMargin;
        mDiscountAmountValueTVParam.leftMargin = textViewLeftMargin;
        mDiscountAmountValueTV.setLayoutParams(mDiscountAmountValueTVParam);

        RelativeLayout.LayoutParams mCouponAmountTVParam = (RelativeLayout.LayoutParams) mCouponAmountTV.getLayoutParams();
        mCouponAmountTVParam.topMargin = 2 * textViewTopMargin;
        mCouponAmountTV.setLayoutParams(mCouponAmountTVParam);

        RelativeLayout.LayoutParams mCouponAmountValueTVParam = (RelativeLayout.LayoutParams) mCouponAmountValueTV.getLayoutParams();
        mCouponAmountValueTVParam.topMargin = 2 * textViewTopMargin;
        mCouponAmountValueTVParam.leftMargin = textViewLeftMargin;
        mCouponAmountValueTV.setLayoutParams(mCouponAmountValueTVParam);

        RelativeLayout.LayoutParams mRedeemAmountTVParam = (RelativeLayout.LayoutParams) mRedeemAmountTV.getLayoutParams();
        mRedeemAmountTVParam.topMargin = 3 * textViewTopMargin;
        mRedeemAmountTV.setLayoutParams(mRedeemAmountTVParam);

        RelativeLayout.LayoutParams mRedeemAmountValueTVParam = (RelativeLayout.LayoutParams) mRedeemAmountValueTV.getLayoutParams();
        mRedeemAmountValueTVParam.topMargin = 3 * textViewTopMargin;
        mRedeemAmountValueTVParam.leftMargin = textViewLeftMargin;
        mRedeemAmountValueTV.setLayoutParams(mRedeemAmountValueTVParam);

        RelativeLayout.LayoutParams mTaxTVParam = (RelativeLayout.LayoutParams) mTaxTV.getLayoutParams();
        mTaxTVParam.topMargin = 2 * textViewTopMargin;
        mTaxTV.setLayoutParams(mTaxTVParam);

        RelativeLayout.LayoutParams mTaxValueTVParam = (RelativeLayout.LayoutParams) mTaxValueTV.getLayoutParams();
        mTaxValueTVParam.topMargin = 2 * textViewTopMargin;
        mTaxValueTVParam.leftMargin = textViewLeftMargin;
        mTaxValueTV.setLayoutParams(mTaxValueTVParam);

        RelativeLayout.LayoutParams mNetAMountTVParam = (RelativeLayout.LayoutParams) mNetAMountTV.getLayoutParams();
        mNetAMountTVParam.topMargin = textViewTopMargin;
        mNetAMountTV.setLayoutParams(mNetAMountTVParam);

        RelativeLayout.LayoutParams mNetAMountValeTVParam = (RelativeLayout.LayoutParams) mNetAMountValeTV.getLayoutParams();
        mNetAMountValeTVParam.topMargin = textViewTopMargin;
        mNetAMountValeTVParam.leftMargin = textViewLeftMargin;
        mNetAMountValeTV.setLayoutParams(mNetAMountValeTVParam);

        mGotSGDTV.setPadding((int) (deviceWidth * 0.005f), 0, 0, 0);

        RelativeLayout.LayoutParams mCouponCodeTVParam = (RelativeLayout.LayoutParams) mCouponCodeTV.getLayoutParams();
        mCouponCodeTVParam.height = editTextHeight;
        mCouponCodeTVParam.leftMargin = (int) (deviceHeight * 0.13);
        mCouponCodeTV.setLayoutParams(mCouponCodeTVParam);

        RelativeLayout.LayoutParams mRedeemPointsTVParam = (RelativeLayout.LayoutParams) mRedeemPointsTV.getLayoutParams();
        mRedeemPointsTVParam.height = editTextHeight;
        mRedeemPointsTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mRedeemPointsTV.setLayoutParams(mRedeemPointsTVParam);

        RelativeLayout.LayoutParams mCouponCodeETParam = (RelativeLayout.LayoutParams) mCouponCodeET.getLayoutParams();
        mCouponCodeETParam.width = (int) (deviceWidth * 0.1f);
        mCouponCodeETParam.height = editTextHeight;
        mCouponCodeETParam.leftMargin = (int) (deviceWidth * 0.01f);
        mCouponCodeET.setLayoutParams(mCouponCodeETParam);
        mCouponCodeET.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mRedeemPointsETParam = (RelativeLayout.LayoutParams) mRedeemPointsET.getLayoutParams();
        mRedeemPointsETParam.width = (int) (deviceWidth * 0.1f);
        mRedeemPointsETParam.height = editTextHeight;
        mRedeemPointsETParam.topMargin = (int) (deviceHeight * 0.02f);
        mRedeemPointsET.setLayoutParams(mRedeemPointsETParam);
        mRedeemPointsET.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mCouponCodeApplyButtonParam = (RelativeLayout.LayoutParams) mCouponCodeApplyButton.getLayoutParams();
        mCouponCodeApplyButtonParam.height = buttonHeight;
        mCouponCodeApplyButtonParam.width = (int) (deviceWidth * 0.07f);
        mCouponCodeApplyButtonParam.leftMargin = (int) (deviceWidth * 0.01f);
        mCouponCodeApplyButton.setLayoutParams(mCouponCodeApplyButtonParam);
        mCouponCodeApplyButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mRedeemPointsApplyButtonParam = (RelativeLayout.LayoutParams) mRedeemPointsApplyButton.getLayoutParams();
        mRedeemPointsApplyButtonParam.height = buttonHeight;
        mRedeemPointsApplyButtonParam.width = (int) (deviceWidth * 0.07f);
        //mRedeemPointsApplyButtonParam.topMargin =  (int) (deviceHeight * 0.02f);
        mRedeemPointsApplyButton.setLayoutParams(mRedeemPointsApplyButtonParam);
        mRedeemPointsApplyButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mPointsTVParam = (RelativeLayout.LayoutParams) mPointsTV.getLayoutParams();
        mPointsTVParam.height = (int) (deviceHeight * 0.115f);
        mPointsTVParam.width = (int) (deviceHeight * 0.115f);
        mPointsTVParam.rightMargin = (int) (deviceHeight * 0.015f);
        mPointsTV.setLayoutParams(mPointsTVParam);

        RelativeLayout.LayoutParams mProductNameTVParam = (RelativeLayout.LayoutParams) mProductNameTV.getLayoutParams();
        mProductNameTVParam.leftMargin = (int) (deviceWidth * 0.04f);
        mProductNameTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mProductNameTVParam.width = (int) (deviceWidth * 0.2f);
        mProductNameTV.setLayoutParams(mProductNameTVParam);

        RelativeLayout.LayoutParams mUnitPriceTVParam = (RelativeLayout.LayoutParams) mUnitPriceTV.getLayoutParams();
        mUnitPriceTVParam.leftMargin = (int) (deviceWidth * 0.18f);
        mUnitPriceTVParam.topMargin = (int) (deviceHeight * 0.01f);
        mUnitPriceTVParam.width = (int) (deviceWidth * .1f);
        mUnitPriceTV.setLayoutParams(mUnitPriceTVParam);

        mUnitPriceTV.setText("Unit Price" + "\n" + "(" + UiController.sCurrency + ")");

        RelativeLayout.LayoutParams mTotalPriceTVParam = (RelativeLayout.LayoutParams) mTotalPriceTV.getLayoutParams();
        mTotalPriceTVParam.leftMargin = (int) (deviceWidth * 0.16f);
        mTotalPriceTVParam.width = (int) (deviceWidth * .1f);
        mTotalPriceTV.setLayoutParams(mTotalPriceTVParam);

        mTotalPriceTV.setText("Total Price" + "\n" + "(" + UiController.sCurrency + ")");

        RelativeLayout.LayoutParams mQuantityTVParam = (RelativeLayout.LayoutParams) mQuantityTV.getLayoutParams();
        mQuantityTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        mQuantityTVParam.width = (int) (deviceWidth * .07f);
        mQuantityTV.setLayoutParams(mQuantityTVParam);

        mOrderListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        mMemberShipIDET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                if (charSequence.length() == 10) {

                    Util.hideSoftKeypad(getActivity());
                    new CustomerWrapper() {
                        @Override
                        public void getCustomerWrapper(Context context,String membershipId, String customerName, String earnedLoyaltyPoints) {

                            try {
                                EARNED_LOYALTY_POINTS = Integer.parseInt(earnedLoyaltyPoints);
                                mPointsTV.setText("Points\n" + EARNED_LOYALTY_POINTS);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                RetailPosLoging.getInstance().registerLog(OrderOnHoldFragment.class.getName(), ex);
                            }

                            mCustomerNameET.setText(customerName);

                        }
                    }.getCustomer(mMemberShipIDET.getText().toString(), getActivity());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mAddItemButton.setOnClickListener(this);
        mCompleteButton.setOnClickListener(this);
        mPutOnHoldButton.setOnClickListener(this);
        mRedeemPointsApplyButton.setOnClickListener(this);
        mCouponCodeApplyButton.setOnClickListener(this);

    }

    private Dialog getDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_item_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(
                context.getResources().getDrawable(
                        R.drawable.dilaog_circular_corner));

        WindowManager.LayoutParams params = dialog.getWindow()
                .getAttributes();

        params.width = (int) (deviceWidth * .55f);
        params.height = (int) (deviceHeight * .7f);

        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddOrderFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("AddOrderFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }

    void setDefaultValues() {
        GROSS_AMOUNT = 0.0;
        DISCOUNT_AMOUNT = 0.0;
        COUPON_AMOUNT = 0.0;
        REDEEMED_AMOUNT = 0.0;
        TAX_AMOUNT = 0.0;
        NET_AMOUNT = 0.0;
        mGrossAmountValueTV.setText("0.00");
        mDiscountAmountValueTV.setText("0.00");
        mCouponAmountValueTV.setText("0.00");
        mRedeemAmountValueTV.setText("0.00");
        mTaxValueTV.setText("0.00");
        mNetAMountValeTV.setText(UiController.sCurrency + " " + "0.00");
        mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(mSavePreferences.getDiscountPercentage()) + "%)");

    }

}
