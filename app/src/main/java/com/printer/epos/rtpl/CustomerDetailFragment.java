package com.printer.epos.rtpl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.adapter.CustomerDetailAdapter;
import com.printer.epos.rtpl.wrapper.CustomerWrapper;

import de.greenrobot.event.EventBus;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.printer.epos.rtpl.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.printer.epos.rtpl.ItemDetailActivity}
 * on handsets.
 */
public class CustomerDetailFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public void onResume() {
        super.onResume();

        ((Home) getActivity()).setTitleText("Customer Detail");
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
    }


    String date_of_birth_string;

    private CustomerDetailAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_detail, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        RelativeLayout formScroll = (RelativeLayout) rootView.findViewById(R.id.formScroll);
        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        formScroll.setLayoutParams(form_param);

        LinearLayout linLeft = (LinearLayout) rootView.findViewById(R.id.lin_Left);
        RelativeLayout.LayoutParams linLeft_param = (RelativeLayout.LayoutParams) linLeft.getLayoutParams();
        linLeft_param.width = (int) (deviceWidth * .46f);
        linLeft_param.leftMargin = (int) (deviceWidth * .02f);
        linLeft.setLayoutParams(linLeft_param);

        TextView full_name = (TextView) rootView.findViewById(R.id.full_name);
        full_name.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView fullNameET = (TextView) rootView.findViewById(R.id.full_nameET);
        fullNameET.setPadding(0, 0, 0, 0);

        TextView membership_id = (TextView) rootView.findViewById(R.id.membership_id);
        membership_id.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView membershipIdET = (TextView) rootView.findViewById(R.id.membership_idET);
        membershipIdET.setPadding(0, 0, 0, 0);

        TextView mobile_no = (TextView) rootView.findViewById(R.id.mobile_no);
        mobile_no.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView mobileNoET = (TextView) rootView.findViewById(R.id.mobile_noET);
        mobileNoET.setPadding(0, 0, 0, 0);

        TextView date_of_birth = (TextView) rootView.findViewById(R.id.date_of_birth);
        date_of_birth.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView dateOfBirthET = (TextView) rootView.findViewById(R.id.date_of_birthET);
        dateOfBirthET.setPadding(0, 0, 0, 0);

        TextView membership_validity = (TextView) rootView.findViewById(R.id.membership_validity);
        membership_validity.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView membershipValidityET = (TextView) rootView.findViewById(R.id.membership_validityET);
        membershipValidityET.setPadding(0, 0, 0, (int) (deviceHeight * .02f));

        TextView email_id = (TextView) rootView.findViewById(R.id.email_id);
        email_id.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView emailIdET = (TextView) rootView.findViewById(R.id.email_idET);
        emailIdET.setPadding(0, 0, 0, 0);

        TextView nricPassport = (TextView) rootView.findViewById(R.id.nricPassport);
        nricPassport.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView nricPassportET = (TextView) rootView.findViewById(R.id.nricPassportET);
        nricPassportET.setPadding(0, 0, 0, 0);


        TextView genderTv = (TextView) rootView.findViewById(R.id.gender);
        genderTv.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView gender = (TextView) rootView.findViewById(R.id.gender_radio_group);
        gender.setPadding(0, 0, 0, 0);

        TextView address = (TextView) rootView.findViewById(R.id.address);
        address.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView addressET = (TextView) rootView.findViewById(R.id.addressET);
        addressET.setPadding(0, 0, 0, (int) (deviceHeight * .02f));

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.getLayoutParams().height = (int) (deviceHeight * .25f);

        TextView transaction = (TextView) rootView.findViewById(R.id.transaction);
        TextView date = (TextView) rootView.findViewById(R.id.date);
        TextView amount = (TextView) rootView.findViewById(R.id.amount);
        TextView order_id = (TextView) rootView.findViewById(R.id.order_id);
        TextView status = (TextView) rootView.findViewById(R.id.status);
        TextView points_text = (TextView) rootView.findViewById(R.id.points_text);

        transaction.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .03f), 0, (int) (deviceHeight * .03f));

        LinearLayout.LayoutParams date_param = (LinearLayout.LayoutParams) date.getLayoutParams();
        date_param.width = (int) (deviceWidth * .22f);
        date_param.leftMargin = (int) (deviceWidth * .02f);
        date_param.bottomMargin = (int) (deviceHeight * .03f);
        date.setLayoutParams(date_param);

        LinearLayout.LayoutParams amount_param = (LinearLayout.LayoutParams) amount.getLayoutParams();
        amount_param.leftMargin = (int) (deviceWidth * .02f);
        amount_param.width = (int) (deviceWidth * .22f);
        amount_param.bottomMargin = (int) (deviceHeight * .03f);
        amount.setLayoutParams(amount_param);

        LinearLayout.LayoutParams order_id_param = (LinearLayout.LayoutParams) order_id.getLayoutParams();
        order_id_param.width = (int) (deviceWidth * .22f);
        order_id_param.leftMargin = (int) (deviceWidth * .02f);
        order_id_param.bottomMargin = (int) (deviceHeight * .03f);
        order_id.setLayoutParams(order_id_param);

        LinearLayout.LayoutParams status_param = (LinearLayout.LayoutParams) status.getLayoutParams();
        status_param.width = (int) (deviceWidth * .22f);
        status_param.leftMargin = (int) (deviceWidth * .02f);
        status_param.bottomMargin = (int) (deviceHeight * .03f);
        status.setLayoutParams(status_param);

        RelativeLayout.LayoutParams points_text_param = (RelativeLayout.LayoutParams) points_text.getLayoutParams();
        points_text_param.width = (int) (deviceHeight * .15f);
        points_text_param.height = (int) (deviceHeight * .15f);
       // points_text_param.rightMargin = (int) (deviceWidth * .04f);
       // points_text_param.topMargin = (int) (deviceHeight * .02f);
        points_text.setLayoutParams(points_text_param);


        CustomerWrapper wrapper = EventBus.getDefault().removeStickyEvent(CustomerWrapper.class);

        if (wrapper != null) {

            membershipIdET.setText(wrapper.getMembershipId());
            mobileNoET.setText(wrapper.getMobileNo());
            fullNameET.setText(wrapper.getFirstName() + " " + wrapper.getLastName());
            nricPassportET.setText(wrapper.getNricPassport());
             if(wrapper.getDob().equals("null")){
                 dateOfBirthET.setText("");
             }
            else{
                 dateOfBirthET.setText(wrapper.getDob());

             }
//            if (wrapper.getDob() != null) {
//                date_of_birth_string = wrapper.getDob();
//                dateOfBirthET.setText(changeDateFormat(date_of_birth_string));
//            }
            emailIdET.setText(wrapper.getEmail());
            addressET.setText(wrapper.getAddress()
                    + (wrapper.getPostalCode() != null && !wrapper.getPostalCode().equals("null") && !wrapper.getPostalCode().equals("") ? "("+wrapper.getPostalCode()+")" : ""));
            gender.setText(wrapper.getGender());
            membershipValidityET.setText(wrapper.getMembershipValidity() + " " + wrapper.getMembershipType());

            if (wrapper.getTransactionList().size() > 0) {
                mAdapter = new CustomerDetailAdapter(this, wrapper.getTransactionList());
                listView.setAdapter(mAdapter);
            }

            if (wrapper.getEarnedLoyaltyPoints().length() > 0) {
                points_text.setText(wrapper.getEarnedLoyaltyPoints().trim() + "\nPoints");
            }

            amount.setText("Amount (" + UiController.sCurrency + ")");

        }

        RelativeLayout list_header = (RelativeLayout) rootView.findViewById(R.id.list_header);
        RelativeLayout.LayoutParams list_header_Param = (RelativeLayout.LayoutParams) list_header.getLayoutParams();
        list_header_Param.leftMargin = (int) (deviceWidth * 0.02f);
        list_header_Param.rightMargin = (int) (deviceWidth * 0.02f);
        list_header_Param.bottomMargin = (int) (deviceHeight * 0.02f);
        list_header.setLayoutParams(list_header_Param);


        RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) listView.getLayoutParams();

        if ((wrapper != null ? wrapper.getTransactionList().size() : 0) > 0) {
            if (mAdapter.getCount() > 3) {
                View item = mAdapter.getView(0, null, listView);
                item.measure(0, 0);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (3.1 * item.getMeasuredHeight()));
//            mOrderListView.setLayoutParams(params);

                mOrderListViewParam.height = 3 * item.getMeasuredHeight();
            }
        }
        listView.setLayoutParams(mOrderListViewParam);

        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        return rootView;
    }

    public String changeDateFormat(String current_date) {
        if (!TextUtils.isEmpty(current_date) && !current_date.contains("/") && current_date.contains("-"))
            try {
                //    String current_date = "2014-11-25";   "yyyy-mm-dd"
                System.out.println("Date is : " + current_date);
                String dob = current_date;
                String bdate = dob.substring(0, dob.indexOf("-"));
                String bmonth = dob.substring(dob.indexOf("-") + 1, dob.lastIndexOf("-"));
                String byear = dob.substring(dob.lastIndexOf("-") + 1);
                dob = byear + "-" + bmonth + "-" + bdate;

                String month_name = null;

                switch (Integer.parseInt(bmonth)) {
                    case 1:
                        month_name = "Jan";
                        break;
                    case 2:
                        month_name = "Feb";
                        break;
                    case 3:
                        month_name = "Mar";
                        break;
                    case 4:
                        month_name = "Apr";
                        break;
                    case 5:
                        month_name = "May";
                        break;
                    case 6:
                        month_name = "June";
                        break;
                    case 7:
                        month_name = "July";
                        break;
                    case 8:
                        month_name = "Aug";
                        break;
                    case 9:
                        month_name = "Sep";
                        break;
                    case 10:
                        month_name = "Oct";
                        break;
                    case 11:
                        month_name = "Nov";
                        break;
                    case 12:
                        month_name = "Dec";
                        break;

                    default:
                        break;

                }
                return byear + " " + month_name + ", " + bdate;
            } catch (Exception e) {

                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(CustomerDetailFragment.class.getName(), e);
                return null;
            }
        else
            return null;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backButton)
            backClick();
    }

    void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }
}
