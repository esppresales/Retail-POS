package com.printer.epos.rtpl;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.CountrySpinnerAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.CountryWrapper;
import com.printer.epos.rtpl.wrapper.StaffWrapper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class AddStaffFragment extends BaseFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    private boolean isUpdated = false;
    private static String imagePath;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddStaffFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setCountrySpinnerAdapter();
    }

    private Home hContext;

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setEnabledButtons(false, true, true, false);

        hContext.backButton.setOnClickListener(this);
        hContext.saveButton.setOnClickListener(this);

        if (wrapper != null) {
            hContext.saveButton.setText("UPDATE");
            hContext.setTitleText("Update Staff");
            update = true;
        } else {
            hContext.saveButton.setText(getString(R.string.save));
            hContext.setTitleText(getString(R.string.add_staff));
            update = true;
        }

    }

    private int deviceWidth;
    private int deviceHeight;

    private SavePreferences mSavePreferences;

    private EditText firstNameET, lastNameET, emailIdET, usernameET, passwordET, mobileNumberET,
            nricPassportET, dobET, addressET, postalCodeET;

    private Spinner countryET;
    private ImageView img;
    private RadioGroup genderRadioGroup, roleRadioGroup;


    private ImageLoader mImageLoader;
    private View rootView;
    private StaffWrapper wrapper;
    private boolean update = false;

    private DisplayImageOptions options1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_staff, container, false);
        imagePath = null;
        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.container);
        setupUI(rl, getActivity());

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        mImageLoader = ImageLoader.getInstance();
        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(false).cacheInMemory(false)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();


