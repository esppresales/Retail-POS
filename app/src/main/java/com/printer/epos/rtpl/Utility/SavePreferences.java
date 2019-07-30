package com.printer.epos.rtpl.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.printer.epos.rtpl.UiController;

/**
 * Created by ranosys-puneet on 19/3/15.
 */
public class SavePreferences {

    Context context;
    final String user_pref = "EPOS";


    private SharedPreferences shared_preferences;

    static final int MODE_PRIVATE = 0;

    public SavePreferences(Context context) {
        shared_preferences = context.getSharedPreferences(user_pref, MODE_PRIVATE);
    }

    public String get_ip() {
        String iP = "";
        iP = shared_preferences.getString("iP", null);
        return iP;
    }

    public void remove_ip() {
        SharedPreferences.Editor ed = shared_preferences.edit();
        ed.clear();
        ed.remove("iP");
        ed.commit();
    }

    public void store_ip(String iP) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("iP", iP);
        editor.commit();
    }

    public void store_emailId(String emailId) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("emailId", emailId);
        editor.commit();

    }

    public String get_emailId() {
        String emailId = "";
        emailId = shared_preferences.getString("emailId", null);
        return emailId;
    }

    public void remove_emailId() {
        SharedPreferences.Editor ed = shared_preferences.edit();
        ed.clear();
        ed.remove("emailId");
        ed.commit();
    }

    public void store_accessToken(String accessToken) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("accessToken", accessToken);
        editor.commit();
    }

    public String get_accessToken() {
        String accessToken = "";
        accessToken = shared_preferences.getString("accessToken", null);
        return accessToken;
    }

    public void remove_accessToken() {
        SharedPreferences.Editor ed = shared_preferences.edit();
        ed.clear();
        ed.remove("accessToken");
        ed.commit();
    }

    public void store_roleId(String roleId) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("roleId", roleId);
        editor.commit();
    }

    public String get_roleId() {
        String roleId = "";
        roleId = shared_preferences.getString("roleId", "1");
        return roleId;
    }

    public void remove_roleId() {
        SharedPreferences.Editor ed = shared_preferences.edit();
        ed.clear();
        ed.remove("roleId");
        ed.commit();
    }

    public void store_id(String id) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("id", id);
        editor.commit();
    }

    public String get_id() {
        String id = "";
        id = shared_preferences.getString("id", null);
        return id;
    }

    public void remove_id() {
        SharedPreferences.Editor ed = shared_preferences.edit();
        ed.clear();
        ed.remove("id");
        ed.commit();
    }

    public void storeTaxPercentage(String taxPercentage) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("tax_percentage", taxPercentage);
        editor.commit();
    }

    public String getTaxPercentage() {
        String taxPer = "";
        taxPer = shared_preferences.getString("tax_percentage", "0.00");
        return taxPer;
    }

    public void storeGSTRegNo(String gstRegNo) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("gst_reg_no", gstRegNo);
        editor.commit();
    }

    public String getGSTRegNo() {
        String taxPer = "";
        taxPer = shared_preferences.getString("gst_reg_no", " ");
        return taxPer;
    }

    public void storeMasterPrinterIp(String ipAddress) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("master_printer_ip", ipAddress);
        editor.commit();
    }

    public String getMasterPrinterIp() {
        String ipAddr = "";
        ipAddr = shared_preferences.getString("master_printer_ip", "");
        return ipAddr;
    }

    public void storeSlavePrinterIp(String ipAddress) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("slave_printer_ip", ipAddress);
        editor.commit();
    }

    public String getSlavePrinterIp() {
        String ipAddr = "";
        ipAddr = shared_preferences.getString("slave_printer_ip", "");
        return ipAddr;
    }

    public void storeBarcodeDeviceName(String name) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("barcode_device_name", name);
        editor.commit();
    }

    public String getBarcodeDeviceName() {
        String name = "";
        name = shared_preferences.getString("barcode_device_name", "");
        return name;
    }

    public void storeMSRDeviceName(String name) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("msr_device_name", name);
        editor.commit();
    }

    public String getMSRDeviceName() {
        String name = "";
        name = shared_preferences.getString("msr_device_name", "");
        return name;
    }

    public void storeCustomerDisplayName(String name) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("customer_display_name", name);
        editor.commit();
    }

    public String getCustomerDisplayName() {
        String name = "";
        name = shared_preferences.getString("customer_display_name", "");
        return name;
    }

    /**
     * * Discount Details ***
     */
    public void storeDiscountPercentage(String percentage) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("discount_percentage", percentage);
        editor.commit();
    }

    public void storeDiscountFromDate(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("discount_from_date", data);
        editor.commit();
    }

    public void storeDiscountToDate(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("discount_to_date", data);
        editor.commit();
    }

    public void storeDiscountMinRestiction(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("discount_min_restriction", data);
        editor.commit();
    }

    public void storeDiscountMinSpend(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("discount_min_spend", data);
        editor.commit();
    }

    public String getDiscountMinSpend() {
        String name = "";
        name = shared_preferences.getString("discount_min_spend", "0.00");
        return name;
    }

    public String getDiscountMinRestiction() {
        String name = "";
        name = shared_preferences.getString("discount_min_restriction", "0");
        return name;
    }

    public String getDiscountToDate() {
        String name = "";
        name = shared_preferences.getString("discount_to_date", "");
        return name;
    }


    public String getDiscountFromDate() {
        String name = "";
        name = shared_preferences.getString("discount_from_date", "");
       // Log.i("getDiscountFromDate---+++>",name);
        return name;
    }

    public String getDiscountPercentage() {
        String name = "";
        name = shared_preferences.getString("discount_percentage", "0.00");
        return name;
    }

    /**
     * ** Loyalty Points ****
     */
    public void store_is_loyalty_on(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("is_loyalty_on", data);
        editor.commit();
    }

    public void store_earned_amount(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("earned_amount", data);
        editor.commit();
    }

    public String getEarnedAmount() {
        String name = "";
        name = shared_preferences.getString("earned_amount", "0.0");
        return name;
    }

    public String getIsloyaltyOn() {
        String name = "";
        name = shared_preferences.getString("is_loyalty_on", "");
        return name;
    }


    /**
     * ** Receipt Header Details ****
     */
    public void storeReceiptName(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("receipt_name", data);
        editor.commit();
    }

    public void storeReceiptWebsite(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("website", data);
        editor.commit();
    }

    public void storeReceiptHeader1(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("header1", data);
        editor.commit();
    }

    public void storeReceiptHeader2(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("header2", data);
        editor.commit();
    }

    public void storeReceiptHeader3(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("header3", data);
        editor.commit();
    }

    public String getReceiptName() {
        String name = "";
        name = shared_preferences.getString("receipt_name", "");
        return name;
    }

    public String getReceiptWebsite() {
        String name = "";
        name = shared_preferences.getString("website", "");
        return name;
    }

    public String getReceiptHeader1() {
        String name = "";
        name = shared_preferences.getString("header1", "");
        return name;
    }

    public String getReceiptHeader2() {
        String name = "";
        name = shared_preferences.getString("header2", "");
        return name;
    }

    public String getReceiptHeader3() {
        String name = "";
        name = shared_preferences.getString("header3", "");
        return name;
    }

    public void storeReceiptMessage(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("message", data);
        editor.commit();
    }

    public String getReceiptMessage() {
        String name = "";
        name = shared_preferences.getString("message", "");
        return name;
    }

    public void storeReceiptLogoFlag(int data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putInt("logo_used", data);
        editor.commit();
    }

    public int getReceiptLogoFlag() {

        return shared_preferences.getInt("logo_used",0);
    }

    public void storeReceiptCouponFlag(int data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putInt("coupon_used", data);
        editor.commit();
    }

    public int getReceiptCouponFlag() {

        return shared_preferences.getInt("coupon_used",0);
    }

    public void storeReceiptCouponUrl(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("coupon_logo", data);
        editor.commit();
    }

    public String getReceiptCouponUrl() {

        return shared_preferences.getString("coupon_logo","");
    }

    public void storeReceiptLogoUrl(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("logo", data);
        editor.commit();
    }

    public String getReceiptLogoUrl() {

        return shared_preferences.getString("logo","");
    }

    public void storeReceiptLogoFilePath(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("logo_file", data);
        editor.commit();
    }

    public String getReceiptLogoFilePath() {

        return shared_preferences.getString("logo_file","");
    }

    public void storeReceiptCouponFilePath(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("coupon_logo_file_path", data);
        editor.commit();
    }

    public String getReceiptCouponFilePath() {

        return shared_preferences.getString("coupon_logo_file_path","");
    }



    /**
     * * Currency Details ***
     */

    public void storeCurrencyName(String data) {
        UiController.sCurrency = data;
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("currency_name", data);
        editor.commit();
    }

    public void storeCurrencyId(String data) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putString("currency_id", data);
        editor.commit();
    }

    public String getCurrencyName() {
        String name = "";
        name = shared_preferences.getString("currency_name", "SGD");
        return name;
    }

    public String getCurrencyId() {
        String name = "";
        name = shared_preferences.getString("currency_id", "0");
        return name;
    }


    public void removeAll() {

        remove_id();
        remove_accessToken();
        remove_ip();
        remove_emailId();
        remove_roleId();
        // Clear All Shared prefernce
        shared_preferences.edit().clear().commit();
    }
}
