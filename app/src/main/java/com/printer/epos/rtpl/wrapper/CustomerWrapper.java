package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.printer.epos.rtpl.AddCustomerFragment;
import com.printer.epos.rtpl.CustomerFragment;
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
public class CustomerWrapper {

    public String id;
    public String firstName;
    public String lastName;
    public String membershipId;
    public String email;
    public String mobileNo;
    public String nricPassport;
    public String dob;
    public String gender;
    public String membershipValidity;
    public String membershipExpiry;
    public String membershipType;
    public String address;
    public String earnedLoyaltyPoints;
    public String createdDate;
    public String modifiedDate;
    public String postalCode;
    public String countryID;
    public ArrayList<CustomerTransactionWrapper> transactionList;

    private CustomerWrapper mWrapper;
    private static List<CustomerWrapper> list;

    private static Fragment mFragment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMembershipValidity() {
        return membershipValidity;
    }

    public void setMembershipValidity(String membershipValidity) {
        this.membershipValidity = membershipValidity;
    }

    public String getMembershipExpiry() {
        return membershipExpiry;
    }

    public void setMembershipExpiry(String membershipExpiry) {
        this.membershipExpiry = membershipExpiry;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getNricPassport() {
        return nricPassport;
    }

    public void setNricPassport(String nricPassport) {
        this.nricPassport = nricPassport;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEarnedLoyaltyPoints() {
        return earnedLoyaltyPoints;
    }

    public void setEarnedLoyaltyPoints(String earnedLoyaltyPoints) {
        this.earnedLoyaltyPoints = earnedLoyaltyPoints;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public ArrayList<CustomerTransactionWrapper> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(JSONArray transactionList) {
        this.transactionList = new ArrayList<CustomerTransactionWrapper>();

        for (int i = 0; i < transactionList.length(); i++) {

            try {
                JSONObject obj = transactionList.getJSONObject(i);
                CustomerTransactionWrapper _objWrapper = new CustomerTransactionWrapper();
                _objWrapper.setDate(obj.getString("created_date"));
                _objWrapper.setAmount(obj.getString("final_amount"));
                _objWrapper.setOrder_id(obj.getString("id"));
                _objWrapper.setStatus(obj.getString("status"));
                this.transactionList.add(_objWrapper);

            } catch (Exception ex) {
                ex.printStackTrace();
                RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), ex);
            }
        }

    }

    public static void setWrapper(JSONObject data) {

    }

    public static void setWrapperForListAdapter(JSONArray arr) {
//        "data":{"created_date":"2015-03-23","id":6,"first_name":"Puneet","mobile_number":"9461815403","address":"mansarovar","dob":"1990-08-18","last_name":"gupta","gender":"male","email_id":"puneet@ranosys.com","membership_...
        list = new ArrayList<CustomerWrapper>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                CustomerWrapper wrapper = new CustomerWrapper();
                JSONObject jObj = arr.getJSONObject(i);
                wrapper.setId(jObj.getString("id"));
                wrapper.setFirstName(jObj.getString("first_name"));
                wrapper.setLastName(jObj.getString("last_name"));
                wrapper.setMembershipId(jObj.getString("membership_id"));
                wrapper.setEmail(jObj.getString("email_id"));
                wrapper.setMobileNo(jObj.getString("mobile_number"));
                wrapper.setNricPassport(jObj.getString("nric"));
                wrapper.setDob(jObj.getString("dob"));
                wrapper.setGender(jObj.getString("gender"));
                wrapper.setMembershipValidity(jObj.getString("membership_validity"));
                wrapper.setMembershipType(jObj.getString("membership_type"));
                wrapper.setAddress(jObj.getString("address"));
                wrapper.setPostalCode(jObj.getString("postal_code"));
                wrapper.setCountryID(jObj.getString("country_id"));
                wrapper.setEarnedLoyaltyPoints(jObj.getString("earned_loyalty_points"));
                wrapper.setCreatedDate(jObj.getString("created_date"));
                wrapper.setModifiedDate(jObj.getString("modified_date"));
                wrapper.setMembershipExpiry(jObj.getString("membership_expiry"));
                wrapper.setTransactionList(jObj.getJSONArray("transactions"));
                list.add(wrapper);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), ex);
        }
        setCustomerAdapter();
    }

    public static void setCustomerAdapter() {
        if (mFragment instanceof CustomerFragment) {
            ((CustomerFragment) mFragment).setCustomerAdapter(list);
        }
    }

    public static void addCustomer(HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;

        new WebServiceCalling().callWS(mContext,
                UiController.appUrl + "customers", map, new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);

                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            if (mFragment instanceof AddCustomerFragment)
                                ((AddCustomerFragment) mFragment).backClick();

                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof AddCustomerFragment)
                                                ((AddCustomerFragment) mFragment).backClick();
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/

                            JSONObject data = responseObj.getJSONObject("data");
                            setWrapper(data);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public static void getCustomerList(HashMap map, final Context mContext, Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWS(mContext, UiController.appUrl + "customers", map,
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
                            RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public void getCustomer(final String membershipId, final Context mContext) {


        new WebServiceCalling().callWS(mContext, UiController.appUrl + "customers/" + membershipId, null,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);

                            JSONObject data = responseObj.getJSONObject("data");

                            String earnedLoyaltyPoints = data.getString("earned_loyalty_points");
                            String membership_id = data.getString("membership_id");
                            if (earnedLoyaltyPoints.trim().equals("null"))
                                earnedLoyaltyPoints = "0";

                            String firstName = data.getString("first_name");
                            String lastName = data.getString("last_name");

                            getCustomerWrapper(mContext, membership_id, firstName + " " + lastName, earnedLoyaltyPoints);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public void getCustomerWrapper(Context context,String membershipId, String customerName, String earnedLoyaltyPoints) {
        Log.i(CategoryWrapper.class.getName(), "getCategoryWrapperList called");

    }

    public static void deleteCustomer(String id, final Context mContext, final Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWSForDelete(mContext, UiController.appUrl + "customers/" + id,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            getCustomerList(null, mContext, fragment);
                            /*new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            getCustomerList(null, mContext, fragment);
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });
*/

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public static void updateCustomer(String id, HashMap<String, Object> map, final Context mContext, final Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWSForUpdate(mContext, UiController.appUrl + "customers/" + id, map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            if (mFragment instanceof AddCustomerFragment) {
                                ((AddCustomerFragment) mFragment).backClick();
                            }
                           /* new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            if (mFragment instanceof AddCustomerFragment) {
                                                ((AddCustomerFragment) mFragment).backClick();
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(CustomerWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public class CustomerTransactionWrapper {
        public String id;
        public String date;
        public String amount;
        public String order_id;
        public String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}

