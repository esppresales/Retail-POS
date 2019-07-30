package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.wrapper.settingswrapper.PeripheralDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hp pc on 03-05-2015.
 */
public class DeviceConfigDialog extends BaseDialog {

    private PrinterDetails printerDetails;
    private PeripheralDetails peripheralDetails;
    private SettingsModuleWrapper.RefreshSettingsListener refreshSettings;
    private Context context;
    private SavePreferences pref;
    private TextView firstTV;
    private TextView secondTV;
    private EditText firstET;
    private EditText secondET;
    private EditText thirdET;


    public DeviceConfigDialog(Context context, PrinterDetails printerDetails, PeripheralDetails peripheralDetails, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        super(context, R.layout.printer_configuration_dialog_layout);
        this.printerDetails = printerDetails;
        this.peripheralDetails = peripheralDetails;
        this.refreshSettings = refreshSettings;
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new SavePreferences(context);
        View view = getView();

        firstTV = (TextView) view.findViewById(R.id.firstTV);
        secondTV = (TextView) view.findViewById(R.id.secondTV);
        firstET = (EditText) view.findViewById(R.id.firstET);
        secondET = (EditText) view.findViewById(R.id.secondET);
        thirdET = (EditText) view.findViewById(R.id.thirdET);


        if (printerDetails != null) {
            setTitle("Printer Configuration");
            firstET.setText(pref.getMasterPrinterIp());
            secondET.setText(pref.getSlavePrinterIp());

        } else if (peripheralDetails != null) {
            setTitle("Peripherals Configuration");
            firstTV.setText("Barcode Device Name");
            secondTV.setText("MSR Device Name");

            firstET.setText(pref.getBarcodeDeviceName());
            secondET.setText(pref.getMSRDeviceName());
            thirdET.setText(pref.getCustomerDisplayName());

        }
    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();
        if (peripheralDetails != null) {
            if (validate(firstET, secondET, thirdET, 0)) {
                setDeviceConfig(context, firstET.getText().toString(), secondET.getText().toString(), thirdET.getText().toString(), refreshSettings);
                dismiss();
            }
        } else {
            if (validate(firstET, secondET, null, 1)) {
                setPrinterConfig(context, firstET.getText().toString(), secondET.getText().toString(), refreshSettings);
                dismiss();
            }
        }

    }

    private boolean validate(EditText firstEt, EditText secondET, EditText thirdET, int type) {
        boolean isValid = true;
        if (peripheralDetails != null) {
            if (TextUtils.isEmpty(firstEt.getText().toString())) {
                firstEt.setError("Please fill barcode device name");
                isValid = false;
            }

            if (TextUtils.isEmpty(secondET.getText().toString())) {
                secondET.setError("Please fill msr device name");
                isValid = false;
            }

            if (TextUtils.isEmpty(thirdET.getText().toString())) {
                thirdET.setError("Please fill customer display name");
                isValid = false;
            }
        } else {
            if (!isIpAddress(firstEt, "Please set correct IP address"))
                isValid = false;
            if (!isIpAddress(secondET, "Please set correct IP address"))
                isValid = false;
        }

        return isValid;
    }

    public boolean isIpAddress(EditText input_edit_text, String msgToShow) {
        boolean isValid = true;
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(msgToShow);
            isValid = false;
        } else if (!checkForIpAddress(value)) {
            input_edit_text.setError(msgToShow);
            isValid = false;
        }
        return isValid;

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


    private void setDeviceConfig(Context context, String barcodeDevice, String msrDevice, String customerDisplay, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        SettingsWebClient.updateSettings(context, SettingsWebClient.getPeripheralConfigMap(barcodeDevice, msrDevice, customerDisplay), refreshSettings);
    }

    private void setPrinterConfig(Context context, String masterPrinterIp, String slavePrinterIp, SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        SettingsWebClient.updateSettings(context, SettingsWebClient.getPrinterMap(masterPrinterIp, slavePrinterIp, ""), refreshSettings);
    }
}
