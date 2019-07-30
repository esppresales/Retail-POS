package com.printer.epos.rtpl;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.eposdevice.EposCallbackCode;
import com.epson.eposdevice.printer.Printer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.OrderPreviewListAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.DeviceType;
import com.printer.epos.rtpl.wrapper.MultiplePrintManager;
import com.printer.epos.rtpl.wrapper.OrderPreviewWrapper;
import com.printer.epos.rtpl.wrapper.OrderWrapper;
import com.printer.epos.rtpl.wrapper.ProductOrderWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class OrderPreviewFragment extends BaseFragment implements View.OnClickListener , AdapterView.OnItemSelectedListener{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private boolean confirmButtonClicked;
    private Dialog payDialog;

    private OrderPreviewWrapper wrapper;
    private boolean isPrinterOk = false;
    private double CASH_DUE = 0.0;
    private String paymentType = "";
    double amountIncludesGST = 0.0;
    private String receiptNo = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            /*
      The dummy content this fragment is presenting.
     */
            DummyContent.DummyItem mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

        }
    }

    private Home hContext;
    private Context localContext = null;
    private ProgressDialog mDialog;

    @Override
    public void onResume() {
        super.onResume();

        peripheralManager.connectDevice(DeviceType.PRINTER,
                new SavePreferences(getActivity()).getMasterPrinterIp());
        if (getActivity() instanceof Home)
            ((Home) getActivity()).setEnabledButtons(false, true, false, false);

        localContext = getActivity();

        hContext.backButton.setOnClickListener(this);
        hContext.setTitleText("Order Preview");

        if (TextUtils.isEmpty(new SavePreferences(getActivity()).getMasterPrinterIp()))
            Crouton.makeText(getActivity(), "Please set master printer name first from settings", Style.ALERT).show();

    }

    private int deviceWidth;
    private int deviceHeight;
    private String orderId;

    private SavePreferences mSavePreferences;

    private ScrollView formScroll;
    private RelativeLayout mAmountLayout, mNetAmountLayout, mPointsLayout;
    private TextView mOrderDateTV, mMembershipIDTV, mCustomerNameTV, mProductDetailsTV, mAmountTV, mQuantityTV, mTotalPriceTV,
            mGrossAmountTV, mGrossAmountValueTV,mTaxTV, mTaxValueTV, mNetAMountTV, mNetAMountValeTV, mDiscountPercentageTV,
            mDiscountAmountTV, mDiscountAmountValueTV, mCouponAmountTV, mCouponAmountValueTV, mRedeemAmountTV,
            mRedeemAmountValueTV,  mRedeemPointsTV, mRedeemPointsValueTV,mPaymentTypeTV, mPaymentTypeValueTV, mSubTotalTV, mSubTotalValueTV,
            mAmountBeforeGSTTV, mAmountBeforeGSTValueTV, mAmountAfterDiscountTV, mAmountAfterDiscountValueTV, mGstInclusiveTV, mGstInclusiveValueTV, mAmountIncludesGSTTV, mAmountIncludesGSTValueTV;;

    private TextView mOrderDateET, mMemberShipIDET, mCustomerNameET;

    private Button mPayButton;
    private View listSepartor, listSepartor1;
    private ListView mOrderListView;
    private OrderPreviewListAdapter mOrderAdapter;

    private View rootView;


    private List<ProductOrderWrapper> orderList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_order_preview, container, false);
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
        mNetAmountLayout = (RelativeLayout) rootView.findViewById(R.id.netAmountLayout);
        mPointsLayout = (RelativeLayout) rootView.findViewById(R.id.pointsLayout);

        mMembershipIDTV = (TextView) rootView.findViewById(R.id.membershipIdTV);
        mOrderDateTV = (TextView) rootView.findViewById(R.id.orderDateTV);
        mCustomerNameTV = (TextView) rootView.findViewById(R.id.customerNameTV);

        mAmountTV = (TextView) rootView.findViewById(R.id.amountTV);
        mProductDetailsTV = (TextView) rootView.findViewById(R.id.productDetailsTV);

        mQuantityTV = (TextView) rootView.findViewById(R.id.quantityTV);
        mTotalPriceTV = (TextView) rootView.findViewById(R.id.totalPriceTV);
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
        mPaymentTypeTV = (TextView) rootView.findViewById(R.id.paymentTypeTV);
        mPaymentTypeValueTV = (TextView) rootView.findViewById(R.id.paymentTypeValueTV);
        mNetAMountTV = (TextView) rootView.findViewById(R.id.netAmountTV);
        mNetAMountValeTV = (TextView) rootView.findViewById(R.id.netAmountValueTV);
        mSubTotalTV= (TextView)rootView.findViewById(R.id.subTotalTV);
        mSubTotalValueTV= (TextView)rootView.findViewById(R.id.subTotalValueTV);

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

        mMemberShipIDET = (TextView) rootView.findViewById(R.id.membershipIdET);
        mCustomerNameET = (TextView) rootView.findViewById(R.id.customerNameET);
        mOrderDateET = (TextView) rootView.findViewById(R.id.orderDateET);

        mPayButton = (Button) rootView.findViewById(R.id.payButton);

        mOrderListView = (ListView) rootView.findViewById(R.id.orderListView);
        listSepartor = rootView.findViewById(R.id.listSeparator);
        listSepartor1 = rootView.findViewById(R.id.listSeparator1);

        setDimensionsToViews();


        wrapper = EventBus.getDefault().removeStickyEvent(OrderPreviewWrapper.class);

        if (wrapper != null) {
            String membership_Id = wrapper.getMembershipId();
            String customer_Name = wrapper.getCustomerName();
            String order_Date = wrapper.getOrderDate();
            String coupon_Code = wrapper.getCouponCode();
            orderList = wrapper.getOrderList();

            if (Validation.isValidData(membership_Id))
                mMemberShipIDET.setText(membership_Id);
            if (Validation.isValidData(order_Date))
                mOrderDateET.setText(order_Date);
            if (Validation.isValidData(customer_Name))
                mCustomerNameET.setText(customer_Name);
            if (Validation.isValidData("" + wrapper.getRedeemPoints())) {
                if (wrapper != null && (wrapper.getRedeemPoints() == 0)) {
                    mRedeemAmountTV.setText("Redemption Amount ");
                } else {
                    mRedeemAmountTV.setText("Redemption Amount (" + wrapper.getRedeemPoints() + " Points)");
                }
            }

            if (TextUtils.isEmpty(wrapper.getCouponCode()))
                mCouponAmountTV.setText("Coupon Amount");
            else {
                mCouponAmountTV.setText("Coupon Amount (" + coupon_Code + ")");
            }

            if (wrapper.getDiscountPercentage() == 0) {
                mDiscountAmountTV.setText("Discount Amount (0.00%)");
            } else {
                mDiscountAmountTV.setText("Discount Amount (" + Util.priceFormat(wrapper.getDiscountPercentage()) + "%)");
            }

            mCouponAmountValueTV.setText(Util.priceFormat(wrapper.getCouponAmount()));
            mRedeemAmountValueTV.setText(Util.priceFormat(wrapper.getRedeemedAmount()));
            mDiscountAmountValueTV.setText(Util.priceFormat(wrapper.getDiscountAmount()));
            if(TextUtils.isEmpty(wrapper.getPaymentType())) {
                mPaymentTypeValueTV.setVisibility(View.GONE);
                mPaymentTypeTV.setVisibility(View.GONE);
            }else {
                mPaymentTypeValueTV.setVisibility(View.VISIBLE);
                mPaymentTypeTV.setVisibility(View.VISIBLE);
                mPaymentTypeValueTV.setText(wrapper.getPaymentType());
            }

            double gstAmount = 0.0;
            double amountBeforeGST = 0.0;
            double discountAmount = 0.0;
            double totalDiscountAmount = 0.0;
            double amountAfterDiscount = 0.0;
            double gstInclusive = 0.0;
            gstAmount = Util.roundUpToTwoDecimal(wrapper.getGrossAmount() - wrapper.getGrossAmount()/(1 + (wrapper.getTaxPercentage()) / 100));
            amountBeforeGST = Util.roundUpToTwoDecimal(wrapper.getGrossAmount() - gstAmount);
            discountAmount = Util.roundUpToTwoDecimal((amountBeforeGST * wrapper.getDiscountPercentage()) / 100);
            totalDiscountAmount = Util.roundUpToTwoDecimal(discountAmount + wrapper.getCouponAmount() + wrapper.getRedeemedAmount());
            if(totalDiscountAmount > 0) {
                amountAfterDiscount = Util.roundUpToTwoDecimal(amountBeforeGST - totalDiscountAmount);
                gstInclusive = Util.roundUpToTwoDecimal((amountAfterDiscount * wrapper.getTaxPercentage())/100);
            }else{
                amountAfterDiscount = amountBeforeGST - totalDiscountAmount;
                gstInclusive = gstAmount;

            }
            amountIncludesGST = Util.roundUpToTwoDecimal(amountAfterDiscount + gstInclusive);

            mSubTotalValueTV.setText(Util.priceFormat(wrapper.getGrossAmount()));
            mAmountBeforeGSTValueTV.setText(Util.priceFormat(amountBeforeGST));
            mGstInclusiveTV.setText(""+wrapper.getTaxPercentage() + "% GST inclusive");

            if (TextUtils.isEmpty(wrapper.getCouponCode())) {
                mCouponAmountTV.setText("Coupon Amount");
            } else {
                mCouponAmountTV.setText("Coupon Amount (" + wrapper.getCouponCode() + ")");

            }
            if (wrapper.getRedeemPoints() != 0) {
                mRedeemAmountTV.setText("Redemption Amount");
            } else {
                mRedeemAmountTV.setText("Redemption Amount (" + wrapper.getRedeemPoints() + " Points)");
            }
            mDiscountAmountTV.setText(getString(R.string.discount_Amount) + "(" + wrapper.getDiscountPercentage() + "%)");

            mDiscountAmountValueTV.setText(Util.priceFormat(discountAmount));
            mCouponAmountValueTV.setText(Util.priceFormat(wrapper.getCouponAmount()));
            mRedeemAmountValueTV.setText(Util.priceFormat(wrapper.getRedeemedAmount()));
            mPaymentTypeValueTV.setText(wrapper.getPaymentType());
            mGstInclusiveValueTV.setText(Util.priceFormat(gstInclusive));
            mAmountIncludesGSTValueTV.setText(Util.priceFormat(amountIncludesGST));
            mAmountAfterDiscountValueTV.setText(Util.priceFormat(amountAfterDiscount));

        }


        if (orderList != null) {
            mOrderAdapter = new OrderPreviewListAdapter(this, orderList);
            mOrderListView.setAdapter(mOrderAdapter);

            RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
            mOrderListViewParam.leftMargin = (int) (deviceWidth * 0.02f);
            mOrderListViewParam.rightMargin = (int) (deviceWidth * 0.01f);

            View item = mOrderAdapter.getView(0, null, mOrderListView);
            item.measure(0, 0);
            mOrderListViewParam.height = mOrderAdapter.getCount() * item.getMeasuredHeight();

            if (mOrderAdapter.getCount() > 3) {
                mOrderListViewParam.height = 3 * item.getMeasuredHeight();
            }
            mOrderListView.setLayoutParams(mOrderListViewParam);
        }
        return rootView;
    }

    private void setDimensionsToViews() {

        int buttonHeight = (int) (deviceHeight * 0.08);
        int textViewHeight = (int) (deviceHeight * 0.05);
        int textViewTopMargin = (int) (deviceHeight * 0.008f);
        int textViewLeftMargin = (int) (deviceWidth * 0.01f);

        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        form_param.bottomMargin = (int) (deviceHeight * .03f);
        formScroll.setLayoutParams(form_param);

        RelativeLayout.LayoutParams mOrderDateTVParam = (RelativeLayout.LayoutParams) mOrderDateTV.getLayoutParams();
        mOrderDateTVParam.width = (int) (deviceWidth * 0.22f);
        mOrderDateTVParam.height = textViewHeight;
        mOrderDateTVParam.leftMargin = (int) (deviceWidth * 0.04f);
        mOrderDateTVParam.rightMargin = (int) (deviceWidth * 0.03f);
        mOrderDateTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mOrderDateTV.setLayoutParams(mOrderDateTVParam);

        RelativeLayout.LayoutParams mOrderDateETParam = (RelativeLayout.LayoutParams) mOrderDateET.getLayoutParams();
        mOrderDateETParam.width = (int) (deviceWidth * 0.22f);
        mOrderDateETParam.height = textViewHeight;
        mOrderDateETParam.leftMargin = (int) (deviceWidth * 0.04f);
        mOrderDateETParam.rightMargin = (int) (deviceWidth * 0.02f);
        mOrderDateET.setLayoutParams(mOrderDateETParam);

        RelativeLayout.LayoutParams mMembershipIDTVParam = (RelativeLayout.LayoutParams) mMembershipIDTV.getLayoutParams();
        mMembershipIDTVParam.width = (int) (deviceWidth * 0.22f);
        mMembershipIDTVParam.height = textViewHeight;
        mMembershipIDTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        mMembershipIDTVParam.rightMargin = (int) (deviceWidth * 0.02f);
        mMembershipIDTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mMembershipIDTV.setLayoutParams(mMembershipIDTVParam);

        RelativeLayout.LayoutParams mMembershipIDETParam = (RelativeLayout.LayoutParams) mMemberShipIDET.getLayoutParams();
        mMembershipIDETParam.width = (int) (deviceWidth * 0.22f);
        mMembershipIDETParam.height = textViewHeight;
        mMembershipIDETParam.leftMargin = (int) (deviceWidth * 0.02f);
        mMembershipIDETParam.rightMargin = (int) (deviceWidth * 0.02f);
        mMemberShipIDET.setLayoutParams(mMembershipIDETParam);

        RelativeLayout.LayoutParams mCustomerNameTVParam = (RelativeLayout.LayoutParams) mCustomerNameTV.getLayoutParams();
        mCustomerNameTVParam.width = (int) (deviceWidth * 0.25f);
        mCustomerNameTVParam.height = textViewHeight;
        mCustomerNameTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        mCustomerNameTVParam.rightMargin = (int) (deviceWidth * 0.02f);
        mCustomerNameTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mCustomerNameTV.setLayoutParams(mCustomerNameTVParam);

        RelativeLayout.LayoutParams mCustomerNameETParam = (RelativeLayout.LayoutParams) mCustomerNameET.getLayoutParams();
        mCustomerNameETParam.height = textViewHeight;
        mCustomerNameET.setLayoutParams(mCustomerNameETParam);

        RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
        mOrderListViewParam.height = (int) (deviceHeight * 0.35f);
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
        mAmountLayoutParams.width = (int) (deviceWidth * 0.38f);
        //  mAmountLayoutParams.rightMargin = (int) (deviceWidth * 0.03f);
        mAmountLayout.setLayoutParams(mAmountLayoutParams);
        mAmountLayout.setPadding((int) (deviceWidth * 0.01f), (int) (deviceWidth * 0.01f), (int) (deviceWidth * 0.02f), (int) (deviceWidth * 0.02f));


        RelativeLayout.LayoutParams mNetAmountLayoutParams = (RelativeLayout.LayoutParams) mNetAmountLayout.getLayoutParams();
        mNetAmountLayoutParams.width = (int) (deviceWidth * 0.38f);
        //  mAmountLayoutParams.rightMargin = (int) (deviceWidth * 0.03f);
        mNetAmountLayout.setLayoutParams(mNetAmountLayoutParams);
        mNetAmountLayout.setPadding((int) (deviceWidth * 0.01f), 0, 0, (int) (deviceWidth * 0.02f));


        mPointsLayout.setPadding((int) (deviceWidth * 0.01f), (int) (deviceHeight * 0.01f), (int) (deviceWidth * 0.015f), 0);

        RelativeLayout.LayoutParams mSubTotalTVParam = (RelativeLayout.LayoutParams) mSubTotalTV.getLayoutParams();
        mSubTotalTVParam.topMargin = textViewTopMargin;
        mSubTotalTV.setLayoutParams(mSubTotalTVParam);

        RelativeLayout.LayoutParams mSubTotalValueTVParam = (RelativeLayout.LayoutParams) mSubTotalValueTV.getLayoutParams();
        mSubTotalValueTVParam.topMargin = textViewTopMargin;
        mSubTotalValueTV.setLayoutParams(mSubTotalValueTVParam);

        RelativeLayout.LayoutParams mAmountBeforeGSTTVParams = (RelativeLayout.LayoutParams) mAmountBeforeGSTTV.getLayoutParams();
        mAmountBeforeGSTTVParams.topMargin = textViewTopMargin;
        mAmountBeforeGSTTV.setLayoutParams(mAmountBeforeGSTTVParams);

        RelativeLayout.LayoutParams mAmountBeforeGSTValueTVParam = (RelativeLayout.LayoutParams) mAmountBeforeGSTValueTV.getLayoutParams();
        mAmountBeforeGSTValueTVParam.topMargin = textViewTopMargin;
        mAmountBeforeGSTValueTV.setLayoutParams(mAmountBeforeGSTValueTVParam);

        RelativeLayout.LayoutParams mDiscountAmountTVParam = (RelativeLayout.LayoutParams) mDiscountAmountTV.getLayoutParams();
        mDiscountAmountTVParam.topMargin = textViewTopMargin;
        mDiscountAmountTV.setLayoutParams(mDiscountAmountTVParam);

        RelativeLayout.LayoutParams mDiscountAmountValueTVParam = (RelativeLayout.LayoutParams) mDiscountAmountValueTV.getLayoutParams();
        mDiscountAmountValueTVParam.topMargin = textViewTopMargin;
        mDiscountAmountValueTV.setLayoutParams(mDiscountAmountValueTVParam);

        RelativeLayout.LayoutParams mAmountAfterDiscountTVParam = (RelativeLayout.LayoutParams) mAmountAfterDiscountTV.getLayoutParams();
        mAmountAfterDiscountTVParam.topMargin = textViewTopMargin;
        mAmountAfterDiscountTV.setLayoutParams(mAmountAfterDiscountTVParam);

        RelativeLayout.LayoutParams mGetAmountAfterDiscountValueTVParam = (RelativeLayout.LayoutParams) mAmountAfterDiscountValueTV.getLayoutParams();
        mGetAmountAfterDiscountValueTVParam.topMargin = textViewTopMargin;
        mAmountAfterDiscountValueTV.setLayoutParams(mGetAmountAfterDiscountValueTVParam);

        RelativeLayout.LayoutParams mGstInclusiveTVParam = (RelativeLayout.LayoutParams) mGstInclusiveTV.getLayoutParams();
        mGstInclusiveTVParam.topMargin = textViewTopMargin;
        mGstInclusiveTV.setLayoutParams(mGstInclusiveTVParam);

        RelativeLayout.LayoutParams mGstInclusiveValueTVParam = (RelativeLayout.LayoutParams) mGstInclusiveValueTV.getLayoutParams();
        mGstInclusiveValueTVParam.topMargin = textViewTopMargin;
        mGstInclusiveValueTV.setLayoutParams(mGstInclusiveValueTVParam);


        RelativeLayout.LayoutParams mAmountIncludesGSTTVParam = (RelativeLayout.LayoutParams) mAmountIncludesGSTTV.getLayoutParams();
        mAmountIncludesGSTTVParam.topMargin = textViewTopMargin;
        mAmountIncludesGSTTV.setLayoutParams(mAmountIncludesGSTTVParam);

        RelativeLayout.LayoutParams mAmountIncludesGSTValueTVParam = (RelativeLayout.LayoutParams) mAmountIncludesGSTValueTV.getLayoutParams();
        mAmountIncludesGSTValueTVParam.topMargin = textViewTopMargin;
        mAmountIncludesGSTValueTV.setLayoutParams(mAmountIncludesGSTValueTVParam);

        RelativeLayout.LayoutParams mPayButtonParam = (RelativeLayout.LayoutParams) mPayButton.getLayoutParams();
        mPayButtonParam.height = buttonHeight;
        mPayButtonParam.width = (int) (deviceWidth * 0.12f);
        mPayButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mPayButtonParam.rightMargin = (int) (deviceWidth * 0.02f);
        mPayButtonParam.leftMargin = textViewLeftMargin;
        mPayButton.setLayoutParams(mPayButtonParam);
        mPayButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mSubTotalTVLayoutParams = (RelativeLayout.LayoutParams) mSubTotalTV.getLayoutParams();
        mSubTotalTVLayoutParams.topMargin = 2 * textViewTopMargin;

        RelativeLayout.LayoutParams mSubTotalValueTVLayoutParams = (RelativeLayout.LayoutParams) mSubTotalValueTV.getLayoutParams();
        mSubTotalValueTVLayoutParams.leftMargin = textViewLeftMargin;
        mSubTotalValueTVLayoutParams.topMargin = 2 * textViewTopMargin;

        RelativeLayout.LayoutParams mDiscountAmountTVLayoutParams = (RelativeLayout.LayoutParams) mDiscountAmountTV.getLayoutParams();
        mDiscountAmountTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mDiscountAmountTV.setLayoutParams(mDiscountAmountTVLayoutParams);

        RelativeLayout.LayoutParams mDiscountAmountValueTVLayoutParams = (RelativeLayout.LayoutParams) mDiscountAmountValueTV.getLayoutParams();
        mDiscountAmountValueTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mDiscountAmountValueTVLayoutParams.leftMargin = textViewLeftMargin;
        mDiscountAmountValueTV.setLayoutParams(mDiscountAmountValueTVLayoutParams);

        RelativeLayout.LayoutParams mCouponAmountTVParam = (RelativeLayout.LayoutParams) mCouponAmountTV.getLayoutParams();
        mCouponAmountTVParam.topMargin = 2 * textViewTopMargin;
        mCouponAmountTV.setLayoutParams(mCouponAmountTVParam);

        RelativeLayout.LayoutParams mCouponAmountValueTVParam = (RelativeLayout.LayoutParams) mCouponAmountValueTV.getLayoutParams();
        mCouponAmountValueTVParam.topMargin = 2 * textViewTopMargin;
        mCouponAmountValueTVParam.leftMargin = textViewLeftMargin;
        mCouponAmountValueTV.setLayoutParams(mCouponAmountValueTVParam);

        RelativeLayout.LayoutParams mRedeemAmountTVParam = (RelativeLayout.LayoutParams) mRedeemAmountTV.getLayoutParams();
        mRedeemAmountTVParam.topMargin = 2 * textViewTopMargin;
        mRedeemAmountTV.setLayoutParams(mRedeemAmountTVParam);

        RelativeLayout.LayoutParams mRedeemAmountValueTVParam = (RelativeLayout.LayoutParams) mRedeemAmountValueTV.getLayoutParams();
        mRedeemAmountValueTVParam.topMargin = 2 * textViewTopMargin;
        mRedeemAmountValueTVParam.leftMargin = textViewLeftMargin;
        mRedeemAmountValueTV.setLayoutParams(mRedeemAmountValueTVParam);

        RelativeLayout.LayoutParams mAmountAfterDiscountTVLayoutParams = (RelativeLayout.LayoutParams) mAmountAfterDiscountTV.getLayoutParams();
        mAmountAfterDiscountTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mAmountAfterDiscountTV.setLayoutParams(mAmountAfterDiscountTVLayoutParams);

        RelativeLayout.LayoutParams mAmountAfterDiscountValueTVLayoutParams = (RelativeLayout.LayoutParams) mAmountAfterDiscountValueTV.getLayoutParams();
        mAmountAfterDiscountValueTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mAmountAfterDiscountValueTV.setLayoutParams(mAmountAfterDiscountValueTVLayoutParams);

        RelativeLayout.LayoutParams mGstInclusiveTVLayoutParams = (RelativeLayout.LayoutParams) mGstInclusiveTV.getLayoutParams();
        mGstInclusiveTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mGstInclusiveTV.setLayoutParams(mGstInclusiveTVLayoutParams);

        RelativeLayout.LayoutParams mGstInclusiveValueTVLayoutParams = (RelativeLayout.LayoutParams) mGstInclusiveValueTV.getLayoutParams();
        mGstInclusiveValueTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mGstInclusiveValueTV.setLayoutParams(mGstInclusiveValueTVLayoutParams);

        RelativeLayout.LayoutParams mAmountIncludesGSTTVLayoutParams = (RelativeLayout.LayoutParams) mAmountIncludesGSTTV.getLayoutParams();
        mAmountIncludesGSTTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mAmountIncludesGSTTV.setLayoutParams(mAmountIncludesGSTTVLayoutParams);

        RelativeLayout.LayoutParams mAmountIncludesGSTValueTVLayoutParams = (RelativeLayout.LayoutParams) mAmountIncludesGSTValueTV.getLayoutParams();
        mAmountIncludesGSTValueTVLayoutParams.topMargin = 2 * textViewTopMargin;
        mAmountIncludesGSTValueTV.setLayoutParams(mAmountIncludesGSTValueTVLayoutParams);

        RelativeLayout.LayoutParams mPaymentTypeTVParams = (RelativeLayout.LayoutParams) mPaymentTypeTV.getLayoutParams();
        mPaymentTypeTVParams.topMargin = 2 * textViewTopMargin;
        mPaymentTypeTV.setLayoutParams(mPaymentTypeTVParams);

        RelativeLayout.LayoutParams mPaymentTypeValueTVParam = (RelativeLayout.LayoutParams) mPaymentTypeValueTV.getLayoutParams();
        mPaymentTypeValueTVParam.topMargin = 2 * textViewTopMargin;
        mPaymentTypeValueTVParam.leftMargin = textViewLeftMargin;
        mPaymentTypeValueTV.setLayoutParams(mPaymentTypeValueTVParam);

        mAmountTV.setText("Amount" + "\n" + "(" + UiController.sCurrency + ")");
        mTotalPriceTV.setText("Total" + "\n" + "(" + UiController.sCurrency + ")");

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
        mPayButton.setOnClickListener(this);
    }

    private HashMap<String, Object> valueKey;
    private ArrayList<String> list;


    private void backClick() {
        getActivity().onBackPressed();
    }


    private void finishNewOrder() {
        int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getActivity().getSupportFragmentManager().getBackStackEntryAt(0).getName().equals("com.printer.epos.rtpl.OrderListingFragment"))
                size -= 1;

            for (int i = 0; i < size; ++i) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }

        Bundle arguments = new Bundle();
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");

        if (getActivity() instanceof Home) {
            ((Home) getActivity()).changeFragment(FragmentUtils.NewOrderFragment, arguments, true, false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
            case R.id.payButton: {
                payDialog(getActivity());
                break;
            }
        }
    }

    private void hideKeyboard(View view, Context context) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.requestFocus();
    }

    private void setupUI(final View view, final Context context) {
        view.setFocusableInTouchMode(true);

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view, context);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }


    private void payDialog(final Context context) {
        try {
            payDialog = getDialog(context);
            View view = payDialog.findViewById(R.id.container);
            setupUI(view, context);

            TextView accept_button = (Button) payDialog.findViewById(R.id.accept_button);
            final EditText amount_paidET = (EditText) payDialog.findViewById(R.id.amount_paidET);

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

            TextView cancel_button = (TextView) payDialog.findViewById(R.id.cancelDialog);

            TextView currency = (TextView) payDialog.findViewById(R.id.currency);

            TextView save_button = (TextView) payDialog.findViewById(R.id.saveCategory);
            TextView totalAmount = (TextView) payDialog.findViewById(R.id.totalAmount);
            final TextView amountPaid = (TextView) payDialog.findViewById(R.id.amountPaid);
            final TextView cashDue = (TextView) payDialog.findViewById(R.id.cashDue);

//            totalAmount.setText(Util.priceFormat(wrapper.getNetAmount()));
            totalAmount.setText(Util.priceFormat(amountIncludesGST));
            currency.setText(UiController.sCurrency);

            Spinner paymentModeSpinner = (Spinner) payDialog.findViewById(R.id.paymentSpinner);

            setPaymentSpinnerAdapter(paymentModeSpinner);
            paymentModeSpinner.setOnItemSelectedListener(this);

            accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Util.hideSoftKeypad(context);
                    if(!Util.isValidPrice(amount_paidET) && new Validation().checkValidation((ViewGroup) payDialog.findViewById(R.id.container))){
                        return;
                    }

                    if (amount_paidET.getText().toString().length() > 0) {
                        double amount = Double.parseDouble(amount_paidET.getText().toString());
                        double netAmount = Double.parseDouble(Util.price(wrapper.getNetAmount()));
                        if (amount >= amountIncludesGST) {
                            amountPaid.setText(Util.priceFormat(amount));
                            double due = amount - amountIncludesGST;
                            if (due > 1) {
                                cashDue.setText(Util.priceFormat(due));
                            }else {
                                cashDue.setText(Util.priceFormat(due));
                            }
                            wrapper.setCashDue(Util.priceFormat(due));

                            confirmButtonClicked = false;

                            if (peripheralManager != null) {
                                peripheralManager.resetDisplay();
                                peripheralManager.displayAlignedText("CHANGE DUE:", Util.priceFormat(due));
                                blockUi();
                               // if(!TextUtils.isEmpty(paymentType) && paymentType.equalsIgnoreCase("CASH")) {
                                    peripheralManager.openCashDrawer();
                               // }
                            }

                        } else {
                            Crouton.makeText(getActivity(), "Paid amount should be equal or more than net amount.", Style.ALERT).show();
                        }
                    } else {
                        Crouton.makeText(getActivity(), "Paid amount should be equal or more than net amount.", Style.ALERT).show();
                    }

                }

            });
            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (new Validation().checkValidation((ViewGroup) payDialog.findViewById(R.id.container))) {
                        if (!amountPaid.getText().toString().equals("0.00")) {
                            confirmButtonClicked = true;
                            payDialog.dismiss();
                            if(isPrinterOk) {
                                addOrder();
                            }else {
                                if (getActivity() != null)
                                    Crouton.makeText(getActivity(), "Please fix printer error first.", Style.ALERT).show();
                            }
                        } else {
                            if (getActivity() != null)
                                Crouton.makeText(getActivity(), "Please fill and accept correct amount.", Style.ALERT).show();
                        }
                    }
                }

            });


            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    payDialog.dismiss();

                }
            });


            payDialog.show();
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(OrderPreviewFragment.class.getName(), ex);
        }
    }
    private void setPaymentSpinnerAdapter(Spinner paymentModeSpinner) {

        final Context context = getActivity();
        final List<String> paymentModes = new ArrayList<String>();
        paymentModes.add(context.getString(R.string.title_select_payment_mode));
        paymentModes.add("Visa/Master");
        paymentModes.add("Nets");
        paymentModes.add("Cash");
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

    private Dialog getDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pay_popup_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(
                context.getResources().getDrawable(
                        R.drawable.dilaog_circular_corner));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private void addOrder() {
        if (localContext != null)
            Util.hideSoftKeypad(localContext);
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

                    map.put("membership_id", wrapper.getMembershipId());
                    map.put("customer_name", wrapper.getCustomerName());
                    map.put("coupon_code", wrapper.getCouponCode());
                    map.put("redeemed_points", wrapper.getRedeemPoints());
                    map.put("employee_id", mSavePreferences.get_id());
                    map.put("status", "completed");
                    map.put("payment_type", wrapper.getPaymentType());

                    map.put("product_id", product_id);
                    map.put("qty", quantity);

                    OrderWrapper.addOrder(map, localContext, this);

                } else {
                    new CustomDialog().showOneButtonAlertDialog(localContext, "Item List can't blank.",
                            "Please add at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(OrderPreviewFragment.class.getName(), ex);
        }

    }


    private void printText() {
        if (peripheralManager.getPrinter() != null) {
            printReceiptText();
            peripheralManager.getPrinter().clearCommandBuffer();
        } else {
            Toast.makeText(getActivity(), "Please connect to printer first", Toast.LENGTH_LONG).show();
            finishNewOrder();
        }
    }

    private synchronized void printReceiptText() {
        if (Util.printerDetailsList != null && Util.printerDetailsList.size() != 0) {
            //Master Printer
            PrinterDetails details = Util.printerDetailsList.get(0);
            if (details.getIsEnabled().equals("1"))
                buildReceipt(peripheralManager.getPrinter(), details.getPrinterName());

            //Slave Printers
            for (int i = 1; i < Util.printerDetailsList.size(); i++) {
                PrinterDetails printerDetails = Util.printerDetailsList.get(i);
                if (printerDetails.getIsEnabled().equals("1")) {
                    if (MultiplePrintManager.getMultiplePrinterManager().getPrinterList().size() != 0) {
                        MultiplePrintManager.PrinterBuilder printerBuilder = MultiplePrintManager.getMultiplePrinterManager().getPrinterList().get(i - 1);
                        if (printerBuilder.getPrinterId().equals(printerDetails.getPrinterName()))
                            buildReceipt(printerBuilder.getPrinter(), printerBuilder.getPrinterId());
                    }
                }
            }
            if (isPrinterOk)
                finishNewOrder();

        }
    }

    private void buildReceipt(Printer printer, String printerId) {
        ReceiptBuilder builder = new ReceiptBuilder.Builder(printer, UiController.context)
                .setName(mSavePreferences.getReceiptName())
                .setHeader1(mSavePreferences.getReceiptHeader1())
                .setHeader2(mSavePreferences.getReceiptHeader2())
                .setHeader3(mSavePreferences.getReceiptHeader3())
                .setHeader4(mSavePreferences.getReceiptWebsite())
                .setUserName(wrapper.getCustomerName())
                .setProductList(getProductListPrintText())
                .setPrinterId(printerId)
                .setCouponCode(wrapper.getCouponCode())
                .setRedeemedPoints(wrapper.getRedeemPoints())
                .setDiscountPercentage(wrapper.getDiscountPercentage())
                .setGrossAmount(wrapper.getGrossAmount())
                .setDiscountAmount(wrapper.getDiscountAmount())
                .setCouponAmount(wrapper.getCouponAmount())
                .setRedeemedAmount(wrapper.getRedeemedAmount())
                .setTaxAmount(wrapper.getTaxAmount())
                .setTaxPercentage(wrapper.getTaxPercentage())
                .setTotalAmount(wrapper.getNetAmount())
                .setPrintFooterMessage(mSavePreferences.getReceiptMessage())
                .setPrintBarcode(orderId)
                .setPaymentType(paymentType)
                .setCashDue(Double.parseDouble(wrapper.getCashDue()))
                .build();

    }

    private StringBuffer getProductListPrintText() {
        StringBuffer orderItems = new StringBuffer();
        String orderHeader = String.format("%-24s", getString(R.string.text_item_description)) + " " + String.format("%-5s", getString(R.string.text_qty)) + " " + String.format("%-12s", getString(R.string.text_price)) + " " + String.format("%-12s", getString(R.string.text_amount)) ;
        orderItems.append(orderHeader);
        String seperator = getString(R.string.receipt_separator)+"\n";
        orderItems.append(seperator);

        int i = 0;
        for (ProductOrderWrapper wrapper : orderList) {

            String productName = wrapper.getProductName();

            if(productName.length() > 24){
                productName = productName.substring(0,20) + "...";
            }
            //orderId = wrapper.getId();
            productName = String.format("%-24s", productName);
            receiptNo = wrapper.getReceiptNo();
            String quantity = String.format("%-4s", String.valueOf(wrapper.getAddedQuantity()));
            String sellingPrice = String.format("%-12s", Util.priceFormat(String.valueOf(wrapper.getSellingPrice())));
            String totalPrice = String.format("%-12s", Util.priceFormat(String.valueOf(wrapper.getTotalPrice())));
            //String items = "<text>"+productName+"&#9;"+quantity+"&#9;"+sellingPrice+"&#9;"+totalPrice+"&#10;</text>\n";
            String items = productName + " " + " "+quantity + " " + sellingPrice + " " + totalPrice + "\n";
            orderItems.append(items);

        }

        return orderItems;
    }

    public void printReceipt(String id) {
        orderId = id;
        printText();
    }


    @Override
    public void onPrinterStatusReceived(final String message, final int code, final int status) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code != EposCallbackCode.SUCCESS) {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Printer Error", message, "RETRY", 0, new DialogButtonListener() {
                            @Override
                            public void onPositiveClick() {
                                //if (confirmButtonClicked)
                                isPrinterOk = false;
                                checkPrinter();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });
                    } else
                        isPrinterOk = true;
                }
            });
        }

    }

    private void blockUi() {
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
                    if (mDialog.isShowing())
                        mDialog.dismiss();
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                    RetailPosLoging.getInstance().registerLog(OrderPreviewFragment.class.getName(), e);
                }
            }
        };

        // start thread
        background.start();

    }

    private void checkPrinter() {
        try {
            peripheralManager.getPrinter().addTextAlign(Printer.ALIGN_CENTER);
            peripheralManager.getPrinter().sendData();
            peripheralManager.getPrinter().clearCommandBuffer();
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(OrderPreviewFragment.class.getName(), ex);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position != 0) {
            String item = parent.getItemAtPosition(position).toString();
            paymentType = item;
            wrapper.setPaymentType(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

