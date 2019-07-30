package com.printer.epos.rtpl.wrapper.settingswrapper;

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
public class CurrencyWrapper {

    @Expose
    private CurrencyData data;
    private CurrencyListener mCurrency;

    /**
     * @return The data
     */
    public CurrencyData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(CurrencyData data) {
        this.data = data;
    }

    public class CurrencyData {

        @Expose
        private List<CurrencyDetails> currencies = new ArrayList<CurrencyDetails>();

        /**
         * @return The currencies
         */
        public List<CurrencyDetails> getCurrencies() {
            return currencies;
        }

        /**
         * @param currencies The currencies
         */
        public void setCurrencies(List<CurrencyDetails> currencies) {
            this.currencies = currencies;
        }
    }

    public void getCurrencies(Context context, CurrencyListener currency) {
        mCurrency = currency;
        new WebServiceCalling().callWS(context, UiController.appUrl + "config", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<CurrencyWrapper>() {
                    }.getType();
                    CurrencyWrapper currencyWrapper = gson.fromJson(reader, listType);
                    mCurrency.onCurrencyReceived(currencyWrapper);
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(CurrencyWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    public interface CurrencyListener {
        public void onCurrencyReceived(CurrencyWrapper currencyWrapper);
    }
}
