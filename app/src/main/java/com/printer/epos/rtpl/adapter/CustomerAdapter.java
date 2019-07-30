package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.OrderPreviewFragment;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.CustomerWrapper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Puneet Gupta on 2/23/2015.
 */
public class CustomerAdapter extends BaseAdapter {
    List<CustomerWrapper> data;
    List<CustomerWrapper> filteredList;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;
    EventBus bus = EventBus.getDefault();

    String currency;


    public CustomerAdapter(Fragment fragment,
                           List<CustomerWrapper> data) {

        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        this.filteredList = data;

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public CustomerWrapper getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_customer_list_item, parent, false);
            holder = new ViewHolder();
            holder.RelativeText = (RelativeLayout) convertView.findViewById(R.id.RelativeText);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.phone = (TextView) convertView.findViewById(R.id.phone);
            holder.points = (TextView) convertView.findViewById(R.id.points);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.overflowMenuButton = (Button) convertView.findViewById(R.id.overflow_menu);


//            RelativeLayout.LayoutParams title_param = (RelativeLayout.LayoutParams) holder.title.getLayoutParams();
            //  title_param.rightMargin = (int) (deviceWidth * .02f);
            // title_param.leftMargin = (int) (deviceWidth * .02f);
            // title_param.topMargin = (int) (deviceHeight * .02f);
//            holder.title.setLayoutParams(title_param);

//            RelativeLayout.LayoutParams phone_param = (RelativeLayout.LayoutParams) holder.phone.getLayoutParams();
            // phone_param.leftMargin = (int) (deviceWidth * .03f);
            //  phone_param.topMargin = (int) (deviceHeight * .03f);
            // phone_param.bottomMargin = (int) (deviceHeight * .02f);
            // phone_param.width = (int) (deviceWidth * .28f);
//            holder.phone.setLayoutParams(phone_param);

//            RelativeLayout.LayoutParams points_param = (RelativeLayout.LayoutParams) holder.points.getLayoutParams();
            // points_param.width = (int) (deviceWidth * .2f);
            // points_param.leftMargin = (int) (deviceWidth * .02f);
            // points_param.topMargin = (int) (deviceHeight * .03f);
            //points_param.bottomMargin = (int) (deviceHeight * .02f);
//            holder.points.setLayoutParams(points_param);

//            RelativeLayout.LayoutParams date_param = (RelativeLayout.LayoutParams) holder.date.getLayoutParams();
            // date_param.width = (int) (deviceWidth * .3f);
            // date_param.leftMargin = (int) (deviceWidth * .02f);
            // date_param.topMargin = (int) (deviceHeight * .03f);
            // date_param.bottomMargin = (int) (deviceHeight * .02f);
//            holder.date.setLayoutParams(date_param);

            RelativeLayout.LayoutParams overflowMenuButton_param = (RelativeLayout.LayoutParams) holder.overflowMenuButton.getLayoutParams();
            overflowMenuButton_param.width = (int) (deviceHeight * .04f);
            overflowMenuButton_param.height = (int) (deviceHeight * .04f);
            overflowMenuButton_param.rightMargin = (int) (deviceWidth * .03f);
            holder.overflowMenuButton.setLayoutParams(overflowMenuButton_param);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomerWrapper _objModel = getItem(position);
        final int temp_position = position;

        holder.title.setText(_objModel.getFirstName() + " " + _objModel.getLastName());
        holder.phone.setText(_objModel.getMobileNo());
        holder.points.setText(_objModel.getEarnedLoyaltyPoints() + " Points");
        holder.date.setText("Membership Expiry: " + _objModel.getMembershipExpiry());
//        try {
//            holder.RelativeText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    viewOverFlowMenu(temp_position);
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            RetailPosLoging.getInstance().registerLog(CustomerAdapter.class.getName(), ex);
//        }

        try {

            holder.RelativeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewOverFlowMenu(temp_position);
                }
            });

            holder.overflowMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.overflow_menu:
                            PopupMenu popup = new PopupMenu(mContext, v);
                            popup.getMenuInflater().inflate(R.menu.menu_product_list,
                                    popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.view:
                                            viewOverFlowMenu(temp_position);
                                            break;
                                        case R.id.edit:
                                            editOverFlowMenu(temp_position);
                                            break;
                                        case R.id.delete:
                                            deleteOverFlowMenu(temp_position);
                                            break;
                                        default:
                                            break;
                                    }
                                    return true;
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(CustomerAdapter.class.getName(), e);
        }

        // holder.parcelPhoto
        // .setImageUrl(_objModel.getParcelPhoto(), mImageLoader);

        //holder.deliveryCost.setText(gSTInstance.getTime(_objModel.getTime()));

        return convertView;
    }

    class ViewHolder {
        //        RelativeLayout RelativeText;
        TextView title;
        TextView phone;
        TextView points;
        TextView date;
        Button overflowMenuButton;
        RelativeLayout RelativeText;
    }


    public void viewOverFlowMenu(int position) {
//        CustomerDetailFragment newFragment = new CustomerDetailFragment();
//        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();


        Bundle arguments = new Bundle();
        bus.postSticky(getItem(position));
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "61");
//        newFragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container, newFragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(mFragment.getActivity() instanceof Home){
            ((Home) mFragment.getActivity()).changeFragment(FragmentUtils.CustomerDetailFragment, arguments, true, false);
        }

    }

    public void editOverFlowMenu(int position) {
//        AddCustomerFragment newFragment = new AddCustomerFragment();
//        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();


        Bundle arguments = new Bundle();
        bus.postSticky(getItem(position));
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "4");
//        newFragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container, newFragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(mFragment.getActivity() instanceof Home){
            ((Home) mFragment.getActivity()).changeFragment(FragmentUtils.AddCustomerFragment, arguments, true, false);
        }

    }

    public void deleteOverFlowMenu(final int position) {
        new CustomDialog().showTwoButtonAlertDialog(mContext, "", mContext.getString(R.string.customer_delete_alert), mContext.getString(R.string.ok_button),
                mContext.getString(R.string.cancel), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                    @Override
                    public void onPositiveClick() {

                        CustomerWrapper.deleteCustomer(getItem(position).getId(), mContext, mFragment);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
    }

    public void getFilter(String query) {

        filteredList = filterList(data, query);

        if (filteredList != null && filteredList.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private List<CustomerWrapper> filterList(
            List<CustomerWrapper> originalList, String query) {
        List<CustomerWrapper> newList = new ArrayList<CustomerWrapper>();
        if (Validation.isValidData(query)) {
            for (CustomerWrapper item : originalList) {
                if (item.getFirstName().toLowerCase().contains(query.toLowerCase())
                        || item.getLastName().toLowerCase().contains(query.toLowerCase())
                        || (item.getFirstName() + " " + item.getLastName()).toLowerCase().contains(query.toLowerCase())) {
                    newList.add(item);
                }
            }
        } else
            newList = originalList;
        return newList;
    }
}