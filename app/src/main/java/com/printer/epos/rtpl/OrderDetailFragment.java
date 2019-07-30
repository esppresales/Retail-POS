package com.printer.epos.rtpl;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.OrderDetailListAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.DeviceType;
import com.printer.epos.rtpl.wrapper.OrderWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by android-sristi on 6/4/15.
 */
public class OrderDetailFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ORDER_TYPE = "order_type";
    private String orderType = "Completed";

    private String mDiscountPercentage;
    private String mCouponCode;
    private String mRedeemedPoints;


    private double GROSS_AMOUNT = 0.0;
    private double DISCOUNT_AMOUNT = 0.0;
    private double COUPON_AMOUNT = 0.0;
    private double REDEEMED_AMOUNT = 0.0;
    private double TAX_AMOUNT = 0.0;
    private double NET_AMOUNT = 0.0;
    private double TAX_PERCENTAGE = 0.0;
    private double REDEEMED_POINT_PRICE = 0.0;
    private String paymentType = "";
    public static double cashDueValue = 0.0;
    double amountIncludesGST = 0.0;



    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }

    }

    Home hContext;

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setEnabledButtons(false, true, true, false);
        peripheralManager.connectDevice(DeviceType.PRINTER,
                new SavePreferences(getActivity()).getMasterPrinterIp());

        hContext.backButton.setOnClickListener(this);
        hContext.saveButton.setOnClickListener(this);


        hContext.saveButton.setText(getString(R.string.save));
        hContext.saveButton.setVisibility(View.GONE);
        hContext.setTitleText(getString(R.string.order_detail));
        update = true;


        wrapper = EventBus.getDefault().removeStickyEvent(OrderWrapper.OrderInnerWrapper.class);

        if (wrapper != null) {
            if (Validation.isValidData(wrapper.getId()))
                mOrderIdET.setText(wrapper.getId());
            if (Validation.isValidData(wrapper.getCreatedDate()))
                mOrderDateET.setText(wrapper.getCreatedDate());
            if (wrapper.getMembershipId() != null)
                mMemberShipIDET.setText("" + wrapper.getMembershipId());
            if (Validation.isValidData(wrapper.getCustomerName()))
                mCustomerNameET.setText(wrapper.getCustomerName());

            mDiscountPercentage = wrapper.getDiscountPercentage();
            mCouponCode = wrapper.getCouponCode();
            mRedeemedPoints = wrapper.getRedeemedPoints();
            GROSS_AMOUNT = Double.valueOf(wrapper.getGrossAmount());
            DISCOUNT_AMOUNT = Double.valueOf(wrapper.getDiscountAmount());
            REDEEMED_AMOUNT = Double.valueOf(wrapper.getRedeemedPoints());
            paymentTypeValue = wrapper.getPaymentType();
            receiptNo = wrapper.getReceiptNo();

            //REDEEMED_POINT_PRICE = Double.valueOf(mSavePreferences.getEarnedAmount());
            //REDEEMED_AMOUNT = REDEEMED_AMOUNT * REDEEMED_POINT_PRICE;

            if (wrapper.getCouponDiscountAmount() != null)
                COUPON_AMOUNT = Double.valueOf(wrapper.getCouponDiscountAmount().toString());
            else
                COUPON_AMOUNT = 0.0;

            TAX_AMOUNT = Double.valueOf(wrapper.getTaxAmount());
            NET_AMOUNT = Double.valueOf(wrapper.getFinalAmount());
            TAX_PERCENTAGE = Double.valueOf(wrapper.getTaxPercentage());

            setOrderDetailValues();

            if (wrapper.getProductDetails().size() > 0) {
                productList = wrapper.getProductDetails();
                mOrderAdapter = new OrderDetailListAdapter(this, productList, receiptNo);
                mOrderListView.setAdapter(mOrderAdapter);

                RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
                mOrderListViewParam.leftMargin = (int) (deviceWidth * 0.02f);
                mOrderListViewParam.rightMargin = (int) (deviceWidth * 0.01f);

                View item = mOrderAdapter.getView(0, null, mOrderListView);
                item.measure(0, 0);
                mOrderListViewParam.height = (int) (mOrderAdapter.getCount() * item.getMeasuredHeight());

                if (mOrderAdapter.getCount() > 3) {
                    mOrderListViewParam.height = (int) (3 * item.getMeasuredHeight());
                }
                mOrderListView.setLayoutParams(mOrderListViewParam);
            }
        }

