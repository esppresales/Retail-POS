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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.adapter.OrderListingAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.OrderWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-sristi on 6/4/15.
 */
public class OrderListingFragment extends BaseFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    private static boolean TAB_SELECTED = true;

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderListingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

        }
    }

    private Home hContext;
    private List<OrderWrapper.OrderInnerWrapper> dataCompletedList = new ArrayList<OrderWrapper.OrderInnerWrapper>();
    private List<OrderWrapper.OrderInnerWrapper> dataPendingList = new ArrayList<OrderWrapper.OrderInnerWrapper>();

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setEnabledButtons(true, false, false, true);
        ((Home) getActivity()).SearchBarET.setHint("Search Order Name/Number");


        if (wrapper != null) {
            hContext.saveButton.setText("UPDATE");
            hContext.saveButton.setVisibility(View.GONE);
            hContext.setTitleText("Update Order");
            update = true;
        } else {
            hContext.saveButton.setText(getString(R.string.save));
            hContext.saveButton.setVisibility(View.GONE);
            hContext.setTitleText(getString(R.string.order_listing));
            update = true;
        }

        new OrderWrapper() {
            @Override
            public void getOrderWrapperList(Context context, final List<OrderInnerWrapper> dataCompletedList, final List<OrderInnerWrapper> dataPendingList) {

                OrderListingFragment.this.dataCompletedList = dataCompletedList;
                OrderListingFragment.this.dataPendingList = dataPendingList;

                if (mOnHoldOrderButton.isSelected()) {
                    mOrderAdapter = new OrderListingAdapter(OrderListingFragment.this, dataPendingList, false);
                }
                if (mCompletedOrderButton.isSelected()) {
                    mOrderAdapter = new OrderListingAdapter(OrderListingFragment.this, dataCompletedList, true);
                }

                mOrderListView.setAdapter(mOrderAdapter);


                ((Home) getActivity()).SearchBarET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        try {

                            if(mOrderAdapter != null)
                                mOrderAdapter.getFilter(((Home) getActivity()).SearchBarET.getText().toString());

                        } catch (Exception ex) {
                            RetailPosLoging.getInstance().registerLog(OrderListingFragment.class.getName(), ex);
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                mOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                        if (mCompletedOrderButton.isSelected())
                            openFragment(FragmentUtils.OrderDetailFragment, position);
                        if (mOnHoldOrderButton.isSelected()){
//                            NewOrderFragment fragment = new NewOrderFragment();
//                            fragment.resetAllValues();
                            openFragment(FragmentUtils.NewOrderFragment, position);
                        }



                    }
                });
            }
        }.getOrderList(getActivity());

        if (TAB_SELECTED) {
            mCompletedOrderButton.setSelected(true);
            mOnHoldOrderButton.setSelected(false);
        } else {
            mCompletedOrderButton.setSelected(false);
            mOnHoldOrderButton.setSelected(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private int deviceWidth;
    private int deviceHeight;

    private SavePreferences mSavePreferences;

    private ListView mOrderListView;
    private FloatingActionButton floatingActionButton;
    private Button mCompletedOrderButton, mOnHoldOrderButton;
    private OrderListingAdapter mOrderAdapter;
    private RelativeLayout buttonLayout, formView;

    private OrderWrapper wrapper;
    private boolean update = false;
    private int selectedButton = 1; // 1 for Completed orders and 2 for On Hold Orders
    private int listPositionForCompleted = 0, itemPositionForCompleted = 0, listPositionForOnHold = 0, itemPositionForOnHold = 0;

    private EventBus bus = EventBus.getDefault();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_order_listing, container, false);
        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.pink_icon);
        //floatingActionButton.canScrollHorizontally(100);
        mOrderListView = (ListView) rootView.findViewById(R.id.orderListView);
        mOrderListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.hideSoftKeypad(getActivity());
                return false;
            }
        });
        mCompletedOrderButton = (Button) rootView.findViewById(R.id.completedOrderButton);
        mOnHoldOrderButton = (Button) rootView.findViewById(R.id.onHoldOrderButton);
        buttonLayout = (RelativeLayout) rootView.findViewById(R.id.buttonLayout);
        formView = (RelativeLayout) rootView.findViewById(R.id.formView);

        setDimensionsToViews();

        return rootView;
    }

    private void setDimensionsToViews() {

        RelativeLayout.LayoutParams buttonLayoutParam = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
        buttonLayoutParam.topMargin = (int) (deviceHeight * 0.02);

        RelativeLayout.LayoutParams formViewParam = (RelativeLayout.LayoutParams) formView.getLayoutParams();
        formViewParam.leftMargin = (int) (deviceWidth * .02f);
        formViewParam.rightMargin = (int) (deviceWidth * .02f);
        formViewParam.bottomMargin = (int) (deviceHeight * .06f);
        formView.setLayoutParams(formViewParam);

        RelativeLayout.LayoutParams floatingActionButton_param = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
//        floatingActionButton_param.width = (int)(deviceHeight*.2f);
//        floatingActionButton_param.height = (int)(deviceHeight*.2f);
        floatingActionButton_param.bottomMargin = (int) (deviceHeight * .02f);
        floatingActionButton_param.rightMargin = (int) (deviceWidth * .12f);
        floatingActionButton.setLayoutParams(floatingActionButton_param);
        floatingActionButton.setOnClickListener(this);

        RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) mOrderListView.getLayoutParams();
        mOrderListViewParam.leftMargin = (int) (deviceWidth * .02f);
        mOrderListViewParam.rightMargin = (int) (deviceWidth * .02f);
        mOrderListViewParam.bottomMargin = (int) (deviceHeight * .05f);
        mOrderListViewParam.topMargin = (int) (deviceHeight * .01f);
        mOrderListView.setLayoutParams(mOrderListViewParam);
        mOrderListView.setPadding((int) (deviceWidth * .0f), 0, (int) (deviceWidth * .0f), 0);

        RelativeLayout.LayoutParams mCompletedOrderButtonParam = (RelativeLayout.LayoutParams) mCompletedOrderButton.getLayoutParams();
        mCompletedOrderButtonParam.width = (int) (deviceWidth * .2f);
        mCompletedOrderButtonParam.height = (int) (deviceHeight * .1f);
        mCompletedOrderButton.setLayoutParams(mCompletedOrderButtonParam);

        RelativeLayout.LayoutParams mOnHoldOrderButtonParam = (RelativeLayout.LayoutParams) mOnHoldOrderButton.getLayoutParams();
        mOnHoldOrderButtonParam.width = (int) (deviceWidth * .2f);
        mOnHoldOrderButtonParam.height = (int) (deviceHeight * .1f);
        mOnHoldOrderButton.setLayoutParams(mOnHoldOrderButtonParam);

        mCompletedOrderButton.setOnClickListener(this);
        mOnHoldOrderButton.setOnClickListener(this);

        mCompletedOrderButton.setSelected(true);
        mOnHoldOrderButton.setSelected(false);

    }

    private HashMap<String, Object> valueKey;
    private ArrayList<String> list;


    void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("OrderListingFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("OrderListingFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
            case R.id.pink_icon: {
//                NewOrderFragment newFragment = new NewOrderFragment();
//                newFragment.resetAllValues();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();


                Bundle arguments = new Bundle();
                arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");
//                newFragment.setArguments(arguments);
//                transaction.replace(R.id.item_detail_container, newFragment);
//                transaction.addToBackStack(DashboardFragment.class.toString());

                // Commit the transaction
//                transaction.commit();

                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.NewOrderFragment, arguments, true, false);
                }
                break;
            }

            case R.id.completedOrderButton: {
                ((Home) getActivity()).SearchBarET.setText("");
                if (!mCompletedOrderButton.isSelected()) {
                    mCompletedOrderButton.setSelected(true);
                    mOnHoldOrderButton.setSelected(false);
                    TAB_SELECTED = true;

                    if (dataCompletedList != null && dataCompletedList.size() > 0) {
                        if (mOrderListView != null && mOrderListView.getChildAt(0) != null) {
                            listPositionForOnHold = mOrderListView.getChildAt(0).getTop();
                            itemPositionForOnHold = mOrderListView.getFirstVisiblePosition();
                            View v = mOrderListView.getChildAt(0);
                            listPositionForOnHold = (v == null) ? 0 : (v.getTop() - mOrderListView.getPaddingTop());


                            ((Home) getActivity()).SearchBarET.setText("");
                        }

                    }

                    mOrderAdapter = new OrderListingAdapter(this, dataCompletedList, true);
                    setListAdapter(mOrderAdapter, listPositionForCompleted, itemPositionForCompleted);

                }
                break;
            }
            case R.id.onHoldOrderButton: {
                ((Home) getActivity()).SearchBarET.setText("");
                if (!mOnHoldOrderButton.isSelected()) {
                    mCompletedOrderButton.setSelected(false);
                    mOnHoldOrderButton.setSelected(true);
                    TAB_SELECTED = false;

//                    ((Home) getActivity()).SearchBarET.setText("");

                    if (dataPendingList != null && dataPendingList.size() > 0) {
                        if (mOrderListView != null && mOrderListView.getChildAt(0) != null) {
                            if (mOrderListView.getChildCount() != 0) {
                                listPositionForCompleted = mOrderListView.getChildAt(0).getTop();
                                itemPositionForCompleted = mOrderListView.getFirstVisiblePosition();
                                View v = mOrderListView.getChildAt(0);
                                listPositionForCompleted = (v == null) ? 0 : (v.getTop() - mOrderListView.getPaddingTop());


                            }

                        }

                    }

                    mOrderAdapter = new OrderListingAdapter(this, dataPendingList, false);
                    setListAdapter(mOrderAdapter, listPositionForOnHold, itemPositionForOnHold);
                }
                break;
            }
        }
    }

    private void setListAdapter(final OrderListingAdapter adapter, int position, int index) {
        mOrderListView.setAdapter(adapter);
        mOrderListView.setSelectionFromTop(index, position);
    }

    private void openFragment(FragmentUtils fragmentName,int position){

//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle arguments = new Bundle();
        bus.postSticky(mOrderAdapter.getItem(position));
        arguments.putString("item_id", "order 1");
        if (mCompletedOrderButton.isSelected())
            arguments.putString("order_type", "Completed");
        if (mOnHoldOrderButton.isSelected())
            arguments.putString("order_type", "OnHold");
//        fragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container,fragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(getActivity() instanceof Home){
            ((Home) getActivity()).changeFragment(fragmentName, arguments, true, false);
        }

    }
}