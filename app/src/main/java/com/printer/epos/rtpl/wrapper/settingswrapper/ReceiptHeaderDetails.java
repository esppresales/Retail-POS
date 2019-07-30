package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class ReceiptHeaderDetails {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String website;
    @Expose
    private String header1;
    @Expose
    private String header2;
    @Expose
    private String header3;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("logo_image")
    @Expose
    private String logoImage;
    @SerializedName("logo_used")
    @Expose
    private Integer logoUsed;
    @Expose
    private String message;
    @SerializedName("coupon_image")
    @Expose
    private String couponImage;
    @SerializedName("coupon_used")
    @Expose
    private Integer couponUsed;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The website
     */
    public String getWebsite() {
        return website;
    }

    /**
     *
     * @param website
     * The website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     *
     * @return
     * The header1
     */
    public String getHeader1() {
        return header1;
    }

    /**
     *
     * @param header1
     * The header1
     */
    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    /**
     *
     * @return
     * The header2
     */
    public String getHeader2() {
        return header2;
    }

    /**
     *
     * @param header2
     * The header2
     */
    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    /**
     *
     * @return
     * The header3
     */
    public String getHeader3() {
        return header3;
    }

    /**
     *
     * @param header3
     * The header3
     */
    public void setHeader3(String header3) {
        this.header3 = header3;
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
     *
     * @return
     * The logoImage
     */
    public String getLogoImage() {
        return logoImage;
    }

    /**
     *
     * @param logoImage
     * The logo_image
     */
    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    /**
     *
     * @return
     * The logoUsed
     */
    public Integer getLogoUsed() {
        return logoUsed;
    }

    /**
     *
     * @param logoUsed
     * The logo_used
     */
    public void setLogoUsed(Integer logoUsed) {
        this.logoUsed = logoUsed;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The couponImage
     */
    public String getCouponImage() {
        return couponImage;
    }

    /**
     *
     * @param couponImage
     * The coupon_image
     */
    public void setCouponImage(String couponImage) {
        this.couponImage = couponImage;
    }

    /**
     *
     * @return
     * The couponUsed
     */
    public Integer getCouponUsed() {
        return couponUsed;
    }

    /**
     *
     * @param couponUsed
     * The coupon_used
     */
    public void setCouponUsed(Integer couponUsed) {
        this.couponUsed = couponUsed;
    }

}
