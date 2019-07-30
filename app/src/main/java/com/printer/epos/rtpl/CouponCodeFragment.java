package com.printer.epos.rtpl;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.printer.epos.rtpl.adapter.CouponCodeAdapter;
import com.printer.epos.rtpl.dialogs.AddCouponCodeDialog;
import com.printer.epos.rtpl.wrapper.CouponCodeWrapper;

import java.util.List;

/**
 * Created by android-pc3 on 3/4/15.
 */
public class CouponCodeFragment extends BaseFragment implements View.OnClickListener, CouponCodeWrapper.RefreshList {

    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coupon_code, container, false);

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

        list = (ListView) rootView.findViewById(R.id.list);
        RelativeLayout.LayoutParams list_param = (RelativeLayout.LayoutParams) list.getLayoutParams();
        list_param.leftMargin = (int) (deviceWidth * .02f);
        list_param.rightMargin = (int) (deviceWidth * .02f);
        list_param.bottomMargin = (int) (deviceHeight * .06f);
        list.setLayoutParams(list_param);
        list.setPadding((int) (deviceWidth * .02f), 0, (int) (deviceWidth * .02f), 0);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((Home) getActivity()).setTitleText("Coupon Codes");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);

        new CouponCodeWrapper().getCouponCodeList(getActivity(), CouponCodeFragment.this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pink_icon:
                new AddCouponCodeDialog(getActivity(), null, CouponCodeFragment.this).show();
                break;
            case R.id.backButton:
                backClick();
                break;
            default:
                break;
        }

    }

    @Override
    public void onListRefresh(Context context, List<CouponCodeWrapper.CouponCodeData> data) {
        CouponCodeAdapter mAdapter = new CouponCodeAdapter(CouponCodeFragment.this, data);
        list.setAdapter(mAdapter);
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
