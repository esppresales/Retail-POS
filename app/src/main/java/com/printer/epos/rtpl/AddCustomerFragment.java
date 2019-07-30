package com.printer.epos.rtpl;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.CountrySpinnerAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.CountryWrapper;
import com.printer.epos.rtpl.wrapper.CustomerWrapper;
import com.printer.epos.rtpl.wrapper.DeviceType;

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
public class AddCustomerFragment extends BaseFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddCustomerFragment() {
    }

    private int scanCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            /*
      The dummy content this fragment is presenting.
     */
            DummyContent.DummyItem mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        peripheralManager.connectDevice(DeviceType.MSR, new SavePreferences(getActivity()).getMSRDeviceName());
        ((Home) getActivity()).setEnabledButtons(false, true, true, false);

        hContext.backButton.setOnClickListener(this);
        hContext.saveButton.setOnClickListener(this);

        if (wrapper != null) {
            hContext.saveButton.setText("UPDATE");
            hContext.setTitleText("Update Customer");
        } else {
            hContext.saveButton.setText(getString(R.string.save));
            hContext.setTitleText(getString(R.string.add_customer));
        }
        setCountrySpinnerAdapter();
//        if (EposDeviceClient.getScanner() == null)
//            Toast.makeText(getActivity(), "MSR device not connected with printer", Toast.LENGTH_LONG).show();
        //createDevice(new SavePreferences(getActivity()).getMSRDeviceName(), Device.DEV_TYPE_SCANNER, Device.FALSE);
      /*  else
            Toast.makeText(getActivity(), "Device is already connected", Toast.LENGTH_LONG).show();*/
    }


    private EditText membershipIdET;
    private EditText mobileNoET;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText dateOfBirthET;
    private EditText membershipValidityET;
    private EditText emailIdET;
    private EditText nricPassportET;
    private EditText addressET;
    private EditText postalCodeET;
    private Spinner countryET;
    private RadioGroup genderRadioGroup;
    private RadioGroup membershipValidityRadioGroup;
    private View rootView;

    private String date_of_birth_string;
    private CustomerWrapper wrapper = null;

    private Home hContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_customer, container, false);

        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        ScrollView formScroll = (ScrollView) rootView.findViewById(R.id.formScroll);
        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        form_param.bottomMargin = (int) (deviceHeight * .03f);
        formScroll.setLayoutParams(form_param);

        TextView first_name = (TextView) rootView.findViewById(R.id.first_name);
        first_name.getLayoutParams().width = (int) (deviceWidth * .4f);
        first_name.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .04f), 0, 0);

        firstNameET = (EditText) rootView.findViewById(R.id.first_nameET);
        RelativeLayout.LayoutParams firstNameET_param = (RelativeLayout.LayoutParams) firstNameET.getLayoutParams();
        firstNameET_param.height = (int) (deviceHeight * .07f);
        firstNameET_param.width = (int) (deviceWidth * .38f);
        firstNameET_param.leftMargin = (int) (deviceWidth * .02f);
        firstNameET.setLayoutParams(firstNameET_param);
        firstNameET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView last_name = (TextView) rootView.findViewById(R.id.last_name);
        last_name.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .04f), 0, 0);

        lastNameET = (EditText) rootView.findViewById(R.id.last_nameET);
        RelativeLayout.LayoutParams lastNameET_param = (RelativeLayout.LayoutParams) lastNameET.getLayoutParams();
        lastNameET_param.height = (int) (deviceHeight * .07f);
        lastNameET_param.width = (int) (deviceWidth * .38f);
        lastNameET_param.leftMargin = (int) (deviceWidth * .06f);
        lastNameET.setLayoutParams(lastNameET_param);
        lastNameET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView membership_id = (TextView) rootView.findViewById(R.id.membership_id);
        membership_id.getLayoutParams().width = (int) (deviceWidth * .4f);
        membership_id.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        membershipIdET = (EditText) rootView.findViewById(R.id.membership_idET);
        RelativeLayout.LayoutParams membershipIdET_param = (RelativeLayout.LayoutParams) membershipIdET.getLayoutParams();
        membershipIdET_param.height = (int) (deviceHeight * .07f);
        membershipIdET_param.width = (int) (deviceWidth * .38f);
        membershipIdET_param.leftMargin = (int) (deviceWidth * .02f);
        membershipIdET.setLayoutParams(membershipIdET_param);
        membershipIdET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView email_id = (TextView) rootView.findViewById(R.id.email_id);
        email_id.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        emailIdET = (EditText) rootView.findViewById(R.id.email_idET);
        RelativeLayout.LayoutParams emailIdET_param = (RelativeLayout.LayoutParams) emailIdET.getLayoutParams();
        emailIdET_param.height = (int) (deviceHeight * .07f);
        emailIdET_param.width = (int) (deviceWidth * .38f);
        emailIdET_param.leftMargin = (int) (deviceWidth * .06f);
        emailIdET.setLayoutParams(emailIdET_param);
        emailIdET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView nricPassport = (TextView) rootView.findViewById(R.id.nricPassport);
        nricPassport.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        nricPassportET = (EditText) rootView.findViewById(R.id.nricPassportET);
        RelativeLayout.LayoutParams nricPassportET_param = (RelativeLayout.LayoutParams) nricPassportET.getLayoutParams();
        nricPassportET_param.height = (int) (deviceHeight * .07f);
        nricPassportET_param.width = (int) (deviceWidth * .38f);
        nricPassportET_param.leftMargin = (int) (deviceWidth * .06f);
        nricPassportET.setLayoutParams(nricPassportET_param);
        nricPassportET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);


        TextView mobile_no = (TextView) rootView.findViewById(R.id.mobile_no);
        mobile_no.getLayoutParams().width = (int) (deviceWidth * .4f);
        mobile_no.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        mobileNoET = (EditText) rootView.findViewById(R.id.mobile_noET);
        RelativeLayout.LayoutParams mobileNoET_param = (RelativeLayout.LayoutParams) mobileNoET.getLayoutParams();
        mobileNoET_param.height = (int) (deviceHeight * .07f);
        mobileNoET_param.width = (int) (deviceWidth * .38f);
        mobileNoET_param.leftMargin = (int) (deviceWidth * .02f);
        mobileNoET.setLayoutParams(mobileNoET_param);
        mobileNoET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);


        TextView date_of_birth = (TextView) rootView.findViewById(R.id.date_of_birth);
        date_of_birth.getLayoutParams().width = (int) (deviceWidth * .4f);
        date_of_birth.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        dateOfBirthET = (EditText) rootView.findViewById(R.id.date_of_birthET);
        RelativeLayout.LayoutParams dateOfBirthET_param = (RelativeLayout.LayoutParams) dateOfBirthET.getLayoutParams();
        dateOfBirthET_param.height = (int) (deviceHeight * .07f);
        dateOfBirthET_param.width = (int) (deviceWidth * .38f);
        dateOfBirthET_param.leftMargin = (int) (deviceWidth * .02f);
        dateOfBirthET.setLayoutParams(dateOfBirthET_param);
        dateOfBirthET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);
        dateOfBirthET.setOnClickListener(this);

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
        femaleRadio_param.leftMargin = (int) (deviceWidth * .06f);
        femaleRadio.setLayoutParams(femaleRadio_param);

