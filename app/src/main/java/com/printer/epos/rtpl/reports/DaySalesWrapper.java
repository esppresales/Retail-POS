package com.printer.epos.rtpl.reports;

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
 * Created by ranosys-archana on 19/9/16.
 */
public class DaySalesWrapper {
    @Expose
    private DaySalesData data;

    /**
     * @return The data
     */
    public DaySalesData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(DaySalesData data) {
        this.data = data;
    }

    protected void getDaySalesData(Context context, String url) {
        new WebServiceCalling().callWS(context, url, null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<DaySalesWrapper>() {
                    }.getType();
                    DaySalesWrapper wrapper = gson.fromJson(reader, listType);
                    System.out.println(wrapper);
                    onDaySalesDataReceived(wrapper);


                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(DaySalesWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    protected void onDaySalesDataReceived(DaySalesWrapper wrapper) {

    }
}
