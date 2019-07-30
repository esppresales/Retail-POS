package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-sristi on 8/4/15.
 */
public class ProductListAdapter extends BaseAdapter {
    List<ProductWrapper> data;
    List<ProductWrapper> list;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public ProductListAdapter(Fragment fragment,
                              List<ProductWrapper> data) {
        // super(fragment.getActivity(), R.layout.product_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        this.list = data;

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
        list = filterList(data, "");
    }

    @Override
    public ProductWrapper getItem(int position) {
        return list.get(position);
    }

    public void addItem(ProductWrapper wrapper) {
        list.add(wrapper);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }


    public Object getCollectionItem(int position) {
        // TODO Auto-generated method stub

        return list.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.product_list_item, parent, false);
            holder = new ViewHolder();
            holder.productNameTV = (TextView) convertView.findViewById(R.id.productNameTV);
            holder.productSKUTV = (TextView) convertView.findViewById(R.id.productSKUTV);
            holder.productPriceTV = (TextView) convertView.findViewById(R.id.productPriceTV);

            convertView.setPadding((int) (deviceWidth * .0f), (int) (deviceHeight * .02f), (int) (deviceWidth * .0f), (int) (deviceHeight * .02f));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductWrapper _objModel = list.get(position);

        holder.productNameTV.setText(_objModel.getProductName());
        holder.productSKUTV.setText(_objModel.getBarcode());
        holder.productPriceTV.setText(Util.priceFormat(_objModel.getSellingPrice()));

        return convertView;
    }

    class ViewHolder {
        TextView productNameTV;
        TextView productSKUTV;
        TextView productPriceTV;
    }

    public void getFilter(String query) {

        list = filterList(data, query);

        if (list != null && list.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private List<ProductWrapper> filterList(
            List<ProductWrapper> originalList, String query) {
        List<ProductWrapper> newList = new ArrayList<ProductWrapper>();
        for (ProductWrapper item : originalList) {
            if (item.getProductName().toLowerCase().contains(query.toLowerCase())
                    || item.getBarcode().toLowerCase().contains(query.toLowerCase())) {
                newList.add(item);
            }
        }
        return newList;
    }

}

