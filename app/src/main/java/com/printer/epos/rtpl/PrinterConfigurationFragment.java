package com.printer.epos.rtpl;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;
import com.printer.epos.rtpl.dialogs.SettingsWebClient;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrinterConfigurationFragment extends BaseFragment implements View.OnClickListener {

    //private ListView list;
    private ScrollView list;
    private LinearLayout mLayout;
    private int TOGGLE_BUTTON_WIDTH = 0;
    private int TOGGLE_BUTTON_HEIGHT = 0;

    private StringBuilder printerNameBuilder;
    private StringBuilder printerTypeBuilder;
    private StringBuilder isOnOffBuilder;

    private boolean isMasterEt = true;

    private int sPrinterId = 0;


    public PrinterConfigurationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_printer_configuration, container, false);


        printerNameBuilder = new StringBuilder();
        printerTypeBuilder = new StringBuilder();
        isOnOffBuilder = new StringBuilder();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;


        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.pink_icon);
        floatingActionButton.canScrollHorizontally(100);

        RelativeLayout.LayoutParams floatingActionButton_param = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();

        floatingActionButton_param.bottomMargin = (int) (deviceHeight * .02f);
        floatingActionButton_param.rightMargin = (int) (deviceWidth * .12f);
        floatingActionButton.setLayoutParams(floatingActionButton_param);
        floatingActionButton.setOnClickListener(this);

        //list = (ListView) rootView.findViewById(R.id.list);
        list = (ScrollView) rootView.findViewById(R.id.list);
        RelativeLayout.LayoutParams list_param = (RelativeLayout.LayoutParams) list.getLayoutParams();
        list_param.leftMargin = (int) (deviceWidth * .02f);
        list_param.rightMargin = (int) (deviceWidth * .02f);
        list_param.bottomMargin = (int) (deviceHeight * .06f);
        list.setLayoutParams(list_param);

        mLayout = (LinearLayout) rootView.findViewById(R.id.itemContainer);

        addAllPrinterItems();

        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();

        ((Home) getActivity()).setTitleText("Printer Configuration");
        ((Home) getActivity()).setEnabledButtons(false, true, true, false);
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).saveButton.setOnClickListener(this);

        if (Util.printerDetailsList.size() == 0)
            setFixedListItems();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pink_icon:
                PrinterDetails wrapper = new PrinterDetails();
                wrapper.setPrinterType("Printer " + getPrinterId());
                wrapper.setIsEnabled("0");
                addItems(wrapper, 1);
                sPrinterId = sPrinterId + 1;
                break;

            case R.id.backButton:
                backClick();
                break;
            case R.id.saveButton: {
                updatePrinterConfiguration();
                break;
            }
            case R.id.delete:
                deletePrinter(view);
                sPrinterId = sPrinterId - 1;
                break;
            default:
                break;
        }
    }

    private void addAllPrinterItems()
    {
        sPrinterId = 0;
        for (int i = 0; i < Util.printerDetailsList.size(); i++) {
            PrinterDetails wrapper = Util.printerDetailsList.get(i);
            ++sPrinterId;
            addItems(wrapper, 0);
        }
    }

    private void deletePrinter(final View view)
    {
        final RelativeLayout layout = (RelativeLayout) (view.getParent()).getParent();
        final PrinterDetails details = (PrinterDetails)layout.getTag();

        if(details.isNewItems())
        {
            Util.printerDetailsList.remove(details);
            ((ViewGroup)layout.getParent()).removeView(layout);
            //addAllPrinterItems();
            list.invalidate();

        }
        else
        {
            new CustomDialog().showTwoButtonAlertDialog(getActivity(), null,
                    "Do you want to delete this?", getString(R.string.ok_button), getString(R.string.cancel_button), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                        @Override
                        public void onPositiveClick() {
                            deletePrinter(details.getId());
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });

        }

    }

    private void deletePrinter(String id)
    {
        new WebServiceCalling().callWSForDelete(getActivity(),UiController.appUrl + "settings/"+id, new WebServiceHandler() {
            @Override
            public void onSuccess(String response) {
                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(response);
                    String msg = responseObj.getString("msg");
                    Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                    backClick();

                } catch (JSONException e) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(PrinterConfigurationFragment.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void updatePrinterConfiguration() {

        if (!checkValidation(list)) {
            getItems(list);
            printerNameBuilder.deleteCharAt(printerNameBuilder.length() - 1);
            printerTypeBuilder.deleteCharAt(printerTypeBuilder.length() - 1);
            isOnOffBuilder.deleteCharAt(isOnOffBuilder.length() - 1);

            SettingsWebClient.updateSettings(getActivity(), SettingsWebClient.getPrinterMap(printerTypeBuilder.toString(),
                    printerNameBuilder.toString(), isOnOffBuilder.toString()), null);
            backClick();
        }
    }

    private void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStackImmediate();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }

    private void setFixedListItems() {
        PrinterDetails wrapper = new PrinterDetails();
        wrapper.setPrinterType("Master Printer");
        wrapper.setIsEnabled("0");
        addItems(wrapper, 1);
        ++sPrinterId;

        PrinterDetails wrapper1 = new PrinterDetails();
        wrapper1.setPrinterType("Printer 1");
        wrapper1.setIsEnabled("0");
        addItems(wrapper1, 1);
        ++sPrinterId;

        PrinterDetails wrapper2 = new PrinterDetails();
        wrapper2.setPrinterType("Printer 2");
        wrapper2.setIsEnabled("0");
        addItems(wrapper2, 1);
        ++sPrinterId;
    }

    private int getPrinterId() {
        int id = 0;
        int size = Util.printerDetailsList.size();
        PrinterDetails obj = Util.printerDetailsList.get(size - 1);
        String printerType = obj.getPrinterType();
        if (printerType != null) {
            if(printerType.equals("Master Printer"))
                id = 1;
            else
                id = Integer.parseInt(printerType.substring("Printer".length() + 1, printerType.length())) + 1;
        }

        return id;
    }

    private void getWidthAndHeightOfToggle() {
        BitmapDrawable bd = (BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.ic_off_button_bg);
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

    private void addItems(PrinterDetails wrapper, int add) {
        View child = getActivity().getLayoutInflater().inflate(R.layout.adapter_printer_configuration_list_item, null);

        EditText printerID = (EditText) child.findViewById(R.id.printerId);
        TextView printerName = (TextView) child.findViewById(R.id.name);
        ToggleButton iSOnOffswitch = (ToggleButton) child.findViewById(R.id.onOffBtn);
        ImageView delete = (ImageView)child.findViewById(R.id.delete);

        delete.setOnClickListener(this);

        if(wrapper.getPrinterType().equals("Master Printer"))
            delete.setVisibility(View.GONE);

        setToggleDimension(iSOnOffswitch);

        if (wrapper.getPrinterType() != null)
            printerName.setText(wrapper.getPrinterType());

        if (wrapper.getPrinterName() != null)
            printerID.setText(wrapper.getPrinterName().equals("null") ? "" : wrapper.getPrinterName());

        if (wrapper.getIsEnabled() != null && wrapper.getIsEnabled().equals("0"))
            iSOnOffswitch.setChecked(false);
        else
            iSOnOffswitch.setChecked(true);

        if (add == 1) {
            wrapper.setIsNewItems(true);
            Util.printerDetailsList.add(wrapper);
        }


        child.setTag(wrapper);

        mLayout.addView(child);
    }

    private void getItems(ViewGroup rootLayout) {

        int childCount = rootLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {

            if (rootLayout.getChildAt(i) instanceof ViewGroup) {
                getItems((ViewGroup) rootLayout.getChildAt(i));
            } else if (rootLayout.getChildAt(i) instanceof View) {

                View view = rootLayout.getChildAt(i);
                if (view instanceof EditText) {

                    if(TextUtils.isEmpty(((EditText) view).getText().toString()))
                        printerNameBuilder.append(null + ",");
                    else
                        printerNameBuilder.append(((EditText) view).getText().toString() + ",");
                    if (isMasterEt) {
                        SavePreferences prefs = new SavePreferences(getActivity());
                        prefs.storeMasterPrinterIp(((EditText) view).getText().toString());
                        isMasterEt = false;
                    }

                } else if (view instanceof ToggleButton) {
                    if (((ToggleButton) view).isChecked())
                        isOnOffBuilder.append("1,");
                    else
                        isOnOffBuilder.append("0,");
                } else if (view instanceof TextView) {
                    printerTypeBuilder.append(((TextView) view).getText().toString() + ",");
                }
            }
        }

    }

    private boolean checkValidation(ViewGroup viewGroup)
    {
        boolean flag = false;
        LinearLayout parentLL = (LinearLayout) viewGroup.findViewById(R.id.itemContainer);
        if(parentLL != null && parentLL.getChildCount() != 0){
            for (int i = 0; i < parentLL.getChildCount(); i++) {
                LinearLayout ll = (LinearLayout) parentLL.getChildAt(i).findViewById(R.id.ll);
                if (ll != null) {
                    EditText et = (EditText) ll.findViewById(R.id.printerId);
                    ToggleButton tt = (ToggleButton) ll.findViewById(R.id.onOffBtn);
                    if(tt.isChecked())
                    {
                        if(TextUtils.isEmpty(et.getText().toString())) {
                            et.setError("Printer id is required");
                            flag = true;
                        }
                    }
                }

            }
        }
        return flag;
    }

}
