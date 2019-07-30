package com.printer.epos.rtpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.epson.eposdevice.EposException;
import com.epson.eposdevice.printer.Printer;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.PeripheralManager;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by android-pc3 on 19/5/15.
 */
public class ReceiptBuilder {

    private final String name;
    private final String header1;
    private final String header2;
    private final String header3;
    private final String header4;
    private final String userName;
    private final String message;
    private final String barcode;
    private final String logoImage;
    private final String couponCode;
    private final String couponCodeImage;
    private final StringBuffer productList;
    private final double discountPercentage;
    private final double discountAmount;
    private final double grossAmount;
    private final double couponAmount;
    private final int redeemPoints;
    private final double redeemedAmount;
    private final double taxAmount;
    private final double taxPercentage;
    private final double totalAmount;
    private final String printerId;
    private static PeripheralManager peripheralManager;
    private static Printer networkPrinter;
    private static Context context;
    private Socket connection = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private Document doc = null;
    private static final int PORT = 8009;
    private String reqXml;
    private boolean isReturnOrder;
    private String paymentType;
    private double cashDue;
    public static String receiptNo;

    public static class Builder {
        private String name;
        private String header1;
        private String header2;
        private String header3;
        private String header4;
        private String header5;
        private String userName;
        private String printerId;
        private StringBuffer productList;
        private double discountPercentage;
        private double discountAmount;
        private double grossAmount;
        private String couponCode;
        private double couponAmount;
        private double redeemedAmount;
        private double taxPercentage;
        private double taxAmount;
        private double totalAmount;
        private String message;
        private String barcode;
        private String logoImage;
        private String couponCodeImage;
        private boolean isReturnOrder;
        private int redeemPoints;
        private String paymentType;
        private double cashDue;
        // private String receiptNo;


        /* public Builder(PeripheralManager manager,Context mContext) {
             peripheralManager = manager;
             context = mContext;
         }*/
        public Builder(Printer printer,Context mContext) {
            networkPrinter = printer;
            context = mContext;
        }

        //builder methods for setting property
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

//        public Builder setReceiptNo(String receiptNo) {
//            this.receiptNo = receiptNo;
//            return this;
//        }

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
        public Builder setHeader5(String header5) {
            this.header5 = header5;
            return this;
        }


        public Builder setUserName(String username) {
            this.userName = username;
            return this;
        }

        public Builder setProductList(StringBuffer productList) {
            this.productList = productList;
            return this;
        }

        public Builder setGrossAmount(double grossAmount) {
            this.grossAmount = grossAmount;
            return this;
        }

