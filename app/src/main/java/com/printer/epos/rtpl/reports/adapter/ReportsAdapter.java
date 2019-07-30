package com.printer.epos.rtpl.reports.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.reports.ReportsWrapper;

import java.util.List;



/**
 * Created by android-pc3 on 20/4/15.
 */
public class ReportsAdapter extends ArrayAdapter<ReportsWrapper> {

    private final List<ReportsWrapper> data;
    private final Context mContext;

    public ReportsAdapter(Context context,List<ReportsWrapper> data) {
        super(context, R.layout.reports_row_item_layout, data);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.data = data;
    }

    @Override
    public ReportsWrapper getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.reports_row_item_layout, parent, false);
            holder = new ViewHolder();
            holder.rowtitle = (TextView) convertView.findViewById(R.id.rowTitle);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReportsWrapper _objModel = data.get(position);

        holder.rowtitle.setText(_objModel.getReportTitle());


        return convertView;

    }

    class ViewHolder {
        TextView rowtitle;

    }


}
