package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.wrapper.settingswrapper.CurrencyDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.SettingsModuleWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp pc on 03-05-2015.
 */
public class CurrencyDialog extends BaseDialog {
    private CurrencyDetails details;
    private SettingsModuleWrapper.RefreshSettingsListener refreshSettings;
    private Context context;
    private SavePreferences pref;
    private Spinner currSpinner;
    private List<String> currencies;
    private HashMap<String, String> valueKey;
    private List<CurrencyDetails> currencyDetailsList;
    private ArrayAdapter<String> adapter;


    public CurrencyDialog(Context context, List<CurrencyDetails> currencyDetailsList, final SettingsModuleWrapper.RefreshSettingsListener refreshSettings) {
        super(context, R.layout.currency_dialog_layout);
        this.refreshSettings = refreshSettings;
        this.context = context;
        this.currencyDetailsList = currencyDetailsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Currency");
        pref = new SavePreferences(context);
        View view = getView();
        currSpinner = (Spinner) view.findViewById(R.id.currency);
        setCurrencies();
    }

    private void setCurrencies() {
        currencies = new ArrayList<String>();
        valueKey = new HashMap<String, String>();
        currencies.add(0, "Select Currency");
        int selection = 0;

        for (int i = 0; i < currencyDetailsList.size(); i++) {
            CurrencyDetails curr = currencyDetailsList.get(i);
            valueKey.put(curr.getName(), curr.getId());
            currencies.add(curr.getShortName() + " - " + curr.getName());
            if (pref.getCurrencyId() != null && curr.getId().equals(pref.getCurrencyId())) {
                selection = currencyDetailsList.indexOf(curr);
                selection = ++selection;

            }
        }

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, currencies) {
            private int boundRight
                    ,
                    padding
                    ,
                    deviceHeight
                    ,
                    boundbottom;
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                deviceHeight = displayMetrics.heightPixels;
                padding = (int) (deviceHeight * .01f);
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_view,
                            null);
                    holder = new ViewHolder();
                    holder.mTextView = (TextView) convertView;

                    int boundRight = (int) (deviceHeight * .025f);
                    int boundbottom = (int) (deviceHeight * .025f * 0.9f);

                    Drawable rightDrawable = context.getResources().getDrawable(
                            R.drawable.dropdown_arrow);
                    rightDrawable.setBounds(0, 0, boundRight, boundbottom);
                    holder.mTextView.setCompoundDrawables(null, null, rightDrawable,
                            null);
                    holder.mTextView.setCompoundDrawablePadding(padding);
                    holder.mTextView.setPadding(padding, 0, padding, 0);
                    convertView.setTag(holder);
                } else
                    holder = (ViewHolder) convertView.getTag();

                holder.mTextView.setTextColor(context.getResources().getColor(
                        android.R.color.black));
                holder.mTextView.setText("" + currencies.get(position));

                return convertView;
            }

            class ViewHolder {
                TextView mTextView;
            }
        };

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currSpinner.setAdapter(adapter);
        currSpinner.setSelection(selection);
    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();
        if (currSpinner.getSelectedItemPosition() != 0) {
            CurrencyDetails details = new CurrencyDetails();
            String countryName = currSpinner.getSelectedItem().toString();
            countryName = countryName.substring(countryName.indexOf("- ") + 1, countryName.length()).trim();
            details.setId(valueKey.get(countryName));
            details.setName(countryName);

            SettingsWebClient.updateSettings(context, SettingsWebClient.getCurrencyMap(details), refreshSettings);
            dismiss();
        } else
            new CustomDialog().showOneButtonAlertDialog(context, "Currency is not selected.",
                    "Please select the currency.", "OK", android.R.drawable.ic_dialog_alert, null);
    }
}