        public Builder setDiscountPercentage(double discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public Builder setDiscountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public Builder setRedeemedPoints(int redeemPoints) {
            this.redeemPoints = redeemPoints;
            return this;
        }

        public Builder setRedeemedAmount(double redeemedAmount) {
            this.redeemedAmount = redeemedAmount;
            return this;
        }

        public Builder setCouponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public Builder setCouponAmount(double couponAmount) {
            this.couponAmount = couponAmount;
            return this;
        }

        public Builder setTaxAmount(double taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public Builder setTaxPercentage(double taxPercentage) {
            this.taxPercentage = taxPercentage;
            return this;
        }

        public Builder setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder setPaymentType(String paymentType) {
            this.paymentType = paymentType;
            return this;
        }
        public Builder setCashDue(double change) {
            this.cashDue = change;
            return this;
        }

        public Builder setPrinterId(String printerId) {
            this.printerId = printerId;
            return this;
        }

        public Builder setPrintFooterMessage(String message) {
            this.message = message;
            return this;
        }
        public Builder setPrintLogo(String image) {
            this.logoImage = image;
            return this;
        }
        public Builder setPrintCouponLogo(String image) {
            this.couponCodeImage = image;
            return this;
        }

        public Builder setPrintBarcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public Builder setIfReturnOrder(boolean isReturnOrder) {
            this.isReturnOrder = isReturnOrder;
            return this;
        }


        public ReceiptBuilder build() {
            return new ReceiptBuilder(this);
        }

        public String toString(){
            String receiptData = "header1 :" +header1 + " " +"name : "+ name +"header2"+ header2+
                    "header3 : "+header3+
                    "header4 : "+header4+
                    "header5 : "+header5+
                    "userName"+userName+
                    "printerId :"+printerId+
                    "productList : "+productList+
                    "discountPercentage : "+discountPercentage+
                    "discountAmount : " +discountAmount+
                    "grossAmount : " +grossAmount+
                    "couponCode : "+couponCode+
                    "couponAmount :"+couponAmount+
                    "redeemedAmount :"+ redeemedAmount+
                    "taxPercentage :"+taxPercentage+
                    "taxAmount : " + taxAmount+
                    "totalAmount : "+totalAmount+
                    "message : "+message+
                    "barcode : "+barcode+
                    "logoImage : "+logoImage+
                    "couponCodeImage : "+ couponCodeImage+
                    "isReturnOrder : "+isReturnOrder+
                    "redeemPoints : "+redeemPoints+
                    "paymentType : "+paymentType+
                    "cashDue : " + cashDue;
            return receiptData.toString();
        }
    }

    //private constructor to enforce object creation through builder
    private ReceiptBuilder(Builder builder) {
        this.name = builder.name;
        this.header1 = builder.header1;
        this.header2 = builder.header2;
        this.header3 = builder.header3;
        this.header4 = builder.header4;
        this.userName = builder.userName;
        this.productList = builder.productList;
        this.discountPercentage = builder.discountPercentage;
        this.discountAmount = builder.discountAmount;
        this.grossAmount = builder.grossAmount;
        this.couponCode = builder.couponCode;
        this.couponAmount = builder.couponAmount;
        this.redeemPoints = builder.redeemPoints;
        this.redeemedAmount = builder.redeemedAmount;
        this.taxAmount = builder.taxAmount;
        this.totalAmount = builder.totalAmount;
        this.taxPercentage = builder.taxPercentage;
        this.printerId = builder.printerId;
        this.message = builder.message;
        this.barcode = builder.barcode;
        this.logoImage = builder.logoImage;
        this.couponCodeImage = builder.couponCodeImage;
        this.isReturnOrder = builder.isReturnOrder;
        this.paymentType = builder.paymentType;
        this.cashDue = builder.cashDue;

        if(networkPrinter != null){
              makeReceipt(networkPrinter);
        }

    }

    private void makeReceipt(Printer printer) {

        try {

  /*                  String xml = "<epos-print xmlns=\"http://www.epson-pos.com/schemas/2011/03/epos-print\">\n" +
                    "<text align=\"center\"/>\n" +
                    "<image width="+ width +"height="+height+">"+logoImage+"</image>\n" +
                    "<text font=\"font_b\"/>\n" +
                    "<text>&#10;"+name+"&#10;"+header1+"&#10;"+header2+"&#10;"+header3+"&#10;"+header4+"&#10;</text>\n" +
                    "<text align=\"center\"/>\n" +
                    "<text>--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"left\"/>\n" +
                    "<text>Customer Name: "+userName+"&#9;&#9;&#9;&#9;&#10;</text>\n" +
                    "<text>"+Util.getCurrentTimeStamp()+"&#9;&#9;&#9;&#9;&#10;</text>\n" +
                    "<text align=\"left\"/>\n" +productList+
                    "<text>--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"right\"/>\n" +
                    "<text>Discount Amt: "+discountAmount+"&#9;&#10;</text>\n" +
                    "<text>Coupon Amt: "+couponAmount+"&#9;&#10;</text>\n" +
                    "<text>Redemption Amt: "+redeemedAmount+"&#9;&#10;</text>\n" +
                    "<text>Tax Amt: "+taxAmount+"&#9;&#10;</text>\n" +
                    "<text>Total Amt: "+totalAmount+"&#9;&#10;</text>\n" +
                    "<text>--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"center\"/>\n" +
                    "<barcode type=\"upc_a\" width=\"2\" height=\"64\" hri=\"none\">"+barcode+"</\n" +
                            "barcode>\n"+
                    "<text>&#10;--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"center\"/>\n" +
                    "<text>"+message+"&#9;&#10;</text>\n" +
                    "<cut type=\"feed\"/>"+
                    "</epos-print>";

            reqXml = "<device_data>"
                    + "<sequence>100</sequence>"
                    + "<device_id>"
                    + printerId
                    + "</device_id>"
                    + "<data>"
                    + "<type>print</type>"
                    + "<timeout>10000</timeout>"
                    + "<printdata>"
                    + xml + "</printdata>" + "</data>"
                    + "</device_data>" + "\0";

            openConnection();*/
            //openConnection();
            printer.clearCommandBuffer();
            printer.addTextAlign(Printer.ALIGN_CENTER);

            printLogoImage(printer);

            printer.addTextFont(Printer.FONT_B);

            if(isReturnOrder)
            {
                printer.addFeed();
                printer.addText("Returned Order");
            }

            // set all print headers
            setPrintHeader(printer);
            //set all order items including other details like orderId,customer name, date etc.
            setOrderItems(printer);
            //set all order amounts like discount,tax,coupon etc.
            setPrintAmountItems(printer);
            //set print footer text
            printFooterText(printer);
            //set barcode
            printer.addTextAlign(Printer.ALIGN_CENTER);
            if(barcode.contains("OR")) {
                printer.addBarcode(barcode.substring(2), Printer.BARCODE_UPC_A, Printer.HRI_BELOW, Printer.FONT_A, 3, 80);
            }else{
                printer.addBarcode(barcode, Printer.BARCODE_UPC_A, Printer.HRI_BELOW, Printer.FONT_A, 3, 80);
            }
            //printer.addBarcode(barcode, Printer.BARCODE_UPC_A, Printer.HRI_BELOW, Printer.FONT_A, 3, 80);
            printer.addFeed();

            printer.addCut(Printer.CUT_FEED);
            printer.sendData();
            printer.clearCommandBuffer();
            //set coupon code image
            printCouponCodeImage(printer);

        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ReceiptBuilder.class.getName(), e);
        }

           /* String xml = "<epos-print xmlns=\"http://www.epson-pos.com/schemas/2011/03/epos-print\">\n" +
                    "<text align=\"center\"/>\n" +
                    "<image>"+logoImage+"</image>\n" +
                    "<text font=\"font_b\"/>\n" +
                    "<text>&#10;"+name+"&#10;"+header1+"&#10;"+header2+"&#10;"+header3+"&#10;"+header4+"&#10;</text>\n" +
                    "<text align=\"center\"/>\n" +
                    "<text>--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"left\"/>\n" +
                    "<text>Customer Name: "+userName+"&#9;&#9;&#9;&#9;&#10;</text>\n" +
                    "<text>"+Util.getCurrentTimeStamp()+"&#9;&#9;&#9;&#9;&#10;</text>\n" +
                    "<text align=\"left\"/>\n" +productList+
                    "<text>--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"right\"/>\n" +
                    "<text>Discount Amt: "+discountAmount+"&#9;&#10;</text>\n" +
                    "<text>Coupon Amt: "+couponAmount+"&#9;&#10;</text>\n" +
                    "<text>Redemption Amt: "+redeemedAmount+"&#9;&#10;</text>\n" +
                    "<text>Tax Amt: "+taxAmount+"&#9;&#10;</text>\n" +
                    "<text>Total Amt: "+totalAmount+"&#9;&#10;</text>\n" +
                    "<text>--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"center\"/>\n" +
                    "<barcode type=\"upc_a\">"+barcode+"</\n" +
                    "barcode>\n" +
                    "<text>&#10;--------------------------------------------------------&#10;</text>\n" +
                    "<text align=\"center\"/>\n" +
                    "<text>"+message+"&#9;&#10;</text>\n" +
                    "<cut type=\"feed\"/>"+
                    "</epos-print>";

            reqXml = "<device_data>"
                    + "<sequence>100</sequence>"
                    + "<device_id>"
                    + printerId
                    + "</device_id>"
                    + "<data>"
                    + "<type>print</type>"
                    + "<timeout>10000</timeout>"
                    + "<printdata>"
                    + xml + "</printdata>" + "</data>"
                    + "</device_data>" + "\0";
*/


        //openConnection();
    }

