package com.printer.epos.rtpl;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.EditCategoryListener;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.adapter.ProductCategoryAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.CategoryWrapper;

import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.printer.epos.rtpl.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.printer.epos.rtpl.ItemDetailActivity}
 * on handsets.
 */
public class ProductCategoryFragment extends BaseFragment implements View.OnClickListener, EditCategoryListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductCategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    private GridView list;
    private ProductCategoryAdapter mAdapter;
    private int scrollHeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_category, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.pink_icon);
        floatingActionButton.canScrollHorizontally(100);
        floatingActionButton.setOnClickListener(this);

        RelativeLayout.LayoutParams floatingActionButton_param = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
//        floatingActionButton_param.width = (int)(deviceHeight*.2f);
//        floatingActionButton_param.height = (int)(deviceHeight*.2f);
        floatingActionButton_param.bottomMargin = (int) (deviceHeight * .02f);
        floatingActionButton_param.rightMargin = (int) (deviceWidth * .12f);
        floatingActionButton.setLayoutParams(floatingActionButton_param);

        list = (GridView) rootView.findViewById(R.id.list);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.hideSoftKeypad(getActivity());
                return false;
            }
        });
        RelativeLayout.LayoutParams list_param = (RelativeLayout.LayoutParams) list.getLayoutParams();
        list_param.leftMargin = (int) (deviceWidth * .02f);
        list_param.rightMargin = (int) (deviceWidth * .02f);
        list_param.bottomMargin = (int) (deviceHeight * .06f);
        list.setLayoutParams(list_param);
        list.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), (int) (deviceWidth * .02f), (int) (deviceHeight * .02f));
        list.setVerticalSpacing((int) (deviceWidth * .02f));
        list.setHorizontalSpacing((int) (deviceWidth * .02f));

        if (!UiController.mSavePreferences.get_roleId().equals("1"))
            floatingActionButton.setVisibility(View.INVISIBLE);




        return rootView;
    }

    private List<CategoryWrapper.CategoryListWrapper> dataList;

    @Override
    public void onResume() {
        super.onResume();

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, final List<CategoryListWrapper> data) {
                dataList = data;
                mAdapter = new ProductCategoryAdapter(ProductCategoryFragment.this, getActivity(), data);
                list.setAdapter(mAdapter);
                list.setSelection(scrollHeight);

                ((Home) getActivity()).SearchBarET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        try {
                            if(mAdapter != null)
                                mAdapter.getFilter(((Home) getActivity()).SearchBarET.getText().toString());

                        } catch (Exception ex) {
                            RetailPosLoging.getInstance().registerLog(ProductCategoryFragment.class.getName(), ex);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    }
                });
            }
        }.getProductCategoryList(getActivity());

        ((Home) getActivity()).setTitleText(getString(R.string.Product_Category));
        ((Home) getActivity()).setEnabledButtons(true, false, false, true);
        ((Home) getActivity()).SearchBarET.setHint("Search product category name");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pink_icon:

                scrollHeight = list.getFirstVisiblePosition();
                if (UiController.mSavePreferences.get_roleId().equals("1")) {
                    CustomDialog cd = new CustomDialog();
                    cd.editCategoryDialog(getActivity(), "", "", "", "", this, true);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onCategorySaved(String name, String description) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("description", description);

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {

                dataList = data;
                mAdapter = new ProductCategoryAdapter(ProductCategoryFragment.this, getActivity(), data);
                list.setAdapter(mAdapter);
                list.setSelection(scrollHeight);

            }
        }.saveProductCategory(getActivity(), UiController.appUrl + "categories", map);

    }

    @Override
    public void onCategoryUpdate(String id, String name, String description, String markAsDelete) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("description", description);

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {

                dataList = data;
                mAdapter = new ProductCategoryAdapter(ProductCategoryFragment.this, getActivity(), data);
                list.setAdapter(mAdapter);
                list.setSelection(scrollHeight);
            }
        }.editProductCategory(getActivity(), UiController.appUrl + "categories/" + id, map);
    }

    public void viewCategory(CategoryWrapper.CategoryListWrapper _objModel) {
        scrollHeight = list.getFirstVisiblePosition();
        if (UiController.mSavePreferences.get_roleId().equals("1")) {
            new CustomDialog().editCategoryDialog(getActivity(), _objModel.getId(), _objModel.getName(), _objModel.getDescription(), _objModel.getMarkAsDelete(), ProductCategoryFragment.this, false);
        }
    }
}
