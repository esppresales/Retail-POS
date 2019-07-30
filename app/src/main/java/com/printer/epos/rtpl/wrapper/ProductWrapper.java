package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.printer.epos.rtpl.AddOrderFragment;
import com.printer.epos.rtpl.AddOrderItemFragment;
import com.printer.epos.rtpl.AddProductFragment;
import com.printer.epos.rtpl.OrderOnHoldFragment;
import com.printer.epos.rtpl.ProductFragment;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ranosys-puneet on 16/3/15.
 */
public class ProductWrapper extends ProductOrderWrapper {

    private static List<ProductWrapper> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductCost() {
        return productCost;
    }

    public void setProductCost(String productCost) {
        this.productCost = productCost;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStockLocation() {
        return stockLocation;
    }

    public void setStockLocation(String stockLocation) {
        this.stockLocation = stockLocation;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMinStockAlertQuantity() {
        return minStockAlertQuantity;
    }

    public void setMinStockAlertQuantity(String minStockAlertQuantity) {
        this.minStockAlertQuantity = minStockAlertQuantity;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    private static void setProductWrapper(JSONObject data) {

    }

    public static void addProduct(HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;
        String u= UiController.appUrl + "products";
        new WebServiceCalling().callWS(mContext,u, map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);

                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            if (mFragment instanceof AddProductFragment)
                                ((AddProductFragment) mFragment).backClick();

                          /*  new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof AddProductFragment)
                                                ((AddProductFragment) mFragment).backClick();
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            RetailPosLoging.getInstance().registerLog(ProductWrapper.class.getName(), e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    private static Fragment mFragment;

    public static void getProductList(HashMap map, final Context mContext, Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWS(mContext, UiController.appUrl + "products", map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);

                            JSONArray data = responseObj.getJSONArray("data");
                            setWrapperForListAdapter(data);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(ProductWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public static void setWrapperForListAdapter(JSONArray arr) {
//        "data":{"created_date":"2015-03-23","id":6,"first_name":"Puneet","mobile_number":"9461815403","address":"mansarovar","dob":"1990-08-18","last_name":"gupta","gender":"male","email_id":"puneet@ranosys.com","membership_...
        list = new ArrayList<ProductWrapper>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                ProductWrapper wrapper = new ProductWrapper();

                JSONObject jObj = arr.getJSONObject(i);
                wrapper.setId(jObj.getString("id"));
                wrapper.setProductName(jObj.getString("name"));
                wrapper.setProductDescription(jObj.getString("description"));
                wrapper.setProductCost(jObj.getString("cost_price"));
                wrapper.setSellingPrice(jObj.getString("selling_price"));
                wrapper.setQuantity(jObj.getString("quantity"));
                wrapper.setProductCategory(jObj.getString("category_id"));
                wrapper.setSupplier(jObj.getString("supplier"));
                wrapper.setMinStockAlertQuantity(jObj.getString("min_stock_alert_qty"));
                wrapper.setStockLocation(jObj.getString("stock_location"));
                wrapper.setProductImage(jObj.getString("image"));
                wrapper.setAddedBy(jObj.getString("added_by"));
                wrapper.setBarcode(jObj.getString("barcode"));
              //  wrapper.setReceiptNo(jObj.getString("receipt_no"));

                list.add(wrapper);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ProductWrapper.class.getName(), ex);
        }
        setProductAdapter();
    }

    public static void setProductAdapter() {
        if (mFragment instanceof ProductFragment) {
            ((ProductFragment) mFragment).setProductAdapter(list);
        }
        if (mFragment instanceof AddOrderFragment) {
            ((AddOrderFragment) mFragment).setProductList(list);
        }
        if (mFragment instanceof OrderOnHoldFragment) {
            ((OrderOnHoldFragment) mFragment).setProductList(list);
        }

        if (mFragment instanceof AddProductFragment) {
            ((AddProductFragment) mFragment).setProductAdapter(list);
        }

        if (mFragment instanceof AddOrderItemFragment) {
            ((AddOrderItemFragment) mFragment).setProductList(list);
        }
    }

    public static void deleteProduct(String id, final Context mContext, final Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWSForDelete(mContext, UiController.appUrl + "products/" + id,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            getProductList(null, mContext, fragment);
                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,

                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            getProductList(null, mContext, fragment);
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });
*/

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(ProductWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public static void updateProduct(String id, HashMap<String, Object> map, final Context mContext, final Fragment fragment) {

        mFragment = fragment;
        Log.i("url for update product",UiController.appUrl + "products/" + id);
        new WebServiceCalling().callWSForUpdate(mContext, UiController.appUrl + "products/" + id, map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            if (mFragment instanceof AddProductFragment) {
                                ((AddProductFragment) mFragment).backClick();
                            }
                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof AddProductFragment) {
                                                ((AddProductFragment) mFragment).backClick();
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });
*/

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(ProductWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                        /*new CustomDialog().showOneButtonAlertDialog(mContext,null,"Category is disabled, Please select another one.",
                                mContext.getResources().getString(R.string.ok_button),0,null);*/
                    }
                });
    }

}
