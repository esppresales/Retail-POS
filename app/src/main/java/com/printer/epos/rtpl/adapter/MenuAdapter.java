package com.printer.epos.rtpl.adapter;

/**
 * Created by ranosys-puneet on 11/3/15.
 */

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.dummy.DummyContent;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

    private Activity activity;
    private List<DummyContent.DummyItem> data;
    private static LayoutInflater inflater = null;

    int selection = 0;

    public int deviceWidth;
    public int deviceHeight;

    public MenuAdapter(Activity a, List<DummyContent.DummyItem> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;

    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);


        RelativeLayout parentview = (RelativeLayout) vi.findViewById(R.id.parent);
        parentview.getLayoutParams().height = (int) (deviceWidth * .050f);

        TextView title = (TextView) vi.findViewById(R.id.title); // title
        ImageView thumb = (ImageView) vi.findViewById(R.id.list_image); // title

        DummyContent.DummyItem wrapper = data.get(position);

        // Setting all values in listview
        title.setText(wrapper.content);
        thumb.setBackgroundResource(wrapper.imageName);

        RelativeLayout.LayoutParams thumb_param = (RelativeLayout.LayoutParams) thumb.getLayoutParams();
        thumb_param.height = (int) (deviceHeight * .035f);
        thumb_param.width = (int) (deviceHeight * .035f);
        thumb_param.setMargins((int) (deviceWidth * .02f), (int) (deviceHeight * .01f), (int) (deviceWidth * .02f), (int) (deviceHeight * .01f));
        thumb.setLayoutParams(thumb_param);

        if (selection == position) {
            parentview.setBackgroundColor(activity.getResources().getColor(R.color.menu_list_selector_color));
        } else {
            parentview.setBackgroundColor(activity.getResources().getColor(R.color.menu_list_color));
        }

        return vi;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }
}
