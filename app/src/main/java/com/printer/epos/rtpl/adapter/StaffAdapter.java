package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.OrderPreviewFragment;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.StaffWrapper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Puneet Gupta on 2/20/2015.
 */
public class StaffAdapter extends BaseAdapter {
    List<StaffWrapper> data;
    List<StaffWrapper> filteredList;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public StaffAdapter(Fragment fragment,
                        List<StaffWrapper> data) {
//        super(fragment.getActivity(), R.layout.adapter_staff_item_list, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        this.filteredList = data;

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false).cacheInMemory(false)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();
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
    public StaffWrapper getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_staff_item_list, parent, false);
            holder = new ViewHolder();
            holder.ListHeading = (LinearLayout) convertView.findViewById(R.id.ListHeading);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.overflowMenuButton = (Button) convertView.findViewById(R.id.overflow_menu);

            LinearLayout.LayoutParams img_param = (LinearLayout.LayoutParams) holder.img.getLayoutParams();
            img_param.topMargin = (int) (deviceHeight * .01f);
            img_param.bottomMargin = (int) (deviceHeight * .01f);
            img_param.leftMargin = (int) (deviceWidth * .01f);
            img_param.rightMargin = (int) (deviceWidth * .01f);
            img_param.width = (int) (deviceWidth * .06f);
            img_param.height = (int) (deviceWidth * .06f);
            holder.img.setLayoutParams(img_param);

            holder.name = (TextView) convertView.findViewById(R.id.heading_name);
            holder.name.getLayoutParams().width = (int) (deviceWidth * .2f);

            holder.mobile = (TextView) convertView.findViewById(R.id.heading_mobile);
            holder.mobile.getLayoutParams().width = (int) (deviceWidth * .2f);

            holder.userName = (TextView) convertView.findViewById(R.id.heading_username);
            holder.userName.getLayoutParams().width = (int) (deviceWidth * .15f);

            holder.role = (TextView) convertView.findViewById(R.id.heading_role);
            holder.role.getLayoutParams().width = (int) (deviceWidth * .12f);

            holder.lastLogin = (TextView) convertView.findViewById(R.id.heading_last_login);
            holder.lastLogin.getLayoutParams().width = (int) (deviceWidth * .28f);

            holder.status = (TextView) convertView.findViewById(R.id.heading_status);
            holder.status.getLayoutParams().width = (int) (deviceWidth * .1f);

            RelativeLayout.LayoutParams overflowMenuButton_param = (RelativeLayout.LayoutParams) holder.overflowMenuButton.getLayoutParams();
            overflowMenuButton_param.width = (int) (deviceHeight * .04f);
            overflowMenuButton_param.height = (int) (deviceHeight * .04f);
            overflowMenuButton_param.rightMargin = (int) (deviceWidth * .02f);
            holder.overflowMenuButton.setLayoutParams(overflowMenuButton_param);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StaffWrapper _objModel = getItem(position);
        final int temp_position = position;

        holder.name.setText(_objModel.getFirstName() + " " + _objModel.getLastName());
        holder.mobile.setText(_objModel.getMobileNumber());
        holder.userName.setText(_objModel.getUserName());
        holder.role.setText(_objModel.getRole());
        holder.lastLogin.setText(_objModel.getLast_login());

        holder.ListHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOverFlowMenu(temp_position);
            }
        });

        if (_objModel.isActive.equals("1")) {
            holder.status.setText("Active");
            holder.status.setTextColor(mContext.getResources().getColor(R.color.green_text_color));
        } else {
            holder.status.setText("Inactive");
            holder.status.setTextColor(mContext.getResources().getColor(R.color.pink));
        }

        if (_objModel.role.equals("1"))
            holder.role.setText("Manager");
        else
            holder.role.setText("Employee");

        try {

            holder.overflowMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.overflow_menu:
                            PopupMenu popup = new PopupMenu(mContext, v);

                            popup.getMenuInflater().inflate(R.menu.menu_staff_list,
                                    popup.getMenu());
                            if (_objModel.isActive.equals("1")) {
                                popup.getMenu().add(0, 3, 0, "Deactivate");
                            } else {
                                popup.getMenu().add(0, 3, 0, "Activate");
                            }

//                            popup.getMenuInflater().inflate(R.menu.menu_staff_list,
//                                    popup.getMenu());
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
                                        case 3:
                                            updateStatusFlowMenu(temp_position);
                                            break;
                                        default:
                                            break;
                                    }
                                    return true;
                                }
                            });
                            Util.hideSoftKeypad(mContext);
                            break;
                        default:
                            break;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(StaffAdapter.class.getName(), e);
        }

        if (_objModel.getImage() != null) {
            imageLoader.displayImage(_objModel.getImage(),
                    holder.img, options1, new ImageLoadingListener() {

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


        //holder.deliveryCost.setText(gSTInstance.getTime(_objModel.getTime()));

        return convertView;
    }

    class ViewHolder {
        LinearLayout ListHeading;
        ImageView img;
        TextView name;
        TextView mobile;
        TextView userName;
        TextView role;
        TextView lastLogin;
        TextView status;
        Button overflowMenuButton;
    }


    public void viewOverFlowMenu(int position) {
//        StaffDetailFragment newFragment = new StaffDetailFragment();
//        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();


        Bundle arguments = new Bundle();
        bus.postSticky(getItem(position));
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "31");
//        newFragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container, newFragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(mFragment.getActivity() instanceof Home){
            ((Home) mFragment.getActivity()).changeFragment(FragmentUtils.StaffDetailFragment, arguments, true, false);
        }
    }

    public void editOverFlowMenu(int position) {
//        AddStaffFragment newFragment = new AddStaffFragment();
//        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();


        Bundle arguments = new Bundle();
        bus.postSticky(getItem(position));
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "2");
//        newFragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container, newFragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(mFragment.getActivity() instanceof Home){
            ((Home) mFragment.getActivity()).changeFragment(FragmentUtils.AddStaffFragment, arguments, true, false);
        }
    }

    public void updateStatusFlowMenu(final int position) {
        String id = getItem(position).getIsActive();
        String msg = "";

        if (id.equals("0"))
            msg = mContext.getString(R.string.staff_active_status_alert);
        else if (id.equals("1"))
            msg = mContext.getString(R.string.staff_update_status_alert);

        new CustomDialog().showTwoButtonAlertDialog(mContext, "", msg, mContext.getString(R.string.ok_button),
                mContext.getString(R.string.cancel), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                    @Override
                    public void onPositiveClick() {

                        StaffWrapper.updateStatus(getItem(position).getId(), mContext, mFragment);
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

    private List<StaffWrapper> filterList(List<StaffWrapper> originalList, String query) {
        List<StaffWrapper> newList = new ArrayList<StaffWrapper>();
        if (Validation.isValidData(query)) {
            for (StaffWrapper item : originalList) {
                if (item.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                        (item.getFirstName() + " " + item.getLastName()).toLowerCase().contains(query.toLowerCase())) {
                    newList.add(item);
                }
            }
        } else
            newList = originalList;
        return newList;
    }
}