//        quantityET = (EditText) rootView.findViewById(R.id.quantityET);
//        RelativeLayout.LayoutParams quantityET_param = (RelativeLayout.LayoutParams) quantityET.getLayoutParams();
//        quantityET_param.height = (int) (deviceHeight * .07f);
//        quantityET_param.width = (int) (deviceWidth * .28f);
//        quantityET_param.leftMargin = (int) (deviceWidth * .06f);
//        quantityET.setLayoutParams(quantityET_param);
//        quantityET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView membership_validity = (TextView) rootView.findViewById(R.id.membership_validity);
        membership_validity.getLayoutParams().width = (int) (deviceWidth * .4f);
        membership_validity.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        membershipValidityET = (EditText) rootView.findViewById(R.id.membership_validityET);
        RelativeLayout.LayoutParams membershipValidityET_param = (RelativeLayout.LayoutParams) membershipValidityET.getLayoutParams();
        membershipValidityET_param.height = (int) (deviceHeight * .07f);
        membershipValidityET_param.width = (int) (deviceWidth * .1f);
        membershipValidityET_param.leftMargin = (int) (deviceWidth * .02f);
        membershipValidityET.setLayoutParams(membershipValidityET_param);
        membershipValidityET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        membershipValidityRadioGroup = (RadioGroup) rootView.findViewById(R.id.membership_validity_radio_group);
        membershipValidityRadioGroup.getLayoutParams().width = (int) (deviceWidth * .28f);

        RadioButton monthsRadio = (RadioButton) rootView.findViewById(R.id.months_radio);
        monthsRadio.setCompoundDrawablePadding((int) (deviceWidth * .01f));
        RadioGroup.LayoutParams monthsRadio_param = (RadioGroup.LayoutParams) monthsRadio.getLayoutParams();
//        maleRadio_param.width = (int)(deviceHeight*.05f);
        monthsRadio_param.leftMargin = (int) (deviceWidth * .04f);
        monthsRadio_param.height = (int) (deviceHeight * .07f);
        monthsRadio.setLayoutParams(monthsRadio_param);

        RadioButton yearsRadio = (RadioButton) rootView.findViewById(R.id.years_radio);
        yearsRadio.setCompoundDrawablePadding((int) (deviceWidth * .01f));
        RadioGroup.LayoutParams yearsRadio_param = (RadioGroup.LayoutParams) yearsRadio.getLayoutParams();
