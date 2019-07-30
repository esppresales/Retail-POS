package com.printer.epos.rtpl.reports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 27/4/15.
 */
public class StockInHandData {

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String barcode;
    @SerializedName("cost_price")
    @Expose
    private String costPrice;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @Expose
    private String quantity;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @Expose
    private String image;
    @Expose
    private String supplier;
    @SerializedName("min_stock_alert_qty")
    @Expose
    private String minStockAlertQty;
    @SerializedName("stock_location")
    @Expose
    private String stockLocation;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("added_by")
    @Expose
    private String addedBy;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * @param barcode The barcode
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * @return The costPrice
     */
    public String getCostPrice() {
        return costPrice;
    }

    /**
     * @param costPrice The cost_price
     */
    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    /**
     * @return The sellingPrice
     */
    public String getSellingPrice() {
        return sellingPrice;
    }

    /**
     * @param sellingPrice The selling_price
     */
    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    /**
     * @return The quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity The quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * @return The categoryId
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId The category_id
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return The image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return The supplier
     */
    public String getSupplier() {
        return supplier;
    }

    /**
     * @param supplier The supplier
     */
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    /**
     * @return The minStockAlertQty
     */
    public String getMinStockAlertQty() {
        return minStockAlertQty;
    }

    /**
     * @param minStockAlertQty The min_stock_alert_qty
     */
    public void setMinStockAlertQty(String minStockAlertQty) {
        this.minStockAlertQty = minStockAlertQty;
    }

    /**
     * @return The stockLocation
     */
    public String getStockLocation() {
        return stockLocation;
    }

    /**
     * @param stockLocation The stock_location
     */
    public void setStockLocation(String stockLocation) {
        this.stockLocation = stockLocation;
    }

    /**
     * @return The createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate The created_date
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return The addedBy
     */
    public String getAddedBy() {
        return addedBy;
    }

    /**
     * @param addedBy The added_by
     */
    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    /**
     * @return The modifiedDate
     */
    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     * @param modifiedDate The modified_date
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
