package com.printer.epos.rtpl.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.printer.epos.rtpl.NewOrderFragment;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.settingswrapper.CurrencyDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.DiscountDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.LoyaltyPointDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.PeripheralDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.ReceiptHeaderDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.TaxDetails;




@SuppressWarnings("ALL")
public class FetchSettingsService extends Service implements SettingsModuleWrapper.RefreshSettingsListener {

    private ReceiptHeaderDetails receiptHeaderDetails;
    private DiscountDetails discountDetails;
    private LoyaltyPointDetails loyaltyPointDetails;
    private TaxDetails taxDetails;
    private PeripheralDetails peripheralDetails;
    private CurrencyDetails currencyDetails;
    private ResultReceiver rec;
    private NewOrderFragment newOrderFragment;

    /*public FetchSettingsService() {
        super("FetchSettingsService");
    }*/

   /* @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FetchSettingsService", "onBind");
    }
*/


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("FetchSettingsService", "onstart");
        onBind(intent);

    }

    /*@Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                rec = intent.getParcelableExtra("receiverTag");
                new SettingsModuleWrapper().getSettings(this, this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }*/


    @Override
    public IBinder onBind(Intent intent) {

        if (intent != null) {

            try {
                rec = intent.getParcelableExtra("receiverTag");
                new SettingsModuleWrapper().getSettings(this, this);
            } catch (Exception ex) {
                ex.printStackTrace();
                RetailPosLoging.getInstance().registerLog(FetchSettingsService.class.getName(), ex);
            }
        }
        return null;
    }

    @Override
    public void onSettingsReceived(SettingsModuleWrapper wrapper) {
        peripheralDetails = wrapper.getData().getPeripheralDetails();
        Util.printerDetailsList.clear();
        Util.printerDetailsList = wrapper.getData().getPrinterDetails();
        receiptHeaderDetails = wrapper.getData().getReceiptHeaderDetails();
        discountDetails = wrapper.getData().getDiscountDetails();
        taxDetails = wrapper.getData().getTaxDetails();
        loyaltyPointDetails = wrapper.getData().getLoyaltyPointDetails();
        currencyDetails = wrapper.getData().getCurrencyDetails();


        Log.d("peripheralDetails--->",""+peripheralDetails.getBarcodeDeviceName()+"\n"+peripheralDetails.getCustomerDisplayName()
        +"\n"+peripheralDetails.getMsrDeviceName());

        storeAllSettingsData();

        if(receiptHeaderDetails != null)
            saveReceiptImages();

        if (rec != null)
            rec.send(0, null);
    }

    private void storeAllSettingsData() {
        if(discountDetails != null) {
            storeDiscountDetails();
        }

        if(loyaltyPointDetails != null)
            storeLoyaltyPointsDetail();

        if(taxDetails != null)
            storeTaxPercentage();

        if(peripheralDetails != null)
         storePeripheralConfiguration();

        if(currencyDetails != null)
            storeCurrencyDetails();

        if(receiptHeaderDetails != null)
            storeReceiptHeaderDetails();

        storeMasterPrinterId();

        Log.i(FetchSettingsService.class.getName(), "All Settings Data Stored");
    }

    private void storeMasterPrinterId() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        if(Util.printerDetailsList != null && Util.printerDetailsList.size() != 0){
            String masterPrinterId = Util.printerDetailsList.get(0).getPrinterName();
            pref.storeMasterPrinterIp(masterPrinterId);
        }

    }

    private void storeDiscountDetails() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        pref.storeDiscountPercentage(discountDetails.getPercentage());
        pref.storeDiscountMinSpend(discountDetails.getMinSpend());
        pref.storeDiscountFromDate(discountDetails.getFromDate());
        pref.storeDiscountToDate(discountDetails.getToDate());
        pref.storeDiscountMinRestiction(discountDetails.getMinRestriction());

        Log.i(FetchSettingsService.class.getName(), "Discount Details Stored");
    }

    private void storeLoyaltyPointsDetail() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        pref.store_is_loyalty_on(String.valueOf(loyaltyPointDetails.getIsLoyaltyOn()));
        pref.store_earned_amount(loyaltyPointDetails.getEarnedAmount());

        Log.i(FetchSettingsService.class.getName(), "Loyalty Points Details Stored");
    }

    private void storeTaxPercentage() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        pref.storeTaxPercentage(taxDetails.getPercentage());
        pref.storeGSTRegNo(taxDetails.getGstRegNo());
        Log.i(FetchSettingsService.class.getName(), "Tax Percentage Details Stored");
    }


    private void storePeripheralConfiguration() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        pref.storeBarcodeDeviceName(peripheralDetails.getBarcodeDeviceName());
        pref.storeMSRDeviceName(peripheralDetails.getMsrDeviceName());
        pref.storeCustomerDisplayName(peripheralDetails.getCustomerDisplayName());

        Log.i(FetchSettingsService.class.getName(), "Peripherals Configuration Details Stored");
    }

    private void storeCurrencyDetails() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        pref.storeCurrencyName(currencyDetails.getShortName());
        pref.storeCurrencyId(currencyDetails.getId());

        Log.i(FetchSettingsService.class.getName(), "Currency Details Stored: " + pref.getCurrencyName());
    }

    private void storeReceiptHeaderDetails() {
        SavePreferences pref = new SavePreferences(getApplicationContext());
        pref.storeReceiptName(receiptHeaderDetails.getName());
        pref.storeReceiptWebsite(receiptHeaderDetails.getWebsite());
        pref.storeReceiptHeader1(receiptHeaderDetails.getHeader1());
        pref.storeReceiptHeader2(receiptHeaderDetails.getHeader2());
        pref.storeReceiptHeader3(receiptHeaderDetails.getHeader3());
        pref.storeReceiptCouponUrl(receiptHeaderDetails.getCouponImage());
        pref.storeReceiptLogoUrl(receiptHeaderDetails.getLogoImage());
        pref.storeReceiptCouponFlag(receiptHeaderDetails.getCouponUsed());
        pref.storeReceiptLogoFlag(receiptHeaderDetails.getLogoUsed());
        pref.storeReceiptMessage(receiptHeaderDetails.getMessage());

        Log.i(FetchSettingsService.class.getName(), "Receipt Header Details Stored");

    }

    private void saveReceiptImages()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(receiptHeaderDetails.getLogoImage()))
                    Util.saveBitmapToFile(getBaseContext(),Util.getBitmap(receiptHeaderDetails.getLogoImage()),"logo.png",0);
                if(!TextUtils.isEmpty(receiptHeaderDetails.getCouponImage()))
                    Util.saveBitmapToFile(getBaseContext(),Util.getBitmap(receiptHeaderDetails.getCouponImage()),"coupon.png",1);
            }
        }).start();
    }


}
