package com.printer.epos.rtpl;

import android.content.Context;

import com.epson.eposdevice.EposException;
import com.epson.eposdevice.printer.Printer;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.reports.DaySalesWrapper;


/**
 * Created by android-pc3 on 19/5/15.
 */
public class DaySalesReportBuilder {

    private final String name;
    private final String header1;
    private final String header2;
    private final String header3;
    private final String header4;
    private String date = "";
    private final String printerId;
    private static Printer networkPrinter;
    private static Context context;
    private double taxPercentage;
    private final StringBuffer productList;
    private static DaySalesWrapper daySalesWrapper;

    public static class Builder {
        private String name;
        private String header1;
        private String header2;
        private String header3;
        private String header4;
        private String date;
        private String printerId;
        private StringBuffer productList;

        private String barcode;
        private double taxPercentage;

        public Builder(Printer printer, Context mContext, DaySalesWrapper wrapper) {
            networkPrinter = printer;
            context = mContext;
            daySalesWrapper = wrapper;
        }
        //builder methods for setting property
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setHeader1(String header1) {
            this.header1 = header1;
            return this;
        }

        public Builder setHeader2(String header2) {
            this.header2 = header2;
            return this;
        }

        public Builder setHeader3(String header3) {
            this.header3 = header3;
            return this;
        }

        public Builder setHeader4(String header4) {
            this.header4 = header4;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setProductList(StringBuffer productList) {
            this.productList = productList;
            return this;
        }


        public DaySalesReportBuilder build() {
            return new DaySalesReportBuilder(this);
        }

        public Builder setTaxPercentage(double taxPercentage) {
            this.taxPercentage = taxPercentage;
            return this;
        }

        public String toString(){
            String receiptData = "header1 :" +header1 + " " +"name : "+ name +"header2"+ header2+
                    "header3 : "+header3+
                    "header4 : "+header4+
                    "printerId :"+printerId+
                    "productList : "+productList+
                    "taxPercentage :"+taxPercentage+
                    "barcode : "+barcode+
                    "date : "+date;
            return receiptData.toString();
        }
    }

    //private constructor to enforce object creation through builder
    private DaySalesReportBuilder(Builder builder) {
        this.name = builder.name;
        this.header1 = builder.header1;
        this.header2 = builder.header2;
        this.header3 = builder.header3;
        this.header4 = builder.header4;
        this.date = builder.date;
        this.productList = builder.productList;
        this.taxPercentage = builder.taxPercentage;
        this.printerId = builder.printerId;
        if(networkPrinter != null) {
            makeReceipt(networkPrinter,daySalesWrapper);
        }
    }

    private void makeReceipt(Printer printer, DaySalesWrapper wrapper) {

        try {
            printer.clearCommandBuffer();
            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextFont(Printer.FONT_B);

            // set all print headers
            setPrintHeader(printer);
            //set all order items including other details like orderId,customer name, date etc.
            setOrderItems(printer);
            //  set all order amounts like discount,tax,coupon etc.
            setPrintAmountItems(printer,wrapper);

            printer.addCut(Printer.CUT_FEED);
            printer.sendData();
            printer.clearCommandBuffer();

        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(DaySalesReportBuilder.class.getName(), e);
        }
    }


    private void setPrintHeader(Printer printer) throws EposException {

        if(!( name.trim().equals("")) || (name.equals(null)) ){
            printer.addFeed();
            printer.addText(name);
        }
        if(!( header1.trim().equals("")) || (header1.equals(null)) ){
            printer.addFeed();
            printer.addText(header1);
        }
        if(!( header2.trim().equals("")) || (header2.equals(null)) ){
            printer.addFeed();
            printer.addText(header2);
        }
        if(!( header3.trim().equals("")) || (header3.equals(null)) ){
            printer.addFeed();
            printer.addText(header3);
        }
        if(!( header4.trim().equals("")) || (header4.equals(null)) ){
            printer.addFeed();
            printer.addText(header4);
        }
        printer.addFeed();
        printer.addFeed();
    }

    private void setOrderItems(Printer printer) throws EposException {

        printer.addTextAlign(Printer.ALIGN_CENTER);
        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.TRUE, Printer.COLOR_1);
        printer.addText(context.getString(R.string.text_day_sales_report));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_CENTER);
        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.FALSE, Printer.COLOR_1);

        printer.addText(context.getString(R.string.receipt_separator));
        printer.addFeed();
        printer.addTextAlign(Printer.ALIGN_LEFT);
        printer.addText(date);
        printer.addFeed();
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_LEFT);
        printer.addText(productList.toString());
        printer.addFeed();
    }

    private void setPrintAmountItems(Printer printer, DaySalesWrapper wrapper) throws EposException {
        float totalAmount = wrapper.getData().getTotalSum();
        printer.addText(context.getString(R.string.receipt_separator));

        printer.addTextAlign(Printer.ALIGN_LEFT);
        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.TRUE, Printer.COLOR_1);
        String totalQty = String.valueOf(wrapper.getData().getTotalQty());

       // printer.addText(String.format("%-28s",context.getString(R.string.text_total_transaction)+" " + wrapper.getData().getTotalTransactions()) + totalQty);
        printer.addText(context.getString(R.string.text_total_transaction)+" " + wrapper.getData().getTotalTransactions());
        printer.addFeed();
        printer.addText(context.getString(R.string.text_total_qty)+" " + String.format("%5s",totalQty));

        printer.addFeed();
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(context.getString(R.string.text_cash_with_colon)+" " + Util.priceFormat(wrapper.getData().getCashTotalSum()));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(context.getString(R.string.text_nets_with_colon)+" " + Util.priceFormat(wrapper.getData().getNetsTotalSum()));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(context.getString(R.string.text_visa_master_with_colon)+" " + Util.priceFormat(wrapper.getData().getCardTotalSum()));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(context.getString(R.string.text_subTotal)+" " + Util.priceFormat(totalAmount));
        printer.addFeed();
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(context.getString(R.string.text_amount_beforeGst)+" " + Util.priceFormat(wrapper.getData().getAmountBeforeGst()));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(Util.priceFormat(taxPercentage) + context.getString(R.string.text_gstInclusive)+" " + Util.priceFormat(wrapper.getData().getGstInclusive()));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addText(context.getString(R.string.text_amount_includes_gst) +" "+ Util.priceFormat(wrapper.getData().getAmountIncludesGst()));
        printer.addFeed();
        printer.addFeed();

        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.FALSE, Printer.COLOR_1);

    }

}