//        femaleRadio_param.width = (int) (deviceHeight * .05f);
        yearsRadio_param.height = (int) (deviceHeight * .07f);
        yearsRadio_param.leftMargin = (int) (deviceWidth * .02f);
        yearsRadio.setLayoutParams(yearsRadio_param);

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
        postal_code.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        postalCodeET = (EditText) rootView.findViewById(R.id.postal_codeET);
        RelativeLayout.LayoutParams postalCodeET_param = (RelativeLayout.LayoutParams) postalCodeET.getLayoutParams();
        postalCodeET_param.height = (int) (deviceHeight * .07f);
        postalCodeET_param.width = (int) (deviceWidth * .38f);
        postalCodeET_param.leftMargin = (int) (deviceWidth * .02f);
        postalCodeET_param.bottomMargin = (int) (deviceHeight * .1f);
        postalCodeET.setLayoutParams(postalCodeET_param);
        postalCodeET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView country = (TextView) rootView.findViewById(R.id.country);
        country.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        countryET = (Spinner) rootView.findViewById(R.id.countryET);
        RelativeLayout.LayoutParams countryET_param = (RelativeLayout.LayoutParams) countryET.getLayoutParams();
        countryET_param.height = (int) (deviceHeight * .07f);
        countryET_param.width = (int) (deviceWidth * .38f);
        countryET_param.leftMargin = (int) (deviceWidth * .06f);
        countryET_param.bottomMargin = (int) (deviceHeight * .1f);
        countryET.setLayoutParams(countryET_param);
        countryET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        wrapper = EventBus.getDefault().removeStickyEvent(CustomerWrapper.class);

        if (wrapper != null) {

            firstNameET.setText(wrapper.getFirstName());
            lastNameET.setText(wrapper.getLastName());
            membershipIdET.setText(wrapper.getMembershipId());
            emailIdET.setText(wrapper.getEmail());
            mobileNoET.setText(wrapper.getMobileNo());
            nricPassportET.setText(wrapper.getNricPassport());

            if (wrapper.getMembershipValidity() != null && !wrapper.getMembershipValidity().equals("null"))
                membershipValidityET.setText(wrapper.getMembershipValidity());

            postalCodeET.setText(wrapper.getPostalCode());

            if (wrapper.getGender().equalsIgnoreCase("male")) {
                maleRadio.setChecked(true);
            } else {
                femaleRadio.setChecked(true);
            }
            if (wrapper.getMembershipType().equalsIgnoreCase("months")) {
                monthsRadio.setChecked(true);
            } else {
                yearsRadio.setChecked(true);
            }
            dateOfBirthET.setEnabled(true);
            dateOfBirthET.setText(wrapper.getDob() != null && !wrapper.getDob().equals("null") ? wrapper.getDob() : "");
           /* if (wrapper.getDob() != null) {
                date_of_birth_string = wrapper.getDob();
                dateOfBirthET.setText(changeDateFormat(date_of_birth_string));
            }*/
            emailIdET.setText(wrapper.getEmail());
            addressET.setText(wrapper.getAddress());
            if (wrapper.getGender().equalsIgnoreCase("male")) {
                maleRadio.setChecked(true);
            } else {
                femaleRadio.setChecked(true);
            }
        }

        return rootView;
    }

    private HashMap<String, Object> valueKey;
    private ArrayList<String> list;

    private void setCountrySpinnerAdapter() {

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
                saveClick();
                break;
            }
            case R.id.date_of_birthET: {
                dateOfBirthClick();
                break;
            }

        }
    }


    private void saveClick() {
        Util.hideSoftKeypad(this.getActivity());
        try {

            //if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.form)))

            if (validation()) {
                if (countryET.getSelectedItemPosition() != 0) {


                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("first_name", firstNameET.getText().toString());
                    map.put("last_name", lastNameET.getText().toString());
                    map.put("membership_id", membershipIdET.getText().toString());
                    map.put("email_id", emailIdET.getText().toString());
                    map.put("mobile_number", mobileNoET.getText().toString());
                    map.put("nric", nricPassportET.getText().toString());
                    map.put("dob", date_of_birth_string);
                    map.put("gender", (genderRadioGroup.getCheckedRadioButtonId() == R.id.male_radio) ? "Male" : "Female");
                    map.put("address", addressET.getText().toString());
                    map.put("membership_validity", membershipValidityET.getText().toString());
                    map.put("membership_type", (membershipValidityRadioGroup.getCheckedRadioButtonId() == R.id.months_radio) ? "months" : "years");
                    map.put("postal_code", postalCodeET.getText().toString());
                    map.put("country_id", valueKey.get(countryET.getSelectedItem().toString()));


                    if (wrapper == null) {
                        CustomerWrapper.addCustomer(map, getActivity(), this);
                    } else {
//                        String url = Mapping.createUrl(map);
                        CustomerWrapper.updateCustomer(wrapper.id, map, getActivity(), this);
                    }
                } else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Country is not selected.",
                            "Please select the country.", "OK", android.R.drawable.ic_dialog_alert, null);
                }


            }
        } catch (Exception ex) {

            RetailPosLoging.getInstance().registerLog(AddCustomerFragment.class.getName(), ex);
            ex.printStackTrace();
        }
    }

    private void dateOfBirthClick() {

        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(dateOfBirthET.getText().toString())) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                Date date = df.parse(dateOfBirthET.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                RetailPosLoging.getInstance().registerLog(AddCustomerFragment.class.getName(), e);
                e.printStackTrace();
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
                        dateOfBirthET.setText(changeDateFormat(temp));
                    }
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 0);
        dateDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        dateDialog.show();



      /*  Calendar calendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String temp = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;
                        date_of_birth_string = temp;
                        dateOfBirthET.setText(changeDateFormat(temp));
                    }
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

       *//* dateDialog.setButton(TimePickerDialog.BUTTON_NEUTRAL, "Reset",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dateOfBirthET.setText(null);
                        date_of_birth_string = null;
                    }
                });*//*
*/


    }

    private boolean validation() {
        boolean is_empty = true;
        if (!Validation.isValidString(membershipIdET.getText().toString())) {
            membershipIdET.setError(getString(R.string.required_membership_id));
            membershipIdET.requestFocus();
            is_empty = false;
        }
        if (phoneNumberRequiredValidation(mobileNoET)) {
            is_empty = false;
        }
        if (!Validation.isValidString(firstNameET.getText().toString())) {
            firstNameET.setError(getString(R.string.required_first_name));
            firstNameET.requestFocus();
            is_empty = false;
        }
      /*  } else if (!Validation.isValidString(lastNameET.getText().toString())) {
            lastNameET.setError(getString(R.string.field_not_valid));
            lastNameET.requestFocus();
            is_empty = false;
        } else if (!Validation.isValidString(dateOfBirthET.getText().toString())) {
            dateOfBirthET.setError(getString(R.string.field_not_valid));
            dateOfBirthET.requestFocus();
            is_empty = false;
        } else if (!Validation.isValidString(addressET.getText().toString())) {
            addressET.setError(getString(R.string.field_not_valid));
            addressET.requestFocus();
            is_empty = false;
        } else if (!Validation.isValidString(emailIdET.getText().toString())) {
            emailIdET.setError(Html.fromHtml("<span style=\"font-family: Roboto-Bold \">Email ID is required</span>"));
//            emailIdET.setError(getString(R.string.field_not_valid));
            emailIdET.requestFocus();
            is_empty = false;
        }*/

        return is_empty;
    }

    private boolean phoneNumberRequiredValidation(EditText input_edit_text) {
        boolean isValid = false;
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_mobile_number));
            isValid = true;
        } else {
            String PATTERN_MATCHER = "(^[0-9]{8,20}+$)";
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_mobile_number));
                isValid = true;
            }

        }

        return isValid;
    }


   /* private void createDevice(String id, int type, int buffer) {

        if (UiController.mDevice.isConnected()) {
            try {
                UiController.mDevice.createDevice(id, type, Device.FALSE, buffer, this);
            } catch (EposException e) {
                Toast.makeText(getActivity(), "Error in create device: " + e.toString(), Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(getActivity(), "App is not connected with printer", Toast.LENGTH_LONG).show();

        }
    }*/


    @Override
    public void onMSRScanData(final String data) {
        super.onMSRScanData(data);
        if (getActivity() != null) {
            if (scanCount == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MagStripeDataParser reader = new MagStripeDataParser(data);
                        membershipIdET.setText(reader.getCardNumber());
                    }
                });
                scanCount++;
            } else
                scanCount = 0;
        }
    }

//    @Override
//    protected void onScanDataReceive(String scanData, DeviceType type) {
//        super.onScanDataReceive(scanData, type);
//        if (type == DeviceType.DEV_TYPE_MSR) {
//            if (scanCount == 0) {
//                MagStripeDataParser reader = new MagStripeDataParser(scanData);
//                membershipIdET.setText(reader.getCardNumber());
//                scanCount++;
//            } else
//                scanCount = 0;
//
//        }
//    }

   /* @Override
    public void onDetach() {
        super.onDetach();
        isDeviceCreated = false;
        if (mScanner != null) {
            mScanner = null;
            connectTerminate();
        }
    }

    *//**
     * All devices are deleted.
     *//*
    private void connectTerminate() {
        try {
            UiController.mDevice.deleteDevice(mScanner, this);
        } catch (EposException e) {
            Toast.makeText(getActivity(), "Error in connection terminate: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }*/
}
