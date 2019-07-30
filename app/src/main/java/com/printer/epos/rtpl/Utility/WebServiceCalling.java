package com.printer.epos.rtpl.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.printer.epos.rtpl.LoginActivity;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.wrapper.PeripheralManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class WebServiceCalling {

    //private RequestQueue mRequestQueue;
    private AQuery aQuery;
    private ProgressDialog mDialog;
    String noInternetConnectionTitle = "";
    String noInternetConnectionMsg = "";
    String okButton = "";
    int alertIcon = 0;
    private final String ACCESS_TOKEN = "access-token";


    public void callWS(final Context mContext, String url, final HashMap<String, Object> params,
                       final WebServiceHandler mHandler) {

        if (Util.isDeviceOnline(mContext)) {


            mDialog = new ProgressDialog(mContext, android.R.style.Theme_Holo_Dialog);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setMessage(mContext.getString(R.string.please_wait_text));
            // mDialog.show();
            aQuery = UiController.getInstance().getAQuery();
            Log.i("Api Call",""+url);
            Log.i("Params",""+params);

            noInternetConnectionTitle = mContext.getResources().getString(R.string.no_internet_connection);
            okButton = mContext.getResources().getString(R.string.ok_button);
            alertIcon = android.R.drawable.ic_dialog_alert;
            noInternetConnectionMsg = "Not connected to Printer? Sorry, POS require active Printer Connection.";

            aQuery.progress(mDialog).ajax(url, params, String.class, new AjaxCallback<String>() {

                        @Override
                        public void callback(String url, String arg0, AjaxStatus ajaxStatus) {

                            try {
                                int status = ajaxStatus.getCode();
                                String msg = "";
                                JSONObject responseObj;

                                //RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                Log.i("Status--->",status+"");
                                switch (status) {
                                    case -101: {
                                        // new CustomDialog().showOneButtonDialog(mContext, null, mContext.getResources().getString(R.string.no_internet_connection),
                                        //mContext.getResources().getString(R.string.ok_button), null);

                                        if (mContext instanceof LoginActivity) {
                                            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                    "Please check your ip address.", okButton, alertIcon, null);
                                        } else {
                                            new CustomDialog().showOneButtonAlertDialog(mContext, noInternetConnectionTitle,
                                                    mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                        }
                                        mHandler.onError("" + arg0);
                                        Log.i("AjaxStatus--->",""+ajaxStatus.getMessage());
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    case 200: {
                                        try {
                                            mHandler.onSuccess(arg0);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                        }
                                        break;
                                    }
                                    case 400: {
                                        try {
                                            responseObj = new JSONObject(ajaxStatus.getError());
                                            mHandler.onError("" + arg0);
                                            if (responseObj.has("msg")) {
                                                msg = responseObj.getString("msg");
//                                        new CustomDialog().showOneButtonDialog(mContext, null, msg, okButton, null);

                                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                        msg, okButton, alertIcon, null);

                                            } else if (responseObj.has("errors")) {
                                                //{"errors":{"email_id":["Email ID \"archana.pareta-buyer@gmail.com\" has already been taken."]}}"

                                                JSONObject obj = responseObj.getJSONArray("errors").getJSONObject(0);
                                                if (obj.keys().hasNext()) {
                                                    try {

                                                        String key = obj.keys().next().toString();
                                                        msg = obj.getString(key);
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                        RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                                    }


                                                    new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                            msg, okButton, alertIcon, null);

                                                }
                                            }
                                            RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                        }
                                        break;
                                    }
                                    case 404: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, "Server not found",
                                                "Existing server has been changed or not working.", okButton, alertIcon, null);
                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    case 403: {
                                        sessionExpired(mContext);
                                        break;

                                    }
                                    case 500: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    default: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                        mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                mHandler.onError(null);
                                RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), e);
                            }
                        }
                    }.header(ACCESS_TOKEN, new SavePreferences(mContext).get_accessToken())
            );

        } else
            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                    mContext.getResources().getString(R.string.no_internet_connection), okButton, alertIcon, null);

    }

    public void callWSForUpdate(final Context mContext, String url, final HashMap<String, Object> params,
                                final WebServiceHandler mHandler) {

        if (Util.isDeviceOnline(mContext)) {

            mDialog = new ProgressDialog(mContext, android.R.style.Theme_Holo_Dialog);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setMessage("Please wait ...");
            // mDialog.show();
            aQuery = UiController.getInstance().getAQuery();

            noInternetConnectionTitle = mContext.getResources().getString(R.string.no_internet_connection);
            okButton = mContext.getResources().getString(R.string.ok_button);
            alertIcon = android.R.drawable.ic_dialog_alert;
            noInternetConnectionMsg = "Not connected to Printer? Sorry, POS require active Printer Connection.";

            aQuery.progress(mDialog).ajax(url, params, String.class, new AjaxCallback<String>() {

                        @Override
                        public void callback(String url, String arg0, AjaxStatus ajaxStatus) {

                            try {
                                int status = ajaxStatus.getCode();
                                String msg = "";
                                JSONObject responseObj;

                                //RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                Log.i("Status", "" + status);
                                switch (status) {
                                    case -101: {
                                        // new CustomDialog().showOneButtonDialog(mContext, null, mContext.getResources().getString(R.string.no_internet_connection),
                                        //mContext.getResources().getString(R.string.ok_button), null);

                                        new CustomDialog().showOneButtonAlertDialog(mContext, noInternetConnectionTitle,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    case 200: {
                                        mHandler.onSuccess(arg0);
                                        break;
                                    }
                                    case 400: {
                                        try {
                                            responseObj = new JSONObject(ajaxStatus.getError());

                                            Log.i("AJAXSTATUS-->", "" + ajaxStatus.getMessage());
                                            Log.i("AJAXERROR-->", "" + ajaxStatus.getError());
                                            Log.i("responseObj-->", "" + responseObj.toString());
                                            mHandler.onError("" + ajaxStatus.getError());
                                            if (responseObj.has("msg")) {
                                                msg = responseObj.getString("msg");
//                                        new CustomDialog().showOneButtonDialog(mContext, null, msg, okButton, null);

                                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                        msg, okButton, alertIcon, null);

                                            } else if (responseObj.has("errors")) {
                                                //{"errors":{"email_id":["Email ID \"archana.pareta-buyer@gmail.com\" has already been taken."]}}"

                                                JSONObject obj = responseObj.getJSONArray("errors").getJSONObject(0);
                                                if (obj.keys().hasNext()) {
                                                    try {

                                                        String key = obj.keys().next().toString();
                                                        msg = obj.getString(key);
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                        RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                                    }


                                                    new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                            msg, okButton, alertIcon, null);

                                                }
                                            } /*else if (responseObj.has("data")) {
                                                JSONArray jsonArray=responseObj.getJSONArray("data");
                                               // String barcode=jsonArray.getJSONObject(0).getString("barcode");
                                                msg = jsonArray.getJSONObject(0).getString("barcode");
                                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                        msg, okButton, alertIcon, null);
                                            }*/
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                        }
                                        break;
                                    }
                                    case 404: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, "Server not found",
                                                "Existing server has been changed or not working.", okButton, alertIcon, null);
                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    case 403: {
                                        sessionExpired(mContext);

                                        break;

                                    }
                                    case 500: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    default: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                        mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                mHandler.onError(null);
                                //RetailPosLoging.getInstance().registerLog(url, ajaxStatus.getCode(), params, arg0, e);
                            }
                        }
                    }.method(AQuery.METHOD_PUT)
                            .header(ACCESS_TOKEN, new SavePreferences(mContext).get_accessToken())
                    //.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    //.header("Content-Type", "multipart/form-data; charset=UTF-8")
            );
        } else
            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                    mContext.getResources().getString(R.string.no_internet_connection), okButton, alertIcon, null);
    }

    public void callWSForStaffUpdate(final Context mContext, String url, final HashMap<String, Object> params,
                                     final WebServiceHandler mHandler) {

        if (Util.isDeviceOnline(mContext)) {

            mDialog = new ProgressDialog(mContext, android.R.style.Theme_Holo_Dialog);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setMessage("Please wait ...");
            // mDialog.show();
            aQuery = UiController.getInstance().getAQuery();

            noInternetConnectionTitle = mContext.getResources().getString(R.string.no_internet_connection);
            okButton = mContext.getResources().getString(R.string.ok_button);
            alertIcon = android.R.drawable.ic_dialog_alert;
            noInternetConnectionMsg = "Not connected to Printer? Sorry, POS require active Printer Connection.";

            aQuery.progress(mDialog).ajax(url, params, String.class, new AjaxCallback<String>() {

                        @Override
                        public void callback(String url, String arg0, AjaxStatus ajaxStatus) {

                            try {
                                int status = ajaxStatus.getCode();
                                String msg = "";
                                JSONObject responseObj;

                                //RetailPosLoging.getInstance().registerLog(url, status, params, arg0);

                                switch (status) {
                                    case -101: {
                                        // new CustomDialog().showOneButtonDialog(mContext, null, mContext.getResources().getString(R.string.no_internet_connection),
                                        //mContext.getResources().getString(R.string.ok_button), null);

                                        new CustomDialog().showOneButtonAlertDialog(mContext, noInternetConnectionTitle,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    case 200: {
                                        mHandler.onSuccess(arg0);
                                        break;
                                    }
                                    case 400: {
                                        try {
                                            responseObj = new JSONObject(ajaxStatus.getError());
                                            mHandler.onError("" + arg0);
                                            if (responseObj.has("msg")) {
                                                msg = responseObj.getString("msg");
//                                        new CustomDialog().showOneButtonDialog(mContext, null, msg, okButton, null);

                                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                        msg, okButton, alertIcon, null);

                                            } else if (responseObj.has("errors")) {
                                                //{"errors":{"email_id":["Email ID \"archana.pareta-buyer@gmail.com\" has already been taken."]}}"

                                                JSONObject obj = responseObj.getJSONArray("errors").getJSONObject(0);
                                                if (obj.keys().hasNext()) {
                                                    try {

                                                        String key = obj.keys().next().toString();
                                                        msg = obj.getString(key);
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                        RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                                    }


                                                    new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                            msg, okButton, alertIcon, null);

                                                }
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), ex);
                                        }
                                        break;
                                    }
                                    case 404: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, "Server not found",
                                                "Existing server has been changed or not working.", okButton, alertIcon, null);
                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    case 403: {
                                        sessionExpired(mContext);
                                        break;

                                    }
                                    case 500: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                    default: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, params, arg0);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), e);
                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                        mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                mHandler.onError(null);
                                //RetailPosLoging.getInstance().registerLog(url, ajaxStatus.getCode(), params, arg0, e);
                            }
                        }
                    }.method(AQuery.METHOD_POST)
                            .header(ACCESS_TOKEN, new SavePreferences(mContext).get_accessToken())
                    //.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    //.header("Content-Type", "multipart/form-data; charset=UTF-8")
            );
        } else
            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                    mContext.getResources().getString(R.string.no_internet_connection), okButton, alertIcon, null);
    }

    public void callWSForDelete(final Context mContext, String url, final WebServiceHandler mHandler) {

        if (Util.isDeviceOnline(mContext)) {

            mDialog = new ProgressDialog(mContext, android.R.style.Theme_Holo_Dialog);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setMessage("Please wait ...");
            // mDialog.show();
            aQuery = UiController.getInstance().getAQuery();

            noInternetConnectionTitle = mContext.getResources().getString(R.string.no_internet_connection);
            okButton = mContext.getResources().getString(R.string.ok_button);
            alertIcon = android.R.drawable.ic_dialog_alert;
            noInternetConnectionMsg = "Not connected to Printer? Sorry, POS require active Printer Connection.";

            aQuery.progress(mDialog).delete(url, String.class,
                    new AjaxCallback<String>() {

                        @Override
                        public void callback(String url, String arg0, AjaxStatus ajaxStatus) {

                            try {
                                int status = ajaxStatus.getCode();
                                String msg = "";
                                JSONObject responseObj;

                                //RetailPosLoging.getInstance().registerLog(url, status, null, arg0);

                                switch (status) {
                                    case -101: {
                                        // new CustomDialog().showOneButtonDialog(mContext, null, mContext.getResources().getString(R.string.no_internet_connection),
                                        //mContext.getResources().getString(R.string.ok_button), null);

                                        new CustomDialog().showOneButtonAlertDialog(mContext, noInternetConnectionTitle,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                        mHandler.onError(arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, null, arg0);
                                        break;
                                    }
                                    case 200: {
                                        mHandler.onSuccess(arg0);
                                        break;
                                    }
                                    case 400: {
                                        responseObj = new JSONObject(ajaxStatus.getError());
                                        mHandler.onError(arg0);
                                        if (responseObj.has("msg")) {
                                            msg = responseObj.getString("msg");
//                                        new CustomDialog().showOneButtonDialog(mContext, null, msg, okButton, null);

                                            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                    msg, okButton, alertIcon, null);

                                        }
                                        break;
                                    }
                                    case 404: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, "Server not found",
                                                "Existing server has been changed or not working.", okButton, alertIcon, null);
                                        mHandler.onError(arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, null, arg0);
                                        break;
                                    }
                                    case 403: {
                                        sessionExpired(mContext);

                                        break;

                                    }
                                    case 500: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, null, arg0);
                                        break;
                                    }
                                    default: {
                                        new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                                mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);

                                        mHandler.onError("" + arg0);
                                        RetailPosLoging.getInstance().registerLog(url, status, null, arg0);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                        mContext.getResources().getString(R.string.error_message), okButton, alertIcon, null);
                                mHandler.onError(null);
                                RetailPosLoging.getInstance().registerLog(url, ajaxStatus.getCode(), null, arg0);
                            }
                        }
                    }.header(ACCESS_TOKEN, new SavePreferences(mContext).get_accessToken())
            );
        } else
            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                    mContext.getResources().getString(R.string.no_internet_connection), okButton, alertIcon, null);
    }

    public static void logout(final Activity context) {
        new WebServiceCalling().callWS(context, UiController.appUrl + "logout", null, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                try {

                    JSONObject responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    new CustomDialog().showOneButtonAlertDialog(context, null,
                            msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                @Override
                                public void onPositiveClick() {
                                    SavePreferences pref = new SavePreferences(context);
                                    String ipAddr = pref.get_ip();

                                    String logoImage = pref.getReceiptLogoFilePath();
                                    String couponCodeImage = pref.getReceiptCouponFilePath();

                                    String dis=pref.getDiscountMinRestiction();


                                    pref.removeAll();

                                    pref.storeDiscountMinRestiction(dis);
                                    pref.store_ip(ipAddr);

                                    pref.storeReceiptCouponFilePath(couponCodeImage);
                                    pref.storeReceiptLogoFilePath(logoImage);

                                    PeripheralManager peripheralManager = PeripheralManager.getInstance(context);
                                    peripheralManager.setCallback(peripheralManager.getCallback());
                                    peripheralManager.deleteDevices();


                                    context.finish();
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);

                                }

                                @Override
                                public void onNegativeClick() {

                                }


                            });
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(WebServiceCalling.class.getName(), e);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void sessionExpired(final Context context) {

        PeripheralManager peripheralManager = PeripheralManager.getInstance(context);
        peripheralManager.setCallback(peripheralManager.getCallback());
        peripheralManager.deleteDevices();

        new CustomDialog().showOneButtonAlertDialog(context, null,
                "Your Session has been expired!", "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                    @Override
                    public void onPositiveClick() {
                        ((Activity) context).finish();
                        SavePreferences pref = new SavePreferences(context);
                        String ipAddr = pref.get_ip();
                        pref.removeAll();
                        pref.store_ip(ipAddr);
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });

    }


}


