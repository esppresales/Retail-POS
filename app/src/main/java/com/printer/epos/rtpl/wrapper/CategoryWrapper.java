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
 * Created by android-pc3 on 23/3/15.
 */
public class CategoryWrapper {


    @Expose
    private List<CategoryListWrapper> data = new ArrayList<CategoryListWrapper>();
    @Expose
    private String totalItems;

    /**
     * @return The data
     */
    public List<CategoryListWrapper> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<CategoryListWrapper> data) {
        this.data = data;
    }

    /**
     * @return The totalItems
     */
    public String getTotalItems() {
        return totalItems;
    }

    /**
     * @param totalItems The totalItems
     */
    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }


    public class CategoryListWrapper {

        private String id;

        private String name;

        private String description;

        @SerializedName("product_count")
        @Expose
        private String productCount;

        @SerializedName("created_date")
        private String createdDate;

        @SerializedName("modified_date")
        private String modifiedDate;

        @SerializedName("mark_as_delete")
        @Expose
        private String markAsDelete;

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
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
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

        /**
         * @return The productCount
         */
        public String getProductCount() {
            return productCount;
        }

        /**
         * @param productCount The product_count
         */
        public void setProductCount(String productCount) {
            this.productCount = productCount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return The markAsDelete
         */
        public String getMarkAsDelete() {
            return markAsDelete;
        }

        /**
         * @param markAsDelete The mark_as_delete
         */
        public void setMarkAsDelete(String markAsDelete) {
            this.markAsDelete = markAsDelete;
        }

    }

    private void confirmDialog(final Context context, String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        getProductCategoryList(context);
       /* new CustomDialog().showOneButtonAlertDialog(context, null,
                msg, context.getString(R.string.ok_button), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                    @Override
                    public void onPositiveClick() {
                        getProductCategoryList(context);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });*/
    }

    public void deleteProductCategory(final Context mContext, String url) {
        new WebServiceCalling().callWSForDelete(mContext, url,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject responseObj = null;
                        try {
                            responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            confirmDialog(mContext, msg);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CategoryWrapper.class.getName(), e);
                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    public void editProductCategory(final Context mContext, String url, HashMap<String, Object> map) {
        new WebServiceCalling().callWSForUpdate(mContext, url, map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject responseObj = null;
                        try {
                            responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                            getProductCategoryList(mContext);
                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            getProductCategoryList(mContext);
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/
                            System.out.println(msg);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CategoryWrapper.class.getName(), e);
                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    public void saveProductCategory(final Context context, String url, HashMap<String, Object> map) {
        new WebServiceCalling().callWS(context, url, map, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                    getProductCategoryList(context);
                   /* new CustomDialog().showOneButtonAlertDialog(context, "", msg, "OK", android.R.drawable.ic_dialog_alert,
                            new DialogButtonListener() {
                                @Override
                                public void onPositiveClick() {
                                    getProductCategoryList(context);
                                }

                                @Override
                                public void onNegativeClick() {

                                }
                            });*/
                    System.out.println(msg);


                } catch (JSONException e) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(CategoryWrapper.class.getName(), e);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getProductCategoryList(final Context context) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "categories", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<CategoryWrapper>() {
                    }.getType();
                    CategoryWrapper productWrapper = gson.fromJson(reader, listType);
                    System.out.println(productWrapper.getData());

                    for (int i = 0; i < productWrapper.getData().size(); i++) {
                        CategoryWrapper.CategoryListWrapper wrapper = productWrapper.getData().get(i);
                        data.add(wrapper);
                    }
                    getCategoryWrapperList(context, data);
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(CategoryWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {
        Log.i(CategoryWrapper.class.getName(), "getCategoryWrapperList called");

    }


}

