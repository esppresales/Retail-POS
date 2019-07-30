package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by android-pc3 on 3/4/15.
 */
public class CouponCodeWrapper {

    @Expose
    private List<CouponCodeData> data = new ArrayList<CouponCodeData>();
    private RefreshList mRefreshList = null;


    /**
     * @return The data
     */
    public List<CouponCodeData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<CouponCodeData> data) {
        this.data = data;
    }

    public class CouponCodeData {

        @Expose
        private String id;
        @SerializedName("coupon_code")
        @Expose
        private String couponCode;
        @Expose
        private String amount;
        @SerializedName("validity_from_date")
        @Expose
        private String validityFromDate;
        @SerializedName("validity_to_date")
        @Expose
        private String validityToDate;
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("modified_date")
        @Expose
        private String modifiedDate;

        /**
         * @return The id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return The couponCode
         */
        public String getCouponCode() {
            return couponCode;
        }

        /**
         * @param couponCode The coupon_code
         */
        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }

        /**
         * @return The amount
         */
        public String getAmount() {
            return amount;
        }

        /**
         * @param amount The amount
         */
        public void setAmount(String amount) {
            this.amount = amount;
        }

        /**
         * @return The validityFromDate
         */
        public String getValidityFromDate() {
            return validityFromDate;
        }

        /**
         * @param validityFromDate The validity_from_date
         */
        public void setValidityFromDate(String validityFromDate) {
            this.validityFromDate = validityFromDate;
        }

        /**
         * @return The validityToDate
         */
        public String getValidityToDate() {
            return validityToDate;
        }

        /**
         * @param validityToDate The validity_to_date
         */
        public void setValidityToDate(String validityToDate) {
            this.validityToDate = validityToDate;
        }

        /**
         * @return The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         * @param createdDate The created_date
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         * @return The modifiedDate
         */
        public String getModifiedDate() {
            return modifiedDate;
        }

        /**
         * @param modifiedDate The modified_date
         */
        public void setModifiedDate(String modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        @Override
        public String toString() {
            return couponCode;
        }
    }

    public void getCouponCodeList(final Context context, RefreshList refreshList) {
        mRefreshList = refreshList;
        new WebServiceCalling().callWS(context, UiController.appUrl + "coupons", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<CouponCodeWrapper>() {
                    }.getType();
                    CouponCodeWrapper setingsWrapper = gson.fromJson(reader, listType);

                    for (int i = 0; i < setingsWrapper.getData().size(); i++) {
                        CouponCodeWrapper.CouponCodeData wrapper = setingsWrapper.getData().get(i);
                        data.add(wrapper);
                    }
                    if (mRefreshList != null)
                        mRefreshList.onListRefresh(context, data);
                    else
                        getCoupons(context, data);
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(CouponCodeWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    public void getCoupons(Context context, List<CouponCodeData> data) {
        Log.i(CategoryWrapper.class.getName(), "getCategoryWrapperList called");

    }

    public void deleteProduct(String id, final Context mContext, RefreshList refreshList) {

        mRefreshList = refreshList;
        new WebServiceCalling().callWSForDelete(mContext, UiController.appUrl + "coupons/" + id,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            getCouponCodeList(mContext, mRefreshList);

                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            getCouponCodeList(mContext, mRefreshList);
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });

*/
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CouponCodeWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public void addCouponCode(final Context mContext, HashMap<String, Object> map, RefreshList refreshList) {
        mRefreshList = refreshList;
        new WebServiceCalling().callWS(mContext, UiController.appUrl + "coupons", map, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    getCouponCodeList(mContext, mRefreshList);
                   /* new CustomDialog().showOneButtonAlertDialog(mContext, "", msg, "OK", android.R.drawable.ic_dialog_alert,
                            new DialogButtonListener() {
                                @Override
                                public void onPositiveClick() {
                                    getCouponCodeList(mContext, mRefreshList);
                                }

                                @Override
                                public void onNegativeClick() {

                                }
                            });*/
                    System.out.println(msg);


                } catch (JSONException e) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(CouponCodeWrapper.class.getName(), e);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void editCouponCode(final Context mContext, HashMap<String, Object> map, String id, RefreshList refreshList) {
        mRefreshList = refreshList;
        new WebServiceCalling().callWSForUpdate(mContext, UiController.appUrl + "coupons/" + id, map, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    getCouponCodeList(mContext, mRefreshList);
                    /*new CustomDialog().showOneButtonAlertDialog(mContext, "", msg, "OK", android.R.drawable.ic_dialog_alert,
                            new DialogButtonListener() {
                                @Override
                                public void onPositiveClick() {
                                    getCouponCodeList(mContext, mRefreshList);
                                }

                                @Override
                                public void onNegativeClick() {

                                }
                            });*/
                    System.out.println(msg);


                } catch (JSONException e) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(CouponCodeWrapper.class.getName(), e);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public interface RefreshList {
        public void onListRefresh(Context context, List<CouponCodeData> data);
    }
}
