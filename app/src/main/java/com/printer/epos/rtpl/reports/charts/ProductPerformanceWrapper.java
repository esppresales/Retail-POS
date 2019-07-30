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
 * Created by android-pc3 on 28/4/15.
 */
public class ProductPerformanceWrapper {

    @Expose
    private ProductPerformanceData data;

    /**
     * @return The data
     */
    public ProductPerformanceData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(ProductPerformanceData data) {
        this.data = data;
    }

    protected void getProductPerformanceData(Context context, String url) {
        new WebServiceCalling().callWS(context, url, null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<ProductPerformanceWrapper>() {
                    }.getType();
                    ProductPerformanceWrapper wrapper = gson.fromJson(reader, listType);
                    System.out.println(wrapper.getData());
                    onProductPerformanceDataReceived(wrapper);


                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(ProductPerformanceWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    protected void onProductPerformanceDataReceived(ProductPerformanceWrapper wrapper) {

    }
}
