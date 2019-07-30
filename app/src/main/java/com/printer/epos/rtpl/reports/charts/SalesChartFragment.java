package com.printer.epos.rtpl.reports.charts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.printer.epos.rtpl.BaseFragment;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesChartFragment extends BaseFragment implements View.OnClickListener {


    private EditText mFromDate;
    private EditText mToDate;
    private Button mGetChart;
    private Button mExportCsv;
    //private WebView mChartView;
    private BarChart mChartView;

    private View rootView;

    private String mFromDateStr;
    private String mToDateStr;
    private JSONObject mJsonData;
    private JSONArray mJsonArray;
    private final String REPORTS_FILE_NAME = "SalesChartReport";
    private final String SALES_CHART_URL = "file:///android_asset/SalesChart.html";

    private List<SalesChartData.EmployeeData> mSalesDataList;

    private TextView mTotalTransaction;
    private TextView mTotalSales;

    public SalesChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sales_chart, container, false);
        mFromDate = (EditText) rootView.findViewById(R.id.fromDate);
        mToDate = (EditText) rootView.findViewById(R.id.toDate);
        mGetChart = (Button) rootView.findViewById(R.id.getChart);
        mExportCsv = (Button) rootView.findViewById(R.id.exportCsv);
        mTotalSales = (TextView) rootView.findViewById(R.id.totalSales);
        mTotalTransaction = (TextView) rootView.findViewById(R.id.totalTransaction);
        //mChartView = (WebView) rootView.findViewById(R.id.chartWebView);
        mChartView = (BarChart) rootView.findViewById(R.id.chartWebView);

        setUpBarGraph();

        mGetChart.setOnClickListener(this);
        mExportCsv.setOnClickListener(this);

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.datePickerDialog(getActivity(), mFromDate, 0);
            }
        });

        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.datePickerDialog(getActivity(), mToDate, 0);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setTitleText("Sales Chart");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
        //loadChart();
        //loadBarGraph();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                backClick();
                break;
            case R.id.getChart:
                if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.container)) &&
                        Util.isDateAfter(mFromDate.getText().toString(), mToDate.getText().toString(), getActivity())) {
                    mFromDateStr = mFromDate.getText().toString();
                    mToDateStr = mToDate.getText().toString();
                    getSalesChart();
                }

                break;
            case R.id.exportCsv:
                if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.container)) &&
                        Util.isDateAfter(mFromDate.getText().toString(), mToDate.getText().toString(), getActivity())) {
                    mFromDateStr = mFromDate.getText().toString();
                    mToDateStr = mToDate.getText().toString();
                    DownloadReports.downloadCsvReport(getActivity(), getExportUrl(), REPORTS_FILE_NAME);
                }
            default:
                break;
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

    private String getExportUrl() {
        String url = UiController.appUrl + "sales-chart?" + "from_date=" + mFromDateStr + "&to_date="
                + mToDateStr + "&export=export";
        return url;
    }

    private String getUrl() {
        String url = UiController.appUrl + "sales-chart?" + "from_date=" + mFromDateStr + "&to_date="
                + mToDateStr;
        return url;
    }

    private void getSalesChart() {
        new SalesChartWrapper() {
            @Override
            protected void onSalesChartDataReceived(SalesChartWrapper wrapper) {
                super.onSalesChartDataReceived(wrapper);
                mSalesDataList = wrapper.getData().getEmployeeData();
                mTotalTransaction.setText(wrapper.getData().getTotalTransactionData().getTotalTransactions());
                String totalSales = wrapper.getData().getTotalTransactionData().getTotalSales();
                mTotalSales.setText(UiController.sCurrency+" "+ Util.priceFormat(totalSales));
                if(mSalesDataList.size() != 0)
                    loadBarGraph();
                //createJson();
                //loadChart();
            }
        }.getSalesChartData(getActivity(), getUrl());
    }

    private void loadBarGraph()
    {
        BarData data = new BarData(getXAxisValues(), getDataSet());
        mChartView.setData(data);
        mChartView.animateXY(2000, 2000);
        mChartView.invalidate();

    }

    private List<String> getXAxisValues()
    {
        List<String> xAxis = new ArrayList<>();
        for (int i = 0; i < mSalesDataList.size(); i++) {
            xAxis.add(mSalesDataList.get(i).getEmployeeName());
        }
        return xAxis;
    }

    private List<BarDataSet> getDataSet() {
        List<BarDataSet> dataSets = new ArrayList<>();
        List<BarEntry> valueSet = new ArrayList<>();
        for (int i = 0; i < mSalesDataList.size(); i++) {
            valueSet.add(new BarEntry(Float.parseFloat(mSalesDataList.get(i).getEmployeeTotalSales()), i));
        }

        BarDataSet barDataSet = new BarDataSet(valueSet, "Employee");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSets.add(barDataSet);

        return dataSets;
    }


    private void createJson() {
        try {
            mJsonArray = new JSONArray();
            for (int i = 0; i < mSalesDataList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("employee_name", mSalesDataList.get(i).getEmployeeName());
                object.put("employee_total_sales", mSalesDataList.get(i).getEmployeeTotalSales());

                mJsonArray.put(object);

            }
            mJsonData = new JSONObject();
            mJsonData.put("data", mJsonArray);
            System.out.println(mJsonData.toString());


        } catch (JSONException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(SalesChartFragment.class.getName(), e);
        }
    }

    private void setUpBarGraph()
    {
        mChartView.setDrawBarShadow(false);
        mChartView.setDrawValueAboveBar(true);
        mChartView.setPinchZoom(false);
        mChartView.setDrawGridBackground(false);
        mChartView.setDescription(null);

        XAxis xAxis = mChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(15f);
        xAxis.setTextColor(getActivity().getResources().getColor(R.color.blue_button_color));
        xAxis.setSpaceBetweenLabels(2);


        YAxis leftAxis = mChartView.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setTextSize(12f);

        YAxis rightAxis = mChartView.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextSize(12f);

        Legend l = mChartView.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(14f);
        l.setTextSize(16f);
        l.setXEntrySpace(8f);
    }

    /*@SuppressLint("JavascriptInterface")
    private void loadChart() {
        WebSettings settings = mChartView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        mChartView.addJavascriptInterface(new JsonString(), "jsonString");
        mChartView.addJavascriptInterface(new TotalTransaction(), "totalTransaction");
        mChartView.addJavascriptInterface(new TotalSalesData(), "totalSales");
        mChartView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });
        mChartView.setWebViewClient(new ChartWebViewClient(getActivity()));
        mChartView.loadUrl(SALES_CHART_URL);
    }
*/
    private class JsonString {
        @JavascriptInterface
        public String getJsonString() {
            return  "{\"data\":[{\"employee_name\":\"Admin Admin\",\"employee_total_sales\":\"434.4\"},{\"employee_name\":\"John Smith\",\"employee_total_sales\":\"79.2\"}]}";
            //return mJsonData.toString();
        }
    }

    private class TotalTransaction {
        @JavascriptInterface
        public String getTotalTransactionData() {
            return mTotalTransaction.toString();
        }
    }

    private class TotalSalesData {
        @JavascriptInterface
        public String getTotalSalesData() {
            String totalSales = UiController.sCurrency + " " + Util.priceFormat(mTotalSales.toString());
            return totalSales;
        }
    }
}