//        ScrollView formScroll = (ScrollView) rootView.findViewById(R.id.formScroll);
//        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
//        form_param.leftMargin = (int) (deviceWidth * .02f);
//        form_param.rightMargin = (int) (deviceWidth * .02f);
//        form_param.bottomMargin = (int) (deviceHeight * .03f);
//        formScroll.setLayoutParams(form_param);

        img = (ImageView) rootView.findViewById(R.id.img);
        RelativeLayout.LayoutParams img_param = (RelativeLayout.LayoutParams) img.getLayoutParams();
        img_param.height = (int) (deviceHeight * .15f);
        img_param.width = (int) (deviceHeight * .15f);
        img_param.topMargin = (int) (deviceHeight * .02f);
        img.setLayoutParams(img_param);
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // showPicker();
                selectImage();
            }
        });

        TextView first_name = (TextView) rootView.findViewById(R.id.first_name);
        first_name.getLayoutParams().width = (int) (deviceWidth * .4f);
        first_name.setPadding((int) (deviceWidth * .0f), (int) (deviceHeight * .04f), 0, 0);

        firstNameET = (EditText) rootView.findViewById(R.id.first_nameET);
        RelativeLayout.LayoutParams firstNameET_param = (RelativeLayout.LayoutParams) firstNameET.getLayoutParams();
        firstNameET_param.height = (int) (deviceHeight * .07f);
        firstNameET_param.width = (int) (deviceWidth * .4f);
        firstNameET.setLayoutParams(firstNameET_param);
        firstNameET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView last_name = (TextView) rootView.findViewById(R.id.last_name);
        last_name.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .04f), 0, 0);

        lastNameET = (EditText) rootView.findViewById(R.id.last_nameET);
        RelativeLayout.LayoutParams lastNameET_param = (RelativeLayout.LayoutParams) lastNameET.getLayoutParams();
        lastNameET_param.height = (int) (deviceHeight * .07f);
        lastNameET_param.width = (int) (deviceWidth * .4f);
        lastNameET_param.leftMargin = (int) (deviceWidth * .06f);
        lastNameET.setLayoutParams(lastNameET_param);
        lastNameET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);


        TextView email_id = (TextView) rootView.findViewById(R.id.email_id);
        email_id.getLayoutParams().width = (int) (deviceWidth * .4f);
        email_id.setPadding((int) (deviceWidth * .0f), (int) (deviceHeight * .02f), 0, 0);

        emailIdET = (EditText) rootView.findViewById(R.id.email_idET);
        RelativeLayout.LayoutParams emailIdET_param = (RelativeLayout.LayoutParams) emailIdET.getLayoutParams();
        emailIdET_param.height = (int) (deviceHeight * .07f);
        emailIdET_param.width = (int) (deviceWidth * .4f);
        emailIdET.setLayoutParams(emailIdET_param);
        emailIdET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView username = (TextView) rootView.findViewById(R.id.username);
        username.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        usernameET = (EditText) rootView.findViewById(R.id.usernameET);
        RelativeLayout.LayoutParams usernameET_param = (RelativeLayout.LayoutParams) usernameET.getLayoutParams();
        usernameET_param.height = (int) (deviceHeight * .07f);
        usernameET_param.width = (int) (deviceWidth * .4f);
        usernameET_param.leftMargin = (int) (deviceWidth * .06f);
        usernameET.setLayoutParams(usernameET_param);
        usernameET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView password = (TextView) rootView.findViewById(R.id.password);
        password.getLayoutParams().width = (int) (deviceWidth * .4f);
        password.setPadding((int) (deviceWidth * .0f), (int) (deviceHeight * .02f), 0, 0);

        passwordET = (EditText) rootView.findViewById(R.id.password__ET);
        RelativeLayout.LayoutParams passwordET_param = (RelativeLayout.LayoutParams) passwordET.getLayoutParams();
        passwordET_param.height = (int) (deviceHeight * .07f);
        passwordET_param.width = (int) (deviceWidth * .4f);
        passwordET.setLayoutParams(passwordET_param);
        passwordET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView mobile_no = (TextView) rootView.findViewById(R.id.mobile_no);
        mobile_no.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        mobileNumberET = (EditText) rootView.findViewById(R.id.mobile_noET);
        RelativeLayout.LayoutParams mobileNumberET_param = (RelativeLayout.LayoutParams) mobileNumberET.getLayoutParams();
        mobileNumberET_param.height = (int) (deviceHeight * .07f);
        mobileNumberET_param.width = (int) (deviceWidth * .4f);
        mobileNumberET_param.leftMargin = (int) (deviceWidth * .06f);
        mobileNumberET.setLayoutParams(mobileNumberET_param);
        mobileNumberET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView nricPassport = (TextView) rootView.findViewById(R.id.nricPassport);
        nricPassport.getLayoutParams().width = (int) (deviceWidth * .19f);
        nricPassport.setPadding((int) (deviceWidth * .0f), (int) (deviceHeight * .02f), 0, 0);

        nricPassportET = (EditText) rootView.findViewById(R.id.nricPassportET);
        RelativeLayout.LayoutParams nricPassportET_param = (RelativeLayout.LayoutParams) nricPassportET.getLayoutParams();
        nricPassportET_param.height = (int) (deviceHeight * .07f);
        nricPassportET_param.width = (int) (deviceWidth * .19f);
        nricPassportET.setLayoutParams(nricPassportET_param);
        nricPassportET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView date_of_birth = (TextView) rootView.findViewById(R.id.date_of_birth);
        date_of_birth.getLayoutParams().width = (int) (deviceWidth * .21f);
        date_of_birth.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        dobET = (EditText) rootView.findViewById(R.id.date_of_birthET);
        RelativeLayout.LayoutParams dobET_param = (RelativeLayout.LayoutParams) dobET.getLayoutParams();
        dobET_param.height = (int) (deviceHeight * .07f);
        dobET_param.width = (int) (deviceWidth * .19f);
        dobET_param.leftMargin = (int) (deviceWidth * .02f);
        dobET.setLayoutParams(dobET_param);
        dobET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);
        dobET.setOnClickListener(this);

        TextView gender = (TextView) rootView.findViewById(R.id.gender);
        gender.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        genderRadioGroup = (RadioGroup) rootView.findViewById(R.id.gender_radio_group);

        RadioButton maleRadio = (RadioButton) rootView.findViewById(R.id.male_radio);
        maleRadio.setCompoundDrawablePadding((int) (deviceWidth * .01f));
        RadioGroup.LayoutParams maleRadio_param = (RadioGroup.LayoutParams) maleRadio.getLayoutParams();
//        maleRadio_param.width = (int)(deviceHeight*.05f);
        maleRadio_param.leftMargin = (int) (deviceWidth * .06f);
        maleRadio_param.height = (int) (deviceHeight * .07f);
        maleRadio.setLayoutParams(maleRadio_param);

        RadioButton femaleRadio = (RadioButton) rootView.findViewById(R.id.female_radio);
        femaleRadio.setCompoundDrawablePadding((int) (deviceWidth * .01f));
        RadioGroup.LayoutParams femaleRadio_param = (RadioGroup.LayoutParams) femaleRadio.getLayoutParams();
