
package com.printer.epos.rtpl.Utility;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.UiController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static final String EXTRA_PASSWORD_MATACH = "Matched";
    public static final String EXTRA_PASSWORD_MISMATCH = "Password mismatch";
    public static final String EXTRA_NEW_PASSWORD_INVALID = "New password is required";
    public static final String EXTRA_CONFIRM_PASSWORD_INVALID = "Confirm password is required";
    public static final String PASSWORD_MIN_LENGTH = "Enter atleast 6 characters in password";

    public static String confirmPassword(String password, String confirmPass) {

        if (isValidString(password)) {
            if (isValidString(confirmPass)) {

                if (password.equals(confirmPass)) {
                    if (minPasswordLength(password)) {
                        return EXTRA_PASSWORD_MATACH;
                    } else {
                        return PASSWORD_MIN_LENGTH;
                    }
                } else {
                    return EXTRA_PASSWORD_MISMATCH;
                }
            } else {
                return EXTRA_CONFIRM_PASSWORD_INVALID;
            }
        } else {
            return EXTRA_NEW_PASSWORD_INVALID;
        }
    }

    public static Boolean isValidString(String string) {

        if (string != null && string.length() > 0) {
            return true;
        }
        return false;
    }

    public static Boolean isValidData(String string) {

        if (string != null && string.length() > 0 && !string.equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    public static boolean minPasswordLength(String password) {
        boolean minLength = false;
        if (!TextUtils.isEmpty(password)) {
            if (password.length() < 0) {
                minLength = false;
            } else {
                minLength = true;
            }
        }
        return minLength;
    }

    public static boolean isValidEmailAddress(String email) {
        if (isValidString(email)) {
            String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            return email.matches(emailPattern);
        } else
            return false;
    }

    public static boolean isValidlink(String link) {
        if (isValidString(link)) {

            return (Patterns.WEB_URL.matcher(link).matches());
        } else
            return false;
    }

    private boolean isValid = true;

    public boolean checkValidation(ViewGroup rootLayout) {
// isValid = true;
        Resources r = UiController.getInstance().getResources();
        int childCount = rootLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {

            if (rootLayout.getChildAt(i) instanceof ViewGroup) {
                isValid = checkValidation((ViewGroup) rootLayout.getChildAt(i));
            } else if (rootLayout.getChildAt(i) instanceof View) {
                View view = rootLayout.getChildAt(i);
                if (view instanceof EditText) {
                    EditText et = (EditText) view;
                    et.setError(null);
                    switch (et.getId()) {
                        case R.id.passwordET:
                        case R.id.password:
                            passwordValidation(et);
                            break;

                        case R.id.email_idET:
                            emailValidator(et);
                            break;
                        case R.id.first_nameET:
                            firstNameValidation(et);
                            break;
                        case R.id.last_nameET:
                            lastNameValidation(et);
                            break;
                        case R.id.mobile_noET:
                            phoneNumberRequiredValidation(et);
                            break;
                        case R.id.membership_idET:
                            membershipIdRequiredValidation(et);
                            break;
                        case R.id.customerNameET:
                            stringValidation(et, "Customer Name is required.");
                            break;
                        case R.id.date_of_birthET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_date_of_birth));
                            break;
                        case R.id.addressET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_address));
                            break;
                        case R.id.stock_locationET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_address));
                            break;
                        case R.id.product_nameET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_product_name));
                            break;
                        case R.id.barcodeET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_barcode));
                            break;
                        case R.id.product_priceET:
                            ValidationValues(et, "^\\d{0,9}(\\d\\.\\d?|\\.\\d)?\\d?$", r.getString(R.string.required_product_price), r.getString(R.string.required_valid_product_price));
                            break;
                        case R.id.selling_priceET:
                            ValidationValues(et, "^\\d{0,9}(\\d\\.\\d?|\\.\\d)?\\d?$", r.getString(R.string.required_selling_price), r.getString(R.string.required_valid_selling_price));
                            break;
                        case R.id.quantityET:
                            ValidationValues(et, "(^[0-9]{1,6}+$)", r.getString(R.string.required_quantity), r.getString(R.string.required_valid_quantity));
                            break;
                        case R.id.min_stock_alert_quantityET:
                            ValidationValues(et, "(^[0-9]{1,6}+$)", r.getString(R.string.required_min_stock_alert), r.getString(R.string.required_valid_min_stock_alert));
                            break;
                        case R.id.product_descriptionET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_product_description));
                            break;
                        case R.id.supplierET:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_supplier));
                            break;
                        case R.id.productCategory:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_category));
                            break;
                        case R.id.productDescription:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_description));
                            break;
                        case R.id.ipAddr:
                            stringValidation(et, UiController.getInstance().getResources().getString(R.string.required_ipAddress));
                            break;
                        case R.id.taxET:
                            stringValidation(et, "Tax percentage is not valid");
                            break;
                        /*case R.id.gstRegNoET:
                            stringValidation(et, "GST reg no is not valid");
                            break;*/
                        case R.id.firstET:
                            stringValidation(et, "Please fill correct value");
                            break;
                        case R.id.secondET:
                            stringValidation(et, "Please fill correct value");
                            break;
                        case R.id.usernameET:
                            stringValidation(et, "Please fill correct Username");
                            break;
                        case R.id.postal_codeET:
                            stringValidation(et, "Postal Code is required");
                            break;
                        case R.id.nricPassportET:
                            stringValidation(et, "NRIC/Passport is required");
                            break;
                        case R.id.membership_validityET:
                            stringValidation(et, "Membership Validity is required");
                            break;
                        case R.id.code:
                            stringValidation(et, "Coupon code is required");
                            break;
                        case R.id.amount:
                            stringValidation(et, "Coupon Amount is required");
                            break;
                        case R.id.startDate:
                            stringValidation(et, "Start Date is required");
                            break;
                        case R.id.name:
                            stringValidation(et, "Name is required");
                            break;
                        case R.id.header4:
                            stringValidation(et, "Header 4 is required");
                            break;
                        case R.id.header1:
                            stringValidation(et, "Header 1 is required");
                            break;
                        case R.id.header2:
                            stringValidation(et, "Header 2 is required");
                            break;
                        case R.id.header33:
                            stringValidation(et, "Header 3 is required");
                            break;
                        case R.id.endDate:
                            stringValidation(et, "End Date is required");
                            break;
                        case R.id.earnedAmount:
                            earnedAmountValidation(et, "Earned Point is required");
                            break;
                        case R.id.discountPer:
                            discountValidation(et, "Discount Percentage is required");
                            break;
                        case R.id.offerFrom:
                            stringValidation(et, "Offer period is required");
                            break;
                        case R.id.offerTo:
                            stringValidation(et, "Offer period is required");
                            break;
                        case R.id.spendRestriction:
                            stringValidation(et, "Spend Restriction is required");
                            break;
                        case R.id.exportCsv:
                            stringValidation(et, "Product Quantity is required");
                            break;
                        case R.id.getList:
                            stringValidation(et, "Product Quantity is required");
                            break;
                        case R.id.fromDate:
                            stringValidation(et, "Start Date is required");
                            break;
                        case R.id.toDate:
                            stringValidation(et, "End date is required");
                            break;
                        case R.id.amount_paidET:
                            stringValidation(et, "Amount is required");
                            break;
                        case R.id.printerId:
                            stringValidation(et, "Printer name is required");
                            break;
                        case R.id.date:
                            stringValidation(et, "Date is required");
                            break;
                        default:
                            break;

                    }
                }else{
                    if (rootLayout instanceof Spinner) {
                        Spinner spinner = (Spinner) rootLayout;
                        TextView errorText = (TextView)spinner.getSelectedView();
                        errorText.setError(null);
                        switch (spinner.getId()) {
                            case R.id.paymentSpinner:
                                String value = errorText.getText().toString();
                                if (TextUtils.isEmpty(value) || spinner.getSelectedItemPosition() == 0) {
                                    errorText.setError("Payment mode is required.");
                                    isValid = false;
                                }
                                break;
                        }
                    }
                }
            }
        }
        return isValid;
    }

    private void discountValidation(EditText input_edit_text, String msg) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(msg);
            isValid = false;
        } else if (Double.parseDouble(value) > 100) {
            input_edit_text.setError("Discount cannot exceed 100 %");
            isValid = false;
        }
    }

    public void isIpAddress(EditText input_edit_text, String msgToShow) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(msgToShow);
            isValid = false;
        } else if (!checkForIpAddress(value)) {
            input_edit_text.setError(msgToShow);
            isValid = false;
        }

    }

    private boolean checkForIpAddress(String value) {
        String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private void ValidationValues(EditText input_edit_text, String PATTERN_MATCHER, String setError1, String setError2) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(setError1);
            isValid = false;
        } else {
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(setError2);
                isValid = false;
            }

        }
    }


    private void firstNameValidation(EditText input_edit_text) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_first_name));
            isValid = false;
        } else {
            String PATTERN_MATCHER = "(^[a-zA-Z ]{1,50}+$)";
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_first_name));
                isValid = false;
            }

        }
    }

    private void lastNameValidation(EditText input_edit_text) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_last_name));
            isValid = false;
        } else {
            String PATTERN_MATCHER = "(^[a-zA-Z ]{1,50}+$)";
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_last_name));
                isValid = false;
            }

        }
    }


