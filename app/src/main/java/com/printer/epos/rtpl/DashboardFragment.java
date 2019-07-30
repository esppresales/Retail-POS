package com.printer.epos.rtpl;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.services.MinStockAlertNotificationService;
import com.printer.epos.rtpl.wrapper.HomeWrapper;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.printer.epos.rtpl.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.printer.epos.rtpl.ItemDetailActivity}
 * on handsets.
 */
public class DashboardFragment extends BaseFragment implements View.OnClickListener {

    private SavePreferences pref;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new SavePreferences(getActivity());

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    private HomeWrapper.Data mData;

    @Override
    public void onResume() {
        super.onResume();
        //start service for min stock alert notification.
        if(getActivity() != null)
            getActivity().startService(new Intent(getActivity(), MinStockAlertNotificationService.class));

        if (getActivity() instanceof Home) {
            ((Home) getActivity()).setTitleText("Home");
            ((Home) getActivity()).setEnabledButtons(true, false, false, false);
        }
        new HomeWrapper() {
            @Override
            public void getHomeData(Context context, Data mData) {
                DashboardFragment.this.mData = mData;


                if (mData.getTodaysTotal() != null)
                    salesValue.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getTodaysTotal()));
                else
                    salesValue.setText(UiController.sCurrency + " " + " 0.00");

                if (mData.getTodaysTransactions() != null)
                    transactionValue.setText(mData.getTodaysTransactions());
                else
                    transactionValue.setText("0");



                Log.i("dicount detail--->","getTodaysDiscount: "+mData.getTodaysDiscount()+" getDiscountMinRestiction: "
                    + pref.getDiscountMinRestiction().toString());

                if (mData.getTodaysDiscount() != null && mData.getMinSpend() != null) {
                    SavePreferences pref = new SavePreferences(getActivity());
                    if(pref.getDiscountMinRestiction().toString().trim().equals("1")){
                        discountValue.setText(mData.getTodaysDiscount() + "%");
                        discount_hint.setText("Minimum " + UiController.sCurrency + " " + mData.getMinSpend() + " purchase");
                    }else{
                        discountValue.setText("None");
                        discount_hint.setText("None");
                    }
                } else {
                    discountValue.setText("None");
                    discount_hint.setText("None");
                }
                salesAmountVY.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getYesterdaysTotal()));
                returnAmountVY.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getYesterdaysReturn()));
                transactionsVY.setText(mData.getYesterdaysTransactions());

                salesAmountVLW.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getLastWeekTotal()));
                returnAmountVLW.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getLastWeekReturn()));
                transactionsVLW.setText(mData.getLastWeekTransactions());

                if (mData.getLastMonthTotal() != null)
                    salesAmountVLM.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getLastMonthTotal()));
                else
                    salesAmountVLM.setText(UiController.sCurrency + " 0.00");
                if (mData.getLastMonthReturn() != null)
                    returnAmountVLM.setText(UiController.sCurrency + " " + Util.priceFormat(mData.getLastMonthReturn()));
                else
                    returnAmountVLM.setText(UiController.sCurrency + " 0.00");
                transactionsVLM.setText(mData.getLastMonthTransactions());

