package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.printer.epos.rtpl.AddStaffFragment;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.StaffFragment;
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
public class StaffWrapper {

    public String id;
    public String image;
    public String firstName;
    public String lastName;
    public String userName;
    public String email;
    public String password;
    public String mobileNumber;
    public String dob;
    public String gender;
    public String role;
    public String address;
    public String postalCode;
    public String countryID;
    public String nricPassport;
    public String isActive;
    public String last_login;
    public String createdDate;
    public String modifiedDate;

    public ArrayList<StaffTransactionWrapper> transactionList;

    private static List<StaffWrapper> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNricPassport() {
        return nricPassport;
    }

    public void setNricPassport(String nricPassport) {
        this.nricPassport = nricPassport;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
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

    public ArrayList<StaffTransactionWrapper> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(JSONArray transactionList) {
        this.transactionList = new ArrayList<StaffTransactionWrapper>();

        for (int i = 0; i < transactionList.length(); i++) {

            try {
                JSONObject obj = transactionList.getJSONObject(i);
                StaffTransactionWrapper _objWrapper = new StaffTransactionWrapper();
                _objWrapper.setDate(obj.getString("created_date"));
                _objWrapper.setAmount(obj.getString("final_amount"));
                _objWrapper.setOrder_id(obj.getString("id"));
                _objWrapper.setStatus(obj.getString("status"));
                this.transactionList.add(_objWrapper);

            } catch (Exception ex) {
                ex.printStackTrace();
                RetailPosLoging.getInstance().registerLog(StaffWrapper.class.getName(), ex);
            }
        }

    }

    public static void addStaff(HashMap map, final Context mContext, Fragment fragment) {
        mFragment = fragment;
        new WebServiceCalling().callWS(mContext,
                UiController.appUrl + "users", map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");

                            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                            if (mFragment instanceof AddStaffFragment)
                                ((AddStaffFragment) mFragment).backClick();

                          /*  new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {

                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(StaffWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    private static Fragment mFragment;

    public static void getStaffList(HashMap map, final Context mContext, Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWS(mContext, UiController.appUrl + "users", map,
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
                            RetailPosLoging.getInstance().registerLog(StaffWrapper.class.getName(), e);
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
        list = new ArrayList<StaffWrapper>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                StaffWrapper wrapper = new StaffWrapper();


                JSONObject jObj = arr.getJSONObject(i);
                wrapper.setId(jObj.getString("id"));
                wrapper.setFirstName(jObj.getString("first_name"));
                wrapper.setLastName(jObj.getString("last_name"));
                wrapper.setEmail(jObj.getString("email_id"));
                wrapper.setPassword("Missing in WS");
                wrapper.setMobileNumber(jObj.getString("mobile_number"));
                wrapper.setDob(jObj.getString("dob"));
                wrapper.setGender(jObj.getString("gender"));
                wrapper.setRole(jObj.getString("role_id"));
                wrapper.setImage(jObj.getString("user_photo"));
                wrapper.setUserName(jObj.getString("username"));
                wrapper.setNricPassport(jObj.getString("nric"));
                wrapper.setCountryID(jObj.getString("country_id"));
                wrapper.setPostalCode(jObj.getString("postal_code"));
                wrapper.setAddress(jObj.getString("address"));
                wrapper.setIsActive(jObj.getString("is_active"));
                wrapper.setCreatedDate(jObj.getString("created_date"));
                wrapper.setModifiedDate(jObj.getString("modified_date"));
                wrapper.setLast_login(jObj.getString("last_login"));
//                String auth_key = jObj.getString("auth_key");
//                String access_token = jObj.getString("access_token");
                wrapper.setTransactionList(jObj.getJSONArray("transactions"));

                list.add(wrapper);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(StaffWrapper.class.getName(), ex);
        }
        setStaffAdapter();
    }

    public static void setStaffAdapter() {
        if (mFragment instanceof StaffFragment) {
            ((StaffFragment) mFragment).setStaffAdapter(list);
        }
    }

    public static void updateStatus(String id, final Context mContext, final Fragment fragment) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        mFragment = fragment;
        new WebServiceCalling().callWSForStaffUpdate(mContext,
                UiController.appUrl + "users/" + id, map,
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
                            RetailPosLoging.getInstance().registerLog(StaffWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public static void updateStaff(String id, HashMap<String, Object> map, final Context mContext, final Fragment fragment) {

        mFragment = fragment;
        new WebServiceCalling().callWSForUpdate(mContext, UiController.appUrl + "users/" + id, map,
                new WebServiceHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {

                            JSONObject responseObj = new JSONObject(response);
                            String msg = responseObj.getString("msg");

                            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                            if (mFragment instanceof AddStaffFragment)
                                ((AddStaffFragment) mFragment).backClick();

                          /*  new CustomDialog().showOneButtonAlertDialog(mContext, null,
                                    msg, "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                        @Override
                                        public void onPositiveClick() {

                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });*/


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            RetailPosLoging.getInstance().registerLog(StaffWrapper.class.getName(), e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("" + error);
                    }
                });
    }

    public class StaffTransactionWrapper {
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
