package com.printer.epos.rtpl.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.printer.epos.rtpl.BaseFragment;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.reports.adapter.ReportsAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by android-pc3 on 20/4/15.
 */
public class ReportsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView mList;
    private List<ReportsWrapper> mDataList;
    private ArrayList<String> mReportsItems;

    private final int STAFF_TIME_TRACKING_POS = 0;
    private final int SALES_CHART_POS = 1;
    private final int PRODUCT_PERFORMANCE_POS = 2;
    private final int PRODUCT_THRESHOLD_POS = 3;
    private final int STOCK_IN_HAND_POS = 4;
    private final int DAY_SALES_REPORT_POS = 5;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        mList = (ListView) rootView.findViewById(R.id.list);
        mList.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setTitleText(getString(R.string.reports_title));
        ((Home) getActivity()).setEnabledButtons(true, false, false, false);

        mDataList = new ArrayList<ReportsWrapper>();
        mReportsItems = Util.getReportsItems(getActivity());

        setListAdapter();
    }

    private void setListAdapter() {
        for (int i = 0; i < mReportsItems.size(); i++) {
            ReportsWrapper wrapper = new ReportsWrapper();
            wrapper.setReportTitle(mReportsItems.get(i));
            mDataList.add(wrapper);
        }

        ReportsAdapter mAdapter = new ReportsAdapter(getActivity(), mDataList);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case STAFF_TIME_TRACKING_POS:
                openFragment(FragmentUtils.StaffTimeTrackingFragment);
                break;
            case SALES_CHART_POS:
                openFragment(FragmentUtils.SalesChartFragment);
                break;
            case PRODUCT_PERFORMANCE_POS:
                openFragment(FragmentUtils.ProductPerformanceFragment);
                break;
            case PRODUCT_THRESHOLD_POS:
                openFragment(FragmentUtils.ProductThresholdFragment);
                break;
            case STOCK_IN_HAND_POS:
                openFragment(FragmentUtils.StockInHandFragment);
                break;
            case DAY_SALES_REPORT_POS:
                openFragment(FragmentUtils.DaySalesFragment);
                break;
            default:
                break;
        }
    }

    private void openFragment(FragmentUtils fragmentName) {
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.item_detail_container, fragment);
//        transaction.addToBackStack(ReportsFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(getActivity() instanceof Home){
            ((Home) getActivity()).changeFragment(fragmentName, null, true, false);
        }
    }

}