//        if (EposDeviceClient.getPrinter() == null)
//            Toast.makeText(getActivity(), "App is not connected with printer", Toast.LENGTH_LONG).show();
        /*if (!isDeviceCreated)
            createDevice(mSavePreferences.getMasterPrinterIp(), Device.DEV_TYPE_PRINTER, Device.FALSE);*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public int deviceWidth;
    public int deviceHeight;
    private String orderId;

    SavePreferences mSavePreferences;

    private ScrollView formScroll;
    private RelativeLayout mAmountLayout, mNetAmountLayout, mPointsLayout;
    private TextView mOrderIdTV, mOrderDateTV, mMembershipIDTV, mCustomerNameTV, mProductDetailsTV, mAmountTV, mQuantityTV, mTotalPriceTV,
            mDiscountAmountTV, mDiscountAmountValueTV, mCouponAmountTV, mCouponAmountValueTV, mRedeemAmountTV,
            mRedeemAmountValueTV,  mDiscountPercentageValueTV,
            mCouponTV, mCouponValueTV, mRedeemPointsTV, mRedeemPointsValueTV,mPaymentTypeTV, mPaymentTypeValueTV, mSubTotalTV, mSubTotalValueTV,
            mAmountBeforeGSTTV, mAmountBeforeGSTValueTV,
            mAmountAfterDiscountTV, mAmountAfterDiscountValueTV, mGstInclusiveTV, mGstInclusiveValueTV, mAmountIncludesGSTTV, mAmountIncludesGSTValueTV;

    private TextView mOrderIdET, mOrderDateET, mMemberShipIDET, mCustomerNameET;

    private Button mReturnOrderButton, mCancelButton;
    private View listSepartor, listSepartor1;
    private ListView mOrderListView;
    private OrderDetailListAdapter mOrderAdapter;

    ImageLoader mImageLoader;
    private View rootView;
    private static OrderWrapper.OrderInnerWrapper wrapper;
    private boolean update = false;
    List<OrderWrapper.ProductDetail> productList;
    private String paymentTypeValue;
    private String receiptNo;

    DisplayImageOptions options1;

    EventBus bus = EventBus.getDefault();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        mImageLoader = ImageLoader.getInstance();
        options1 = new DisplayImageOptions.Builder()
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
        mOrderIdTV = (TextView) rootView.findViewById(R.id.orderIdTV);
        mOrderDateTV = (TextView) rootView.findViewById(R.id.orderDateTV);
        mCustomerNameTV = (TextView) rootView.findViewById(R.id.customerNameTV);

        mAmountTV = (TextView) rootView.findViewById(R.id.amountTV);
        mProductDetailsTV = (TextView) rootView.findViewById(R.id.productDetailsTV);

        mQuantityTV = (TextView) rootView.findViewById(R.id.quantityTV);
        mTotalPriceTV = (TextView) rootView.findViewById(R.id.totalPriceTV);
        //mGrossAmountTV = (TextView) rootView.findViewById(R.id.grossAmountTV);
        //mGrossAmountValueTV = (TextView) rootView.findViewById(R.id.grossAmountValueTV);
        mDiscountAmountTV = (TextView) rootView.findViewById(R.id.discountAmountTV);
        mDiscountAmountValueTV = (TextView) rootView.findViewById(R.id.discountAmountValueTV);
        mCouponAmountTV = (TextView) rootView.findViewById(R.id.couponAmountTV);
        mCouponAmountValueTV = (TextView) rootView.findViewById(R.id.couponAmountValueTV);
        mRedeemAmountTV = (TextView) rootView.findViewById(R.id.redeemeAmountTV);
        mRedeemAmountValueTV = (TextView) rootView.findViewById(R.id.redeemeAmountValueTV);
        mPaymentTypeValueTV = (TextView) rootView.findViewById(R.id.paymentTypeValueTV);
        mPaymentTypeTV = (TextView) rootView.findViewById(R.id.paymentTypeTV);
        mDiscountPercentageValueTV = (TextView) rootView.findViewById(R.id.discountPercentageValueTV);
        mCouponTV = (TextView) rootView.findViewById(R.id.couponTV);
        mCouponValueTV = (TextView) rootView.findViewById(R.id.couponValueTV);
        mRedeemPointsTV = (TextView) rootView.findViewById(R.id.redeemPointsTV);
        mRedeemPointsValueTV = (TextView) rootView.findViewById(R.id.redeemPointsValueTV);

        mDiscountPercentageValueTV = (TextView) rootView.findViewById(R.id.discountPercentageValueTV);

        mMemberShipIDET = (TextView) rootView.findViewById(R.id.membershipIdET);
        mCustomerNameET = (TextView) rootView.findViewById(R.id.customerNameET);
        mOrderDateET = (TextView) rootView.findViewById(R.id.orderDateET);
        mOrderIdET = (TextView) rootView.findViewById(R.id.orderIdET);

        mReturnOrderButton = (Button) rootView.findViewById(R.id.returnOrderButton);
        mCancelButton = (Button) rootView.findViewById(R.id.cancelButton);

        mOrderListView = (ListView) rootView.findViewById(R.id.orderListView);
        listSepartor = rootView.findViewById(R.id.listSeparator);
        listSepartor1 = rootView.findViewById(R.id.listSeparator1);

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

        mCancelButton.setOnClickListener(this);

        setDimensionsToViews();

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            getActivity().getActionBar().setTitle(mItem.content);
        }

        if (orderType.equals("Completed")) {
            mReturnOrderButton.setText(getString(R.string.return_order));
            mCancelButton.setVisibility(View.GONE);
        } else {
            mReturnOrderButton.setText(getString(R.string.pay));
            mCancelButton.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


    private void setDimensionsToViews() {

        int editTextHeight = (int) (deviceHeight * 0.08);
        int buttonHeight = (int) (deviceHeight * 0.08);
        int textViewHeight = (int) (deviceHeight * 0.05);
        int textViewTopMargin = (int) (deviceHeight * 0.008f);
        int textViewLeftMargin = (int) (deviceWidth * 0.01f);

        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        form_param.bottomMargin = (int) (deviceHeight * .03f);
        formScroll.setLayoutParams(form_param);

        RelativeLayout.LayoutParams mOrderIdTVParam = (RelativeLayout.LayoutParams) mOrderIdTV.getLayoutParams();
        mOrderIdTVParam.width = (int) (deviceWidth * 0.2f);
        mOrderIdTVParam.height = textViewHeight;
        mOrderIdTVParam.leftMargin = (int) (deviceWidth * 0.04f);
        //mOrderIdTVParam.rightMargin = (int) (deviceWidth * 0.03f);
        mOrderIdTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mOrderIdTV.setLayoutParams(mOrderIdTVParam);

        RelativeLayout.LayoutParams mOrderIdETParam = (RelativeLayout.LayoutParams) mOrderIdET.getLayoutParams();
        mOrderIdETParam.width = (int) (deviceWidth * 0.2f);
        mOrderIdETParam.height = textViewHeight;
        mOrderIdETParam.leftMargin = (int) (deviceWidth * 0.04f);
        //mOrderIdETParam.rightMargin = (int) (deviceWidth * 0.02f);
        mOrderIdET.setLayoutParams(mOrderIdETParam);

        RelativeLayout.LayoutParams mOrderDateTVParam = (RelativeLayout.LayoutParams) mOrderDateTV.getLayoutParams();
        mOrderDateTVParam.width = (int) (deviceWidth * 0.2f);
        mOrderDateTVParam.height = textViewHeight;
        mOrderDateTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        //mOrderDateTVParam.rightMargin = (int) (deviceWidth * 0.02f);
        mOrderDateTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mOrderDateTV.setLayoutParams(mOrderDateTVParam);

        RelativeLayout.LayoutParams mOrderDateETParam = (RelativeLayout.LayoutParams) mOrderDateET.getLayoutParams();
        mOrderDateETParam.width = (int) (deviceWidth * 0.2f);
        mOrderDateETParam.height = textViewHeight;
        mOrderDateETParam.leftMargin = (int) (deviceWidth * 0.02f);
        //mOrderDateETParam.rightMargin = (int) (deviceWidth * 0.02f);
        mOrderDateET.setLayoutParams(mOrderDateETParam);

        RelativeLayout.LayoutParams mMembershipIDTVParam = (RelativeLayout.LayoutParams) mMembershipIDTV.getLayoutParams();
        mMembershipIDTVParam.width = (int) (deviceWidth * 0.2f);
        mMembershipIDTVParam.height = textViewHeight;
        mMembershipIDTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        //mMembershipIDTVParam.rightMargin = (int) (deviceWidth * 0.02f);
        mMembershipIDTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mMembershipIDTV.setLayoutParams(mMembershipIDTVParam);

        RelativeLayout.LayoutParams mMembershipIDETParam = (RelativeLayout.LayoutParams) mMemberShipIDET.getLayoutParams();
        mMembershipIDETParam.width = (int) (deviceWidth * 0.2f);
        mMembershipIDETParam.height = textViewHeight;
        mMembershipIDETParam.leftMargin = (int) (deviceWidth * 0.02f);
        // mMembershipIDETParam.rightMargin = (int) (deviceWidth * 0.02f);
        mMemberShipIDET.setLayoutParams(mMembershipIDETParam);

        RelativeLayout.LayoutParams mCustomerNameTVParam = (RelativeLayout.LayoutParams) mCustomerNameTV.getLayoutParams();
        mCustomerNameTVParam.width = (int) (deviceWidth * 0.22f);
        mCustomerNameTVParam.height = textViewHeight;
        mCustomerNameTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        // mCustomerNameTVParam.rightMargin = (int) (deviceWidth * 0.02f);
        mCustomerNameTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mCustomerNameTV.setLayoutParams(mCustomerNameTVParam);

        RelativeLayout.LayoutParams mCustomerNameETParam = (RelativeLayout.LayoutParams) mCustomerNameET.getLayoutParams();
        //mCustomerNameETParam.width = (int) (deviceWidth * 0.25f);
        mCustomerNameETParam.height = textViewHeight;
//        mCustomerNameETParam.rightMargin = (int) (deviceWidth * 0.05f);
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

        RelativeLayout.LayoutParams mDiscountPercentageValueTVParam = (RelativeLayout.LayoutParams) mDiscountPercentageValueTV.getLayoutParams();
        //mDiscountPercentageValueTVParam.topMargin = textViewTopMargin;
        mDiscountPercentageValueTVParam.leftMargin = (int) (deviceWidth * 0.02f);

        mDiscountPercentageValueTV.setLayoutParams(mDiscountPercentageValueTVParam);

        RelativeLayout.LayoutParams mCouponTVParam = (RelativeLayout.LayoutParams) mCouponTV.getLayoutParams();
        mCouponTVParam.topMargin = textViewTopMargin;
        mCouponTV.setLayoutParams(mCouponTVParam);

        RelativeLayout.LayoutParams mCouponValueTVParam = (RelativeLayout.LayoutParams) mCouponValueTV.getLayoutParams();
        mCouponValueTVParam.topMargin = textViewTopMargin;
        //mCouponValueTVParam.leftMargin = textViewLeftMargin;
        mCouponValueTV.setLayoutParams(mCouponValueTVParam);

        RelativeLayout.LayoutParams mRedeemPointsTVParam = (RelativeLayout.LayoutParams) mRedeemPointsTV.getLayoutParams();
        mRedeemPointsTVParam.topMargin = textViewTopMargin;
        mRedeemPointsTV.setLayoutParams(mRedeemPointsTVParam);

        RelativeLayout.LayoutParams mRedeemPointsValueTVParam = (RelativeLayout.LayoutParams) mRedeemPointsValueTV.getLayoutParams();
        mRedeemPointsValueTVParam.topMargin = textViewTopMargin;
        // mRedeemPointsValueTVParam.leftMargin = textViewLeftMargin;
        mRedeemPointsValueTV.setLayoutParams(mRedeemPointsValueTVParam);

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

        RelativeLayout.LayoutParams mReturnOrderButtonParam = (RelativeLayout.LayoutParams) mReturnOrderButton.getLayoutParams();
        mReturnOrderButtonParam.height = buttonHeight;
        mReturnOrderButtonParam.width = (int) (deviceWidth * 0.15f);
        mReturnOrderButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mReturnOrderButtonParam.bottomMargin = (int) (deviceHeight * 0.03f);
        mReturnOrderButtonParam.rightMargin = (int) (deviceWidth * 0.02f);
        mReturnOrderButtonParam.leftMargin = textViewLeftMargin;
        mReturnOrderButton.setLayoutParams(mReturnOrderButtonParam);
        mReturnOrderButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mCancelButtonParam = (RelativeLayout.LayoutParams) mCancelButton.getLayoutParams();
        mCancelButtonParam.height = buttonHeight;
        mCancelButtonParam.width = (int) (deviceWidth * 0.15f);
        mCancelButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mCancelButtonParam.bottomMargin = (int) (deviceHeight * 0.03f);
        mCancelButtonParam.leftMargin = textViewLeftMargin;
        mCancelButton.setLayoutParams(mCancelButtonParam);
        mCancelButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

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

        mReturnOrderButton.setOnClickListener(this);

    }

    private HashMap<String, Object> valueKey;
    private ArrayList<String> list;


    public void backClick() {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }

            case R.id.returnOrderButton: {
                if (orderType.equals("Completed")) {
                    returnOrderNavigation();
                } else {
                    payDialog(getActivity());
                }
                break;
            }

            case R.id.cancelButton:
                addOrder("cancelled");
                break;

        }
    }

    private void returnOrderNavigation() {
        if (bus != null)
            bus.postSticky(wrapper);
        if (getActivity() instanceof Home) {
            ((Home) getActivity()).changeFragment(FragmentUtils.ReturnOrderFragment, getOrderArguments(), true, false);
        }
    }

    private Bundle getOrderArguments() {
        Bundle arguments = new Bundle();
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "order 2");
        arguments.putDouble("gross_amount", GROSS_AMOUNT);
        arguments.putDouble("discount_amount", DISCOUNT_AMOUNT);
        arguments.putDouble("coupon_amount", COUPON_AMOUNT);
        arguments.putDouble("tax_amount", TAX_AMOUNT);
        arguments.putDouble("redeemed_amount", REDEEMED_AMOUNT);
        arguments.putDouble("net_amount", NET_AMOUNT);

        return arguments;
    }

    public void payDialog(final Context context) {
        try {
            final Dialog dialog = getDialog(context, R.layout.pay_popup_dialog_layout);
            TextView accept_button = (Button) dialog.findViewById(R.id.accept_button);
            final TextView amount_paidET = (EditText) dialog.findViewById(R.id.amount_paidET);
            TextView cancel_button = (TextView) dialog.findViewById(R.id.cancelDialog);
            TextView currency = (TextView) dialog.findViewById(R.id.currency);
            TextView save_button = (TextView) dialog.findViewById(R.id.saveCategory);
            TextView totalAmount = (TextView) dialog.findViewById(R.id.totalAmount);
            final TextView amountPaid = (TextView) dialog.findViewById(R.id.amountPaid);
            final TextView cashDue = (TextView) dialog.findViewById(R.id.cashDue);
            Spinner paymentModeSpinner = (Spinner) dialog.findViewById(R.id.paymentSpinner);

            totalAmount.setText(Util.priceFormat(amountIncludesGST));
            currency.setText(UiController.sCurrency);

            setPaymentSpinnerAdapter(paymentModeSpinner);
            paymentModeSpinner.setOnItemSelectedListener(this);

            accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (amount_paidET.getText().toString().length() > 0) {
                        double amount = Double.parseDouble(amount_paidET.getText().toString());
                        double netAmount = Double.parseDouble(Util.price(NET_AMOUNT));

                        if (amount >= amountIncludesGST) {
                            amountPaid.setText(Util.priceFormat(amount));
                            cashDue.setText(Util.priceFormat(amount - netAmount));
                            cashDueValue = amount - amountIncludesGST;
                            // if(!TextUtils.isEmpty(paymentType) && paymentType.equalsIgnoreCase("CASH")) {
                            peripheralManager.openCashDrawer();
                            // }

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
                    if (new Validation().checkValidation((ViewGroup) dialog.findViewById(R.id.container))) {
                        if (!amountPaid.getText().toString().equals("0.00")) {
                            dialog.dismiss();
                            addOrder("completed");
                        } else
                            Crouton.makeText(getActivity(), "Please fill and accept correct amount.", Style.ALERT).show();
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
            RetailPosLoging.getInstance().registerLog(OrderDetailFragment.class.getName(), ex);
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

    private Dialog getDialog(final Context context, int layout) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(
                context.getResources().getDrawable(
                        R.drawable.dilaog_circular_corner));

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private void addOrder(String status) {
        Util.hideSoftKeypad(this.getActivity());
        try {

            if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.form))) {

                String product_id = "", quantity = "";
                for (int i = 0; i < mOrderAdapter.getCount(); i++) {
                    if (i == 0) {
                        product_id = mOrderAdapter.getItem(i).getId();
                        int total_quantity = Integer.parseInt(mOrderAdapter.getItem(i).getQty());
                        int returnQuantity = 0;

                        if (Validation.isValidData(mOrderAdapter.getItem(i).getReturnQty()))
                            returnQuantity = Integer.parseInt(mOrderAdapter.getItem(i).getReturnQty());

                        quantity = "" + (total_quantity - returnQuantity);
                    } else {
                        product_id += "," + mOrderAdapter.getItem(i).getId();
                        int total_quantity = Integer.parseInt(mOrderAdapter.getItem(i).getQty());
                        int returnQuantity = 0;

                        if (Validation.isValidData(mOrderAdapter.getItem(i).getReturnQty()))
                            returnQuantity = Integer.parseInt(mOrderAdapter.getItem(i).getReturnQty());

                        quantity += "," + (total_quantity - returnQuantity);

                    }
                }
                if (Validation.isValidString(product_id)) {
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    map.put("membership_id", mMemberShipIDET.getText().toString());
                    map.put("customer_name", mCustomerNameET.getText().toString());
                    map.put("coupon_code", wrapper.getCouponCode());
                    map.put("redeemed_points", mRedeemedPoints);
                    map.put("employee_id", mSavePreferences.get_id());
                    map.put("status", status);
                    map.put("order_id", wrapper.getId());
                    map.put("product_id", product_id);
                    map.put("qty", quantity);
                    map.put("payment_type", paymentType);

                    OrderWrapper.addOrder(map, getActivity(), this);

                } else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                            "Please add at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(OrderDetailFragment.class.getName(), ex);
            Toast.makeText(getActivity(), "No Products Found", Toast.LENGTH_LONG).show();
        }

    }

    private void setOrderDetailValues() {
        double gstAmount = 0.0;
        double amountBeforeGST = 0.0;
        double discountAmount = 0.0;
        double totalDiscountAmount = 0.0;
        double amountAfterDiscount = 0.0;
        double gstInclusive = 0.0;
        if(!TextUtils.isEmpty(wrapper.getTaxPercentage())) {
            gstAmount = Util.roundUpToTwoDecimal(GROSS_AMOUNT - GROSS_AMOUNT/(1 + Double.parseDouble(wrapper.getTaxPercentage()) / 100));
            amountBeforeGST = Util.roundUpToTwoDecimal(GROSS_AMOUNT - gstAmount);
            if(!TextUtils.isEmpty(wrapper.getDiscountPercentage())) {
                discountAmount = Util.roundUpToTwoDecimal((amountBeforeGST * Double.parseDouble(wrapper.getDiscountPercentage())) / 100) ;
            }
            totalDiscountAmount = Util.roundUpToTwoDecimal(discountAmount + COUPON_AMOUNT + REDEEMED_AMOUNT);
            if(totalDiscountAmount > 0) {
                amountAfterDiscount = Util.roundUpToTwoDecimal(amountBeforeGST - totalDiscountAmount);
                gstInclusive = Util.roundUpToTwoDecimal((amountAfterDiscount * Double.parseDouble(wrapper.getTaxPercentage()))/100);
            }else{
                amountAfterDiscount = amountBeforeGST - totalDiscountAmount;
                gstInclusive = gstAmount;

            }
            amountIncludesGST = Util.roundUpToTwoDecimal(amountAfterDiscount + gstInclusive);
        }

        mSubTotalValueTV.setText(Util.priceFormat(GROSS_AMOUNT));
        mAmountBeforeGSTValueTV.setText(Util.priceFormat(amountBeforeGST));
        if(!TextUtils.isEmpty(wrapper.getTaxPercentage())) {
            mGstInclusiveTV.setText(""+wrapper.getTaxPercentage() + "% GST inclusive");
        }
        if (TextUtils.isEmpty(mCouponCode)) {
            mCouponAmountTV.setText("Coupon Amount");
        } else {
            mCouponAmountTV.setText("Coupon Amount (" + wrapper.getCouponCode() + ")");

        }
        if (mRedeemedPoints != null && mRedeemedPoints.equals("0")) {
            mRedeemAmountTV.setText("Redemption Amount");
        } else {
            mRedeemAmountTV.setText("Redemption Amount (" + mRedeemedPoints + " Points)");
        }
        if(!TextUtils.isEmpty(wrapper.getDiscountPercentage())) {
            mDiscountAmountTV.setText(getString(R.string.discount_Amount) + "(" + wrapper.getDiscountPercentage() + "%)");
        }
        mDiscountAmountValueTV.setText(Util.priceFormat(discountAmount));
        mCouponAmountValueTV.setText(Util.priceFormat(COUPON_AMOUNT));
        mRedeemAmountValueTV.setText(Util.priceFormat(REDEEMED_AMOUNT));
        if(!TextUtils.isEmpty(paymentTypeValue)) {
            mPaymentTypeValueTV.setText(paymentTypeValue);
        }
        mGstInclusiveValueTV.setText(Util.priceFormat(gstInclusive));
        mAmountIncludesGSTValueTV.setText(Util.priceFormat(amountIncludesGST));
        mAmountAfterDiscountValueTV.setText(Util.priceFormat(amountAfterDiscount));

        TAX_AMOUNT = ((GROSS_AMOUNT) * TAX_PERCENTAGE) / 100;
        NET_AMOUNT = amountIncludesGST;

        if (NET_AMOUNT == 0) {
            mReturnOrderButton.setVisibility(View.INVISIBLE);
            mCancelButton.setVisibility(View.INVISIBLE);
        }
    }


    private void printText() {
        if (peripheralManager.getPrinter() != null) {
            printReceiptText();
            peripheralManager.getPrinter().clearCommandBuffer();
        } else
            Toast.makeText(getActivity(), "Please connect to printer first", Toast.LENGTH_LONG).show();
    }

    private void printReceiptText() {
        ReceiptBuilder builder = new ReceiptBuilder.Builder(peripheralManager.getPrinter(), UiController.context)
                .setName(mSavePreferences.getReceiptName())
                .setHeader1(mSavePreferences.getReceiptHeader1())
                .setHeader2(mSavePreferences.getReceiptHeader2())
                .setHeader3(mSavePreferences.getReceiptHeader3())
                .setHeader4(mSavePreferences.getReceiptWebsite())
                .setUserName(wrapper.getCustomerName())
                .setProductList(getProductListPrintText())
                .setCouponCode(wrapper.getCouponCode())
                .setPrintBarcode(orderId)
                .setRedeemedPoints(Integer.parseInt(wrapper.getRedeemedPoints()))
                .setDiscountPercentage(Double.parseDouble(wrapper.getDiscountPercentage()))
                .setDiscountAmount(Double.parseDouble(Util.priceFormat(wrapper.getDiscountAmount())))
                .setCouponAmount(Double.parseDouble(Util.priceFormat(wrapper.getCouponDiscountAmount().toString())))
                .setRedeemedAmount(Double.parseDouble(Util.priceFormat(wrapper.getRedeemedPoints())))
                .setTaxAmount(Double.parseDouble(Util.priceFormat(wrapper.getTaxAmount())))
                .setTotalAmount(Double.parseDouble(Util.priceFormat(wrapper.getFinalAmount())))
                .setCashDue(cashDueValue).
                        build();
    }

    private StringBuffer getProductListPrintText() {
        StringBuffer orderItems = new StringBuffer();
        String orderHeader = "<text>" + String.format("%-24s", getString(R.string.text_item_description)) + "&#9;" + String.format("%-5s", getString(R.string.text_qty)) + "&#9;" + String.format("%-12s", getString(R.string.text_price)) + "&#9;" + String.format("%-12s", getString(R.string.text_amount)) + "&#10;</text>\n";
        orderItems.append(orderHeader);
        String seperator = "<text>"+getString(R.string.receipt_separator)+"&#10;</text>\n";
        orderItems.append(seperator);

        int i = 0;
        for (OrderWrapper.ProductDetail wrapper : productList) {

            String orderID = wrapper.getId();
            double totalPrice = (Integer.parseInt(wrapper.getQty()) - Integer.parseInt(wrapper.getReturnQty())) * Double.parseDouble(wrapper.getProductPrice());
            String productName = wrapper.getName();

            if(productName.length() > 24){
                productName = productName.substring(0,20) + "...";
            }
            //orderId = wrapper.getId();
            productName = String.format("%-24s", productName);
            String quantity = String.format("%-4s", String.valueOf(wrapper.getReturnQty()));
            String sellingPrice = String.format("%-12s", Util.priceFormat(String.valueOf(wrapper.getProductPrice())));
            String total = String.format("%-12s", Util.priceFormat(String.valueOf(totalPrice)));
            String items = "<text>" + productName + "&#9;" + quantity + "&#9;" + sellingPrice + "&#9;" + total + "&#10;</text>\n";
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
        super.onPrinterStatusReceived(message, code, status);
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code != EposCallbackCode.SUCCESS) {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Printer Error", message, "RETRY", 0, new DialogButtonListener() {
                            @Override
                            public void onPositiveClick() {
                                printText();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });
                    }

                }
            });
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position != 0) {
            String item = parent.getItemAtPosition(position).toString();
            paymentType = item;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}


