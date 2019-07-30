package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;
import com.printer.epos.rtpl.services.FetchSettingsService;
import com.printer.epos.rtpl.wrapper.CouponCodeWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.CurrencyDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.DiscountDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.LoyaltyPointDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.ReceiptHeaderDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.TaxDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by hp pc on 03-05-2015.
 */
public class SettingsWebClient {

    public static void updateSettings(final Context context, HashMap<String, Object> map, final SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        new WebServiceCalling().callWS(context, UiController.appUrl + "settings", map, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.i("updatesettingRespone-->",response);
                    JSONObject responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    //start service for fetch settings

                    Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                    context.startService(new Intent(context, FetchSettingsService.class));
                    /*new CustomDialog().showOneButtonAlertDialog(context, null,
                            msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                @Override
                                public void onPositiveClick() {
                                    new SettingsModuleWrapper().getSettings(context, refreshSettings);
                                }

                                @Override
                                public void onNegativeClick() {

                                }
                            });*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    RetailPosLoging.getInstance().registerLog(SettingsWebClient.class.getName(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public static HashMap<String, Object> getPrinterMap(String printerType, String printerName, String isEnabled) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "PrinterConfiguration");
        map.put("printer_type", printerType);
        map.put("printer_name", printerName);
        map.put("is_enabled", isEnabled);

        return map;
    }

    public static HashMap<String, Object> getPeripheralConfigMap(String barcode, String msr, String display) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "PeripheralConfiguration");
        map.put("barcode_device_name", barcode);
        map.put("msr_device_name", msr);
        map.put("customer_display", display);

        return map;
    }

    public static HashMap<String, Object> getDiscountDetailMap(DiscountDetails details) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "Discounts");
        map.put("percentage", details.getPercentage());
        map.put("from_date", details.getFromDate());
        map.put("to_date", details.getToDate());
        map.put("min_restriction", details.getMinRestriction());
        map.put("min_spend", details.getMinSpend());

        return map;
    }

    public static HashMap<String, Object> getTaxPercentageMap(TaxDetails data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "Taxes");
        map.put("percentage", data.getPercentage());
        map.put("gst_reg_no", data.getGstRegNo());

        return map;
    }

    public static HashMap<String, Object> getCurrencyMap(CurrencyDetails data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "Currency");
        map.put("currency_id", data.getId());

        return map;
    }

    public static HashMap<String, Object> getLoyaltyPointsMap(LoyaltyPointDetails data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "LoyaltyPoints");
        map.put("earned_amount", data.getEarnedAmount());
        map.put("is_loyalty_on", data.getIsLoyaltyOn());

        return map;
    }

    public static HashMap<String, Object> getCouponCodesMap(CouponCodeWrapper.CouponCodeData data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (data == null)
            map.put("model_name", "add Coupons");
        else
            map.put("model_name", "edit Coupons");

        map.put("coupon_code", data.getCouponCode());
        map.put("amount", data.getAmount());
        map.put("validity_from_date", data.getValidityFromDate());
        map.put("validity_to_date", data.getValidityToDate());

        return map;
    }

    public static HashMap<String, Object> getReceiptHeaderMap(ReceiptHeaderDetails data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("model_name", "ReceiptHeader");
        map.put("name", data.getName());
        map.put("website", data.getWebsite());
        map.put("header1", data.getHeader1());
        map.put("header2", data.getHeader2());
        map.put("header3", data.getHeader3());

        if(!TextUtils.isEmpty(data.getLogoImage())) {
            Log.i("getLogoImage", "" + data.getLogoImage());
            map.put("logo_image", new File(data.getLogoImage()));
        }
           // map.put("logo_image", new File("/storage/emulated/0/Samsung/Image/Photo.jpg"));

        if(!TextUtils.isEmpty(data.getCouponImage())) {
            Log.i("getCouponImage", "" + data.getCouponImage());
            map.put("coupon_image", new File(data.getCouponImage()));
        }
           // map.put("logo_image", new File("/storage/emulated/0/Samsung/Image/Photo.jpg"));


        map.put("logo_used", data.getLogoUsed());
        map.put("coupon_used", data.getCouponUsed());
        map.put("message", data.getMessage());

        return map;
    }

}
