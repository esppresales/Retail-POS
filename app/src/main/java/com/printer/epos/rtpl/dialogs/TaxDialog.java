package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.TaxDetails;

/**
 * Created by hp pc on 03-05-2015.
 */
public class TaxDialog extends BaseDialog {

    private TaxDetails details;
    private SettingsModuleWrapper.RefreshSettingsListener refreshSettings;
    private Context context;
    private EditText taxET;
    private SavePreferences pref;


    public TaxDialog(Context context, TaxDetails details, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        super(context, R.layout.tax_dialog_layout);
        this.details = details;
        this.refreshSettings = refreshSettings;
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle("Tax");
        setTitle("GST");
        pref = new SavePreferences(context);
        View view = getView();
        taxET = (EditText) view.findViewById(R.id.taxET);
       // gstRegNoET = (EditText) view.findViewById(R.id.gstRegNoET);
        taxET.setText(pref.getTaxPercentage());
       // gstRegNoET.setText(pref.getGSTRegNo());

    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();
        if (new Validation().checkValidation((ViewGroup) findViewById(R.id.container))) {
            details.setPercentage(taxET.getText().toString());
           // details.setGstRegNo(gstRegNoET.getText().toString());
            SettingsWebClient.updateSettings(context, SettingsWebClient.getTaxPercentageMap(details), refreshSettings);
            dismiss();
        }
    }
}
