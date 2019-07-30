package com.printer.epos.rtpl.wrapper;

import java.util.List;

/**
 * Created by android-sristi on 17/4/15.
 */
public class OrderPreviewWrapper {

    List<ProductOrderWrapper> orderList;
    private String membershipId;
    private String orderDate;
    private String customerName;
    private double discountPercentage;
    private String couponCode;
    private int redeemPoints;
    private double grossAmount;
    private double discountAmount;
    private double couponAmount;
    private double redeemedAmount;
    private double taxPercentage;
    private double taxAmount;
    private double netAmount;
    private String paymentType;
    private String cashDue;
    private String receiptNo;

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getCashDue() {
        return cashDue;
    }

    public void setCashDue(String cashDue) {
        this.cashDue = cashDue;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<ProductOrderWrapper> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<ProductOrderWrapper> orderList) {
        this.orderList = orderList;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getRedeemPoints() {
        return redeemPoints;
    }

    public void setRedeemPoints(int redeemPoints) {
        this.redeemPoints = redeemPoints;
    }

    public double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public double getRedeemedAmount() {
        return redeemedAmount;
    }

    public void setRedeemedAmount(double redeemedAmount) {
        this.redeemedAmount = redeemedAmount;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }
}
