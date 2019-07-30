package com.printer.epos.rtpl.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by android-pc3 on 20/4/15.
 */
public class StaffTimeTrackingAdapter extends BaseAdapter {

    private final List<StaffTimeTrackingData> data;
    private List<StaffTimeTrackingData> filteredList;
    private final Context mContext;


    public StaffTimeTrackingAdapter(Context context, List<StaffTimeTrackingData> data) {
//        super(fragment.getActivity(), R.layout.adapter_staff_time_tracking_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.data = data;
        this.filteredList = data;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_staff_time_tracking_item, parent, false);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.employeeName = (TextView) convertView.findViewById(R.id.employeeName);
            holder.inTime = (TextView) convertView.findViewById(R.id.inTime);
            holder.outTime = (TextView) convertView.findViewById(R.id.outTime);
            holder.duration = (TextView) convertView.findViewById(R.id.duration);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StaffTimeTrackingData _objModel = getItem(position);

        holder.date.setText(Util.convertTolocalDate(_objModel.getLogInTime()));
        holder.employeeName.setText(_objModel.getUserName());
        holder.inTime.setText(Util.convertTolocalTime(_objModel.getLogInTime()));
        holder.outTime.setText(Util.convertTolocalTime(_objModel.getLogOutTime()));
        holder.duration.setText(_objModel.getDuration());

        return convertView;

    }


    @Override
    public StaffTimeTrackingData getItem(int position) {
        return filteredList.get(position);
    }

    class ViewHolder {
        TextView date;
        TextView employeeName;
        TextView inTime;
        TextView outTime;
        TextView duration;

    }

    public void getFilter(String query) {

        filteredList = filterList(data, query);

        if (filteredList != null && filteredList.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private List<StaffTimeTrackingData> filterList(
            List<StaffTimeTrackingData> originalList, String query) {
        List<StaffTimeTrackingData> newList = new ArrayList<StaffTimeTrackingData>();
        if (Validation.isValidData(query)) {
            for (StaffTimeTrackingData item : originalList) {
                if (item.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    newList.add(item);
                }
            }
        } else
            newList = originalList;
        return newList;
    }


}