//                Home.STORE_NAME = mData.getStoreName();
                name.setText(mData.getStoreName());

            }
        }.getHomeWrapper(getActivity());

    }

    private TextView name;
    public TextView date;

    private TextView salesValue;
    private TextView transactionValue;
    private TextView discountValue;
    private TextView discount_hint;
    private TextView salesAmountVY;
    private TextView returnAmountVY;
    private TextView transactionsVY;
    private TextView salesAmountVLW;
    private TextView returnAmountVLW;
    private TextView transactionsVLW;
    private TextView salesAmountVLM;
    private TextView returnAmountVLM;
    private TextView transactionsVLM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        View itemList = inflater.inflate(R.layout.fragment_menu_item_list, container, false);

        name = (TextView) getActivity().findViewById(R.id.name);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        salesValue = (TextView) rootView.findViewById(R.id.sales_value);
        transactionValue = (TextView) rootView.findViewById(R.id.transaction_value);
        discountValue = (TextView) rootView.findViewById(R.id.discount_value);

        salesValue.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .01f), 0);
        transactionValue.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .01f), 0);
        discountValue.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .01f), 0);


        FrameLayout sales = (FrameLayout) rootView.findViewById(R.id.sales);
        RelativeLayout.LayoutParams sales_param = (RelativeLayout.LayoutParams) sales.getLayoutParams();
        sales_param.height = (int) (deviceHeight * .22f);
        sales_param.width = (int) (deviceWidth * .316f);
        sales_param.topMargin = (int) (deviceHeight * .02f);
        sales_param.leftMargin = (int) (deviceWidth * .02f);
        sales_param.rightMargin = (int) (deviceWidth * .02f);
        sales.setLayoutParams(sales_param);

        FrameLayout transaction = (FrameLayout) rootView.findViewById(R.id.transaction);
        RelativeLayout.LayoutParams transaction_param = (RelativeLayout.LayoutParams) transaction.getLayoutParams();
        transaction_param.height = (int) (deviceHeight * .22f);
        transaction_param.width = (int) (deviceWidth * .316f);
        transaction_param.rightMargin = (int) (deviceWidth * .02f);
        transaction_param.topMargin = (int) (deviceHeight * .02f);
        transaction.setLayoutParams(transaction_param);

        FrameLayout discount = (FrameLayout) rootView.findViewById(R.id.discount);
        RelativeLayout.LayoutParams discount_param = (RelativeLayout.LayoutParams) discount.getLayoutParams();
        discount_param.height = (int) (deviceHeight * .22f);
        discount_param.width = (int) (deviceWidth * .317f);
        discount_param.rightMargin = (int) (deviceWidth * .02f);
        discount_param.topMargin = (int) (deviceHeight * .02f);
        discount.setLayoutParams(discount_param);

        TextView sales_heading = (TextView) rootView.findViewById(R.id.sales_heading);
        sales_heading.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), 0, 0);
        TextView transaction_heading = (TextView) rootView.findViewById(R.id.transaction_heading);
        transaction_heading.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), 0, 0);
        TextView discount_heading = (TextView) rootView.findViewById(R.id.discount_heading);
        discount_heading.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), 0, 0);
        discount_hint = (TextView) rootView.findViewById(R.id.discount_hint);
        discount_hint.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

