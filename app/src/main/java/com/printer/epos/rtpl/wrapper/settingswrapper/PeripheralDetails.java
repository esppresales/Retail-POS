package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class PeripheralDetails {

    @Expose
    private Integer id;
    @SerializedName("barcode_device_name")
    @Expose
    private String barcodeDeviceName;
    @SerializedName("customer_display")
    @Expose
    private String customerDisplayName;
    @SerializedName("msr_device_name")
    @Expose
    private String msrDeviceName;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The barcodeDeviceName
     */
    public String getBarcodeDeviceName() {
        return barcodeDeviceName;
    }

    /**
     * @param barcodeDeviceName The barcode_device_name
     */
    public void setBarcodeDeviceName(String barcodeDeviceName) {
        this.barcodeDeviceName = barcodeDeviceName;
    }

    /**
     * @return The msrDeviceName
     */
    public String getMsrDeviceName() {
        return msrDeviceName;
    }

    /**
     * @param msrDeviceName The msr_device_name
     */
    public void setMsrDeviceName(String msrDeviceName) {
        this.msrDeviceName = msrDeviceName;
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

    public String getCustomerDisplayName() {
        return customerDisplayName;
    }

    public void setCustomerDisplayName(String customerDisplayName) {
        this.customerDisplayName = customerDisplayName;
    }
}
