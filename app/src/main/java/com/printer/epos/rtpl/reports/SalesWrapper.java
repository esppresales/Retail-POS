package com.printer.epos.rtpl.reports;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by android-pc3 on 21/4/15.
 */
public class SalesWrapper {

    @Expose
    private SalesData data;

    /**
     * @return The data
     */
    public SalesData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(SalesData data) {
        this.data = data;
    }

    protected void getStaffTimeTrackingData(Context context) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "time-tracking", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<StaffTimeTrackingWrapper>() {
                    }.getType();
                    StaffTimeTrackingWrapper wrapper = gson.fromJson(reader, listType);
                    System.out.println(wrapper.getData());

                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(SalesWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

}
