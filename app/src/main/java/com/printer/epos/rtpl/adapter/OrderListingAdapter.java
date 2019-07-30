package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.OrderWrapper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-sristi on 6/4/15.
 */
public class OrderListingAdapter extends BaseAdapter {
    List<OrderWrapper.OrderInnerWrapper> data;
    List<OrderWrapper.OrderInnerWrapper> filteredList;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;
    private boolean completed = true;

    public OrderListingAdapter(Fragment fragment,
                               List<OrderWrapper.OrderInnerWrapper> data, boolean completed) {
//        super(fragment.getActivity(), R.layout.adapter_order_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        this.filteredList = data;
        this.completed = completed;

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
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
    public OrderWrapper.OrderInnerWrapper getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_order_list_item, parent, false);
            holder = new ViewHolder();
            holder.orderNumberTV = (TextView) convertView.findViewById(R.id.orderNumberTV);
            holder.orderNumberValueTV = (TextView) convertView.findViewById(R.id.orderNumberValueTV);
            holder.orderReturn = (TextView) convertView.findViewById(R.id.orderReturn);
            holder.totalAmountTV = (TextView) convertView.findViewById(R.id.totalAmountTV);
            holder.customerNameTV = (TextView) convertView.findViewById(R.id.customerNameTV);
            holder.orderDateTV = (TextView) convertView.findViewById(R.id.orderDateTV);

            RelativeLayout.LayoutParams orderNumberTVParam = (RelativeLayout.LayoutParams) holder.orderNumberTV.getLayoutParams();
            orderNumberTVParam.leftMargin = (int) (deviceWidth * .01f);
            orderNumberTVParam.rightMargin = (int) (deviceWidth * .01f);
            holder.orderNumberTV.setLayoutParams(orderNumberTVParam);

            RelativeLayout.LayoutParams orderNumberValueTVParam = (RelativeLayout.LayoutParams) holder.orderNumberValueTV.getLayoutParams();
            orderNumberValueTVParam.rightMargin = (int) (deviceWidth * .01f);
            holder.orderNumberValueTV.setLayoutParams(orderNumberValueTVParam);

            LinearLayout.LayoutParams customerNameTVParam = (LinearLayout.LayoutParams) holder.customerNameTV.getLayoutParams();
            customerNameTVParam.leftMargin = (int) (deviceWidth * .01f);
            customerNameTVParam.rightMargin = (int) (deviceWidth * .01f);
            customerNameTVParam.width = (int) (deviceWidth * .35f);
            customerNameTVParam.topMargin = (int) (deviceHeight * .02f);
            holder.customerNameTV.setLayoutParams(customerNameTVParam);

            LinearLayout.LayoutParams orderDateTVParam = (LinearLayout.LayoutParams) holder.orderDateTV.getLayoutParams();
            orderDateTVParam.rightMargin = (int) (deviceWidth * .1f);
            orderDateTVParam.topMargin = (int) (deviceHeight * .02f);
            holder.orderDateTV.setLayoutParams(orderDateTVParam);

            LinearLayout.LayoutParams totalAmountTVParam = (LinearLayout.LayoutParams) holder.totalAmountTV.getLayoutParams();
            totalAmountTVParam.leftMargin = (int) (deviceWidth * .01f);
            totalAmountTVParam.rightMargin = (int) (deviceWidth * .01f);
            totalAmountTVParam.topMargin = (int) (deviceHeight * .02f);
            holder.totalAmountTV.setLayoutParams(totalAmountTVParam);

            holder.orderReturn.setPadding((int) (deviceWidth * .02f), 0, 0, 0);

            convertView.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), (int) (deviceWidth * .02f), (int) (deviceHeight * .02f));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderWrapper.OrderInnerWrapper _objModel = getItem(position);
        if (_objModel != null) {
            holder.orderNumberValueTV.setText(_objModel.getId());
        }
        holder.orderDateTV.setText(_objModel.getCreatedDate());
        /*String pattern = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date myDate = format.parse();
            pattern = "dd-MM-yyyy";
            format = new SimpleDateFormat(pattern);

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        if (completed) {
            holder.totalAmountTV.setText("Paid Amount : " + UiController.sCurrency + " " + Util.priceFormat(_objModel.getFinalAmount()));

            Double amount = Double.parseDouble(_objModel.getFinalAmount());
            if (_objModel != null && amount != 0) {
                holder.orderReturn.setVisibility(View.GONE);
            } else if (amount == 0) {
                holder.orderReturn.setVisibility(View.VISIBLE);
            }
        } else {
            holder.totalAmountTV.setText("Total Amount : " + UiController.sCurrency + " " + Util.priceFormat(_objModel.getFinalAmount()));
            if (_objModel != null)
                holder.orderNumberValueTV.setText(_objModel.getId());
        }

        holder.customerNameTV.setText(_objModel.getCustomerName());


        return convertView;
    }

    class ViewHolder {
        TextView orderNumberTV;
        TextView orderNumberValueTV;
        TextView orderReturn;
        TextView customerNameTV;
        TextView orderDateTV;
        TextView totalAmountTV;
    }

    public void getFilter(String query) {

        filteredList = filterList(data, query);

        if (filteredList != null && filteredList.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private List<OrderWrapper.OrderInnerWrapper> filterList(
            List<OrderWrapper.OrderInnerWrapper> originalList, String query) {
        List<OrderWrapper.OrderInnerWrapper> newList = new ArrayList<OrderWrapper.OrderInnerWrapper>();
        if (Validation.isValidData(query)) {
            for (OrderWrapper.OrderInnerWrapper item : originalList) {
                if (item.getCustomerName().toLowerCase().contains(query.toLowerCase())
                        || item.getId().toLowerCase().contains(query.toLowerCase())) {
                    newList.add(item);
                }
            }
        } else
            newList = originalList;
        return newList;
    }

}

