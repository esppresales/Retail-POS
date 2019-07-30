package com.printer.epos.rtpl;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.adapter.SettingsAdapter;
import com.printer.epos.rtpl.dialogs.CurrencyDialog;
import com.printer.epos.rtpl.dialogs.DeviceConfigDialog;
import com.printer.epos.rtpl.dialogs.DiscountDialog;
import com.printer.epos.rtpl.dialogs.LoyaltyPointsDialog;
import com.printer.epos.rtpl.dialogs.TaxDialog;
import com.printer.epos.rtpl.services.FetchSettingsService;
import com.printer.epos.rtpl.wrapper.SettingsWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.CurrencyDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.CurrencyWrapper;
import com.printer.epos.rtpl.wrapper.settingswrapper.DiscountDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.LoyaltyPointDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.PeripheralDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.ReceiptHeaderDetails;
import com.printer.epos.rtpl.wrapper.settingswrapper.TaxDetails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by android-pc3 on 1/4/15.
 */
public class SettingsFragment extends BaseFragment implements AdapterView.OnItemClickListener, CurrencyWrapper.CurrencyListener {

    private ListView mList;

    private List<SettingsWrapper> mDataList;
    private Map<String, String> mSettingItems;

    private final int DISCOUNT_POS = 0;
    private final int TAX_POS = 1;
    private final int COUPON_CODES_POS = 2;
    private final int CURRENCY_POS = 3;
    private final int LOYALTY_POINT_POS = 4;
    private final int PRINTER_CONFIG_POS = 5;
    private final int PERIPHERAL_CONFIG_POS = 6;
    private final int RECEIPT_HEADER_POS = 7;

    private ReceiptHeaderDetails receiptHeaderDetails;
    private DiscountDetails discountDetails;
    private LoyaltyPointDetails loyaltyPointDetails;
    private TaxDetails taxDetails;
    private PrinterDetails printerDetails;
    private PeripheralDetails peripheralDetails;
    private List<CurrencyDetails> currencyDetailsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        mList = (ListView) rootView.findViewById(R.id.list);
        RelativeLayout.LayoutParams list_param = (RelativeLayout.LayoutParams) mList.getLayoutParams();
        list_param.leftMargin = (int) (deviceWidth * .02f);
        list_param.rightMargin = (int) (deviceWidth * .02f);
        list_param.bottomMargin = (int) (deviceHeight * .06f);
        mList.setLayoutParams(list_param);
        mList.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .02f), 0);
        mList.setOnItemClickListener(this);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((Home) getActivity()).setTitleText(getString(R.string.setting_title));
        ((Home) getActivity()).setEnabledButtons(true, false, false, false);

        mDataList = new ArrayList<SettingsWrapper>();
        mSettingItems = Util.getSettingsItems(getActivity());
        setListAdapter();

        if (Util.checkInternet(getActivity())) {
            //start service for fetch settings
            getActivity().startService(new Intent(getActivity(), FetchSettingsService.class));
            new CurrencyWrapper().getCurrencies(getActivity(), this);
        } else
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

    }

    private void setListAdapter() {
        Set<Map.Entry<String, String>> mapSet = mSettingItems.entrySet();
        Iterator<Map.Entry<String, String>> mapIterator = mapSet.iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<String, String> settingsMap = mapIterator.next();
            SettingsWrapper wrapper = new SettingsWrapper();
            wrapper.setRowTitle(settingsMap.getKey());
            wrapper.setRowDetail(settingsMap.getValue());

            mDataList.add(wrapper);
        }

        SettingsAdapter mAdapter = new SettingsAdapter(this, mDataList);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case DISCOUNT_POS:
                if (discountDetails == null)
                    discountDetails = new DiscountDetails();
                new DiscountDialog(getActivity(), discountDetails, null).show();
                break;
            case TAX_POS:
                if (taxDetails == null)
                    taxDetails = new TaxDetails();
                new TaxDialog(getActivity(), taxDetails, null).show();
                break;
            case COUPON_CODES_POS:
//                CouponCodeFragment fragment = new CouponCodeFragment();
//                openFragment(fragment);
                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.CouponCodeFragment, null, true, false);
                }
                break;
            case CURRENCY_POS:
                if (Util.checkInternet(getActivity()))
                    new CurrencyDialog(getActivity(), currencyDetailsList, null).show();
                else
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                break;
            case LOYALTY_POINT_POS:
                if (loyaltyPointDetails == null)
                    loyaltyPointDetails = new LoyaltyPointDetails();
                new LoyaltyPointsDialog(getActivity(), loyaltyPointDetails, null).show();
                break;
            case PRINTER_CONFIG_POS:
                if (printerDetails == null)
                    printerDetails = new PrinterDetails();
                //new DeviceConfigDialog(getActivity(), printerDetails, null, SettingsFragment.this).show();
//                PrinterConfigurationFragment printerConfigurationFragment = new PrinterConfigurationFragment();
//                openFragment(printerConfigurationFragment);

                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.PrinterConfigurationFragment, null, true, false);
                }
                break;
            case PERIPHERAL_CONFIG_POS:
                if (peripheralDetails == null)
                    peripheralDetails = new PeripheralDetails();
                new DeviceConfigDialog(getActivity(), null, peripheralDetails, null).show();

                break;
            case RECEIPT_HEADER_POS:
                if (receiptHeaderDetails == null)
                    receiptHeaderDetails = new ReceiptHeaderDetails();
                //new ReceiptHeaderDialog(getActivity(), receiptHeaderDetails, null).show();
//                ReceiptHeaderFragment receiptHeaderFragment = new ReceiptHeaderFragment();
//                openFragment(receiptHeaderFragment);

                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.ReceiptHeaderFragment, null, true, false);
                }

                break;
            default:
                break;

        }
    }

//    private void openFragment(Fragment fragment) {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.item_detail_container, fragment);
//        transaction.addToBackStack(SettingsFragment.class.toString());
//
//        // Commit the transaction
//        transaction.commit();
//    }


    @Override
    public void onCurrencyReceived(CurrencyWrapper currencyWrapper) {
        currencyDetailsList = currencyWrapper.getData().getCurrencies();
    }
}
