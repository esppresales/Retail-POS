package com.printer.epos.rtpl.reports.charts;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by android-pc3 on 29/4/15.
 */
public class SalesChartWrapper {

    @Expose
    private SalesChartData data;

    /**
     * @return The data
     */
    public SalesChartData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(SalesChartData data) {
        this.data = data;
    }

    protected void getSalesChartData(Context context, String url) {
        new WebServiceCalling().callWS(context, url, null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<SalesChartWrapper>() {
                    }.getType();
                    SalesChartWrapper wrapper = gson.fromJson(reader, listType);
                    System.out.println(wrapper.getData());
                    onSalesChartDataReceived(wrapper);


                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(SalesChartWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    protected void onSalesChartDataReceived(SalesChartWrapper wrapper) {

    }
}
