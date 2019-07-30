package com.printer.epos.rtpl.reports;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-pc3 on 27/4/15.
 */
public class StockInHandWrapper {

    @Expose
    private List<StockInHandData> data = new ArrayList<StockInHandData>();

    /**
     * @return The data
     */
    public List<StockInHandData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<StockInHandData> data) {
        this.data = data;
    }


    protected void getProductList(Context context, String quantity) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "product-threshold/" + quantity, null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<StockInHandWrapper>() {
                    }.getType();
                    StockInHandWrapper wrapper = gson.fromJson(reader, listType);
                    System.out.println(wrapper.getData());
                    onProductListReceived(wrapper);


                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(StockInHandWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });

    }

    public void getProductList(Context context) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "product-threshold", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<StockInHandWrapper>() {
                    }.getType();
                    StockInHandWrapper wrapper = gson.fromJson(reader, listType);
                    System.out.println(wrapper.getData());
                    onProductListReceived(wrapper);


                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(StockInHandWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });

    }

    protected void downloadCsvReport(Context context, String quantity) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(UiController.appUrl + "product-threshold/" + quantity + "?export=export"));

        request.addRequestHeader("access-token", new SavePreferences(context).get_accessToken());
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "ReportsBasedOnQuantity.xls");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        context.registerReceiver(new DownloadReceiver(), new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        manager.enqueue(request);
    }

    protected void downloadCsvReport(Context context) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(UiController.appUrl + "product-threshold?export=export"));

        request.addRequestHeader("access-token", new SavePreferences(context).get_accessToken());
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "Reports.xls");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        context.registerReceiver(new DownloadReceiver(), new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        manager.enqueue(request);
    }


    protected void onProductListReceived(StockInHandWrapper wrapper) {

    }
}
