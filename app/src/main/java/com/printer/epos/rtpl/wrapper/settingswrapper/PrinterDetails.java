package com.printer.epos.rtpl.wrapper.settingswrapper;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class PrinterDetails {


    @Expose
    private String id;
    @SerializedName("printer_type")
    @Expose
    private String printerType;
    @SerializedName("is_enabled")
    @Expose
    private String isEnabled;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("printer_name")
    @Expose
    private String printerName;

    private boolean isNewItems;

    public boolean isNewItems() {
        return isNewItems;
    }

    public void setIsNewItems(boolean isNewItems) {
        this.isNewItems = isNewItems;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The printerType
     */
    public String getPrinterType() {
        return printerType;
    }

    /**
     *
     * @param printerType
     * The printer_type
     */
    public void setPrinterType(String printerType) {
        this.printerType = printerType;
    }

    /**
     *
     * @return
     * The isEnabled
     */
    public String getIsEnabled() {
        return isEnabled;
    }

    /**
     *
     * @param isEnabled
     * The is_enabled
     */
    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     *
     * @return
     * The createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     *
     * @param createdDate
     * The created_date
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     *
     * @return
     * The modifiedDate
     */
    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     *
     * @param modifiedDate
     * The modified_date
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * @return The printerName
     */
    public String getPrinterName() {
        return printerName;
    }

    /**
     * @param printerName The printer_name
     */
    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }
}
