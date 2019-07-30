package com.printer.epos.rtpl;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.eposdevice.EposCallbackCode;
import com.epson.eposdevice.printer.Printer;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.ReturnOrderListAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.DeviceType;
import com.printer.epos.rtpl.wrapper.MultiplePrintManager;
import com.printer.epos.rtpl.wrapper.OrderWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by android-sristi on 6/4/15.
 */
public class ReturnOrderFragment extends BaseFragment implements View.OnClickListener, ReturnOrderListAdapter.ValueChangeListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private boolean isPrinterOk = false;
    private String receiptNo = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReturnOrderFragment() {
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
        }
    }

    private Home hContext;

    @Override
    public void onResume() {
        super.onResume();
        peripheralManager.connectDevice(DeviceType.PRINTER,
                new SavePreferences(getActivity()).getMasterPrinterIp());

        ((Home) getActivity()).setEnabledButtons(false, true, true, false);

        hContext.backButton.setOnClickListener(this);
        hContext.saveButton.setOnClickListener(this);

        if (wrapper != null) {
            hContext.saveButton.setText("UPDATE");
            hContext.saveButton.setVisibility(View.GONE);
            hContext.setTitleText(getString(R.string.return_order_title));
            update = true;
        } else {
            hContext.saveButton.setText(getString(R.string.save));
            hContext.saveButton.setVisibility(View.GONE);
            hContext.setTitleText(getString(R.string.return_order_title));
            update = true;
        }

        checkPrinter();

//        if (EposDeviceClient.getPrinter() == null)
//            Toast.makeText(getActivity(), "App is not connected with printer", Toast.LENGTH_LONG).show();

       /* if (!isDeviceCreated)
            createDevice(mSavePreferences.getMasterPrinterIp(), Device.DEV_TYPE_PRINTER, Device.FALSE);*/
    }

    private double totalReturnAmount;

    private int deviceWidth;
    private int deviceHeight;

    private SavePreferences mSavePreferences;

    private ScrollView formScroll;
    private RelativeLayout mAmountLayout,mNetAmountLayout, mPointsLayout;
    private TextView mOrderIdTV, mOrderDateTV, mMembershipIDTV, mCustomerNameTV, mProductDetailsTV, mAmountTV, mQuantityTV, mTotalPriceTV,
            mReturnAmountTV, mReturnAmountValueTV, mDiscountAmountTV, mDiscountAmountValueTV, mTotalReturnAMountTV, mTotalReturnAMountValeTV,
            mDiscountPercentageTV, mDiscountPercentageValueTV ,mcouponAmountTV,mcouponAmountValueTV,mredeemAmountTV, mReceiptNoTV, mReceiptNoValueTV
            ,mredeemAmountValueTV,mtaxAmountTV,mtaxAmountValueTV, mAmountBeforeGstTV, mAmountBeforeGstValueTV, mAmountAfterDiscountTV, mAmountAfterDiscountValueTV;

    private TextView mOrderIdET, mOrderDateET, mMemberShipIDET, mCustomerNameET;

    private Button mConfirmReturnButton, mPrintButton;
    private View listSepartor, listSepartor1;
    private ListView mOrderListView;
    private ReturnOrderListAdapter mOrderAdapter;

    private static OrderWrapper.OrderInnerWrapper wrapper;
    private List<OrderWrapper.ProductDetail> productList;
    private boolean update = false;

    private double mReturnAmount;
    private double mDiscountPercentage;
    private String currency;
    private double mCouponCodeAmount;
    private double mRedeemedpointAmount;
    private double mTaxAmount;

    private double GROSS_AMOUNT = 0.0;
    private double DISCOUNT_AMOUNT = 0.0;
    private double COUPON_AMOUNT = 0.0;
    private double REDEEMED_AMOUNT = 0.0;
    private double TAX_AMOUNT = 0.0;
    private double NET_AMOUNT = 0.0;
    private double TAX_PERCENTAGE = 0.0;

    private String orderId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_return_order, container, false);
        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;


        formScroll = (ScrollView) rootView.findViewById(R.id.formScroll);

        mAmountLayout = (RelativeLayout) rootView.findViewById(R.id.amountLayout);
        mNetAmountLayout = (RelativeLayout) rootView.findViewById(R.id.netAmountLayout);
      //  mPointsLayout = (RelativeLayout) rootView.findViewById(R.id.pointsLayout);

        mMembershipIDTV = (TextView) rootView.findViewById(R.id.membershipIdTV);
        mOrderIdTV = (TextView) rootView.findViewById(R.id.orderIdTV);
        mOrderDateTV = (TextView) rootView.findViewById(R.id.orderDateTV);
        mCustomerNameTV = (TextView) rootView.findViewById(R.id.customerNameTV);

        mAmountTV = (TextView) rootView.findViewById(R.id.amountTV);
        mProductDetailsTV = (TextView) rootView.findViewById(R.id.productDetailsTV);
        mReceiptNoTV = (TextView) rootView.findViewById(R.id.receiptNoTV);

        mQuantityTV = (TextView) rootView.findViewById(R.id.quantityTV);
        mTotalPriceTV = (TextView) rootView.findViewById(R.id.totalPriceTV);
        mReturnAmountTV = (TextView) rootView.findViewById(R.id.returnAmountTV);
        mReturnAmountValueTV = (TextView) rootView.findViewById(R.id.returnAmountValueTV);
        mDiscountAmountTV = (TextView) rootView.findViewById(R.id.discountAmountTV);
        mDiscountAmountValueTV = (TextView) rootView.findViewById(R.id.discountAmountValueTV);
        mcouponAmountTV = (TextView) rootView.findViewById(R.id.couponAmountTV);
        mcouponAmountValueTV = (TextView) rootView.findViewById(R.id.couponAmountValueTV);
        mredeemAmountTV = (TextView) rootView.findViewById(R.id.redeemAmountTV);
        mredeemAmountValueTV = (TextView) rootView.findViewById(R.id.redeemAmountValueTV);
        mtaxAmountTV = (TextView) rootView.findViewById(R.id.taxAmountTV);
        mtaxAmountValueTV = (TextView) rootView.findViewById(R.id.taxAmountValueTV);
        mTotalReturnAMountTV = (TextView) rootView.findViewById(R.id.totalReturnAmountTV);
        mTotalReturnAMountValeTV = (TextView) rootView.findViewById(R.id.totalReturnAmountValueTV);
        mAmountAfterDiscountTV = (TextView) rootView.findViewById(R.id.amountAfterDiscountTV);
        mAmountAfterDiscountValueTV = (TextView) rootView.findViewById(R.id.amountAfterDiscountValueTV);
        mAmountBeforeGstTV = (TextView) rootView.findViewById(R.id.amountBeforeGSTTV);
        mAmountBeforeGstValueTV = (TextView) rootView.findViewById(R.id.amountBeforeGSTValueTV);

     //   mDiscountPercentageTV = (TextView) rootView.findViewById(R.id.discountPercentageTV);
        //mDiscountPercentageValueTV = (TextView) rootView.findViewById(R.id.discountPercentageValueTV);

       // mDiscountPercentageValueTV = (TextView) rootView.findViewById(R.id.discountPercentageValueTV);

        mMemberShipIDET = (TextView) rootView.findViewById(R.id.membershipIdET);
        mCustomerNameET = (TextView) rootView.findViewById(R.id.customerNameET);
        mOrderDateET = (TextView) rootView.findViewById(R.id.orderDateET);
        mOrderIdET = (TextView) rootView.findViewById(R.id.orderIdET);

        mConfirmReturnButton = (Button) rootView.findViewById(R.id.confirmReturnButton);
        mPrintButton = (Button) rootView.findViewById(R.id.printButton);

        mPrintButton.setVisibility(View.INVISIBLE);

        mOrderListView = (ListView) rootView.findViewById(R.id.orderListView);
        listSepartor = rootView.findViewById(R.id.listSeparator);
        listSepartor1 = rootView.findViewById(R.id.listSeparator1);

        setDimensionsToViews();


        currency = mSavePreferences.getCurrencyName();

        wrapper = EventBus.getDefault().removeStickyEvent(OrderWrapper.OrderInnerWrapper.class);

        if (wrapper != null) {
            receiptNo = wrapper.getReceiptNo();
            mOrderIdET.setText(wrapper.getId());
            if (wrapper.getMembershipId() != null)
                mMemberShipIDET.setText("" + wrapper.getMembershipId());
            else
                mMemberShipIDET.setText("NA");

            if (!TextUtils.isEmpty(wrapper.getCustomerName()))
                mCustomerNameET.setText("" + wrapper.getCustomerName());
            else
                mCustomerNameET.setText("NA");

            mOrderDateET.setText(wrapper.getCreatedDate());
           // mDiscountPercentageValueTV.setText(wrapper.getDiscountPercentage());
        //    mDiscountAmountTV.setText("Discount Amount ("+wrapper.getDiscountPercentage()+"%)");
        //    mtaxAmountTV.setText("Tax Amount ("+wrapper.getTaxPercentage()+"%)");

           // int a = wrapper.getGrossAmount()

          //  mtaxAmountValueTV.setText(wrapper.getTaxAmount());
           /* if(wrapper.getRedeemedPoints().equals("0")){
                mredeemAmountTV.setText("Redemption Amount");
            }
            else {
                mredeemAmountTV.setText("Redemption Amount ("+wrapper.getRedeemedPoints()+ " Points)");
            }*/
           // mredeemAmountValueTV.setText(wrapper.getRedeemedPoints());mcouponAmountTV.setText("Coupon Amount ("+wrapper.getCouponCode()+")");
          //  mcouponAmountValueTV.setText(wrapper.getCouponDiscountAmount()+"");

            mDiscountPercentage = Double.valueOf(wrapper.getDiscountPercentage());
            mTaxAmount = Double.valueOf(wrapper.getTaxAmount());

            setAmountValues();

            productList = wrapper.getProductDetails();
            mOrderAdapter = new ReturnOrderListAdapter(this, productList,receiptNo, this);
            mOrderListView.setAdapter(mOrderAdapter);

            if (wrapper.getCouponDiscountAmount() == null)
                mCouponCodeAmount = 0.00;
            else
                mCouponCodeAmount = Double.valueOf(Util.priceFormat(wrapper.getCouponDiscountAmount().toString()));

            mRedeemedpointAmount = Double.valueOf(wrapper.getRedeemedPoints()) /* * Double.valueOf(mSavePreferences.getEarnedAmount())*/;

            RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
            mOrderListViewParam.leftMargin = (int) (deviceWidth * 0.02f);
            mOrderListViewParam.rightMargin = (int) (deviceWidth * 0.01f);

            if (mOrderAdapter.getCount() > 3) {
                View item = mOrderAdapter.getView(0, null, mOrderListView);
                item.measure(0, 0);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (3.1 * item.getMeasuredHeight()));
//            mOrderListView.setLayoutParams(params);

                mOrderListViewParam.height = 3 * item.getMeasuredHeight();
            }
            mOrderListView.setLayoutParams(mOrderListViewParam);
        }

        if(TextUtils.isEmpty(new SavePreferences(getActivity()).getMasterPrinterIp()))
            Crouton.makeText(getActivity(), "Please set master printer name first from settings", Style.ALERT).show();

        return rootView;
    }

    private void setAmountValues() {

        mReturnAmountValueTV.setText("0.00"/*Util.priceFormat(Double.parseDouble(getArguments().get("gross_amount").toString()))*/);
        mDiscountAmountValueTV.setText("0.00"/*Util.priceFormat(Double.parseDouble(getArguments().get("discount_amount").toString()))*/);
        mTotalReturnAMountValeTV.setText("0.00"/*Util.priceFormat(Double.parseDouble(getArguments().get("net_amount").toString()))*/);
    }

    private void setDimensionsToViews() {

        int editTextHeight = (int) (deviceHeight * 0.08);
        int buttonHeight = (int) (deviceHeight * 0.08);
        int textViewHeight = (int) (deviceHeight * 0.05);
        int textViewTopMargin = (int) (deviceHeight * 0.012f);
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
        // mOrderListViewParam.height= (int) (deviceHeight * 0.35f);
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
       // mAmountLayoutParams.rightMargin = (int) (deviceWidth * 0.03f);
        mAmountLayout.setLayoutParams(mAmountLayoutParams);
        mAmountLayout.setPadding((int) (deviceWidth * 0.01f), (int) (deviceHeight * 0.02f), (int) (deviceWidth * 0.02f), (int) (deviceHeight * 0.02f));

        RelativeLayout.LayoutParams mNetAmountLayoutParams = (RelativeLayout.LayoutParams) mNetAmountLayout.getLayoutParams();
        mNetAmountLayoutParams.width = (int) (deviceWidth * 0.38f);
        //  mAmountLayoutParams.rightMargin = (int) (deviceWidth * 0.03f);
        mNetAmountLayout.setLayoutParams(mNetAmountLayoutParams);
        mNetAmountLayout.setPadding((int) (deviceWidth * 0.01f), 0,0,(int) (deviceWidth * 0.01f));

      //  mPointsLayout.setPadding((int) (deviceWidth * 0.01f), (int) (deviceHeight * 0.01f), (int) (deviceWidth * 0.012f), (int) (deviceHeight * 0.02f));

      //  RelativeLayout.LayoutParams mDiscountPercentageTVParam = (RelativeLayout.LayoutParams) mDiscountPercentageTV.getLayoutParams();
        //mDiscountPercentageTVParam.topMargin = textViewTopMargin;
       // mDiscountPercentageTV.setLayoutParams(mDiscountPercentageTVParam);

      //  RelativeLayout.LayoutParams mDiscountPercentageValueTVParam = (RelativeLayout.LayoutParams) mDiscountPercentageValueTV.getLayoutParams();
        //mDiscountPercentageValueTVParam.topMargin = textViewTopMargin;
      //  mDiscountPercentageValueTVParam.leftMargin = (int) (deviceWidth * 0.02f);

      //  mDiscountPercentageValueTV.setLayoutParams(mDiscountPercentageValueTVParam);

        RelativeLayout.LayoutParams mConfirmReturnButtonParam = (RelativeLayout.LayoutParams) mConfirmReturnButton.getLayoutParams();
        mConfirmReturnButtonParam.height = buttonHeight;
        mConfirmReturnButtonParam.width = (int) (deviceWidth * 0.18f);
        mConfirmReturnButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mConfirmReturnButtonParam.bottomMargin = (int) (deviceHeight * 0.03f);
        mConfirmReturnButtonParam.rightMargin = (int) (deviceWidth * 0.02f);
        mConfirmReturnButtonParam.leftMargin = textViewLeftMargin;
        mConfirmReturnButton.setLayoutParams(mConfirmReturnButtonParam);
        mConfirmReturnButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mPrintButtonParam = (RelativeLayout.LayoutParams) mPrintButton.getLayoutParams();
        mPrintButtonParam.height = buttonHeight;
        mPrintButtonParam.width = (int) (deviceWidth * 0.1f);
        mPrintButtonParam.topMargin = (int) (deviceHeight * 0.02f);
        mPrintButtonParam.bottomMargin = (int) (deviceHeight * 0.03f);
        mPrintButtonParam.rightMargin = (int) (deviceWidth * 0.01f);
        mPrintButtonParam.leftMargin = textViewLeftMargin;
        mPrintButton.setLayoutParams(mPrintButtonParam);
        mPrintButton.setPadding((int) (deviceWidth * 0.01f), 0, (int) (deviceWidth * 0.01f), 0);

        RelativeLayout.LayoutParams mGrossAmountTVParam = (RelativeLayout.LayoutParams) mReturnAmountTV.getLayoutParams();
       // mGrossAmountTVParam.topMargin = (int) (deviceHeight * 0.02f);
        //mCompleteButtonParam.leftMargin = (int) (deviceWidth * 0.008f);
        mReturnAmountTV.setLayoutParams(mGrossAmountTVParam);

        RelativeLayout.LayoutParams mReturnAmountValueTVParam = (RelativeLayout.LayoutParams) mReturnAmountValueTV.getLayoutParams();
        //mGrossAmountTVParam.topMargin = (int) (deviceHeight * 0.02f);
        mReturnAmountValueTVParam.leftMargin = 2 * textViewLeftMargin;
       // mReturnAmountValueTVParam.topMargin = textViewTopMargin;
        mReturnAmountValueTV.setLayoutParams(mReturnAmountValueTVParam);

        RelativeLayout.LayoutParams mDiscountAmountTVParam = (RelativeLayout.LayoutParams) mDiscountAmountTV.getLayoutParams();
        mDiscountAmountTVParam.topMargin = textViewTopMargin;
        mDiscountAmountTV.setLayoutParams(mDiscountAmountTVParam);

        RelativeLayout.LayoutParams mDiscountAmountValueTVParam = (RelativeLayout.LayoutParams) mDiscountAmountValueTV.getLayoutParams();
        mDiscountAmountValueTVParam.topMargin = textViewTopMargin;
        mDiscountAmountValueTVParam.leftMargin = 2 * textViewLeftMargin;
        mDiscountAmountValueTV.setLayoutParams(mDiscountAmountValueTVParam);

        RelativeLayout.LayoutParams mCouponAmountTVParam = (RelativeLayout.LayoutParams) mcouponAmountTV.getLayoutParams();
        mCouponAmountTVParam.topMargin = textViewTopMargin;
        mcouponAmountTV.setLayoutParams(mCouponAmountTVParam);

        RelativeLayout.LayoutParams mCouponAmountValueTVParam = (RelativeLayout.LayoutParams) mcouponAmountValueTV.getLayoutParams();
        mCouponAmountValueTVParam.topMargin = textViewTopMargin;
        mCouponAmountValueTVParam.leftMargin = 2 * textViewLeftMargin;
        mcouponAmountValueTV.setLayoutParams(mCouponAmountValueTVParam);


        RelativeLayout.LayoutParams mRedeemAmountTVParam = (RelativeLayout.LayoutParams) mredeemAmountTV.getLayoutParams();
        mRedeemAmountTVParam.topMargin = textViewTopMargin;
        mredeemAmountTV.setLayoutParams(mRedeemAmountTVParam);

        RelativeLayout.LayoutParams mRedeemAmountValueTVParam = (RelativeLayout.LayoutParams) mredeemAmountValueTV.getLayoutParams();
        mRedeemAmountValueTVParam.topMargin = textViewTopMargin;
        mRedeemAmountValueTVParam.leftMargin = 2 * textViewLeftMargin;
        mredeemAmountValueTV.setLayoutParams(mRedeemAmountValueTVParam);

        RelativeLayout.LayoutParams mTaxAmountTVParam = (RelativeLayout.LayoutParams) mtaxAmountTV.getLayoutParams();
        mTaxAmountTVParam.topMargin = textViewTopMargin;
        mtaxAmountTV.setLayoutParams(mTaxAmountTVParam);

        RelativeLayout.LayoutParams mTaxAmountValueTVParam = (RelativeLayout.LayoutParams) mtaxAmountValueTV.getLayoutParams();
        mTaxAmountValueTVParam.topMargin = textViewTopMargin;
        mTaxAmountValueTVParam.leftMargin = 2 * textViewLeftMargin;
        mtaxAmountValueTV.setLayoutParams(mTaxAmountValueTVParam);

        RelativeLayout.LayoutParams mAmountBeforeGstTVLayoutParams = (RelativeLayout.LayoutParams) mAmountBeforeGstTV.getLayoutParams();
        mAmountBeforeGstTVLayoutParams.topMargin = textViewTopMargin;
        mAmountBeforeGstTV.setLayoutParams(mAmountBeforeGstTVLayoutParams);

        RelativeLayout.LayoutParams mAmountBeforeGstValueTVLayoutParams = (RelativeLayout.LayoutParams) mAmountBeforeGstValueTV.getLayoutParams();
        mAmountBeforeGstValueTVLayoutParams.topMargin = textViewTopMargin;
        mAmountBeforeGstValueTVLayoutParams.leftMargin = 2 * textViewLeftMargin;
        mAmountBeforeGstValueTV.setLayoutParams(mAmountBeforeGstValueTVLayoutParams);

        RelativeLayout.LayoutParams mAmountAfterDiscountTVLayoutParams = (RelativeLayout.LayoutParams) mAmountAfterDiscountTV.getLayoutParams();
        mAmountAfterDiscountTVLayoutParams.topMargin = textViewTopMargin;
        mAmountAfterDiscountTV.setLayoutParams(mAmountAfterDiscountTVLayoutParams);

        RelativeLayout.LayoutParams mAmountAfterDiscountValueTVLayoutParams = (RelativeLayout.LayoutParams) mAmountAfterDiscountValueTV.getLayoutParams();
        mAmountAfterDiscountValueTVLayoutParams.topMargin = textViewTopMargin;
        mAmountAfterDiscountValueTVLayoutParams.leftMargin = 2 * textViewLeftMargin;
        mAmountAfterDiscountValueTV.setLayoutParams(mAmountAfterDiscountValueTVLayoutParams);

        RelativeLayout.LayoutParams mTotalReturnAMountTVParam = (RelativeLayout.LayoutParams) mTotalReturnAMountTV.getLayoutParams();
       // mTotalReturnAMountTVParam.topMargin = textViewTopMargin;
        mTotalReturnAMountTV.setLayoutParams(mTotalReturnAMountTVParam);

        RelativeLayout.LayoutParams mTotalReturnAMountValeTVParam = (RelativeLayout.LayoutParams) mTotalReturnAMountValeTV.getLayoutParams();
       // mTotalReturnAMountValeTVParam.topMargin = textViewTopMargin;
        mTotalReturnAMountValeTVParam.leftMargin = textViewLeftMargin;
        mTotalReturnAMountValeTVParam.rightMargin = (int) (deviceWidth * 0.02f);
        mTotalReturnAMountValeTV.setLayoutParams(mTotalReturnAMountValeTVParam);


        /*RelativeLayout.LayoutParams mProductDetailsTVParam = (RelativeLayout.LayoutParams) mProductDetailsTV.getLayoutParams();
        //mProductDetailsTVParam.topMargin =  (int)(deviceHeight * 0.01f);
        mProductDetailsTVParam.leftMargin = (int) (deviceWidth * 0.04f);
        mProductDetailsTVParam.width = (int) (deviceWidth * .54f);
        mProductDetailsTV.setLayoutParams(mProductDetailsTVParam);

        RelativeLayout.LayoutParams mAmountTVParam = (RelativeLayout.LayoutParams) mAmountTV.getLayoutParams();
        mAmountTVParam.leftMargin = (int) (deviceWidth * 0.6f);
        mAmountTVParam.topMargin = (int) (deviceHeight * 0.01f);
        mAmountTVParam.width = (int) (deviceWidth * .1f);
        mAmountTV.setLayoutParams(mAmountTVParam);*/

        SavePreferences pref = new SavePreferences(getActivity());
        String currency = pref.getCurrencyName();
        mAmountTV.setText("Amount" + "\n" + "("+currency+")");

      /*  RelativeLayout.LayoutParams mTotalPriceTVParam = (RelativeLayout.LayoutParams) mTotalPriceTV.getLayoutParams();
        mTotalPriceTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        mTotalPriceTVParam.width = (int) (deviceWidth * .1f);
        mTotalPriceTV.setLayoutParams(mTotalPriceTVParam);*/

        mTotalPriceTV.setText("Total" + "\n" + "("+currency+")");

       /* RelativeLayout.LayoutParams mQuantityTVParam = (RelativeLayout.LayoutParams) mQuantityTV.getLayoutParams();
        mQuantityTVParam.leftMargin = (int) (deviceWidth * 0.02f);
        mQuantityTVParam.width = (int) (deviceWidth * .1f);
        mQuantityTV.setLayoutParams(mQuantityTVParam);*/


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

        mConfirmReturnButton.setOnClickListener(this);
        mPrintButton.setOnClickListener(this);

    }

    private HashMap<String, Object> valueKey;
    private ArrayList<String> list;

    void backClick() {
        EventBus.getDefault().postSticky(wrapper);
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("OrderDetailFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("OrderDetailFragment", "nothing on backstack, calling super");
//        }
        if(getActivity() != null)
            getActivity().onBackPressed();

    }

    public void finishOrderDetail() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("OrderDetailFragment", "popping backstack");
            fm.popBackStack();
            fm.popBackStack();
        } else {
            Log.i("OrderDetailFragment", "nothing on backstack, calling super");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().postSticky(wrapper);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
//            case R.id.saveButton: {
//                saveCLick(view);
//                break;
//            }
            case R.id.confirmReturnButton: {
                if(totalReturnAmount>0) {
                 if (isPrinterOk)
                        returnOrder();
                    else
                        Crouton.makeText(getActivity(), "Please fix printer error first!", Style.ALERT).show();
                    break;
                }
                else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Invalid Return",
                            "Product cannot be returned.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            }
           /* case R.id.printButton: {
                printText(getSampleText());
                break;
            }*/

        }
    }

    private void returnOrder() {

        try {

            String product_id, quantity;
            List<String> productIds = new ArrayList<String>();
            List<String> quantityIds = new ArrayList<String>();

            for (int i = 0; i < mOrderAdapter.getCount(); i++) {
                if (mOrderAdapter.getItem(i).isReturned()) {
                    productIds.add(mOrderAdapter.getItem(i).getId());
                    int totalQuantity = Integer.parseInt(mOrderAdapter.getItem(i).getQty());
                    int returnQuantity = 0;

                    if (Validation.isValidData(mOrderAdapter.getItem(i).getReturnQty()))
                        returnQuantity = Integer.parseInt(mOrderAdapter.getItem(i).getReturnQty());

                    quantityIds.add("" + (totalQuantity - returnQuantity));

                }
            }
            product_id = StringUtils.join(productIds, ',');
            quantity = StringUtils.join(quantityIds, ',');

            if (Validation.isValidString(product_id)) {
                HashMap<String, Object> map = new HashMap<String, Object>();

                map.put("product_id", product_id);
                map.put("product_qty", quantity);
                map.put("order_id", mOrderIdET.getText().toString());

                OrderWrapper.returnOrder(mOrderIdET.getText().toString(), map, getActivity(), this);

            } else {
                new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
                        "Please select at least one item.", "OK", android.R.drawable.ic_dialog_alert, null);
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ReturnOrderFragment.class.getName(), ex);
        }
    }

    public void printAndOpenCashDrawer(String id) {
        orderId = id;
            peripheralManager.openCashDrawer();
        printText();
    }


    private void printText() {
        if (peripheralManager.getPrinter() != null) {
            printReceiptText();
            peripheralManager.getPrinter().clearCommandBuffer();
        } else
            Toast.makeText(getActivity(), "Please connect to printer first", Toast.LENGTH_LONG).show();
    }

    private synchronized void printReceiptText() {

        if(Util.printerDetailsList != null && Util.printerDetailsList.size() != 0){
            //Master Printer
            PrinterDetails details = Util.printerDetailsList.get(0);
            if(details.getIsEnabled().equals("1"))
                buildReceipt(peripheralManager.getPrinter(),details.getPrinterName());

            //Slave Printers
            for(int i=1;i<Util.printerDetailsList.size();i++)
            {
                PrinterDetails printerDetails = Util.printerDetailsList.get(i);
                if(printerDetails.getIsEnabled().equals("1")){
                    if(MultiplePrintManager.getMultiplePrinterManager().getPrinterList().size() != 0){
                        MultiplePrintManager.PrinterBuilder printerBuilder = MultiplePrintManager.getMultiplePrinterManager().getPrinterList().get(i - 1);
                        if(printerBuilder.getPrinterId().equals(printerDetails.getPrinterName()))
                            buildReceipt(printerBuilder.getPrinter(),printerBuilder.getPrinterId());
                    }
                }
            }

        }
    }

    private void buildReceipt(Printer printer, String printerId)
    {
        ReceiptBuilder builder = new ReceiptBuilder.Builder(printer, UiController.context)
                .setName(mSavePreferences.getReceiptName())
                .setHeader1(mSavePreferences.getReceiptHeader1())
                .setHeader2(mSavePreferences.getReceiptHeader2())
                .setHeader3(mSavePreferences.getReceiptHeader3())
                .setHeader4(mSavePreferences.getReceiptWebsite())
                .setUserName(wrapper.getCustomerName())
                .setProductList(getProductListPrintText())
                .setDiscountPercentage(Double.parseDouble(wrapper.getDiscountPercentage()))
                .setCouponCode(wrapper.getCouponCode())
                .setRedeemedPoints(Integer.parseInt(wrapper.getRedeemedPoints()))
                .setDiscountAmount(Double.parseDouble(wrapper.getDiscountAmount()))
                .setGrossAmount(Double.parseDouble(wrapper.getGrossAmount()))
                .setCouponAmount(mCouponCodeAmount)
                .setRedeemedAmount(mRedeemedpointAmount)
                .setTaxAmount(mTaxAmount)
                .setTaxPercentage(Double.parseDouble(wrapper.getTaxPercentage()))
                .setTotalAmount(Double.parseDouble(wrapper.getFinalAmount()))
                .setPrintFooterMessage(mSavePreferences.getReceiptMessage())
                .setPrintBarcode(orderId)
                .setIfReturnOrder(true)
                .build();
    }


    private StringBuffer getProductListPrintText()
    {
        StringBuffer orderItems = new StringBuffer();
        //String orderHeader =  "<text>"+String.format("%-26s","Item Description")+"&#9;"+String.format("%-3s","Qty")+"&#9;"+ String.format("%-7s","Price")+"&#9;"+String.format("%-7s","Amt")+"&#10;</text>";
        String orderHeader =  String.format("%-24s",getString(R.string.text_item_description))+" "+String.format("%-12s",getString(R.string.text_receipt_no))+String.format("%-3s",getString(R.string.text_qty))+" "+ String.format("%-12s",getString(R.string.text_price))+" "+String.format("%-12s",getString(R.string.amount))+"\n";
        orderItems.append(orderHeader);
        //String seperator = "<text>--------------------------------------------------------&#10;</text>\n";
        String seperator = getString(R.string.receipt_separator)+"\n";
        orderItems.append(seperator);

        int i = 0;
        for (OrderWrapper.ProductDetail wrapper : productList) {

            if(wrapper.isReturned()){
                //orderId = wrapper.getId();
                String productName = wrapper.getName();

                if(productName.length() > 24){
                    productName = productName.substring(0,20) + "...";
                }
                double totalPrice = (Integer.parseInt(wrapper.getQty()) - Integer.parseInt(wrapper.getReturnQty())) * Double.parseDouble(wrapper.getProductPrice());
                productName = String.format("%-24s", productName);
                receiptNo = String.format("%-12s",receiptNo);
                String quantity = String.format("%-3s",String.valueOf(wrapper.getQty()));
                String sellingPrice = String.format("%-12s",Util.priceFormat(String.valueOf(wrapper.getProductPrice())));
                String total = String.format("%-12s",Util.priceFormat(String.valueOf(totalPrice)));
                String items = productName+" "+quantity+" "+sellingPrice+" "+total+"\n";
                orderItems.append(items);
            }

        }

        return orderItems;
    }


    /*private String getOrderId()
    {
        int noOfZeros = 11 - orderId.length();
        StringBuilder orderIdBuilder =  new StringBuilder(orderId);
        for(int k= 0;k<noOfZeros;k++)
            orderIdBuilder.append("0");

        return orderIdBuilder.toString();
    }*/


    @Override
    public void onValueChanged(double value, int countCheck) {
        double taxPercentage = Double.valueOf(wrapper.getTaxPercentage());

        mReturnAmount = mReturnAmount + value;

        double gstAmount = 0.0;
        double amountBeforeGST = 0.0;
        double discountAmount = 0.0;
        double finalDiscountAmount = 0.0;
        double amountAfterDiscount = 0.0;
        double amountIncludesGST = 0.0;
        double gstInclusive = 0.0;
        gstAmount = Util.roundUpToTwoDecimal(mReturnAmount - mReturnAmount/(1 + taxPercentage / 100));
        amountBeforeGST = Util.roundUpToTwoDecimal(mReturnAmount - gstAmount);
        discountAmount = Util.roundUpToTwoDecimal((amountBeforeGST * Double.parseDouble(wrapper.getDiscountPercentage())) / 100);
        finalDiscountAmount = Util.roundUpToTwoDecimal(discountAmount + mCouponCodeAmount + mRedeemedpointAmount);
        if(finalDiscountAmount > 0) {
            amountAfterDiscount = Util.roundUpToTwoDecimal(amountBeforeGST - finalDiscountAmount);
            gstInclusive = Util.roundUpToTwoDecimal((amountAfterDiscount * Double.parseDouble(wrapper.getTaxPercentage()))/100);
        }else{
            amountAfterDiscount = amountBeforeGST - finalDiscountAmount;
            gstInclusive = gstAmount;

        }

        amountIncludesGST = Util.roundUpToTwoDecimal(amountAfterDiscount + gstInclusive);

        if((mReturnAmount==0.00)||(mReturnAmount==0.0)|| (mReturnAmount==0)){
            mCouponCodeAmount=0.00;
            mRedeemedpointAmount=0.00;
            mDiscountAmountTV.setText("Discount Amount");
            mcouponAmountTV.setText("Coupon Amount");
            mredeemAmountTV.setText("Redemption Amount");
            mtaxAmountTV.setText("GST inclusive");
        }
        else {
            mRedeemedpointAmount = Double.valueOf(wrapper.getRedeemedPoints());
            mCouponCodeAmount = Double.valueOf(Util.priceFormat(wrapper.getCouponDiscountAmount().toString()));
            mDiscountAmountTV.setText("Discount Amount ("+Util.priceFormat(wrapper.getDiscountPercentage())+"%)");
            if(wrapper.getCouponCode().equals("")|| wrapper.getCouponCode().equals(null)){
                mcouponAmountTV.setText("Coupon Amount");
            }
            else {
                mcouponAmountTV.setText("Coupon Amount ("+wrapper.getCouponCode()+")");
            }
            if(wrapper.getRedeemedPoints().equals("0")){
                mredeemAmountTV.setText("Redemption Amount");
            }
            else {
                mredeemAmountTV.setText("Redemption Amount ("+wrapper.getRedeemedPoints()+" Points)");
            }
            mtaxAmountTV.setText("GST inclusive ("+Util.priceFormat(wrapper.getTaxPercentage())+"%)");
        }

       // double totalDiscountAmount = (mReturnAmount * mDiscountPercentage) / 100;
        double totalDiscountAmount = Double.parseDouble(wrapper.getDiscountAmount());
        //mTaxAmount = ((mReturnAmount - totalDiscountAmount - mRedeemedpointAmount - mCouponCodeAmount) * taxPercentage) / 100;
        mTaxAmount = ((mReturnAmount) * taxPercentage) / 100;
        totalReturnAmount = (mReturnAmount - totalDiscountAmount - mCouponCodeAmount - mRedeemedpointAmount) + mTaxAmount;

        mReturnAmountValueTV.setText(Util.priceFormat(mReturnAmount));
        if(totalDiscountAmount == 0 || totalDiscountAmount == 0.0 || totalDiscountAmount == 0.00){
            mDiscountAmountValueTV.setText(Util.priceFormat(discountAmount));
        }
        else {
            mDiscountAmountValueTV.setText("-"+Util.priceFormat(discountAmount));
        }
        mTotalReturnAMountValeTV.setText(UiController.sCurrency + " " + Util.priceFormat(amountIncludesGST));
        mAmountAfterDiscountValueTV.setText(Util.priceFormat(amountAfterDiscount));
        mAmountBeforeGstValueTV.setText(Util.priceFormat(amountBeforeGST));

        if((mReturnAmount==0.00)||(mReturnAmount==0.0)|| (mReturnAmount==0)){
            mtaxAmountValueTV.setText(Util.priceFormat(gstInclusive));
            mredeemAmountValueTV.setText(Util.priceFormat(mRedeemedpointAmount));
            mcouponAmountValueTV.setText(Util.priceFormat(mCouponCodeAmount));
        }
        else{
            mtaxAmountValueTV.setText("+"+Util.priceFormat(gstInclusive));

            if(wrapper.getRedeemedPoints().equals("0")) {
                mredeemAmountValueTV.setText(Util.priceFormat(mRedeemedpointAmount));
            }
            else {
                mredeemAmountValueTV.setText("-"+Util.priceFormat(mRedeemedpointAmount));
            }

            if(wrapper.getCouponCode().equals("") || wrapper.getCouponCode().equals(null)){
                mcouponAmountValueTV.setText(Util.priceFormat(mCouponCodeAmount));
            }
            else {
                mcouponAmountValueTV.setText("-"+Util.priceFormat(mCouponCodeAmount));
            }
        }




       // mReturnAmountValueTV.setText(Util.priceFormat(mCouponCodeAmount));

        DISCOUNT_AMOUNT = totalDiscountAmount;
        NET_AMOUNT = totalReturnAmount;

        if (countCheck == 0)
            setAmountValues();
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
                                isPrinterOk = false;
                                checkPrinter();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });
                    }
                    else
                        isPrinterOk = true;

                }
            });
        }
    }

    private void checkPrinter()
    {
        try {
            peripheralManager.getPrinter().addTextAlign(Printer.ALIGN_CENTER);
            peripheralManager.getPrinter().sendData();
            peripheralManager.getPrinter().clearCommandBuffer();
        }catch (Exception ex)
        {
            RetailPosLoging.getInstance().registerLog(ReturnOrderFragment.class.getName(), ex);
        }
    }
}



