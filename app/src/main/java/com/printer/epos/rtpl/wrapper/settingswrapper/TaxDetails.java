package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class TaxDetails {
    @Expose
    private Integer id;
    @Expose
    private String percentage;
    @Expose
    private String type;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("gst_reg_no")
    @Expose
    private String gstRegNo;


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
     * @return The percentage
     */
    public String getPercentage() {
        return percentage;
    }

    /**
     * @param percentage The percentage
     */
    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
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
    /**
     * @return The percentage
     */
    public String getGstRegNo() {
        return gstRegNo;
    }

    /**
     * @param gstRegNo The gstRehNo
     */
    public void setGstRegNo(String gstRegNo) {
        this.gstRegNo = gstRegNo;
    }


}
