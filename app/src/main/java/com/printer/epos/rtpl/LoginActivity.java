package com.printer.epos.rtpl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;
import com.printer.epos.rtpl.services.FetchSettingsService;
import com.printer.epos.rtpl.services.SettingsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends Activity {
    private SavePreferences mSavePreferences;

    private EditText usernameET;
    private EditText passwordET;
    private Button loginButton;
    private TextView versionNo;
    public static boolean fromLogin = false;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        fromLogin = true;

        mSavePreferences = UiController.getInstance().getSavePreferences();


        String accessToken = mSavePreferences.get_accessToken();
        if (mSavePreferences.get_ip() != null) {
            UiController.appUrl = null;
            UiController.appUrl = "http://" + mSavePreferences.get_ip().trim() + UiController.appString;

            if (accessToken != null) {
                Intent i = new Intent(LoginActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        } else {
            new CustomDialog().inputIpAddressDialog(this, mSavePreferences);
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        LinearLayout linearParent = (LinearLayout) findViewById(R.id.linear_parent);
        linearParent.setPadding((int) (deviceWidth * .035f), (int) (deviceWidth * .035f), (int) (deviceWidth * .035f), (int) (deviceWidth * .005f));

        usernameET = (EditText) findViewById(R.id.email_idET);
        LinearLayout.LayoutParams usernameET_Params = (LinearLayout.LayoutParams) usernameET.getLayoutParams();
        usernameET_Params.width = (int) (deviceWidth * .4f);
        usernameET_Params.height = (int) (deviceHeight * .08f);
        usernameET_Params.topMargin = (int) (deviceHeight * .02f);
        usernameET.setLayoutParams(usernameET_Params);
        usernameET.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .02f), 0);
        usernameET.setCompoundDrawablePadding((int) (deviceWidth * .015f));

        passwordET = (EditText) findViewById(R.id.passwordET);
        LinearLayout.LayoutParams passwordET_Params = (LinearLayout.LayoutParams) passwordET.getLayoutParams();
        passwordET_Params.width = (int) (deviceWidth * .4f);
        passwordET_Params.height = (int) (deviceHeight * .08f);
        passwordET_Params.topMargin = (int) (deviceHeight * .03f);
        passwordET.setLayoutParams(passwordET_Params);
        passwordET.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .02f), 0);
        passwordET.setCompoundDrawablePadding((int) (deviceWidth * .015f));

        loginButton = (Button) findViewById(R.id.loginButton);
        LinearLayout.LayoutParams loginButton_Params = (LinearLayout.LayoutParams) loginButton.getLayoutParams();
        loginButton_Params.width = (int) (deviceWidth * .4f);
        loginButton_Params.height = (int) (deviceHeight * .08f);
        loginButton_Params.topMargin = (int) (deviceHeight * .03f);
        loginButton_Params.bottomMargin = (int) (deviceHeight * .03f);
        loginButton.setLayoutParams(loginButton_Params);

        Button forgotPassswordButton = (Button) findViewById(R.id.forgotPassswordButton);
        LinearLayout.LayoutParams forgotPassswordButton_Params = (LinearLayout.LayoutParams) forgotPassswordButton.getLayoutParams();
        forgotPassswordButton_Params.width = (int) (deviceWidth * .4f);
        forgotPassswordButton_Params.height = (int) (deviceHeight * .08f);
        forgotPassswordButton_Params.topMargin = (int) (deviceHeight * .03f);
        forgotPassswordButton.setLayoutParams(forgotPassswordButton_Params);
        forgotPassswordButton.setVisibility(View.GONE);

        TextView heading = (TextView) findViewById(R.id.heading);
        RelativeLayout.LayoutParams heading_Params = (RelativeLayout.LayoutParams) heading.getLayoutParams();
        heading_Params.topMargin = (int) (deviceHeight * .15f);
        heading.setLayoutParams(heading_Params);

        versionNo = (TextView) findViewById(R.id.versionNo);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionNo.setText("Ver : "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(LoginActivity.class.getName(), e);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSavePreferences.get_ip() != null)
                    loginClick();
                else
                    new CustomDialog().inputIpAddressDialog(LoginActivity.this, mSavePreferences);
            }
        });

    }

    public void settingClick(View v) {
        new CustomDialog().inputIpAddressDialog(LoginActivity.this, mSavePreferences);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String mEmail;
    private String mPassword;

    void loginClick() {
        Util.hideSoftKeypad(this);
        try {

            mEmail = usernameET.getText().toString();
            mPassword = passwordET.getText().toString();

//            if(new Validation().checkValidation((ViewGroup) findViewById(R.id.linear_parent)))
            if (validation()) {

//                mEmail = "priyanka";
//                mPassword = "password";


                //loginButton.setClickable(false);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("username", mEmail);
                map.put("password", mPassword);

                new WebServiceCalling().callWS(this, UiController.appUrl + "login", map,
                        new WebServiceHandler() {
                            @Override
                            public void onSuccess(String response) {
                                loginButton.setClickable(true);
                                try {

                                    JSONObject responseObj = new JSONObject(response);
                                    Log.d("LoginResponse--->",response);

//                                    {"data":{"created_date":"2015-03-12","role_id":2,"modified_date":"2015-03-18 23:12:45",
//                                    "is_active":1,"password_hash":"$2y$13$cVy.EQNaP6r0JN.dfyuM5OXz6Mde8gwg6dpGrsZ6N2NBcs1Jlnhf2"
//                                     ,"auth_key":"2TZqRig8VT9ztnmtr2EIHGt39GO7Lzbx","password":"password","id":1,
//                                     "first_name":"priyanka","mobile_number":"12345678","dob":"2015-03-03",
//                                     "user_photo":"pri.jpeg","last_name":"agarwal","gender":"Female","email_id":"priyanka",
//                                            "access_token":"4T0CeezoB3_1tpGFYrjgoqpXss0qd0kL"}}


                                    JSONObject data = responseObj.getJSONObject("data");
                                    String role_id = data.getString("role_id");
                                    mSavePreferences.store_roleId(role_id);
                                    String email_id = data.getString("email_id");
                                    mSavePreferences.store_emailId(email_id);
                                    String access_token = data.getString("access_token");
                                    mSavePreferences.store_accessToken(access_token);
                                    String id = data.getString("id");
                                    mSavePreferences.store_id(id);

                                    try {
                                       /* String first_name = data.getString("first_name");
                                        String last_name = data.getString("last_name");
                                        String password = data.getString("password");
                                        String mobile_number = data.getString("mobile_number");
                                        String dob = data.getString("dob");
                                        String gender = data.getString("gender");
                                        String is_active = data.getString("is_active");
                                        String created_date = data.getString("created_date");
                                        String modified_date = data.getString("modified_date");
                                        String auth_key = data.getString("auth_key");
                                        String password_hash = data.getString("password_hash");*/
                                    } catch (Exception ex) {
                                        RetailPosLoging.getInstance().registerLog(LoginActivity.class.getName(), ex);
                                        ex.printStackTrace();
                                    }
                                    UiController.appUrl = null;
                                    UiController.appUrl = "http://" + mSavePreferences.get_ip().trim() + UiController.appString;

                                    if(mSavePreferences.get_roleId().equals("2")) {
                                        Intent intent = new Intent(LoginActivity.this, FetchSettingsService.class);
                                        intent.putExtra("receiverTag", new SettingsReceiver(new Handler()) {
                                            @Override
                                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                                super.onReceiveResult(resultCode, resultData);
                                                if (resultCode == 0){
                                                    if(mDialog != null && mDialog.isShowing()){
                                                        mDialog.cancel();
                                                        startActivity(new Intent(LoginActivity.this, Home.class));
                                                        finish();
                                                    }

                                                }

                                            }
                                        });
                                        mDialog = new ProgressDialog(LoginActivity.this, android.R.style.Theme_Holo_Dialog);
                                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        mDialog.setCancelable(false);
                                        mDialog.setMessage("Initializing settings ...");
                                        mDialog.show();
                                        startService(intent);

                                    }
                                    else
                                    {
                                        startActivity(new Intent(LoginActivity.this, Home.class));
                                        finish();
                                    }

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    RetailPosLoging.getInstance().registerLog(LoginActivity.class.getName(), e);
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                loginButton.setClickable(true);
                            }
                        });
            }
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(LoginActivity.class.getName(), ex);
            ex.printStackTrace();
        }
    }

    boolean validation() {
//        if (!Validation.isValidEmailAddress(mEmail)) {
        if (!Validation.isValidString(mEmail)) {
            usernameET.setError(getString(R.string.field_not_valid));
            usernameET.requestFocus();
            return false;
        } else if (!Validation.isValidString(mPassword)) {
            // EmailET.setError(Html
            // .fromHtml("<span style=\"font-family: Roboto-Bold \">Email ID is required</span>"));
            usernameET.setError(null);
            passwordET.setError(getString(R.string.password_not_valid));
            passwordET.requestFocus();
            return false;
        }

        return true;
    }

    public void shareLogs(View view) {
        shareLogFile();
    }

    private void shareLogFile() {

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + RetailPosLoging.FILE_PATH));
        startActivity(Intent.createChooser(intent, "Share logs"));
    }
}
