package com.printer.epos.rtpl;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.epson.eposdevice.EposCallbackCode;
import com.epson.eposdevice.printer.Printer;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.adapter.AddOrderListAdapter;
import com.printer.epos.rtpl.adapter.ProductListAdapter;
import com.printer.epos.rtpl.wrapper.DeviceType;
import com.printer.epos.rtpl.wrapper.MultiplePrintManager;
import com.printer.epos.rtpl.wrapper.OrderWrapper;
import com.printer.epos.rtpl.wrapper.ProductOrderWrapper;
import com.printer.epos.rtpl.wrapper.ProductWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewOrderFragment extends BaseFragment implements View.OnClickListener {


    private View rootView;
    private static List<ProductWrapper> productListForPopup;
    public static List<ProductOrderWrapper> orderList;

    protected static ProductListAdapter mProductAdapter;
    protected static AddOrderListAdapter mOrderAdapter;
    protected static ListView mOrderListView;
    protected static final EventBus bus = EventBus.getDefault();
    private static AddOrderItemFragment orderItemFragment;
    private static PlaceOrderFragment placeOrderFragment;
    private static int scanCount = 0;

    protected static int REDEEMED_POINTS = 0;
    protected static String COUPON_CODE = "";
    protected static int EARNED_LOYALTY_POINTS = 0;
    protected static double DISCOUNT_PERCENTAGE = 0.0;
    protected static double TAX_PERCENTAGE = 0.0;
    protected static double MINIMUM_SPEND_AMOUNT = 0.0;
    protected static double REDEEMED_POINT_PRICE = 0.0;


    protected static double GROSS_AMOUNT = 0.0;
    protected static double DISCOUNT_AMOUNT = 0.0;
    protected static double COUPON_AMOUNT = 0.0;
    protected static double REDEEMED_AMOUNT = 0.0;
    protected static double TAX_AMOUNT = 0.0;
    protected static double NET_AMOUNT = 0.0;

    protected static SavePreferences mSavePreferences;
    private static CalculationListener listener;

    protected static String customerName;
    protected static String memberId;


    protected static String orderType = "Completed";
    private static final String ARG_ITEM_ID = "item_id";
    private static final String ARG_ORDER_TYPE = "order_type";


    protected static boolean confirmButtonClicked;
    private String orderID;
    protected static String recentlyAddedItemName = "";
    protected static String  recentlyAddedItemPrice="";


    protected static OrderWrapper.OrderInnerWrapper wrapper;
    private static int initCount = 0;

    protected static boolean isPrinterOk = false;




    public NewOrderFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_new_order, container, false);
        setupUI(rootView);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        LinearLayout orderItem = (LinearLayout) rootView.findViewById(R.id.addItem);
        LinearLayout placeOrder = (LinearLayout) rootView.findViewById(R.id.placeOrder);

        RelativeLayout.LayoutParams orderItemParams = (RelativeLayout.LayoutParams) orderItem.getLayoutParams();
        orderItemParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        orderItemParams.width = (int) (deviceWidth * 0.4f);

        orderItem.setLayoutParams(orderItemParams);

        RelativeLayout.LayoutParams placeOrderParams = (RelativeLayout.LayoutParams) placeOrder.getLayoutParams();
        placeOrderParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        orderItemParams.width = (int) (deviceWidth * 0.6f);

        placeOrder.setLayoutParams(placeOrderParams);

        if(savedInstanceState == null){

            orderItemFragment = new AddOrderItemFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.addItem, orderItemFragment)
                    .commit();
            placeOrderFragment = new PlaceOrderFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.placeOrder, placeOrderFragment)
                    .commit();

        }

        mSavePreferences = UiController.getInstance().getSavePreferences();

        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
        ((Home) getActivity()).backButton.setOnClickListener(this);

        if(orderType != null && orderType.equals("OnHold"))
            ((Home) getActivity()).setTitleText("On Hold");
        else
            ((Home) getActivity()).setTitleText(getString(R.string.new_order));

        wrapper = EventBus.getDefault().removeStickyEvent(OrderWrapper.OrderInnerWrapper.class);

        if(TextUtils.isEmpty(new SavePreferences(getActivity()).getMasterPrinterIp()))
            Crouton.makeText(getActivity(),"Please set master printer name first from settings", Style.ALERT).show();

        if(peripheralManager != null)
            peripheralManager.resetDisplay();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        initCount = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof Home){
            ((Home) getActivity()).changeImputMode(true);
        }
        if(initCount == 0){
            MultiplePrintManager.getMultiplePrinterManager().createNetworkPrinters(peripheralManager);
            peripheralManager.connectDevice(DeviceType.PRINTER,
                    new SavePreferences(getActivity()).getMasterPrinterIp());
            peripheralManager.connectDevice(DeviceType.BARCODE_SCANNER,
                    new SavePreferences(getActivity()).getBarcodeDeviceName());
            peripheralManager.connectDevice(DeviceType.MSR,
                    new SavePreferences(getActivity()).getMSRDeviceName());
            peripheralManager.connectDevice(DeviceType.DISPLAY,
                    new SavePreferences(getActivity()).getCustomerDisplayName());

            initCount++;
        }

        //  peripheralManager.displayShopName(mSavePreferences.getReceiptName());
        SavePreferences pref = new SavePreferences(getActivity());
        if(!TextUtils.isEmpty(pref.getDiscountFromDate())&&!TextUtils.isEmpty(pref.getDiscountToDate())) {
            setDiscountTaxPercentage();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
            default:
                break;
        }
    }

    protected void setCalculationListener(CalculationListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onBarcodeScanData(final String data) {
        super.onBarcodeScanData(data);
        if(getActivity() != null)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    orderItemFragment.addProductViaBarcode(data);
                }
            });
        }

    }

    @Override
    public void onMSRScanData(final String data) {
        super.onMSRScanData(data);

        if(getActivity() != null)
        {
            if (scanCount == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MagStripeDataParser reader = new MagStripeDataParser(data);
                        placeOrderFragment.setMsrData(reader.getCardNumber());
                    }
                });

                scanCount++;
            } else
                scanCount = 0;
        }


    }

    public void backClick() {
//        if(getActivity() != null){
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            if (fm.getBackStackEntryCount() > 0) {
//                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                    fm.popBackStack();
//                }
//                Log.i("AddOrderFragment", "popping backstack");
//                //fm.popBackStackImmediate();
//                resetAllValues();
//            } else {
//                Log.i("AddOrderFragment", "nothing on backstack, calling super");
//            }
//        }
        resetAllValues();
        if(getActivity() != null)
            getActivity().onBackPressed();
    }

    public void onTotalProductPriceReceived(double total, boolean flag)
    {
        try {
            if (!flag) {
                GROSS_AMOUNT = total;
            } else {
                GROSS_AMOUNT += total;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), ex);
        }

        calculationSetting();
    }

    private void setDiscountTaxPercentage() {
        //mTaxTV.setText("Tax (" + mSavePreferences.getTaxPercentage() + "%)");

        try {
            TAX_PERCENTAGE = Double.parseDouble(mSavePreferences.getTaxPercentage());
            MINIMUM_SPEND_AMOUNT = Double.parseDouble(mSavePreferences.getDiscountMinSpend());

            REDEEMED_POINT_PRICE = Double.parseDouble(mSavePreferences.getEarnedAmount());
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), ex);
        }

        boolean applyDiscount;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date1 = sdf.parse(mSavePreferences.getDiscountFromDate());
            Date date2 = sdf.parse(sdf.format(new Date()));
            Date date3 = sdf.parse(mSavePreferences.getDiscountToDate());

            if (date1.compareTo(date2) > 0) {
                System.out.println("Date1 is after Date2");
                applyDiscount = false;
                mSavePreferences.storeDiscountPercentage("0.00");
                return;
            } else if (date3.compareTo(date2) < 0) {
                System.out.println("Date3 is before Date2");
                applyDiscount = false;
                mSavePreferences.storeDiscountPercentage("0.00");
                return;
            } else {
                applyDiscount = true;
            }
            if (applyDiscount) {
                try {
                    DISCOUNT_PERCENTAGE = Double.parseDouble(mSavePreferences.getDiscountPercentage());
                    listener.setDiscountAndTaxPercentage();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), ex);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), ex);
        }
    }

    protected void calculationSetting() {

        if (GROSS_AMOUNT != 0) {

            if (mSavePreferences.getDiscountMinRestiction().equals("0")) {
                DISCOUNT_PERCENTAGE = 0;
                DISCOUNT_AMOUNT = (GROSS_AMOUNT * DISCOUNT_PERCENTAGE) / 100;
                listener.setGrossAndDiscountAmountValue();

            } else if (GROSS_AMOUNT >= MINIMUM_SPEND_AMOUNT) {
                DISCOUNT_PERCENTAGE = Double.parseDouble(mSavePreferences.getDiscountPercentage());
                DISCOUNT_AMOUNT = (GROSS_AMOUNT * DISCOUNT_PERCENTAGE) / 100;
                listener.setGrossAndDiscountAmountValue();
            } else {
                DISCOUNT_PERCENTAGE=0.00;
                DISCOUNT_AMOUNT = 0;
                listener.setGrossAndDiscountAmountValue();
            }

            if (COUPON_AMOUNT != 0) {
                if (GROSS_AMOUNT - DISCOUNT_AMOUNT >= COUPON_AMOUNT) {
                    listener.setCouponAndRedeemedAmount("(You got " + UiController.sCurrency + " " + Util.priceFormat(COUPON_AMOUNT) + " off)");
                } else {
                    COUPON_AMOUNT = 0.0;
                    listener.setCouponAndRedeemedAmount("(You got " + UiController.sCurrency + " " + Util.priceFormat(COUPON_AMOUNT) + " off)");
                    COUPON_CODE = "";
                }
            }

            if (REDEEMED_AMOUNT != 0) {
                if (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT >= REDEEMED_AMOUNT) {
                    listener.setCouponAndRedeemedAmount("");
                } else {
                    REDEEMED_AMOUNT = 0.0;
                    listener.setCouponAndRedeemedAmount("");
                }
            }

            TAX_PERCENTAGE = Double.parseDouble(mSavePreferences.getTaxPercentage());

            //TAX_AMOUNT = ((GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT - REDEEMED_AMOUNT) * TAX_PERCENTAGE) / 100;
            TAX_AMOUNT = ((GROSS_AMOUNT ) * TAX_PERCENTAGE) / 100;
            NET_AMOUNT = (GROSS_AMOUNT - DISCOUNT_AMOUNT - COUPON_AMOUNT - REDEEMED_AMOUNT) + TAX_AMOUNT;
            listener.setTaxAndNetAmount();

        } else {
            listener.setDefaultValues();
//            new CustomDialog().showOneButtonAlertDialog(getActivity(), "No Item.",
//                    "Please add at least one item to apply the coupon code.", "OK", android.R.drawable.ic_dialog_alert, null);
        }
    }



    protected List<ProductWrapper> getProductList()
    {
        return productListForPopup;
    }

    protected List<ProductOrderWrapper> getOrderList()
    {
        return orderList;
    }

    public static void setOrderList(List<ProductOrderWrapper> list)
    {
        orderList = new ArrayList<ProductOrderWrapper>(list);
    }

    protected void openFragment(Bundle arguments) {
        if(getActivity() instanceof Home){
            ((Home) getActivity()).changeFragment(FragmentUtils.OrderPreviewFragment, arguments, true, false);
        }
    }
    protected List<ProductOrderWrapper> getOnHoldItems(List<ProductWrapper> data)
    {
        List<ProductOrderWrapper> orderOnHoldList = new ArrayList<>();
        if(wrapper != null)
        {
            if(wrapper.getProductDetails() != null && wrapper.getProductDetails().size() != 0){

                for(int i=0;i<wrapper.getProductDetails().size();i++)
                {
                    ProductOrderWrapper productOrderWrapper = new ProductOrderWrapper();
                    productOrderWrapper.setProductName(wrapper.getProductDetails().get(i).getName());
                   // productOrderWrapper.setReceiptNo(wrapper.getReceiptNo());

                    int quantity = Integer.parseInt(wrapper.getProductDetails().get(i).getQty()) -
                            Integer.parseInt(wrapper.getProductDetails().get(i).getReturnQty());

                    productOrderWrapper.setAddedQuantity(quantity);
                    productOrderWrapper.setSellingPrice(wrapper.getProductDetails().get(i).getProductPrice());
                    double totalPrice = quantity * Double.parseDouble(wrapper.getProductDetails().get(i).getProductPrice());
                    productOrderWrapper.setTotalPrice(totalPrice);
                    productOrderWrapper.setId(wrapper.getProductDetails().get(i).getId());
                    if(data!= null) {
                        for (int j = 0; j < data.size(); j++) {
                            if(wrapper.getProductDetails().get(i).getId().equals(data.get(j).getId())){
                                productOrderWrapper.setQuantity(data.get(j).getQuantity());
                                break;
                            }
                        }
                    }else {
                        productOrderWrapper.setQuantity(wrapper.getProductDetails().get(i).getQty());
                    }

                    orderOnHoldList.add(productOrderWrapper);

                }
            }
        }
        return orderOnHoldList;
    }

    private void printText() {
        if(orderType.equals("OnHold")){
            if (peripheralManager.getPrinter() != null) {
               // RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), "moving to printReceiptText()");
                printReceiptText();
                peripheralManager.getPrinter().clearCommandBuffer();

                if(isPrinterOk)
                    backClick();
            } else {
                Toast.makeText(getActivity(), "Please connect to printer first", Toast.LENGTH_LONG).show();
                backClick();
            }
        }

    }

    private synchronized void printReceiptText() {
      //  RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), "enetered into printReceiptText()");
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
                .setPrinterId(printerId)
                .setGrossAmount(GROSS_AMOUNT)
                .setDiscountPercentage(DISCOUNT_PERCENTAGE)
                .setCouponCode(COUPON_CODE)
                .setRedeemedPoints(REDEEMED_POINTS)
                .setDiscountAmount(Double.parseDouble(Util.priceFormat(DISCOUNT_AMOUNT)))
                .setCouponAmount(Double.parseDouble(Util.priceFormat(COUPON_AMOUNT)))
                .setRedeemedAmount(Double.parseDouble(Util.priceFormat(REDEEMED_AMOUNT)))
                .setTaxAmount(Double.parseDouble(Util.priceFormat(TAX_AMOUNT)))
                .setTaxPercentage(TAX_PERCENTAGE)
                .setTotalAmount(Double.parseDouble(Util.priceFormat(NET_AMOUNT)))
                .setPrintFooterMessage(mSavePreferences.getReceiptMessage())
                .setPrintBarcode(orderID)
                .setPaymentType(wrapper.getPaymentType())
                .setCashDue(Double.parseDouble(wrapper.getCashDue()))
                .build();
    }



    private String getOrderId()
    {
        int noOfZeros = 11 - orderID.length();
        StringBuilder orderIdBuilder =  new StringBuilder(orderID);
        for(int k= 0;k<noOfZeros;k++)
            orderIdBuilder.append("0");

        return orderIdBuilder.toString();
    }


    private StringBuffer getProductListPrintText()
    {
        StringBuffer orderItems = new StringBuffer();
        String orderHeader =  String.format("%-24s",getString(R.string.text_item_description))+" "+String.format("%-5s",getString(R.string.text_qty))+" "+ String.format("%-12s",getString(R.string.text_price))+" "+String.format("%-12s",getString(R.string.text_amount));
        orderItems.append(orderHeader);
        String seperator = getString(R.string.receipt_separator)+"\n";
        orderItems.append(seperator);

        int i = 0;
        for (ProductOrderWrapper wrapper : orderList) {
            //orderID = wrapper.getId();
            String productName = wrapper.getProductName();

            if(productName.length() > 24){
                productName = productName.substring(0,20) + "...";
            }
            //orderId = wrapper.getId();
            productName = String.format("%-24s", productName);
            String quantity = String.format("%-4s",String.valueOf(wrapper.getAddedQuantity()));
            String sellingPrice = String.format("%-12s",Util.priceFormat(String.valueOf(wrapper.getSellingPrice())));
            String totalPrice = String.format("%-12s",Util.priceFormat(String.valueOf(wrapper.getTotalPrice())));
            //String items = "<text>"+productName+"&#9;"+quantity+"&#9;"+sellingPrice+"&#9;"+totalPrice+"&#10;</text>\n";
            String items = productName+" "+" "+quantity+" "+sellingPrice+" "+totalPrice+"\n";
            orderItems.append(items);

        }
        return orderItems;
    }


    public void printReceipt(String id) {
        orderID = id;
       // RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), "moving to printText()");
        printText();
    }

    @Override
    public void onPrinterStatusReceived(final String message, final int code, final int status) {
        super.onPrinterStatusReceived(message, code, status);
        if(getActivity() != null)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(code != EposCallbackCode.SUCCESS){
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Printer Error", message, "RETRY", 0, new DialogButtonListener() {
                            @Override
                            public void onPositiveClick() {
                                //if(confirmButtonClicked)
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



    protected void resetAllValues()
    {
        productListForPopup = null;

        orderList = null;
        confirmButtonClicked = false;

        customerName = "";
        memberId = "";
        orderType = "Completed";
        recentlyAddedItemName = "";
        recentlyAddedItemPrice = "";

        REDEEMED_POINTS = 0;
        COUPON_CODE = "";
        EARNED_LOYALTY_POINTS = 0;
        DISCOUNT_PERCENTAGE = 0.0;
        TAX_PERCENTAGE = 0.0;
        MINIMUM_SPEND_AMOUNT = 0.0;
        REDEEMED_POINT_PRICE = 0.0;

        GROSS_AMOUNT = 0.0;
        DISCOUNT_AMOUNT = 0.0;
        COUPON_AMOUNT = 0.0;
        REDEEMED_AMOUNT = 0.0;
        TAX_AMOUNT = 0.0;
        NET_AMOUNT = 0.0;

        initCount = 0;
        wrapper = null;
        isPrinterOk = false;
    }

    private void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.requestFocus();
    }

    protected void setupUI(final View view) {
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

    private void checkPrinter()
    {
        try {
            peripheralManager.getPrinter().addTextAlign(Printer.ALIGN_CENTER);
            peripheralManager.getPrinter().sendData();
            peripheralManager.getPrinter().clearCommandBuffer();
        }catch (Exception ex)
        {
            RetailPosLoging.getInstance().registerLog(NewOrderFragment.class.getName(), ex);
        }
    }
}