//    private void passwordValidation(EditText input_edit_text) {
//        String value = input_edit_text.getText().toString();
//        if (TextUtils.isEmpty(value)) {
//            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_password));
//            isValid = false;
//        } else {
//            String PATTERN_MATCHER = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_-]).{8,15})";
//            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
//            Matcher matcher = pattern.matcher(value);
//            if (!matcher.matches()) {
//                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_password));
//                isValid = false;
//            }
//        }
//    }

    private void passwordValidation(EditText input_edit_text) {
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
    }

    private void emailValidator(EditText input_edit_text) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {

            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_email_id));
            isValid = false;

        } else {

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.invalid_email));
                isValid = false;

            } else {

                String PATTERN_MATCHER = "(^.{1,50}+$)";
                Pattern pattern = Pattern.compile(PATTERN_MATCHER);
                Matcher matcher = pattern.matcher(value);

                if (!matcher.matches()) {

                    input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_email_id_range));
                    isValid = false;
                }
            }
        }
    }

    private void phoneNumberRequiredValidation(EditText input_edit_text) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_mobile_number));
            isValid = false;
        } else {
            String PATTERN_MATCHER = "(^[0-9]{8,20}+$)";
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_mobile_number));
                isValid = false;
            }

        }
    }

    private void membershipIdRequiredValidation(EditText input_edit_text) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_membership_id));
            isValid = false;
        } else {
            String PATTERN_MATCHER = "(^[0-9]+$)";
            Pattern pattern = Pattern.compile(PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_membership_id));
                isValid = false;
            }

        }
    }

    private void stringValidation(EditText input_edit_text, String msgToShow) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(msgToShow);
            isValid = false;
        }

    }


    private void earnedAmountValidation(EditText input_edit_text, String msgToShow) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(msgToShow);
            isValid = false;
        } else if (!value.matches("\\d+(\\.\\d+)?")) {
            input_edit_text.setError("Please enter digits only");
            isValid = false;
        }
    }


}
