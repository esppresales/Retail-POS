package com.printer.epos.rtpl.reports;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.printer.epos.rtpl.BaseFragment;
import com.printer.epos.rtpl.DaySalesReportBuilder;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.DeviceType;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Fragment class to generate day wise sales report.
 * @author Ranosys Technologies.
 */
public class DaySalesFragment extends BaseFragment implements View.OnClickListener {


    private EditText mDate;
    private View rootView;
    private String mDateStr;
    private List<DaySalesData.DaySalesReport> mDaySalesReportList;
    private SavePreferences mSavePreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_day_sales_report, container, false);
        mDate = (EditText) rootView.findViewById(R.id.date);
        Button mGetReport = (Button) rootView.findViewById(R.id.getReport);

        mGetReport.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mSavePreferences = UiController.getInstance().getSavePreferences();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Home homeActivity = (Home) getActivity();
        homeActivity.setTitleText(getString(R.string.title_day_sales_report));
        homeActivity.backButton.setOnClickListener(this);
        homeActivity.setEnabledButtons(false, true, false, false);

        peripheralManager.connectDevice(DeviceType.PRINTER,
                new SavePreferences(getActivity()).getMasterPrinterIp());

        if (TextUtils.isEmpty(new SavePreferences(getActivity()).getMasterPrinterIp()))
            Crouton.makeText(getActivity(), "Please set master printer name first from settings", Style.ALERT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.getReport:
                if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.container))) {
                    mDateStr = mDate.getText().toString().trim();
                    getDaySalesReport();
                }
                break;

            case R.id.date:
                Util.datePickerDialog(getActivity(), mDate, 0);
                break;
        }
    }

    private String getUrl() {
        String url = UiController.appUrl + "reports?" + "date=" + mDateStr;
        return url;
    }

    public void getDaySalesReport() {
        new DaySalesWrapper() {
            @Override
            protected void onDaySalesDataReceived(DaySalesWrapper wrapper) {
                // super.onDaySalesDataReceived(wrapper);
                mDaySalesReportList = wrapper.getData().getDaySalesReport();

                if(mDaySalesReportList.size() > 0){
                    printReport(wrapper);
                }else{
                    Toast.makeText(getActivity(),"No data available", Toast.LENGTH_LONG).show();
                }
            }
        }.getDaySalesData(getActivity(), getUrl());
    }

    private void printReport(DaySalesWrapper wrapper){
         if (peripheralManager.getPrinter() != null) {
            printReceiptText(wrapper);
            peripheralManager.getPrinter().clearCommandBuffer();
        } else {
            Crouton.makeText(getActivity(), "Please fix printer error first.", Style.ALERT).show();
        }
    }

    private void printReceiptText(DaySalesWrapper wrapper) {
        DaySalesReportBuilder builder = new DaySalesReportBuilder.Builder(peripheralManager.getPrinter(), UiController.context,wrapper)
                .setName(mSavePreferences.getReceiptName())
                .setHeader1(mSavePreferences.getReceiptHeader1())
                .setHeader2(mSavePreferences.getReceiptHeader2())
                .setHeader3(mSavePreferences.getReceiptHeader3())
                .setHeader4(mSavePreferences.getReceiptWebsite())
                .setProductList(getProductListPrintText())
                .setDate(Util.changeDateFormatWithTime(mDateStr))
                .setTaxPercentage(Double.parseDouble(mSavePreferences.getTaxPercentage())).
                        build();
    }

    private StringBuffer getProductListPrintText()
    {
        StringBuffer orderItems = new StringBuffer();
        String orderHeader =  String.format("%-17s",getString(R.string.text_item_description))+" "+  String.format("%-8s",getString(R.string.text_rcpt_no))+" "+String.format("%-4s",getString(R.string.text_qty))+" "+ String.format("%-8s",getString(R.string.text_price))+" "+ String.format("%-8s",getString(R.string.amount))+" "+String.format("%-6s",getString(R.string.text_disc));
        orderItems.append(orderHeader);
        String seperator = getString(R.string.receipt_separator)+"\n";
        orderItems.append(seperator);
        Log.i("order",""+orderHeader);

        String items = null;
        for (DaySalesData.DaySalesReport wrapper : mDaySalesReportList) {
            for(int k = 0; k < wrapper.getOrders().size(); k++){
                String productName = wrapper.getName();
                if(productName.length() > 17){
                    productName = productName.substring(0,13) + "...";
                }
                productName = String.format("%-17s",productName);
                String receiptNo = String.format("%-8s",wrapper.getOrders().get(k).getReceiptNo());
                String quantity = String.format("%-3s",String.valueOf(wrapper.getOrders().get(k).getOrderQty()));
                String discount = String.format("%-7s",Util.priceFormat(String.valueOf(wrapper.getOrders().get(k).getDiscountPrice())));
                String price = String.format("%-8s",Util.priceFormat(String.valueOf(wrapper.getOrders().get(k).getSellingPrice())));
                String totalPrice = String.format("%-8s",Util.priceFormat(String.valueOf(wrapper.getOrders().get(k).getTotalPrice())));
                if(k == 0){
                    items = productName+" "+receiptNo+"  "+quantity+" " +price+" "+totalPrice+" "+ discount;
                }else{
                    String dummyProductName = "";
                     dummyProductName = String.format("%-17s",dummyProductName);

                    items = dummyProductName+ receiptNo +"  "+quantity+" "+price+" "+totalPrice+ " "+discount;
                }
                orderItems.append(items);

            }
            orderItems.append("\n");
        }
       //orderItems.append(seperator);
        return orderItems;
    }

}

