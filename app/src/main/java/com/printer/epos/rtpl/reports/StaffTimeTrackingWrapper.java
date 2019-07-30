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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-pc3 on 20/4/15.
 */
public class StaffTimeTrackingWrapper {

    @Expose
    private List<StaffTimeTrackingData> data = new ArrayList<StaffTimeTrackingData>();

    /**
     * @return The data
     */
    public List<StaffTimeTrackingData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<StaffTimeTrackingData> data) {
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
                    onTimeTrackingDataReceived(wrapper);


                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(StaffTimeTrackingWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    protected void onTimeTrackingDataReceived(StaffTimeTrackingWrapper wrapper) {

    }


}