//        SavePreferences pref = new SavePreferences(getActivity());
//        discount_hint.setText("Minimum " + UiController.sCurrency + " " + pref.getDiscountMinSpend() + " purchase");

        RelativeLayout bottomLayout = (RelativeLayout) rootView.findViewById(R.id.bottomLayout);
        RelativeLayout.LayoutParams bottomLayout_param = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
        bottomLayout_param.topMargin = (int) (deviceHeight * .02f);
        bottomLayout_param.leftMargin = (int) (deviceWidth * .02f);
        bottomLayout_param.rightMargin = (int) (deviceWidth * .02f);
        bottomLayout.setLayoutParams(bottomLayout_param);

        RelativeLayout addStaff = (RelativeLayout) rootView.findViewById(R.id.addStaff);
        RelativeLayout.LayoutParams addStaff_param = (RelativeLayout.LayoutParams) addStaff.getLayoutParams();
        addStaff_param.topMargin = (int) (deviceHeight * .05f);
        addStaff_param.bottomMargin = (int) (deviceHeight * .05f);
        addStaff.setLayoutParams(addStaff_param);

        ImageView addStaffImg = (ImageView) rootView.findViewById(R.id.addStaffImg);
        RelativeLayout.LayoutParams addStaffImg_param = (RelativeLayout.LayoutParams) addStaffImg.getLayoutParams();
        addStaffImg_param.height = (int) (deviceHeight * .1f);
        addStaffImg_param.width = (int) (deviceHeight * .1125f);
        addStaffImg.setLayoutParams(addStaffImg_param);

        TextView addStaffText = (TextView) rootView.findViewById(R.id.addStaffText);
        addStaffText.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        RelativeLayout addProduct = (RelativeLayout) rootView.findViewById(R.id.addProduct);
        RelativeLayout.LayoutParams addProduct_param = (RelativeLayout.LayoutParams) addProduct.getLayoutParams();
        addProduct_param.topMargin = (int) (deviceHeight * .05f);
        addProduct_param.bottomMargin = (int) (deviceHeight * .05f);
        addProduct_param.rightMargin = (int) (deviceWidth * .12f);
        addProduct.setLayoutParams(addProduct_param);

        ImageView addProductImg = (ImageView) rootView.findViewById(R.id.addProductImg);
        RelativeLayout.LayoutParams addProductImg_param = (RelativeLayout.LayoutParams) addProductImg.getLayoutParams();
        addProductImg_param.height = (int) (deviceHeight * .1f);
        addProductImg_param.width = (int) (deviceHeight * .1125f);
        addProductImg.setLayoutParams(addProductImg_param);

        TextView addProductText = (TextView) rootView.findViewById(R.id.addProductText);
        addProductText.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        RelativeLayout newOrder = (RelativeLayout) rootView.findViewById(R.id.newOrder);
        RelativeLayout.LayoutParams newOrder_param = (RelativeLayout.LayoutParams) newOrder.getLayoutParams();
        newOrder_param.topMargin = (int) (deviceHeight * .05f);
        newOrder_param.bottomMargin = (int) (deviceHeight * .05f);
        newOrder_param.rightMargin = (int) (deviceWidth * .12f);
        newOrder.setLayoutParams(newOrder_param);

        ImageView newOrderImg = (ImageView) rootView.findViewById(R.id.newOrderImg);
        RelativeLayout.LayoutParams newOrderImg_param = (RelativeLayout.LayoutParams) addProductImg.getLayoutParams();
        newOrderImg_param.height = (int) (deviceHeight * .1f);
        newOrderImg_param.width = (int) (deviceHeight * .1125f);
        newOrderImg.setLayoutParams(newOrderImg_param);

        TextView newOrderText = (TextView) rootView.findViewById(R.id.newOrderText);
        newOrderText.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        RelativeLayout addCustomer = (RelativeLayout) rootView.findViewById(R.id.addCustomer);
        RelativeLayout.LayoutParams addCustomer_param = (RelativeLayout.LayoutParams) addCustomer.getLayoutParams();
        addCustomer_param.topMargin = (int) (deviceHeight * .05f);
        addCustomer_param.bottomMargin = (int) (deviceHeight * .05f);
        addCustomer_param.rightMargin = (int) (deviceWidth * .12f);
        addCustomer.setLayoutParams(addCustomer_param);

        ImageView addCustomerImg = (ImageView) rootView.findViewById(R.id.addCustomerImg);
        RelativeLayout.LayoutParams addCustomerImg_param = (RelativeLayout.LayoutParams) addProductImg.getLayoutParams();
        addCustomerImg_param.height = (int) (deviceHeight * .1f);
        addCustomerImg_param.width = (int) (deviceHeight * .1125f);
        addCustomerImg.setLayoutParams(addCustomerImg_param);

        TextView addCustomerText = (TextView) rootView.findViewById(R.id.addCustomerText);
        addCustomerText.setPadding(0, (int) (deviceHeight * .02f), 0, 0);


        TextView yesterday = (TextView) rootView.findViewById(R.id.yesterday);
        RelativeLayout.LayoutParams yesterday_param = (RelativeLayout.LayoutParams) yesterday.getLayoutParams();
        yesterday_param.width = (int) (deviceWidth * .316f);
        yesterday_param.leftMargin = (int) (deviceWidth * .02f);
        yesterday_param.rightMargin = (int) (deviceWidth * .02f);
        yesterday_param.topMargin = (int) (deviceHeight * .02f);
        yesterday_param.height = (int) (deviceHeight * .08f);
        yesterday.setLayoutParams(yesterday_param);
        yesterday.setPadding((int) (deviceWidth * .02f), 0, 0, 0);

        RelativeLayout yesterdayLayout = (RelativeLayout) rootView.findViewById(R.id.yesterdayLayout);
        RelativeLayout.LayoutParams yesterdayLayout_param = (RelativeLayout.LayoutParams) yesterdayLayout.getLayoutParams();
        yesterdayLayout_param.width = (int) (deviceWidth * .316f);
        yesterdayLayout_param.leftMargin = (int) (deviceWidth * .02f);
        yesterdayLayout_param.rightMargin = (int) (deviceWidth * .02f);
        yesterdayLayout.setLayoutParams(yesterdayLayout_param);

        TextView salesAmountY = (TextView) rootView.findViewById(R.id.salesAmountY);
        salesAmountY.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), 0, (int) (deviceHeight * .01f));

        salesAmountVY = (TextView) rootView.findViewById(R.id.salesAmountVY);
        salesAmountVY.setPadding(0, (int) (deviceHeight * .01f), (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView returnAmountY = (TextView) rootView.findViewById(R.id.returnAmountY);
        returnAmountY.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

        returnAmountVY = (TextView) rootView.findViewById(R.id.returnAmountVY);
        returnAmountVY.setPadding(0, 0, (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView transactionsY = (TextView) rootView.findViewById(R.id.transactionsY);
        transactionsY.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

        transactionsVY = (TextView) rootView.findViewById(R.id.transactionsVY);
        transactionsVY.setPadding(0, 0, (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView lastWeek = (TextView) rootView.findViewById(R.id.lastWeek);
        RelativeLayout.LayoutParams lastWeek_param = (RelativeLayout.LayoutParams) lastWeek.getLayoutParams();
        lastWeek_param.width = (int) (deviceWidth * .316f);
        lastWeek_param.rightMargin = (int) (deviceWidth * .02f);
        lastWeek_param.topMargin = (int) (deviceHeight * .02f);
        lastWeek_param.height = (int) (deviceHeight * .08f);
        lastWeek.setLayoutParams(lastWeek_param);
        lastWeek.setPadding((int) (deviceWidth * .02f), 0, 0, 0);

        RelativeLayout lastWeekLayout = (RelativeLayout) rootView.findViewById(R.id.lastWeekLayout);
        RelativeLayout.LayoutParams lastWeekLayout_param = (RelativeLayout.LayoutParams) lastWeekLayout.getLayoutParams();
        lastWeekLayout_param.width = (int) (deviceWidth * .316f);
        lastWeekLayout_param.rightMargin = (int) (deviceWidth * .02f);
        lastWeekLayout.setLayoutParams(lastWeekLayout_param);

        TextView salesAmountLW = (TextView) rootView.findViewById(R.id.salesAmountLW);
        salesAmountLW.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), 0, (int) (deviceHeight * .01f));

        salesAmountVLW = (TextView) rootView.findViewById(R.id.salesAmountVLW);
        salesAmountVLW.setPadding(0, (int) (deviceHeight * .01f), (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView returnAmountLW = (TextView) rootView.findViewById(R.id.returnAmountLW);
        returnAmountLW.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

        returnAmountVLW = (TextView) rootView.findViewById(R.id.returnAmountVLW);
        returnAmountVLW.setPadding(0, 0, (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView transactionsLW = (TextView) rootView.findViewById(R.id.transactionsLW);
        transactionsLW.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

        transactionsVLW = (TextView) rootView.findViewById(R.id.transactionsVLW);
        transactionsVLW.setPadding(0, 0, (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView lastMonth = (TextView) rootView.findViewById(R.id.lastMonth);
        RelativeLayout.LayoutParams lastMonth_param = (RelativeLayout.LayoutParams) lastMonth.getLayoutParams();
        lastMonth_param.width = (int) (deviceWidth * .316f);
        lastMonth_param.rightMargin = (int) (deviceWidth * .02f);
        lastMonth_param.topMargin = (int) (deviceHeight * .02f);
        lastMonth_param.height = (int) (deviceHeight * .08f);
        lastMonth.setLayoutParams(lastMonth_param);
        lastMonth.setPadding((int) (deviceWidth * .02f), 0, 0, 0);

        RelativeLayout lastMonthLayout = (RelativeLayout) rootView.findViewById(R.id.lastMonthLayout);
        RelativeLayout.LayoutParams lastMonthLayout_param = (RelativeLayout.LayoutParams) lastMonthLayout.getLayoutParams();
        lastMonthLayout_param.width = (int) (deviceWidth * .316f);
        lastMonthLayout_param.rightMargin = (int) (deviceWidth * .02f);
        lastMonthLayout.setLayoutParams(lastMonthLayout_param);

        TextView salesAmountLM = (TextView) rootView.findViewById(R.id.salesAmountLM);
        salesAmountLM.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), 0, (int) (deviceHeight * .01f));

        salesAmountVLM = (TextView) rootView.findViewById(R.id.salesAmountVLM);
        salesAmountVLM.setPadding(0, (int) (deviceHeight * .01f), (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView returnAmountLM = (TextView) rootView.findViewById(R.id.returnAmountLM);
        returnAmountLM.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

        returnAmountVLM = (TextView) rootView.findViewById(R.id.returnAmountVLM);
        returnAmountVLM.setPadding(0, 0, (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        TextView transactionsLM = (TextView) rootView.findViewById(R.id.transactionsLM);
        transactionsLM.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .01f));

        transactionsVLM = (TextView) rootView.findViewById(R.id.transactionsVLM);
        transactionsVLM.setPadding(0, 0, (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));

        addStaff.setOnClickListener(this);
        addProduct.setOnClickListener(this);
        newOrder.setOnClickListener(this);
        addCustomer.setOnClickListener(this);

        if (UiController.mSavePreferences.get_roleId().equals("2")) {

            sales.setVisibility(View.GONE);
            transaction.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
            yesterdayLayout.setVisibility(View.GONE);
            lastWeekLayout.setVisibility(View.GONE);
            lastMonthLayout.setVisibility(View.GONE);

            yesterday.setVisibility(View.GONE);
            lastWeek.setVisibility(View.GONE);
            lastMonth.setVisibility(View.GONE);

            addProduct.setVisibility(View.INVISIBLE);
            addStaff.setVisibility(View.INVISIBLE);

            new HomeWrapper() {
                @Override
                public void getHomeData(Context context, Data mData) {
                    name.setText(mData.getStoreName());
                }
            }.getHomeWrapper(getActivity());

            if(LoginActivity.fromLogin) {
                LoginActivity.fromLogin = false;
//                openOrderFragment();

               /* Intent intent = new Intent(getActivity(), FetchSettingsService.class);
                intent.putExtra("receiverTag", new SettingsReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);
                        if (resultCode == 0)

                    }
                });*/

            }
        }




        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addStaff: {
                Bundle arguments = new Bundle();
                arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "2");
                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.AddStaffFragment, arguments, true, false);
                }

                break;
            }
            case R.id.addProduct: {
                Bundle arguments = new Bundle();
                arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "3");
                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.AddProductFragment, arguments, true, false);
                }
                break;
            }
            case R.id.newOrder: {
                Bundle arguments = new Bundle();
                arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");
                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.NewOrderFragment, arguments, true, false);
                }

                break;
            }
            case R.id.addCustomer: {
                Bundle arguments = new Bundle();
                arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "4");
                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.AddCustomerFragment, arguments, true, false);
                }

                break;
            }
        }
    }

    private void openOrderFragment()
    {
        Bundle arguments = new Bundle();
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");
        if(getActivity() instanceof Home){
            ((Home) getActivity()).changeFragment(FragmentUtils.NewOrderFragment, arguments, true, false);
        }

    }

//    @Override
//    protected void onPrinterConnected() {
//        super.onPrinterConnected();
//        if (!isPrinterCreated) {
//            createDevice(new SavePreferences(getActivity()).getMasterPrinterIp(), Device.DEV_TYPE_PRINTER, Device.FALSE);
//        }
//
//        if (!isScannerCreated) {
//            createDevice(new SavePreferences(getActivity()).getMSRDeviceName(), Device.DEV_TYPE_SCANNER, Device.FALSE);
//            createDevice(new SavePreferences(getActivity()).getBarcodeDeviceName(), Device.DEV_TYPE_SCANNER, Device.FALSE);
//        }
//
//        if (!isDisplayCreated) {
//            createDevice(new SavePreferences(getActivity()).getCustomerDisplayName(), Device.DEV_TYPE_DISPLAY, Device.FALSE);
//        }
//
//    }

//    private void createDevice(String id, int type, int buffer) {
//
//        if (UiController.mDevice.isConnected()) {
//            try {
//                UiController.mDevice.createDevice(id, type, Device.FALSE, buffer, this);
//            } catch (EposException e) {
//                Toast.makeText(getActivity(), "Error in create device: " + e.toString(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}
