package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.wrapper.SettingsWrapper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-pc3 on 1/4/15.
 */
public class SettingsAdapter extends ArrayAdapter<SettingsWrapper> {

    List<SettingsWrapper> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;
    EventBus bus = EventBus.getDefault();

    public SettingsAdapter(Fragment fragment, List<SettingsWrapper> data) {
        super(fragment.getActivity(), R.layout.settings_row_item_layout, data);
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
    public SettingsWrapper getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.settings_row_item_layout, parent, false);
            holder = new ViewHolder();
            holder.rowtitle = (TextView) convertView.findViewById(R.id.rowTitle);
            holder.rowDescription = (TextView) convertView.findViewById(R.id.rowDescription);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SettingsWrapper _objModel = data.get(position);

        holder.rowtitle.setText(_objModel.getRowTitle());
        holder.rowDescription.setText(_objModel.getRowDetail());

        return convertView;

    }

    class ViewHolder {
        TextView rowtitle;
        TextView rowDescription;
    }

}
