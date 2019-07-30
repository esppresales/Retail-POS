package com.printer.epos.rtpl;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.adapter.StaffDetailAdapter;
import com.printer.epos.rtpl.wrapper.StaffWrapper;

import de.greenrobot.event.EventBus;


public class StaffDetailFragment extends BaseFragment implements View.OnClickListener {


    private StaffDetailAdapter mAdapter;

    public StaffDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setTitleText("Staff Detail");
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
        ((Home) getActivity()).backButton.setOnClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_staff_detail, container, false);

        SavePreferences mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        ImageLoader mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(false).cacheInMemory(false)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();

        ImageView img = (ImageView) rootView.findViewById(R.id.img);
//        RelativeLayout.LayoutParams img_param = (RelativeLayout.LayoutParams) img.getLayoutParams();
       // img_param.height = (int) (deviceHeight * .15f);
//        img_param.width = (int) (deviceHeight * .15f);
//        img_param.topMargin = (int) (deviceHeight * .04f);
//        img_param.leftMargin = (int) (deviceWidth * .03f);
//        img_param.rightMargin = (int) (deviceWidth * .03f);
//        img.setLayoutParams(img_param);

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

        TextView fullNameET = (TextView) rootView.findViewById(R.id.full_nameET);
//        RelativeLayout.LayoutParams fullNameET_param = (RelativeLayout.LayoutParams) fullNameET.getLayoutParams();
//        fullNameET_param.topMargin = (int) (deviceHeight * .04f);
//        fullNameET.setLayoutParams(fullNameET_param);
       // fullNameET.setPadding(0, (int) (deviceHeight * .04f), 0, 0);

        TextView gender = (TextView) rootView.findViewById(R.id.gender_radio_group);
        gender.setPadding(0, 0, 0, 0);

        TextView dateOfBirthET = (TextView) rootView.findViewById(R.id.date_of_birthET);
        dateOfBirthET.setPadding(0, 0, 0, 0);

        TextView email_id = (TextView) rootView.findViewById(R.id.email_id);
        email_id.setPadding(0, (int) (deviceHeight * .03f), 0, 0);
        rootView.findViewById(R.id.Employee_heading).setPadding((int) (deviceHeight * .04f),
                (int) (deviceHeight * .04f), 0, 0);

        TextView emailIdET = (TextView) rootView.findViewById(R.id.email_idET);
        emailIdET.setPadding(0, 0, 0, 0);

        TextView mobile_no = (TextView) rootView.findViewById(R.id.mobile_no);
        mobile_no.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView mobileNoET = (TextView) rootView.findViewById(R.id.mobile_noET);
        mobileNoET.setPadding(0, 0, 0, 0);

        TextView role = (TextView) rootView.findViewById(R.id.role);
        role.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView roleET = (TextView) rootView.findViewById(R.id.roleET);
        roleET.setPadding(0, 0, 0, 0);

        TextView username = (TextView) rootView.findViewById(R.id.username);
        username.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView usernameET = (TextView) rootView.findViewById(R.id.usernameET);
        usernameET.setPadding(0, 0, 0, 0);

        TextView nricPassport = (TextView) rootView.findViewById(R.id.nricPassport);
        nricPassport.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView nricPassportET = (TextView) rootView.findViewById(R.id.nricPassportET);
        nricPassportET.setPadding(0, 0, 0, 0);

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

        StaffWrapper wrapper = EventBus.getDefault().removeStickyEvent(StaffWrapper.class);

        if (wrapper != null) {

            fullNameET.setText(wrapper.getFirstName() + " " + wrapper.getLastName());
            usernameET.setText(wrapper.getUserName());
            mobileNoET.setText(wrapper.getMobileNumber());
            dateOfBirthET.setText(wrapper.getDob());
            roleET.setText(wrapper.getRole());
            emailIdET.setText(wrapper.getEmail());
            nricPassportET.setText(wrapper.getNricPassport());
            gender.setText(wrapper.getGender());
            addressET.setText(wrapper.getAddress());

            if (wrapper.getRole().trim().equals("1")) {
                roleET.setText("Manager");
            } else {
                roleET.setText("Employee");
            }

            amount.setText("Amount (" + UiController.sCurrency + ")");

            if (wrapper.getImage() != null) {
                mImageLoader.displayImage(wrapper.getImage(),
                        img, options1, new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1,
                                                        FailReason arg2) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onLoadingComplete(String arg0, View img,
                                                          Bitmap bmp) {
                                // TODO Auto-generated method stub
                                if (img instanceof ImageView) {
                                    ((ImageView) img).setImageBitmap(bmp);
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1) {
                                // TODO Auto-generated method stub

                            }
                        });

            }

            if (wrapper.getTransactionList().size() > 0) {
                mAdapter = new StaffDetailAdapter(this, wrapper.getTransactionList());
                listView.setAdapter(mAdapter);
            }

        }

        RelativeLayout list_header = (RelativeLayout) rootView.findViewById(R.id.list_header);
        RelativeLayout.LayoutParams list_header_Param = (RelativeLayout.LayoutParams) list_header.getLayoutParams();
        list_header_Param.leftMargin = (int) (deviceWidth * 0.02f);
        list_header_Param.rightMargin = (int) (deviceWidth * 0.02f);
        list_header_Param.bottomMargin = (int) (deviceHeight * 0.02f);
        list_header.setLayoutParams(list_header_Param);


        RelativeLayout.LayoutParams mOrderListViewParam = (RelativeLayout.LayoutParams) listView.getLayoutParams();

        if (wrapper != null && wrapper.getTransactionList() != null && wrapper.getTransactionList().size() > 0) {
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backButton)
            backClick();

    }

    void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("StaffDetailFragment", "popping back stack");
//            fm.popBackStack();
//        } else {
//            Log.i("StaffDetailFragment", "nothing on back stack, calling super");
//        }
        getActivity().onBackPressed();
    }
}