//        femaleRadio_param.width = (int) (deviceHeight * .05f);
        femaleRadio_param.height = (int) (deviceHeight * .07f);
        femaleRadio_param.leftMargin = (int) (deviceWidth * .03f);
        femaleRadio.setLayoutParams(femaleRadio_param);

        TextView role = (TextView) rootView.findViewById(R.id.role);
        role.getLayoutParams().width = (int) (deviceWidth * .4f);
        role.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        roleRadioGroup = (RadioGroup) rootView.findViewById(R.id.role_radio_group);
        roleRadioGroup.getLayoutParams().width = (int) (deviceWidth * .4f);

        RadioButton staffRadio = (RadioButton) rootView.findViewById(R.id.staff_radio);
        staffRadio.setCompoundDrawablePadding((int) (deviceWidth * .01f));
        RadioGroup.LayoutParams staffRadio_param = (RadioGroup.LayoutParams) staffRadio.getLayoutParams();
//        maleRadio_param.width = (int)(deviceHeight*.05f);
        staffRadio_param.leftMargin = (int) (deviceWidth * .03f);
        staffRadio_param.height = (int) (deviceHeight * .07f);
        staffRadio.setLayoutParams(staffRadio_param);

        RadioButton managerRadio = (RadioButton) rootView.findViewById(R.id.manager_radio);
        managerRadio.setCompoundDrawablePadding((int) (deviceWidth * .01f));
        RadioGroup.LayoutParams managerRadio_param = (RadioGroup.LayoutParams) managerRadio.getLayoutParams();
//        femaleRadio_param.width = (int) (deviceHeight * .05f);
        managerRadio_param.height = (int) (deviceHeight * .07f);
       // managerRadio_param.leftMargin = (int) (deviceWidth * .0f);
        managerRadio.setLayoutParams(managerRadio_param);

        TextView address = (TextView) rootView.findViewById(R.id.address);
        address.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        addressET = (EditText) rootView.findViewById(R.id.addressET);
        RelativeLayout.LayoutParams addressET_param = (RelativeLayout.LayoutParams) addressET.getLayoutParams();
        addressET_param.height = (int) (deviceHeight * .14f);
        addressET_param.width = (int) (deviceWidth * .38f);
        addressET_param.leftMargin = (int) (deviceWidth * .06f);
        addressET.setLayoutParams(addressET_param);
        addressET.setPadding((int) (deviceWidth * .01f), (int) (deviceHeight * .01f), (int) (deviceWidth * .01f), (int) (deviceHeight * .01f));

        TextView postal_code = (TextView) rootView.findViewById(R.id.postal_code);
        postal_code.getLayoutParams().width = (int) (deviceWidth * .4f);
        postal_code.setPadding((int) (deviceWidth * .0f), (int) (deviceHeight * .02f), 0, 0);

        postalCodeET = (EditText) rootView.findViewById(R.id.postal_codeET);
        RelativeLayout.LayoutParams postalCodeET_param = (RelativeLayout.LayoutParams) postalCodeET.getLayoutParams();
        postalCodeET_param.height = (int) (deviceHeight * .07f);
        postalCodeET_param.width = (int) (deviceWidth * .4f);
        postalCodeET_param.bottomMargin = (int) (deviceHeight * .1f);
        postalCodeET.setLayoutParams(postalCodeET_param);
        postalCodeET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView country = (TextView) rootView.findViewById(R.id.country);
        country.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        countryET = (Spinner) rootView.findViewById(R.id.countryET);
        RelativeLayout.LayoutParams countryET_param = (RelativeLayout.LayoutParams) countryET.getLayoutParams();
        countryET_param.height = (int) (deviceHeight * .07f);
        countryET_param.width = (int) (deviceWidth * .4f);
        countryET_param.leftMargin = (int) (deviceWidth * .06f);
        countryET_param.bottomMargin = (int) (deviceHeight * .1f);
        countryET.setLayoutParams(countryET_param);
        countryET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);


        wrapper = EventBus.getDefault().removeStickyEvent(StaffWrapper.class);

        if (wrapper != null) {

            firstNameET.setText(wrapper.getFirstName());
            lastNameET.setText(wrapper.getLastName());
            emailIdET.setText(wrapper.getEmail());
            usernameET.setText(wrapper.getUserName());
//            passwordET.setText(wrapper.getPassword());
            mobileNumberET.setText(wrapper.getMobileNumber());
            nricPassportET.setText(wrapper.getNricPassport());
            addressET.setText(wrapper.getAddress());
            postalCodeET.setText(wrapper.getPostalCode());
            if (wrapper.getDob() != null) {
                date_of_birth_string = wrapper.getDob();
                dobET.setEnabled(true);
                dobET.setText(date_of_birth_string);
            }
            if (wrapper.getGender().equalsIgnoreCase("male")) {
                maleRadio.setChecked(true);
            } else {
                femaleRadio.setChecked(true);
            }
            if (wrapper.getRole().equalsIgnoreCase("1")) {
                managerRadio.setChecked(true);
            } else {
                staffRadio.setChecked(true);
            }

//            stock_location.setText(wrapper.getStockLocation());


            if (wrapper.getImage() != null) {
                mImageLoader.displayImage(wrapper.getImage(),
                        img, options1, new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1,
                                                        FailReason arg2) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onLoadingComplete(String arg0, View img,
                                                          Bitmap bmp) {
                                // TODO Auto-generated method stub
                                if (img instanceof ImageView) {
                                    ((ImageView) img).setImageBitmap(bmp);

                                }
                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1) {
                                // TODO Auto-generated method stub

                            }
                        });

            }

        }
