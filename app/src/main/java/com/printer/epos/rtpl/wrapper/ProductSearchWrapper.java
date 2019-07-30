package com.printer.epos.rtpl.wrapper;

/**
 * Created by android-sristi on 8/4/15.
 */
public class ProductSearchWrapper {


    public String id;
    public String productName;
    public String barCode;
    public String productPrice;
    public String productTotalAmount;
    public String quantity;
    private boolean isAdded = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProductTotalAmount() {
        return productTotalAmount;
    }

    public void setProductTotalAmount(String productTotalAmount) {
        this.productTotalAmount = productTotalAmount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }



}
