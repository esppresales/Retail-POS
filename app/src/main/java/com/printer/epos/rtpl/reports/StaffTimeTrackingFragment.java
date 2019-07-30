package com.printer.epos.rtpl.reports;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.printer.epos.rtpl.BaseFragment;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.Util;

import java.util.List;

/**
 * Created by android-pc3 on 20/4/15.
 */
public class StaffTimeTrackingFragment extends BaseFragment implements View.OnClickListener {

    private ListView mList;
    private StaffTimeTrackingAdapter mAdapter;
    private List<StaffTimeTrackingData> mDataList;

    public int deviceWidth;
    public int deviceHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_staff_time_tracking_report, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        mList = (ListView) rootView.findViewById(R.id.list);
        mList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.hideSoftKeypad(getActivity());
                return false;
            }
        });
        RelativeLayout.LayoutParams list_param = (RelativeLayout.LayoutParams) mList.getLayoutParams();

        list_param.bottomMargin = (int) (deviceHeight * .06f);
        mList.setLayoutParams(list_param);
        mList.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .02f), 0);
        mList.setLayoutParams(list_param);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((Home) getActivity()).setTitleText(getActivity().getString(R.string.reports_title));
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, true);
        ((Home) getActivity()).SearchBarET.setHint("Search staff name");

        new StaffTimeTrackingWrapper() {
            @Override
            protected void onTimeTrackingDataReceived(StaffTimeTrackingWrapper wrapper) {
                super.onTimeTrackingDataReceived(wrapper);
                setAdapter(wrapper);
            }
        }.getStaffTimeTrackingData(getActivity());
    }

    private void setAdapter(StaffTimeTrackingWrapper wrapper) {
        mDataList = wrapper.getData();
        mAdapter = new StaffTimeTrackingAdapter(getActivity(), mDataList);
        mList.setAdapter(mAdapter);

        ((Home) getActivity()).SearchBarET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    mAdapter.getFilter(((Home) getActivity()).SearchBarET.getText().toString());
                } catch (Exception ex) {
                    RetailPosLoging.getInstance().registerLog(StaffTimeTrackingFragment.class.getName(), ex);
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                backClick();
                break;
            default:
                break;
        }
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
}
