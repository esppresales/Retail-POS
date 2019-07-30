package com.printer.epos.rtpl.reports;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.printer.epos.rtpl.BaseFragment;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.reports.adapter.StockInHandAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockInHandFragment extends BaseFragment implements View.OnClickListener {


    private String mQuantity;

    private ListView list;
    private EditText mProductUnit;
    private Button mExportCsv;
    private Button mGetList;

    private List<StockInHandData> mDataList;
    private StockInHandAdapter mAdapter;
    private View rootView;

    public StockInHandFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_stock_in_hand, container, false);
        list = (ListView) rootView.findViewById(R.id.list);
        mProductUnit = (EditText) rootView.findViewById(R.id.nofUnits);
        mExportCsv = (Button) rootView.findViewById(R.id.exportCsv);
        mGetList = (Button) rootView.findViewById(R.id.getList);

        mGetList.setOnClickListener(this);
        mExportCsv.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setTitleText("Stock In Hand");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.getList:
                if (validation(mProductUnit)) {
                    mQuantity = mProductUnit.getText().toString();
                    getProductList();
                }
                break;
            case R.id.backButton:
                backClick();
                break;
            case R.id.exportCsv:
                if (validation(mProductUnit)) {
                    mQuantity = mProductUnit.getText().toString();
                    new StockInHandWrapper().downloadCsvReport(getActivity(), mQuantity);
                }
                break;
            default:
                break;
        }
    }

    private void getProductList() {
        new StockInHandWrapper() {
            @Override
            protected void onProductListReceived(StockInHandWrapper wrapper) {
                super.onProductListReceived(wrapper);
                mDataList = wrapper.getData();
                list.setVisibility(View.VISIBLE);
                mAdapter = new StockInHandAdapter(getActivity(), mDataList);
                list.setAdapter(mAdapter);
            }
        }.getProductList(getActivity(), mQuantity);
    }

    public void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStackImmediate();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }

    private boolean validation(EditText et) {
        if (!TextUtils.isEmpty(et.getText().toString()))
            return true;
        else {
            et.setError("Number of Units is required");
            return false;
        }
    }
}
