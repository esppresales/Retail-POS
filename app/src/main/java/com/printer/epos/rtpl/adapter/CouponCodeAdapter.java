package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.dialogs.AddCouponCodeDialog;
import com.printer.epos.rtpl.wrapper.CouponCodeWrapper;

import java.util.List;

/**
 * Created by android-pc3 on 3/4/15.
 */
public class CouponCodeAdapter extends ArrayAdapter<CouponCodeWrapper.CouponCodeData> {

    List<CouponCodeWrapper.CouponCodeData> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;


    public CouponCodeAdapter(Fragment fragment,
                             List<CouponCodeWrapper.CouponCodeData> data) {
        super(fragment.getActivity(), R.layout.adapter_coupon_code_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;


        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_coupon_code_list_item, parent, false);
            holder = new ViewHolder();
            holder.couponCode = (TextView) convertView.findViewById(R.id.couponCode);
            holder.discount = (TextView) convertView.findViewById(R.id.discount);
            holder.validity = (TextView) convertView.findViewById(R.id.validity);
            holder.overflowMenuButton = (Button) convertView.findViewById(R.id.overflow_menu);

            RelativeLayout.LayoutParams overflowMenuButton_param = (RelativeLayout.LayoutParams) holder.overflowMenuButton.getLayoutParams();
            overflowMenuButton_param.width = (int) (deviceHeight * .04f);
            overflowMenuButton_param.height = (int) (deviceHeight * .04f);
            overflowMenuButton_param.rightMargin = (int) (deviceWidth * .03f);
            holder.overflowMenuButton.setLayoutParams(overflowMenuButton_param);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CouponCodeWrapper.CouponCodeData _objModel = data.get(position);

        holder.couponCode.setText(_objModel.getCouponCode());
        holder.discount.setText("Discount " + UiController.sCurrency + " " + Util.priceFormat(_objModel.getAmount()));
        String validity = "Validity: " + Util.parseDate(_objModel.getValidityFromDate()) + " to " + Util.parseDate(_objModel.getValidityToDate());
        holder.validity.setText(validity);

        try {
            final int temp_position = position;
            holder.overflowMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.overflow_menu:
                            PopupMenu popup = new PopupMenu(mContext, v);
                            popup.getMenuInflater().inflate(R.menu.menu_coupon_code,
                                    popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.edit:
                                            editOverflowMenu(temp_position);
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
            RetailPosLoging.getInstance().registerLog(CouponCodeAdapter.class.getName(), e);
        }

        return convertView;
    }

    @Override
    public CouponCodeWrapper.CouponCodeData getItem(int position) {
        return data.get(position);
    }

    private void deleteOverFlowMenu(final int position) {
        new CustomDialog().showTwoButtonAlertDialog(mContext, "", mContext.getString(R.string.coupon_delete_alert), mContext.getString(R.string.ok_button),
                mContext.getString(R.string.cancel), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                    @Override
                    public void onPositiveClick() {
                        new CouponCodeWrapper().deleteProduct(getItem(position).getId(), mContext, (CouponCodeWrapper.RefreshList) mFragment);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });

    }

    private void editOverflowMenu(final int position) {
        CouponCodeWrapper.CouponCodeData couponCodeData = getItem(position);
        new AddCouponCodeDialog(mContext, couponCodeData, (CouponCodeWrapper.RefreshList) mFragment).show();
        //new CustomDialog().addCouponCodeDialog(mContext, couponCodeData, (CouponCodeWrapper.RefreshList) mFragment);
    }

    class ViewHolder {
        TextView couponCode;
        TextView discount;
        TextView validity;
        Button overflowMenuButton;

    }
}
