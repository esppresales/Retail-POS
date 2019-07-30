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

/**
 * Created by android-pc3 on 23/3/15.
 */
public class HomeWrapper {


    @Expose
    private Data data;

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
        this.data = data;
    }




    public class Data {

        @SerializedName("todays_total")
        @Expose
        private String todaysTotal;
        @SerializedName("todays_transactions")
        @Expose
        private String todaysTransactions;
        @SerializedName("yesterdays_total")
        @Expose
        private String yesterdaysTotal;
        @SerializedName("yesterdays_return")
        @Expose
        private String yesterdaysReturn;
        @SerializedName("yesterdays_transactions")
        @Expose
        private String yesterdaysTransactions;
        @SerializedName("lastWeek_total")
        @Expose
        private String lastWeekTotal;
        @SerializedName("lastWeek_return")
        @Expose
        private String lastWeekReturn;
        @SerializedName("lastWeek_transactions")
        @Expose
        private String lastWeekTransactions;
        @SerializedName("lastMonth_total")
        @Expose
        private String lastMonthTotal;
        @SerializedName("lastMonth_return")
        @Expose
        private String lastMonthReturn;
        @SerializedName("lastMonth_transactions")
        @Expose
        private String lastMonthTransactions;
        @SerializedName("todays_discount")
        @Expose
        private String todaysDiscount;
        @SerializedName("min_spend")
        @Expose
        private String minSpend;
        @SerializedName("store_name")
        @Expose
        private String storeName;

        /**
         *
         * @return
         * The todaysTotal
         */
        public String getTodaysTotal() {
            return todaysTotal;
        }

        /**
         *
         * @param todaysTotal
         * The todays_total
         */
        public void setTodaysTotal(String todaysTotal) {
            this.todaysTotal = todaysTotal;
        }

        /**
         *
         * @return
         * The todaysTransactions
         */
        public String getTodaysTransactions() {
            return todaysTransactions;
        }

        /**
         *
         * @param todaysTransactions
         * The todays_transactions
         */
        public void setTodaysTransactions(String todaysTransactions) {
            this.todaysTransactions = todaysTransactions;
        }

        /**
         *
         * @return
         * The yesterdaysTotal
         */
        public String getYesterdaysTotal() {
            return yesterdaysTotal;
        }

        /**
         *
         * @param yesterdaysTotal
         * The yesterdays_total
         */
        public void setYesterdaysTotal(String yesterdaysTotal) {
            this.yesterdaysTotal = yesterdaysTotal;
        }

        /**
         *
         * @return
         * The yesterdaysReturn
         */
        public String getYesterdaysReturn() {
            return yesterdaysReturn;
        }

        /**
         *
         * @param yesterdaysReturn
         * The yesterdays_return
         */
        public void setYesterdaysReturn(String yesterdaysReturn) {
            this.yesterdaysReturn = yesterdaysReturn;
        }

        /**
         *
         * @return
         * The yesterdaysTransactions
         */
        public String getYesterdaysTransactions() {
            return yesterdaysTransactions;
        }

        /**
         *
         * @param yesterdaysTransactions
         * The yesterdays_transactions
         */
        public void setYesterdaysTransactions(String yesterdaysTransactions) {
            this.yesterdaysTransactions = yesterdaysTransactions;
        }

        /**
         *
         * @return
         * The lastWeekTotal
         */
        public String getLastWeekTotal() {
            return lastWeekTotal;
        }

        /**
         *
         * @param lastWeekTotal
         * The lastWeek_total
         */
        public void setLastWeekTotal(String lastWeekTotal) {
            this.lastWeekTotal = lastWeekTotal;
        }

        /**
         *
         * @return
         * The lastWeekReturn
         */
        public String getLastWeekReturn() {
            return lastWeekReturn;
        }

        /**
         *
         * @param lastWeekReturn
         * The lastWeek_return
         */
        public void setLastWeekReturn(String lastWeekReturn) {
            this.lastWeekReturn = lastWeekReturn;
        }

        /**
         *
         * @return
         * The lastWeekTransactions
         */
        public String getLastWeekTransactions() {
            return lastWeekTransactions;
        }

        /**
         *
         * @param lastWeekTransactions
         * The lastWeek_transactions
         */
        public void setLastWeekTransactions(String lastWeekTransactions) {
            this.lastWeekTransactions = lastWeekTransactions;
        }

        /**
         *
         * @return
         * The lastMonthTotal
         */
        public String getLastMonthTotal() {
            return lastMonthTotal;
        }

        /**
         *
         * @param lastMonthTotal
         * The lastMonth_total
         */
        public void setLastMonthTotal(String lastMonthTotal) {
            this.lastMonthTotal = lastMonthTotal;
        }

        /**
         *
         * @return
         * The lastMonthReturn
         */
        public String getLastMonthReturn() {
            return lastMonthReturn;
        }

        /**
         *
         * @param lastMonthReturn
         * The lastMonth_return
         */
        public void setLastMonthReturn(String lastMonthReturn) {
            this.lastMonthReturn = lastMonthReturn;
        }

        /**
         *
         * @return
         * The lastMonthTransactions
         */
        public String getLastMonthTransactions() {
            return lastMonthTransactions;
        }

        /**
         *
         * @param lastMonthTransactions
         * The lastMonth_transactions
         */
        public void setLastMonthTransactions(String lastMonthTransactions) {
            this.lastMonthTransactions = lastMonthTransactions;
        }

        /**
         *
         * @return
         * The todaysDiscount
         */
        public String getTodaysDiscount() {
            return todaysDiscount;
        }

        /**
         *
         * @param todaysDiscount
         * The todays_discount
         */
        public void setTodaysDiscount(String todaysDiscount) {
            this.todaysDiscount = todaysDiscount;
        }

        /**
         *
         * @return
         * The minSpend
         */
        public String getMinSpend() {
            return minSpend;
        }

        /**
         *
         * @param minSpend
         * The min_spend
         */
        public void setMinSpend(String minSpend) {
            this.minSpend = minSpend;
        }

        /**
         *
         * @return
         * The storeName
         */
        public String getStoreName() {
            return storeName;
        }

        /**
         *
         * @param storeName
         * The store_name
         */
        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }
    }

    public void getHomeWrapper(final Context context) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "dashboard", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<HomeWrapper>() {
                    }.getType();
                    HomeWrapper homeWrapper = gson.fromJson(reader, listType);
                    System.out.println(homeWrapper.getData());

                    getHomeData(context, homeWrapper.getData());
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(HomeWrapper.class.getName(), e);
                }
            }

            @Override
            public void onError(String error) {
                System.out.println(""+error);
            }
        });
    }

    public void getHomeData(Context context, Data mData) {
        Log.i(HomeWrapper.class.getName(), "getData called");

    }



}

