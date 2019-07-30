package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import java.util.List;

/**
 * Created by android-pc3 on 13/5/15.
 */
public class PrinterConfigurationAdapter extends ArrayAdapter<PrinterDetails> {


    List<PrinterDetails> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;
    private int TOGGLE_BUTTON_WIDTH = 0;
    private int TOGGLE_BUTTON_HEIGHT = 0;
    private PrinterDetails _objModel;


    public PrinterConfigurationAdapter(Fragment fragment,
                                       List<PrinterDetails> data) {
        super(fragment.getActivity(), R.layout.adapter_printer_configuration_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;


        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_printer_configuration_list_item, parent, false);
            holder = new ViewHolder();
            holder.printerID = (EditText) convertView.findViewById(R.id.printerId);
            holder.printerName = (TextView) convertView.findViewById(R.id.name);
            holder.iSOnOffswitch = (ToggleButton) convertView.findViewById(R.id.onOffBtn);

            setToggleDimension(holder.iSOnOffswitch);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        _objModel = data.get(position);
        holder.iSOnOffswitch.setTag(position);

        if (_objModel.getPrinterType() != null)
            holder.printerName.setText(_objModel.getPrinterType());

        if (_objModel.getPrinterName() != null)
            holder.printerID.setText(_objModel.getPrinterName());

        holder.iSOnOffswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int pos = (Integer) compoundButton.getTag();
                if (isChecked) {
                    data.get(pos).setIsEnabled("1");
                    notifyDataSetChanged();
                } else {
                    data.get(pos).setIsEnabled("0");
                    notifyDataSetChanged();
                }
            }
        });

        holder.printerID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                /*if(hasFocus)
                  holder.printerID.setText("");
               else {*/
                //holder.printerID.setFocus
                _objModel.setPrinterName(holder.printerID.getText().toString());
                //}

            }
        });

       /* holder.printerID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.requestFocus();
                        view.requestFocusFromTouch();
                    }
                });
                return false;
            }
        });
*/
        if (_objModel.getIsEnabled() != null && _objModel.getIsEnabled().equals("0"))
            holder.iSOnOffswitch.setChecked(false);
        else
            holder.iSOnOffswitch.setChecked(true);

        return convertView;
    }

    @Override
    public PrinterDetails getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void addItem(PrinterDetails wrapper) {
        data.add(wrapper);
        notifyDataSetChanged();
    }


    private void getWidthAndHeightOfToggle() {
        BitmapDrawable bd = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_off_button_bg);
        TOGGLE_BUTTON_HEIGHT = bd.getBitmap().getHeight();
        TOGGLE_BUTTON_WIDTH = bd.getBitmap().getWidth();
    }

    protected void setToggleDimension(ToggleButton button) {
        getWidthAndHeightOfToggle();
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) button.getLayoutParams();
        param.height = TOGGLE_BUTTON_HEIGHT;
        param.width = TOGGLE_BUTTON_WIDTH;
        button.setLayoutParams(param);
    }


    class ViewHolder {
        TextView printerName;
        EditText printerID;
        ToggleButton iSOnOffswitch;

    }
}
