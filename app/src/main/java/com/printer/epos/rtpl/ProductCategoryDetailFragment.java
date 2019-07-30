package com.printer.epos.rtpl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.printer.epos.rtpl.wrapper.CategoryWrapper;

import de.greenrobot.event.EventBus;

/**
 * Created by android-pc3 on 26/3/15.
 */
public class ProductCategoryDetailFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public void onResume() {
        super.onResume();

        ((Home) getActivity()).setTitleText("Category Detail");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_productcategory_detail, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        ScrollView formScroll = (ScrollView) rootView.findViewById(R.id.formScroll);
        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        form_param.bottomMargin = (int) (deviceHeight * .03f);
        formScroll.setLayoutParams(form_param);


        CategoryWrapper.CategoryListWrapper mWrapper = EventBus.getDefault().removeStickyEvent(CategoryWrapper.CategoryListWrapper.class);
        TextView mProductDescription = (TextView) rootView.findViewById(R.id.product_description);
        TextView mProductQuantity = (TextView) rootView.findViewById(R.id.productQuantity);
        TextView mProductName = (TextView) rootView.findViewById(R.id.categoryName);
        if (mWrapper != null) {
            if (!TextUtils.isEmpty(mWrapper.getDescription()))
                mProductDescription.setText(mWrapper.getDescription());
            else
                mProductDescription.setText("None");

            mProductQuantity.setText(mWrapper.getProductCount());
            mProductName.setText(mWrapper.getName());
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backButton)
            backClick();

    }

    void backClick() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i(ProductCategoryDetailFragment.class.getName(), "popping backstack");
            fm.popBackStack();
        } else {
            Log.i(ProductCategoryDetailFragment.class.getName(), "nothing on backstack, calling super");
        }
    }

}
