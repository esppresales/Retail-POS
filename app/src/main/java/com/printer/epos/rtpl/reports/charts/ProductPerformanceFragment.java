package com.printer.epos.rtpl.reports.charts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class ProductPerformanceFragment extends BaseFragment implements View.OnClickListener {


    private EditText mFromDate;
    private EditText mToDate;
    private Button mGetChart;
    private Button mExportCsv;
    private BarChart mChartView;
    private TextView mTopProductName;
    private TextView mTopProductPrice;
    private TextView mTotalItemsSold;

    private View rootView;

    private String mFromDateStr;
    private String mToDateStr;
    private JSONObject mJsonData;
    private JSONArray mJsonArray;

    private List<ProductPerformanceData.ProductPerformance> mProductPerfomanceDataList;

    private final String REPORTS_FILE_NAME = "ProductPerformanceReport";
    private final String PRODUCT_PERFORMANCE_CHART_URL = "file:///android_asset/ProductPerformanceChart.html";
    /*private String mTopProductName;
    private String mTopProductPrice;
    private String mTotalItemsSold;*/



    public ProductPerformanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_product_performance, container, false);
        mFromDate = (EditText) rootView.findViewById(R.id.fromDate);
        mToDate = (EditText) rootView.findViewById(R.id.toDate);
        mGetChart = (Button) rootView.findViewById(R.id.getChart);
        mExportCsv = (Button) rootView.findViewById(R.id.exportCsv);
        mTopProductName = (TextView) rootView.findViewById(R.id.topProductName);
        mTopProductPrice = (TextView) rootView.findViewById(R.id.topProductPrice);
        mTotalItemsSold = (TextView) rootView.findViewById(R.id.totalItemSold);
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
        ((Home) getActivity()).setTitleText("Product Performance");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);

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
                    getProductPerformance();
                }
                break;
            case R.id.exportCsv:
                if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.container)) &&
                        Util.isDateAfter(mFromDate.getText().toString(), mToDate.getText().toString(), getActivity())) {
                    mFromDateStr = mFromDate.getText().toString();
                    mToDateStr = mToDate.getText().toString();
                    DownloadReports.downloadCsvReport(getActivity(), getExportUrl(), REPORTS_FILE_NAME);
                }
                break;
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

    private String getUrl() {
        String url = UiController.appUrl + "product-performance?" + "from_date=" + mFromDateStr + "&to_date="
                + mToDateStr;
        return url;
    }

    private String getExportUrl() {
        String url = UiController.appUrl + "product-performance?" + "from_date=" + mFromDateStr + "&to_date="
                + mToDateStr + "&export=export";
        return url;
    }

    private void getProductPerformance() {

        new ProductPerformanceWrapper() {
            @Override
            protected void onProductPerformanceDataReceived(ProductPerformanceWrapper wrapper) {
                super.onProductPerformanceDataReceived(wrapper);
                mProductPerfomanceDataList = wrapper.getData().getProductPerformance();
                mTopProductPrice.setText(wrapper.getData().getTopProductDetails().getTopProductPrice());
                mTopProductName.setText(wrapper.getData().getTopProductDetails().getTopSellingProduct());

                Log.i("mTotalItemsSold--->",String.valueOf(wrapper.getData().getTopProductDetails().getTotalItemsSold()));
                mTotalItemsSold.setText(String.valueOf(wrapper.getData().getTopProductDetails().getTotalItemsSold()));

                if(mProductPerfomanceDataList.size() != 0)
                    loadBarGraph();
                //createJson();
                //loadChart();

            }
        }.getProductPerformanceData(getActivity(), getUrl());
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
        for (int i = 0; i < mProductPerfomanceDataList.size(); i++) {
            xAxis.add(mProductPerfomanceDataList.get(i).getProductName());
        }
        return xAxis;
    }

    private List<BarDataSet> getDataSet() {
        List<BarDataSet> dataSets = new ArrayList<>();
        List<BarEntry> valueSet = new ArrayList<>();
        for (int i = 0; i < mProductPerfomanceDataList.size(); i++) {
            valueSet.add(new BarEntry(Float.parseFloat(mProductPerfomanceDataList.get(i).getQuantitySold()), i));
        }

        BarDataSet barDataSet = new BarDataSet(valueSet, "Products");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSets.add(barDataSet);

        return dataSets;
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

    private void createJson() {
        try {
            mJsonArray = new JSONArray();
            for (int i = 0; i < mProductPerfomanceDataList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("name", mProductPerfomanceDataList.get(i).getProductName());
                object.put("quantity_sold", mProductPerfomanceDataList.get(i).getQuantitySold());
                mJsonArray.put(object);
            }
            mJsonData = new JSONObject();
            mJsonData.put("data", mJsonArray);
            System.out.println(mJsonData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ProductPerformanceFragment.class.getName(), e);
        }
    }

   /* @SuppressLint("JavascriptInterface")
    private void loadChart() {
        WebSettings settings = mChartView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        mChartView.addJavascriptInterface(new JsonString(), "jsonString");
        mChartView.addJavascriptInterface(new TopProductString(), "topProduct");
        mChartView.addJavascriptInterface(new TotalItemsSold(), "totalItemsSold");
        mChartView.addJavascriptInterface(new TopProductPrice(), "topProductPrice");
        mChartView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });
        mChartView.setWebViewClient(new ChartWebViewClient(getActivity()));
        mChartView.loadUrl(PRODUCT_PERFORMANCE_CHART_URL);
    }*/

    private class JsonString {
        @JavascriptInterface
        public String getJsonString() {
            return mJsonData.toString();
        }
    }

    private class TopProductString {
        @JavascriptInterface
        public String getTopProductName() {
            return mTopProductName.toString();
        }

    }

    private class TotalItemsSold {
        @JavascriptInterface
        public String getTotalItemsSold() {
            return mTotalItemsSold.toString();
        }

    }

    private class TopProductPrice {
        @JavascriptInterface
        public String getTopProductPrice() {
            String topProductPrice = UiController.sCurrency + " " + Util.priceFormat(mTopProductPrice.toString());
            return topProductPrice;
        }

    }
}
