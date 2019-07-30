package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.settingswrapper.ReceiptHeaderDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;

/**
 * Created by hp pc on 03-05-2015.
 */
public class ReceiptHeaderDialog extends BaseDialog {

    private EditText name;
    private EditText website;
    private EditText header1;
    private EditText header2;
    private EditText header3;
    private SettingsModuleWrapper.RefreshSettingsListener refreshSettings;
    private Context context;
    private SavePreferences pref;
    private ReceiptHeaderDetails details;

    public ReceiptHeaderDialog(Context context, ReceiptHeaderDetails details, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        super(context, R.layout.receipt_header_dialog_layout);
        this.details = details;
        this.refreshSettings = refreshSettings;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Receipt Header");
        pref = new SavePreferences(context);
        View view = getView();

        name = (EditText) view.findViewById(R.id.name);
        website = (EditText) view.findViewById(R.id.header4);
        header1 = (EditText) view.findViewById(R.id.header1);
        header2 = (EditText) view.findViewById(R.id.header2);
        header3 = (EditText) view.findViewById(R.id.header33);
        setUpValues();
    }

    private void setUpValues() {
        name.setText(pref.getReceiptName());
        website.setText(pref.getReceiptWebsite());
        header1.setText(pref.getReceiptHeader1());
        header2.setText(pref.getReceiptHeader2());
        header3.setText(pref.getReceiptHeader3());
    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();
        if (new Validation().checkValidation((ViewGroup) findViewById(R.id.container))) {
            details.setName(name.getText().toString());
            //details.setHeader4(website.getText().toString());
            details.setHeader1(header1.getText().toString());
            details.setHeader2(header2.getText().toString());
            details.setHeader3(header3.getText().toString());
            SettingsWebClient.updateSettings(context, SettingsWebClient.getReceiptHeaderMap(details), refreshSettings);
            dismiss();
        }

    }
}
