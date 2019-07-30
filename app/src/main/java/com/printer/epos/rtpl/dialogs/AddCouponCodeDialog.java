package com.printer.epos.rtpl.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.CouponCodeWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hp pc on 03-05-2015.
 */
public class AddCouponCodeDialog extends BaseDialog {

    private EditText code;
    private EditText amount;
    private EditText startDate;
    private EditText endDate;
    private TextView currency;
    private SavePreferences pref;
    private final Context context;
    private final CouponCodeWrapper.CouponCodeData data;
    private final CouponCodeWrapper.RefreshList refreshList;

    public AddCouponCodeDialog(Context context, CouponCodeWrapper.CouponCodeData data, CouponCodeWrapper.RefreshList refreshList) {
        super(context, R.layout.add_coupon_dialog_layout);
        this.data = data;
        this.refreshList = refreshList;
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Coupon");
        View view = getView();

        code = (EditText) view.findViewById(R.id.code);
        amount = (EditText) view.findViewById(R.id.amount);
        startDate = (EditText) view.findViewById(R.id.startDate);
        endDate = (EditText) view.findViewById(R.id.endDate);
        currency = (TextView) view.findViewById(R.id.currency);
        pref = new SavePreferences(context);

        setUpValues();

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.datePickerDialog(context, startDate);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.datePickerDialog(context, endDate);
            }
        });


    }

    @Override
    protected void onSaveClick() {
        super.onSaveClick();
        if (new Validation().checkValidation((ViewGroup) findViewById(R.id.container)) &&
                Util.isDateAfter(startDate.getText().toString(), endDate.getText().toString(), endDate)) {

            if (data != null) {
                data.setCouponCode(code.getText().toString());
                data.setAmount(amount.getText().toString());
                data.setValidityToDate(endDate.getText().toString());
                data.setValidityFromDate(startDate.getText().toString());

                new CouponCodeWrapper().editCouponCode(context, SettingsWebClient.getCouponCodesMap(data), data.getId(), refreshList);
            } else {
                CouponCodeWrapper.CouponCodeData data = new CouponCodeWrapper().new CouponCodeData();
                data.setCouponCode(code.getText().toString());
                data.setAmount(amount.getText().toString());
                data.setValidityToDate(endDate.getText().toString());
                data.setValidityFromDate(startDate.getText().toString());

                new CouponCodeWrapper().addCouponCode(context, SettingsWebClient.getCouponCodesMap(data), refreshList);
            }
            dismiss();
        }
    }

    private void setUpValues() {
        currency.setText(pref.getCurrencyName());

        if (data != null) {
            code.setText(data.getCouponCode());
            amount.setText(data.getAmount());
            startDate.setText(formatDate(data.getValidityFromDate(), "dd-MMM-yyyy", "yyyy-MM-dd"));
            endDate.setText(formatDate(data.getValidityToDate(), "dd-MMM-yyyy", "yyyy-MM-dd"));
        }
    }

    public static String formatDate (String date, String initDateFormat, String endDateFormat) {

        String parsedDate = "";
        try {
            Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
            parsedDate = formatter.format(initDate);
        }catch (Exception ex)
        {
            RetailPosLoging.getInstance().registerLog(AddCouponCodeDialog.class.getName(), ex);
        }
       return parsedDate;
    }
}