    private void checkPrinter(Printer printer)
    {
        try {
            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.sendData();
            printer.clearCommandBuffer();
        } catch (EposException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ReceiptBuilder.class.getName(), e);
        }

    }

    private void openConnection()
    {
        new AsyncTask<String, Void, String>() {
            boolean flag = false;
            @Override
            protected String doInBackground(String... strings) {
                try {
                    InetSocketAddress serverAddress = new InetSocketAddress("192.168.10.168"/*new SavePreferences(context).get_ip()*/, PORT);
                    connection = new Socket();
                    connection.connect(serverAddress, 5000);
                    reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(
                            connection.getOutputStream()));

                    connection.setSoTimeout(5000);

                    // Recieve reply message from server
                    int chr;
                    StringBuffer buffer = new StringBuffer();
                    while ((chr = reader.read()) != 0) {
                        buffer.append((char) chr);
                    }

                    // Parse recieved xml document(DOM)
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder();
                    doc = builder.parse(new ByteArrayInputStream(buffer.toString()
                            .getBytes("UTF-8")));
                    String firstNode = doc.getFirstChild().getNodeName();
                    if(firstNode.equalsIgnoreCase("connect"))
                        flag = true;
                    openPrinter();
                } catch (Exception ex)
                {
                    RetailPosLoging.getInstance().registerLog(ReceiptBuilder.class.getName(), ex);
                    flag = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(flag)
                {
                    try {
                        writer.write(reqXml);
                        writer.flush();
                        //TODO : Remove in future
                        peripheralManager.getPrinter().addTextAlign(Printer.ALIGN_CENTER);
                        peripheralManager.getPrinter().addImage(Util.decodeBase64(couponCodeImage),0,0,16,16,Printer.COLOR_1,Printer.MODE_MONO);
                        peripheralManager.getPrinter().sendData();
                        peripheralManager.getPrinter().clearCommandBuffer();

                    } catch (Exception e) {
                        e.printStackTrace();
                        RetailPosLoging.getInstance().registerLog(ReceiptBuilder.class.getName(), e);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Send open_device message to use printer.
     */
    private void openPrinter() {

        String req = "<open_device>" + "<device_id>" + new SavePreferences(context).getMasterPrinterIp()
                + "</device_id>" + "<data>" + "<type>type_printer</type>"
                + "</data>" + "</open_device>" + "\0";
        try {
            writer.write(req);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ReceiptBuilder.class.getName(), e);
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
    }

    private void setOrderItems(Printer printer) throws EposException {

        printer.addFeed();
        printer.addTextAlign(Printer.ALIGN_CENTER);
        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.TRUE, Printer.COLOR_1);
        printer.addText(context.getString(R.string.text_official_receipt));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.FALSE, Printer.COLOR_1);
        printer.addText(!TextUtils.isEmpty(receiptNo)?"Receipt No.: " + receiptNo : "Receipt No.: ");
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_CENTER);
        printer.addText(context.getString(R.string.receipt_separator));
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_LEFT);
        printer.addText("Order Number: " + barcode);
        printer.addFeed();
        printer.addTextAlign(Printer.ALIGN_LEFT);

        printer.addText("Customer Name: " + userName);
        printer.addFeed();

        printer.addText(Util.getCurrentTimeStamp());
        printer.addFeed();
        printer.addFeed();

        printer.addTextAlign(Printer.ALIGN_LEFT);
        printer.addText(productList.toString());
        printer.addFeed();
    }

    private void setPrintAmountItems(Printer printer) throws EposException {
        double gstAmount = 0.0;
        double amountBeforeGST = 0.0;
        double discountAmount = 0.0;
        double totalDiscountAmount = 0.0;
        double amountAfterDiscount = 0.0;
        double amountIncludesGST = 0.0;
        double gstInclusive = 0.0;
        gstAmount = Util.roundUpToTwoDecimal(grossAmount - grossAmount/(1 + taxPercentage / 100));
        amountBeforeGST =Util.roundUpToTwoDecimal(grossAmount - gstAmount);
        discountAmount = Util.roundUpToTwoDecimal((amountBeforeGST * discountPercentage) / 100);
        totalDiscountAmount = Util.roundUpToTwoDecimal(discountAmount + couponAmount + redeemedAmount);
        if(totalDiscountAmount > 0) {
            amountAfterDiscount = Util.roundUpToTwoDecimal(amountBeforeGST - totalDiscountAmount);
            gstInclusive = Util.roundUpToTwoDecimal((amountAfterDiscount * taxPercentage)/100);
        }else{
            amountAfterDiscount = amountBeforeGST - totalDiscountAmount;
            gstInclusive = gstAmount;
        }
        amountIncludesGST = Util.roundUpToTwoDecimal(amountAfterDiscount + gstInclusive);

        printer.addText(context.getString(R.string.receipt_separator));
        printer.addFeed();
        printer.addTextAlign(Printer.ALIGN_RIGHT);
        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.TRUE, Printer.COLOR_1);
        printer.addText(context.getString(R.string.text_subTotal)+" " + Util.priceFormat(grossAmount));
        printer.addFeed();
        printer.addFeed();

        printer.addText(context.getString(R.string.text_amount_beforeGst) +" "+ Util.priceFormat(amountBeforeGST));
        if(discountPercentage != 0 && discountPercentage != 0.0 && discountPercentage != 0.00){
            printer.addFeed();
            printer.addText(context.getString(R.string.text_discount)+Util.priceFormatTax(discountPercentage)+context.getString(R.string.symbol_percentage)+ " "+ Util.priceFormat(discountAmount));
        }

        if(couponAmount != 0 && couponAmount != 0.0 && couponAmount != 0.00){
            printer.addFeed();
            printer.addText(context.getString(R.string.text_coupon_amount)+ " "+ Util.priceFormat(couponAmount));
        }

        if(redeemedAmount != 0 && redeemedAmount != 0.0 && redeemedAmount != 0.00){
            printer.addFeed();
            printer.addText(context.getString(R.string.text_redeemption_points)+redeemPoints+context.getString(R.string.text_points) +" "+ Util.priceFormat(redeemedAmount));
        }

        if(discountPercentage > 0 || couponAmount > 0 || redeemedAmount > 0) {
            printer.addFeed();
            printer.addText(context.getString(R.string.text_amount_afterDiscount) +" "+ Util.priceFormatTax(amountAfterDiscount));
        }

        printer.addFeed();
        printer.addText(Util.priceFormatTax(taxPercentage)+context.getString(R.string.text_gstInclusive)+ " "+ Util.priceFormatTax(gstInclusive));

        printer.addFeed();
        printer.addText(context.getString(R.string.text_amount_includes_gst) + " "+UiController.sCurrency+" "+ Util.priceFormat(amountIncludesGST));

        printer.addFeed();
        printer.addFeed();
        if(!isReturnOrder) {
            if(paymentType.equalsIgnoreCase("CASH")){
                printer.addText((!TextUtils.isEmpty(paymentType) ? paymentType.toUpperCase() + ": " + Util.priceFormat(amountIncludesGST + cashDue) : ""));
            }else {
                printer.addText((!TextUtils.isEmpty(paymentType) ? paymentType.toUpperCase() + ": " + Util.priceFormat(amountIncludesGST) : ""));
            }
        }

        printer.addFeed();
        if(!TextUtils.isEmpty(paymentType) && paymentType.equalsIgnoreCase("CASH")) {
            printer.addText(context.getString(R.string.text_change) +" "+ Util.priceFormat(cashDue));
        }

        printer.addTextStyle(Printer.FALSE,Printer.FALSE,Printer.FALSE, Printer.COLOR_1);
        printer.addFeed();
        printer.addText(context.getString(R.string.receipt_separator));
        printer.addFeed();
    }

