package com.printer.epos.rtpl;

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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.adapter.CustomerAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.CustomerWrapper;

import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.printer.epos.rtpl.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.printer.epos.rtpl.ItemDetailActivity}
 * on handsets.
 */
public class CustomerFragment extends BaseFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private  View rootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    private ListView list;
    private CustomerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_customer, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;


        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.pink_icon);
        floatingActionButton.canScrollHorizontally(100);

        RelativeLayout.LayoutParams floatingActionButton_param = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
//        floatingActionButton_param.width = (int)(deviceHeight*.2f);
//        floatingActionButton_param.height = (int)(deviceHeight*.2f);
        floatingActionButton_param.bottomMargin = (int) (deviceHeight * .02f);
        floatingActionButton_param.rightMargin = (int) (deviceWidth * .12f);
        floatingActionButton.setLayoutParams(floatingActionButton_param);
        floatingActionButton.setOnClickListener(this);

        list = (ListView) rootView.findViewById(R.id.list);
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
        list.setPadding((int) (deviceWidth * .0f), 0, (int) (deviceWidth * .0f), 0);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomerWrapper.getCustomerList(null, getActivity(), this);
        ((Home) getActivity()).setTitleText(getString(R.string.customers));
        ((Home) getActivity()).setEnabledButtons(true, false, false, true);
        ((Home) getActivity()).SearchBarET.setHint("Search Customer Name");
    }

    public void setCustomerAdapter(List<CustomerWrapper> data) {

        mAdapter = new CustomerAdapter(this, data);
        list.setAdapter(mAdapter);

        ((Home) getActivity()).SearchBarET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
//                    if (((Home) getActivity()).SearchBarET.getText().toString().length() > 0) {
                        mAdapter.getFilter(((Home) getActivity()).SearchBarET.getText().toString());
//                    }
                } catch (Exception ex) {
                    RetailPosLoging.getInstance().registerLog(CustomerFragment.class.getName(),ex);
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
            case R.id.pink_icon: {
//                AddCustomerFragment newFragment = new AddCustomerFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "4");
//                newFragment.setArguments(arguments);
//                transaction.replace(R.id.item_detail_container, newFragment);
//                transaction.addToBackStack(DashboardFragment.class.toString());

                // Commit the transaction
//                transaction.commit();

                if(getActivity() instanceof Home){
                    ((Home) getActivity()).changeFragment(FragmentUtils.AddCustomerFragment, arguments, true, false);
                }
                break;
            }
        }
    }
}
