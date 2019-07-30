package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class DiscountDetails {

    @Expose
    private Integer id;
    @Expose
    private String percentage;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("min_restriction")
    @Expose
    private String minRestriction;
    @SerializedName("min_spend")
    @Expose
    private String minSpend;
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
     * @return The fromDate
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate The from_date
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return The toDate
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * @param toDate The to_date
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    /**
     * @return The minRestriction
     */
    public String getMinRestriction() {
        return minRestriction;
    }

    /**
     * @param minRestriction The min_restriction
     */
    public void setMinRestriction(String minRestriction) {
        this.minRestriction = minRestriction;
    }

    /**
     * @return The minSpend
     */
    public String getMinSpend() {
        return minSpend;
    }

    /**
     * @param minSpend The min_spend
     */
    public void setMinSpend(String minSpend) {
        this.minSpend = minSpend;
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


}
