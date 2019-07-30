package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.StaffWrapper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Puneet Gupta on 2/23/2015.
 */
public class StaffDetailAdapter extends ArrayAdapter<StaffWrapper.StaffTransactionWrapper> {
    List<StaffWrapper.StaffTransactionWrapper> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;
    EventBus bus = EventBus.getDefault();

    String currency;


    public StaffDetailAdapter(Fragment fragment,
                              List<StaffWrapper.StaffTransactionWrapper> data) {
        super(fragment.getActivity(), R.layout.adapter_customer_list_item, data);
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
    public StaffWrapper.StaffTransactionWrapper getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_customer_detail_list_item, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.amount = (TextView) convertView.findViewById(R.id.amount);
            holder.order_id = (TextView) convertView.findViewById(R.id.order_id);
            holder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setMinimumHeight((int) (deviceHeight * .08f));

            LinearLayout.LayoutParams date_param = (LinearLayout.LayoutParams) holder.date.getLayoutParams();
            date_param.width = (int) (deviceWidth * .22f);
            date_param.leftMargin = (int) (deviceWidth * .02f);
            holder.date.setLayoutParams(date_param);

            LinearLayout.LayoutParams amount_param = (LinearLayout.LayoutParams) holder.amount.getLayoutParams();
            amount_param.leftMargin = (int) (deviceWidth * .02f);
            amount_param.width = (int) (deviceWidth * .22f);
            holder.amount.setLayoutParams(amount_param);

            LinearLayout.LayoutParams order_id_param = (LinearLayout.LayoutParams) holder.order_id.getLayoutParams();
            order_id_param.width = (int) (deviceWidth * .22f);
            order_id_param.leftMargin = (int) (deviceWidth * .02f);
            holder.order_id.setLayoutParams(order_id_param);

            LinearLayout.LayoutParams status_param = (LinearLayout.LayoutParams) holder.status.getLayoutParams();
            status_param.width = (int) (deviceWidth * .22f);
            status_param.leftMargin = (int) (deviceWidth * .02f);
            holder.status.setLayoutParams(status_param);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StaffWrapper.StaffTransactionWrapper _objModel = data.get(position);

        if (_objModel.getStatus().equals("Pending")) {
            holder.status.setTextColor(Color.RED);
        } else {
            holder.status.setTextColor(mContext.getResources().getColor(R.color.green_text_color));
        }

        holder.date.setText(_objModel.getDate());
        holder.amount.setText(Util.priceFormat(_objModel.getAmount()));
        holder.order_id.setText(_objModel.getOrder_id());
        holder.status.setText(_objModel.getStatus());

        return convertView;
    }

    class ViewHolder {
        TextView date;
        TextView amount;
        TextView order_id;
        TextView status;
    }
}
