package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class LoyaltyPointDetails {

    @Expose
    private Integer id;
    @SerializedName("is_loyalty_on")
    @Expose
    private Integer isLoyaltyOn;
    @SerializedName("earned_amount")
    @Expose
    private String earnedAmount;
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
     * @return The isLoyaltyOn
     */
    public Integer getIsLoyaltyOn() {
        return isLoyaltyOn;
    }

    /**
     * @param isLoyaltyOn The is_loyalty_on
     */
    public void setIsLoyaltyOn(Integer isLoyaltyOn) {
        this.isLoyaltyOn = isLoyaltyOn;
    }

    /**
     * @return The earnedAmount
     */
    public String getEarnedAmount() {
        return earnedAmount;
    }

    /**
     * @param earnedAmount The earned_amount
     */
    public void setEarnedAmount(String earnedAmount) {
        this.earnedAmount = earnedAmount;
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