//        formScroll.fullScroll(ScrollView.FOCUS_UP);
        return rootView;
    }

    private HashMap<String, Object> valueKey;
    private ArrayList<String> list;

    void setCountrySpinnerAdapter() {

        new CountryWrapper() {
            @Override
            public void getCountryWrapperList(Context context, List<CountryListWrapper> data) {

                valueKey = new HashMap<String, Object>();
                list = new ArrayList<String>();
                list.add(0, "Select Country");
                int selection = 0;
                for (CountryListWrapper mCountryWrapper : data) {
                    valueKey.put(mCountryWrapper.getCountryName(), mCountryWrapper.getId());
                    list.add(mCountryWrapper.getCountryName());
                    if (wrapper != null && mCountryWrapper.getId().equals(wrapper.getCountryID())) {
                        selection = data.indexOf(mCountryWrapper);
                        selection = ++selection;
                    } else if (wrapper == null && mCountryWrapper.getCountryName().equalsIgnoreCase("Singapore")) {
                        selection = data.indexOf(mCountryWrapper);
                        selection = ++selection;
                    }
                }

                CountrySpinnerAdapter countryAdapter = new CountrySpinnerAdapter(getActivity(), list);
                countryET.setAdapter(countryAdapter);
                countryET.setSelection(selection);
            }
        }.getCountryList(getActivity());
    }


    public void backClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
            case R.id.saveButton: {
                saveCLick();
                break;
            }
            case R.id.date_of_birthET: {
                dateOfBirthClick();
                break;
            }

        }
    }

    void saveCLick() {
        boolean isSubmit, isPassword;
        Util.hideSoftKeypad(this.getActivity());
        try {

            if(new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.form))){
                isSubmit = true;
            }else{
                isSubmit = false;
            }

            if(wrapper == null) {
                if (passwordET.getText().toString().trim().equals("")) {
                    passwordET.setError("Please fill password");
                    isPassword = false;
                } else {
                    passwordET.setError(null);
                    isPassword = true;
                }
            }else{
                isPassword = true;
            }

            if (isSubmit && isPassword) {
                if (countryET.getSelectedItemPosition() != 0) {
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    map.put("first_name", firstNameET.getText().toString());
                    map.put("last_name", lastNameET.getText().toString());
                    map.put("email_id", emailIdET.getText().toString());
                    map.put("username", usernameET.getText().toString());
                    map.put("mobile_number", mobileNumberET.getText().toString());
                    map.put("nric", nricPassportET.getText().toString());
                    map.put("dob", date_of_birth_string);
                    map.put("gender", (genderRadioGroup.getCheckedRadioButtonId() == R.id.male_radio) ? "male" : "female");
                    map.put("role_id", (roleRadioGroup.getCheckedRadioButtonId() == R.id.manager_radio) ? "1" : "2");
                    map.put("address", addressET.getText().toString());
                    map.put("postal_code", postalCodeET.getText().toString());
                    map.put("country_id", valueKey.get(countryET.getSelectedItem().toString()));

                    if (wrapper == null)
                        map.put("is_active", "1");
                    else if (wrapper != null) {
                        if (!TextUtils.isEmpty(wrapper.getIsActive()) && wrapper.getIsActive().equals("1"))
                            map.put("is_active", "1");
                        else
                            map.put("is_active", "0");
                    }

                    if (wrapper == null) {

                        if (!TextUtils.isEmpty(passwordET.getText().toString()))
                            map.put("password", passwordET.getText().toString());

                        if (file != null) {
                            Log.i("filepath in staff--->", "" + file.getAbsolutePath());
                            map.put("user_photo", file);
                            Log.i("Map of staff", "" + map);
                            StaffWrapper.addStaff(map, getActivity(), this);

                        } else {
                            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Staff image is missing.",
                                    "Please add an image.", "OK", android.R.drawable.ic_dialog_alert, null);
                        }
                    } else {
                        if (!TextUtils.isEmpty(passwordET.getText().toString()))
                            map.put("password", passwordET.getText().toString());
                        if (img.getDrawable() != null) {

                            String imageBade64 = "";

                            if(file != null){
                                imageBade64 = new Util().encodeTobase64(img.getDrawable());
                            }

//                            String imageBade64 = new Util().encodeTobase64(img.getDrawable());
                            map.put("user_photo", imageBade64);
                            isUpdated = false;
                        }
                        Log.i("Map of staff", "" + map + "isupdated=" + isUpdated);
                       /* if (isUpdated) {
                            map.put("user_photo", imageBade64);
                            isUpdated = false;
                        }*/

                        StaffWrapper.updateStaff(wrapper.id, map, getActivity(), this);
                    }

                } else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Country is not selected.",
                            "Please select the country.", "OK", android.R.drawable.ic_dialog_alert, null);
                }
            }
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(AddStaffFragment.class.getName(), ex);
            ex.printStackTrace();
        }

    }

    private boolean passwordValidation(EditText input_edit_text) {
        boolean isValid = true;

        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_password));
            isValid = false;
        } else {
            String PATTERN_MATCHER = "(^.{1,50}+$)";
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_password));
                isValid = false;
            }
        }
        return isValid;
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (mCapturedImageURI != null) {
//            outState.putString("cameraImageUri", mCapturedImageURI.toString());
//
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState.containsKey("cameraImageUri")) {
//            mCapturedImageURI = Uri.parse(savedInstanceState
//                    .getString("cameraImageUri"));
//        }
//    }

    @Override
    protected void setImageView(Bitmap bitmap, String filePath) {
        super.setImageView(bitmap, filePath);
        imagePath = filePath;
        img.setImageBitmap(bitmap);
        isUpdated = true;
        file = new File(filePath);
    }

//    @Override
//    protected void setImageView(Uri uri) {
//        super.setImageView(uri);
//        imagePath = uri.getPath();
//        img.setImageURI(uri);
//        isUpdated = true;
//
//    }
//
//    @Override
//    protected void setImageView(Bitmap bmp,Intent data) {
//      //  super.setImageView(bmp,data);
//       // imagePath = uri.getPath();
//        img.setImageBitmap(bmp);
//        isUpdated = true;
//    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CROP_REQUEST
                && resultCode == Activity.RESULT_OK) {

            try {
                // Bundle extras = data.getExtras();
                // // get the cropped bitmap
                // bitmap = extras.getParcelable("data");
                // image_asset = new File(FileUtils.getPath(this,
                // mCapturedImageURI));

                // Bundle extras = data.getExtras();
                // get the cropped bitmap
                imagePath = data.getStringExtra(CropImage.IMAGE_PATH);
                // String imagePath = extras
                // .getString(CropActivity.CROPPED_IMAGE_PATH);

                // Uri picUri = Uri.parse(imagePath);

                bitmap = BitmapFactory.decodeFile(imagePath);

                if (bitmap != null) {
                    // load image into ImageView

                    img.setImageBitmap(bitmap);
                    // idCardBackPicture.setImageBitmap(bitmap);
                    isUpdated = true;

                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(AddStaffFragment.class.getName(), e);
                }
                if (null == image_asset) {
                    try {
                        image_asset = saveImageToExternalStorage(bitmap);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        RetailPosLoging.getInstance().registerLog(AddStaffFragment.class.getName(), e);
                        e1.printStackTrace();
                    }

                }
            }
        }
    }*/

    void dateOfBirthClick() {

        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(dobET.getText().toString())) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                Date date = df.parse(dobET.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(AddStaffFragment.class.getName(), e);
            }

        }
        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String temp = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;
                        date_of_birth_string = temp;
                        dobET.setText(changeDateFormat(temp));
                    }
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 0);
        dateDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        dateDialog.show();


     /*   Calendar calendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String temp = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;
                        date_of_birth_string = temp;
                        dobET.setText(changeDateFormat(temp));
                    }
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dateDialog.setButton(TimePickerDialog.BUTTON_NEUTRAL, "Reset",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dobET.setText(null);
                        date_of_birth_string = null;
                    }
                });

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 0);
        dateDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        dateDialog.show();
        dateDialog.setCancelable(false);
*/
    }

    private void setupUI(final View view, final Context context) {
        view.setFocusableInTouchMode(true);

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    hideKeyboard(view, context);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }

    private void hideKeyboard(View view, Context context) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        view.requestFocus();
    }
}
