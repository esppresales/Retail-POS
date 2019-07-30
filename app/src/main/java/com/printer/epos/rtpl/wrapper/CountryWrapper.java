package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
 * Created by ranosys-puneet on 9/4/15.
 */
public class CountryWrapper {


    @Expose
    private List<CountryListWrapper> data = new ArrayList<CountryListWrapper>();

    /**
     *
     * @return
     * The data
     */
    public List<CountryListWrapper> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<CountryListWrapper> data) {
        this.data = data;
    }

    public class CountryListWrapper {

        @Expose
        private String id;
        @SerializedName("country_name")
        @Expose
        private String countryName;

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The countryName
         */
        public String getCountryName() {
            return countryName;
        }

        /**
         *
         * @param countryName
         * The country_name
         */
        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

    }









    public void getCountryList(final Context context) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "countries", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<CountryWrapper>() {
                    }.getType();
                    CountryWrapper countryWrapper = gson.fromJson(reader, listType);
                    System.out.println(countryWrapper.getData());

                    for (int i = 0; i < countryWrapper.getData().size(); i++) {
                        CountryListWrapper wrapper = countryWrapper.getData().get(i);
                        data.add(wrapper);
                    }
                    getCountryWrapperList(context, data);
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(CountryWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    public void getCountryWrapperList(Context context, List<CountryListWrapper> data) {
        Log.i(CountryWrapper.class.getName(), "getCountryWrapperList called");

    }

}
