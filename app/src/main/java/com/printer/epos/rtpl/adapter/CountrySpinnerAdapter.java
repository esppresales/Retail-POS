package com.printer.epos.rtpl.adapter;

/**
 * Created by ranosys-puneet on 25/3/15.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.printer.epos.rtpl.R;

import java.util.ArrayList;

public class CountrySpinnerAdapter extends BaseAdapter {

    private LayoutInflater mlLayoutInflater;
    private ArrayList<String> mList;
    private Context mContext;
    private boolean showHighLight = false;
    private int boundRight, padding, deviceHeight, boundbottom;


    public CountrySpinnerAdapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        mlLayoutInflater = LayoutInflater.from(context);
        this.mList = list;
        this.showHighLight = showHighLight;
        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        boundRight = (int) (deviceHeight * .020f * 1.11f);
        boundbottom = (int) (deviceHeight * .020f);

        padding = (int) (deviceHeight * .01f);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mlLayoutInflater.inflate(R.layout.spinner_item_view,
                    null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView;

            int boundRight = (int) (deviceHeight * .025f);
            int boundbottom = (int) (deviceHeight * .025f * 0.9f);

            Drawable rightDrawable = mContext.getResources().getDrawable(
                    R.drawable.dropdown_arrow);
            rightDrawable.setBounds(0, 0, boundRight, boundbottom);
            holder.mTextView.setCompoundDrawables(null, null, rightDrawable,
                    null);
            holder.mTextView.setCompoundDrawablePadding(padding);
            holder.mTextView.setPadding(padding, 0, padding, 0);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.mTextView.setTextColor(mContext.getResources().getColor(
                android.R.color.black));
        holder.mTextView.setText("" + mList.get(position));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mlLayoutInflater.inflate(R.layout.spinner_item_view,
                    null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView;

            holder.mTextView.setPadding(padding, padding * 2, padding,
                    padding * 2);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.mTextView.setText("" + mList.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
    }

}