    private void printCouponCodeImage(Printer printer) throws EposException {
        if(new SavePreferences(context).getReceiptCouponFlag() != 0)
        {
            Bitmap couponBitmap = Util.getCouponBitmap(context);
            if(couponBitmap != null){
                printer.addTextAlign(Printer.ALIGN_CENTER);
                printer.addImage(couponBitmap, 0, 0, couponBitmap.getWidth(), couponBitmap.getHeight(), Printer.PARAM_DEFAULT, Printer.MODE_MONO);
                printer.addCut(Printer.CUT_FEED);
                printer.sendData();
                printer.clearCommandBuffer();
            }
        }
    }

    private void printLogoImage(Printer printer) throws EposException{
        if(new SavePreferences(context).getReceiptLogoFlag() != 0){
            Bitmap bitmap = /*BitmapFactory.decodeResource(context.getResources(), R.drawable.black_white_logo_designs_graphic_design_templates_002);*/Util.getPrintLogoBitmap(context);
            if(bitmap != null)
                printer.addImage(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), Printer.PARAM_DEFAULT, Printer.MODE_MONO);
        }
    }

    private void printFooterText(Printer printer)throws EposException
    {
        if(new SavePreferences(context).getReceiptCouponFlag() != 0)
        {
            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addText(message);
            printer.addFeed();
            printer.addText(context.getString(R.string.receipt_separator));
            printer.addFeed();
        }

    }
}
