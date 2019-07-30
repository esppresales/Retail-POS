package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class CurrencyDetails {

    @Expose
    private String id;
    @Expose
    private String name;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("is_enabled")
    @Expose
    private String isEnabled;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
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
     * @return The shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName The short_name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return The isEnabled
     */
    public String getIsEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled The is_enabled
     */
    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
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
