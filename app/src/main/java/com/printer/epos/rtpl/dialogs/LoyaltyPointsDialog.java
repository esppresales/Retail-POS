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
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.settingswrapper.LoyaltyPointDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;

/**
 * Created by hp pc on 03-05-2015.
 */
public class LoyaltyPointsDialog extends BaseDialog {

    private LoyaltyPointDetails details;
    private SettingsModuleWrapper.RefreshSettingsListener refreshSettings;
    private Context context;
    private SavePreferences pref;
    private ToggleButton loyaltyPointsTb;
    private EditText earnedAmount;
    private TextView currency;

    public LoyaltyPointsDialog(Context context, LoyaltyPointDetails details, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        super(context, R.layout.loyalty_points_dialog_layout);
        this.details = details;
        this.refreshSettings = refreshSettings;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Loyalty Points");
        pref = new SavePreferences(context);
        View view = getView();

        loyaltyPointsTb = (ToggleButton) view.findViewById(R.id.loyaltySwitch);
        earnedAmount = (EditText) view.findViewById(R.id.earnedAmount);
        currency = (TextView) view.findViewById(R.id.currency);

        setToggleDimension(loyaltyPointsTb);
        setValues();
    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();
        if (new Validation().checkValidation((ViewGroup) findViewById(R.id.container))) {
            details.setEarnedAmount(earnedAmount.getText().toString());

            if (loyaltyPointsTb.isChecked())
                details.setIsLoyaltyOn(1);
            else
                details.setIsLoyaltyOn(0);

            SettingsWebClient.updateSettings(context, SettingsWebClient.getLoyaltyPointsMap(details), refreshSettings);
            dismiss();
        }
    }

    private void setValues() {
        currency.setText(pref.getCurrencyName());
        earnedAmount.setText(pref.getEarnedAmount());
        if (pref.getIsloyaltyOn().toString().trim().equals("1"))
            loyaltyPointsTb.setChecked(true);
        else
            loyaltyPointsTb.setChecked(false);
    }
}
