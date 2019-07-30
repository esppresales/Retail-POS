package com.printer.epos.rtpl.reports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranosys-archana on 19/9/16.
 */
public class DaySalesData {
    @SerializedName("day_sales_report")
    @Expose
    private List<DaySalesReport> daySalesReport = new ArrayList<>();
    @SerializedName("total_transactions")
    @Expose
    private Integer totalTransactions;
    @SerializedName("total_qty")
    @Expose
    private Integer totalQty;
    @SerializedName("total_sum")
    @Expose
    private float totalSum;
    @SerializedName("cash_total_sum")
    @Expose
    private float cashTotalSum;
    @SerializedName("nets_total_sum")
    @Expose
    private float netsTotalSum;
    @SerializedName("card_total_sum")
    @Expose
    private float cardTotalSum;
    @SerializedName("gst_inclusive")
    @Expose
    private float gstInclusive;
    @SerializedName("amount_before_gst")
    @Expose
    private float amountBeforeGst;
    @SerializedName("amount_includes_gst")
    @Expose
    private float amountIncludesGst;



    public float getGstInclusive() {
        return gstInclusive;
    }

    public void setGstInclusive(float gstInclusive) {
        this.gstInclusive = gstInclusive;
    }

    public float getAmountBeforeGst() {
        return amountBeforeGst;
    }

    public void setAmountBeforeGst(float amountBeforeGst) {
        this.amountBeforeGst = amountBeforeGst;
    }

    public float getAmountIncludesGst() {
        return amountIncludesGst;
    }

    public void setAmountIncludesGst(float amountIncludesGst) {
        this.amountIncludesGst = amountIncludesGst;
    }

    /**
     *
     * @return
     *     The daySalesReport
     */
    public List<DaySalesReport> getDaySalesReport() {
        return daySalesReport;
    }

    /**
     *
     * @param daySalesReport
     *     The day_sales_report
     */
    public void setDaySalesReport(List<DaySalesReport> daySalesReport) {
        this.daySalesReport = daySalesReport;
    }

    /**
     *
     * @return
     *     The totalTransactions
     */
    public Integer getTotalTransactions() {
        return totalTransactions;
    }

    /**
     *
     * @param totalTransactions
     *     The total_transactions
     */
    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    /**
     *
     * @return
     *     The totalQty
     */
    public Integer getTotalQty() {
        return totalQty;
    }

    /**
     *
     * @param totalQty
     *     The total_qty
     */
    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    /**
     *
     * @return
     *     The totalSum
     */
    public float getTotalSum() {
        return totalSum;
    }

    /**
     *
     * @param totalSum
     *     The total_sum
     */
    public void setTotalSum(float totalSum) {
        this.totalSum = totalSum;
    }

    /**
     *
     * @return
     *     The cashTotalSum
     */
    public float getCashTotalSum() {
        return cashTotalSum;
    }

    /**
     *
     * @param cashTotalSum
     *     The cash_total_sum
     */
    public void setCashTotalSum(float cashTotalSum) {
        this.cashTotalSum = cashTotalSum;
    }

    /**
     *
     * @return
     *     The netsTotalSum
     */
    public float getNetsTotalSum() {
        return netsTotalSum;
    }

    /**
     *
     * @param netsTotalSum
     *     The nets_total_sum
     */
    public void setNetsTotalSum(float netsTotalSum) {
        this.netsTotalSum = netsTotalSum;
    }

    /**
     *
     * @return
     *     The cardTotalSum
     */
    public float getCardTotalSum() {
        return cardTotalSum;
    }

    /**
     *
     * @param cardTotalSum
     *     The card_total_sum
     */
    public void setCardTotalSum(float cardTotalSum) {
        this.cardTotalSum = cardTotalSum;
    }
    public class Data {

        @SerializedName("day_sales_report")
        @Expose
        private List<DaySalesReport> daySalesReport = new ArrayList<DaySalesReport>();

        /**
         *
         * @return
         *     The daySalesReport
         */
        public List<DaySalesReport> getDaySalesReport() {
            return daySalesReport;
        }

        /**
         *
         * @param daySalesReport
         *     The day_sales_report
         */
        public void setDaySalesReport(List<DaySalesReport> daySalesReport) {
            this.daySalesReport = daySalesReport;
        }

    }
    public class DaySalesReport {

        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("orders")
        @Expose
        private List<Order> orders = new ArrayList<Order>();

        /**
         *
         * @return
         *     The productId
         */
        public String getProductId() {
            return productId;
        }

        /**
         *
         * @param productId
         *     The product_id
         */
        public void setProductId(String productId) {
            this.productId = productId;
        }

        /**
         *
         * @return
         *     The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         *     The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         *     The orders
         */
        public List<Order> getOrders() {
            return orders;
        }

        /**
         *
         * @param orders
         *     The orders
         */
        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

    }

    public class Order {

        @SerializedName("order_id")
        @Expose
        private String orderId;
        @SerializedName("receipt_no")
        @Expose
        private String receiptNo;
        @SerializedName("order_qty")
        @Expose
        private String orderQty;
        @SerializedName("selling_price")
        @Expose
        private String sellingPrice;
        @SerializedName("total_price")
        @Expose
        private String totalPrice;

        @SerializedName("discount_price")
        @Expose
        private String discountPrice;

        /**
         *
         * @return
         *     The orderId
         */
        public String getOrderId() {
            return orderId;
        }

        /**
         *
         * @param orderId
         *     The order_id
         */
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        /**
         *
         * @return
         */
        public String getDiscountPrice() {
            return discountPrice;
        }

        /**
         *
         * @param discountPrice
         *     The order_id
         */
        public void setDiscountPrice(String discountPrice) {
            this.discountPrice = discountPrice;
        }


        /**
         *
         * @return
         *     The receiptNo
         */
        public String getReceiptNo() {
            return receiptNo;
        }

        /**
         *
         * @param receiptNo
         *     The receipt_no
         */
        public void setReceiptNo(String receiptNo) {
            this.receiptNo = receiptNo;
        }

        /**
         *
         * @return
         *     The orderQty
         */
        public String getOrderQty() {
            return orderQty;
        }

        /**
         *
         * @param orderQty
         *     The order_qty
         */
        public void setOrderQty(String orderQty) {
            this.orderQty = orderQty;
        }

        /**
         *
         * @return
         *     The sellingPrice
         */
        public String getSellingPrice() {
            return sellingPrice;
        }

        /**
         *
         * @param sellingPrice
         *     The selling_price
         */
        public void setSellingPrice(String sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        /**
         *
         * @return
         *     The totalPrice
         */
        public String getTotalPrice() {
            return totalPrice;
        }

        /**
         *
         * @param totalPrice
         *     The total_price
         */
        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

    }
}


