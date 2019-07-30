package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.printer.epos.rtpl.AddOrderFragment;
import com.printer.epos.rtpl.NewOrderFragment;
import com.printer.epos.rtpl.OrderDetailFragment;
import com.printer.epos.rtpl.OrderPreviewFragment;
import com.printer.epos.rtpl.ReceiptBuilder;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.ReturnOrderFragment;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
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
 * Created by android-sristi on 3/4/15.
 */

public class OrderWrapper {

    private static Fragment mFragment;

    @Expose
    private List<OrderInnerWrapper> data = new ArrayList<OrderInnerWrapper>();

    /**
     * @return The data
     */
    public List<OrderInnerWrapper> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<OrderInnerWrapper> data) {
        this.data = data;
    }

    public void getOrderList(final Context context) {

        new WebServiceCalling().callWS(context, UiController.appUrl + "orders", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<OrderWrapper>() {
                    }.getType();
                    OrderWrapper orderWrapper = gson.fromJson(reader, listType);
                    System.out.println(orderWrapper.getData());

                    ArrayList<OrderInnerWrapper> _objCompletedData = new ArrayList<OrderInnerWrapper>();
                    ArrayList<OrderInnerWrapper> _objPendingData = new ArrayList<OrderInnerWrapper>();

                    for (int i = 0; i < orderWrapper.getData().size(); i++) {
                        OrderInnerWrapper innerWrapper = orderWrapper.getData().get(i);

                        if (innerWrapper.getStatus().equalsIgnoreCase("completed") || innerWrapper.getStatus().equalsIgnoreCase("returned"))
                            _objCompletedData.add(innerWrapper);
                        else if (innerWrapper.getStatus().equalsIgnoreCase("on hold"))
                            _objPendingData.add(innerWrapper);
                    }
                    getOrderWrapperList(context, _objCompletedData, _objPendingData);
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(OrderWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    public void getOrderWrapperList(Context context, List<OrderInnerWrapper> completedData, List<OrderInnerWrapper> pendingData) {
        Log.i(CategoryWrapper.class.getName(), "getOrderWrapperList called");

    }

    public static void addOrder(HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;

        new WebServiceCalling().callWS(mContext,
                UiController.appUrl + "orders", map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject responseObj = new JSONObject(response);

                            String msg = responseObj.getString("msg");
                            String orderId = responseObj.getString("data");
                            String receiptNo = responseObj.getString("receipt_no");
                            ReceiptBuilder.receiptNo = receiptNo;

                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

                            if (mFragment instanceof AddOrderFragment)
                                ((AddOrderFragment) mFragment).backClick();
                            if (mFragment instanceof OrderDetailFragment) {
                                ((OrderDetailFragment) mFragment).printReceipt(orderId);
                                ((OrderDetailFragment) mFragment).backClick();
                            }
                            if (mFragment instanceof OrderPreviewFragment) {
                                ((OrderPreviewFragment) mFragment).printReceipt(orderId);
                                //((OrderPreviewFragment) mFragment).finishNewOrder();

                            }
                            if (mFragment instanceof NewOrderFragment) {
                                ((NewOrderFragment) mFragment).printReceipt(orderId);

                            }

                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof AddOrderFragment)
                                                ((AddOrderFragment) mFragment).backClick();
                                            if (mFragment instanceof OrderDetailFragment) {
                                                ((OrderDetailFragment) mFragment).printReceipt();
                                                ((OrderDetailFragment) mFragment).backClick();
                                            }
                                            if (mFragment instanceof OrderPreviewFragment) {
                                                ((OrderPreviewFragment) mFragment).printReceipt();
                                                //((OrderPreviewFragment) mFragment).finishNewOrder();

                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(OrderWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);

                    }
                });
    }

    public static void addOrderWithoutPrint(HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;
        new WebServiceCalling().callWS(mContext,
                UiController.appUrl + "orders", map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

                            if (mFragment instanceof NewOrderFragment) {
                                ((NewOrderFragment) mFragment).backClick();
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(OrderWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }


    public static void returnOrder(String orderId, HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;
        new WebServiceCalling().callWSForUpdate(mContext,
                UiController.appUrl + "orders/" + orderId, map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            JSONObject dataObject = responseObj.getJSONObject("data");

                            String msg = responseObj.getString("msg");
                            String id = dataObject.getString("id");

                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

                            if (mFragment instanceof ReturnOrderFragment) {
                                ((ReturnOrderFragment) mFragment).finishOrderDetail();
                                ((ReturnOrderFragment) mFragment).printAndOpenCashDrawer(id);
                            }

                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof ReturnOrderFragment) {
                                                ((ReturnOrderFragment) mFragment).finishOrderDetail();
                                                ((ReturnOrderFragment) mFragment).printAndOpenCashDrawer();
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(OrderWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public static void getOrderDetail(HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;
        new WebServiceCalling().callWS(mContext,
                UiController.appUrl + "orders", map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);

                            String msg = responseObj.getString("msg");

                            new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof AddOrderFragment)
                                                ((AddOrderFragment) mFragment).backClick();
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(OrderWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }


    public class OrderInnerWrapper {

        @Expose
        private String id;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("customer_name")
        @Expose
        private String customerName;
        @SerializedName("employee_id")
        @Expose
        private String employeeId;
        @Expose
        private String status;
        @SerializedName("gross_amount")
        @Expose
        private String grossAmount;
        @SerializedName("discount_percentage")
        @Expose
        private String discountPercentage;
        @SerializedName("discount_amount")
        @Expose
        private String discountAmount;
        @SerializedName("coupon_code")
        @Expose
        private String couponCode;
        @SerializedName("coupon_discount_amount")
        @Expose
        private Object couponDiscountAmount;
        @SerializedName("tax_percentage")
        @Expose
        private String taxPercentage;
        @SerializedName("tax_amount")
        @Expose
        private String taxAmount;
        @SerializedName("final_amount")
        @Expose
        private String finalAmount;
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("modified_date")
        @Expose
        private String modifiedDate;
        @SerializedName("product_details")
        @Expose
        private List<ProductDetail> productDetails = new ArrayList<ProductDetail>();
        @SerializedName("membership_id")
        @Expose
        private Object membershipId;
        @SerializedName("redeemed_points")
        @Expose
        private String redeemedPoints;
        @SerializedName("earned_loyalty_points")
        @Expose
        private String earnedPoints;
        @SerializedName("receipt_no")
        @Expose
        private String receiptNo;
        @SerializedName("payment_type")
        @Expose
        private String paymentType;

        String cashDue;

        public String getCashDue() {
            return cashDue;
        }

        public void setCashDue(String cashDue) {
            this.cashDue = cashDue;
        }

        public void setMembershipId(Object membershipId) {
            this.membershipId = membershipId;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getReceiptNo() {
            return receiptNo;
        }

        public void setReceiptNo(String receiptNo) {
            this.receiptNo = receiptNo;
        }

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
         * @return The customerId
         */
        public String getCustomerId() {
            return customerId;
        }

        /**
         * @param customerId The customer_id
         */
        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        /**
         * @return The customerName
         */
        public String getCustomerName() {
            return customerName;
        }

        /**
         * @param customerName The customer_name
         */
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        /**
         * @return The employeeId
         */
        public String getEmployeeId() {
            return employeeId;
        }

        /**
         * @param employeeId The employee_id
         */
        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        /**
         * @return The status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return The grossAmount
         */
        public String getGrossAmount() {
            return grossAmount;
        }

        /**
         * @param grossAmount The gross_amount
         */
        public void setGrossAmount(String grossAmount) {
            this.grossAmount = grossAmount;
        }

        /**
         * @return The discountPercentage
         */
        public String getDiscountPercentage() {
            return discountPercentage;
        }

        /**
         * @param discountPercentage The discount_percentage
         */
        public void setDiscountPercentage(String discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        /**
         * @return The discountAmount
         */
        public String getDiscountAmount() {
            return discountAmount;
        }

        /**
         * @param discountAmount The discount_amount
         */
        public void setDiscountAmount(String discountAmount) {
            this.discountAmount = discountAmount;
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
         * @return The couponDiscountAmount
         */
        public Object getCouponDiscountAmount() {
            return couponDiscountAmount;
        }

        /**
         * @param couponDiscountAmount The coupon_discount_amount
         */
        public void setCouponDiscountAmount(Object couponDiscountAmount) {
            this.couponDiscountAmount = couponDiscountAmount;
        }

        /**
         * @return The taxPercentage
         */
        public String getTaxPercentage() {
            return taxPercentage;
        }

        /**
         * @param taxPercentage The tax_percentage
         */
        public void setTaxPercentage(String taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        /**
         * @return The taxAmount
         */
        public String getTaxAmount() {
            return taxAmount;
        }

        /**
         * @param taxAmount The tax_amount
         */
        public void setTaxAmount(String taxAmount) {
            this.taxAmount = taxAmount;
        }

        /**
         * @return The finalAmount
         */
        public String getFinalAmount() {
            return finalAmount;
        }

        /**
         * @param finalAmount The final_amount
         */
        public void setFinalAmount(String finalAmount) {
            this.finalAmount = finalAmount;
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
         * @return The productDetails
         */
        public List<ProductDetail> getProductDetails() {
            return productDetails;
        }

        /**
         * @param productDetails The product_details
         */
        public void setProductDetails(List<ProductDetail> productDetails) {
            this.productDetails = productDetails;
        }

        /**
         * @return The membershipId
         */
        public Object getMembershipId() {
            return membershipId;
        }

        /**
         * @param membershipId The membership_id
         */
        public void setMembershipId(String membershipId) {
            this.membershipId = membershipId;
        }


        public String getRedeemedPoints() {
            return redeemedPoints;
        }

        public void setRedeemedPoints(String redeemedPoints) {
            this.redeemedPoints = redeemedPoints;
        }

        public String getEarnedPoints() {
            return earnedPoints;
        }

        public void setEarnedPoints(String earnedPoints) {
            this.earnedPoints = earnedPoints;
        }
    }

    public class ProductDetail {
        @SerializedName("id")
        @Expose
        private String id;
        @Expose
        private String name;
        @SerializedName("product_price")
        @Expose
        private String productPrice;
        @SerializedName("qty")
        @Expose
        private String qty;
        @SerializedName("return_qty")
        @Expose
        private String returnQty;
        @SerializedName("membership_id")
        @Expose
        private String membershipId;

        private boolean isReturned = false;

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
         * @return The productPrice
         */
        public String getProductPrice() {
            return productPrice;
        }

        /**
         * @param productPrice The product_price
         */
        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        /**
         * @return The qty
         */
        public String getQty() {
            return qty;
        }

        /**
         * @param qty The qty
         */
        public void setQty(String qty) {
            this.qty = qty;
        }

        /**
         * @return The returnQty
         */
        public String getReturnQty() {
            return returnQty;
        }

        /**
         * @param returnQty The return_qty
         */
        public void setReturnQty(String returnQty) {
            this.returnQty = returnQty;
        }

        /**
         * @return The membershipId
         */
        public String getMembershipId() {
            return membershipId;
        }

        /**
         * @param membershipId The membership_id
         */
        public void setMembershipId(String membershipId) {
            this.membershipId = membershipId;
        }

        public boolean isReturned() {
            return isReturned;
        }

        public void setReturned(boolean isReturned) {
            this.isReturned = isReturned;
        }
    }
}

