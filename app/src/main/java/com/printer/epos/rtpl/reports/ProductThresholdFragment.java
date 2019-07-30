package com.printer.epos.rtpl.reports;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.printer.epos.rtpl.BaseFragment;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.reports.adapter.StockInHandAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductThresholdFragment extends BaseFragment implements View.OnClickListener {

    private ListView list;
    private List<StockInHandData> mDataList;
    private StockInHandAdapter mAdapter;

    public ProductThresholdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_threshold, container, false);
        list = (ListView) rootView.findViewById(R.id.list);
        Button mExportCsv = (Button) rootView.findViewById(R.id.exportCsv);
        mExportCsv.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setTitleText("Product Threshold");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
        getProductList();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                backClick();
                break;
            case R.id.exportCsv:
                new StockInHandWrapper().downloadCsvReport(getActivity());
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
        }.getProductList(getActivity());
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
}
