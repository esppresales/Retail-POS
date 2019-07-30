package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.settingswrapper.DiscountDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;

/**
 * Created by hp pc on 03-05-2015.
 */
public class DiscountDialog extends BaseDialog {

    private DiscountDetails details;
    private SettingsModuleWrapper.RefreshSettingsListener refreshSettings;
    private Context context;
    private ToggleButton loyaltyPointsTb;
    private EditText discount;
    private EditText from;
    private EditText to;
    private EditText spendRestriction;
    private TextView currency;
    private SavePreferences pref;

    public DiscountDialog(Context context, DiscountDetails details, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        super(context, R.layout.dialog_discount_dialog_layout);
        this.details = details;
        this.refreshSettings = refreshSettings;
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Discount");
        pref = new SavePreferences(context);
        View view = getView();
        loyaltyPointsTb = (ToggleButton) view.findViewById(R.id.onOffBtn);
        discount = (EditText) view.findViewById(R.id.discountPer);
        from = (EditText) view.findViewById(R.id.offerFrom);
        to = (EditText) view.findViewById(R.id.offerTo);
        spendRestriction = (EditText) view.findViewById(R.id.spendRestriction);
        currency = (TextView) view.findViewById(R.id.currency);
        currency.setText(pref.getCurrencyName());

        setToggleDimension(loyaltyPointsTb);
        setUpValues();

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.datePickerDialog(context, from);
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.datePickerDialog(context, to);
            }
        });

    }

    private void setUpValues() {
        discount.setText(pref.getDiscountPercentage());
        from.setText(pref.getDiscountFromDate());
        to.setText(pref.getDiscountToDate());
        spendRestriction.setText(pref.getDiscountMinSpend());


        if (pref.getDiscountMinRestiction().toString().trim().equals("1")) {
            loyaltyPointsTb.setChecked(true);
        }
        else {
            loyaltyPointsTb.setChecked(false);
        }
    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();

        if (new Validation().checkValidation((ViewGroup) findViewById(R.id.container)) &&
                Util.isDateAfter(from.getText().toString(), to.getText().toString(), to)) {

            if(spendRestriction.getText().toString().length() > 8){
                spendRestriction.setError(context.getString(R.string.error_max_eight_char_msg));
                spendRestriction.requestFocus();
                return;
            }
            details.setPercentage(discount.getText().toString());
            details.setFromDate(from.getText().toString());
            details.setToDate(to.getText().toString());
            details.setMinSpend(spendRestriction.getText().toString());

            if (loyaltyPointsTb.isChecked()) {
                details.setMinRestriction("1");
            }
            else {
                details.setMinRestriction("0");
            }

            SettingsWebClient.updateSettings(context, SettingsWebClient.getDiscountDetailMap(details), refreshSettings);
            dismiss();
        }
    }
}
