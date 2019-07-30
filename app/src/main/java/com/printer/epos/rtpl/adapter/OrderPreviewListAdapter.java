package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.ProductOrderWrapper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-sristi on 16/4/15.
 */
public class OrderPreviewListAdapter extends ArrayAdapter<ProductOrderWrapper> {
    List<ProductOrderWrapper> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public OrderPreviewListAdapter(Fragment fragment,
                                   List<ProductOrderWrapper> data) {
        super(fragment.getActivity(), R.layout.adapter_order_preview_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;

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
    public ProductOrderWrapper getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_order_preview_list_item, parent, false);
            holder = new ViewHolder();
            holder.orderNameTV = (TextView) convertView.findViewById(R.id.orderNameTV);
            holder.unitPriceTV = (TextView) convertView.findViewById(R.id.unitPriceTV);
            holder.totalPriceTV = (TextView) convertView.findViewById(R.id.totalPriceTV);
            holder.quantityTV = (TextView) convertView.findViewById(R.id.quantityTV);
           // holder.receiptNo = (TextView) convertView.findViewById(R.id.receiptNoTV);

           /* RelativeLayout.LayoutParams orderNameTVParam = (RelativeLayout.LayoutParams) holder.orderNameTV.getLayoutParams();
            orderNameTVParam.rightMargin = (int) (deviceWidth * .02f);
            orderNameTVParam.width = (int) (deviceWidth * .54f);
            holder.orderNameTV.setLayoutParams(orderNameTVParam);

            RelativeLayout.LayoutParams unitPriceTVParam = (RelativeLayout.LayoutParams) holder.unitPriceTV.getLayoutParams();
            unitPriceTVParam.rightMargin = (int) (deviceWidth * .02f);
            unitPriceTVParam.width = (int) (deviceWidth * .1f);
            holder.unitPriceTV.setLayoutParams(unitPriceTVParam);

            RelativeLayout.LayoutParams totalPriceTVParam = (RelativeLayout.LayoutParams) holder.totalPriceTV.getLayoutParams();
            // totalPriceTVParam.rightMargin = (int) (deviceWidth * .02f);
            totalPriceTVParam.width = (int) (deviceWidth * .1f);
            holder.totalPriceTV.setLayoutParams(totalPriceTVParam);

            RelativeLayout.LayoutParams quantityTVParam = (RelativeLayout.LayoutParams) holder.quantityTV.getLayoutParams();
            quantityTVParam.rightMargin = (int) (deviceWidth * .02f);
            quantityTVParam.width = (int) (deviceWidth * .1f);
            holder.quantityTV.setLayoutParams(quantityTVParam);

            convertView.setPadding((int) (deviceWidth * .02f), (int) (deviceWidth * .02f), (int) (deviceWidth * .02f), (int) (deviceWidth * .02f));*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductOrderWrapper _objModel = data.get(position);
        if (_objModel != null) {
            holder.orderNameTV.setText(_objModel.getProductName());
            holder.unitPriceTV.setText(Util.priceFormat(_objModel.getSellingPrice()));
            holder.quantityTV.setText(String.valueOf(_objModel.getAddedQuantity()));
           // holder.receiptNo.setText(String.valueOf(_objModel.getReceiptNo()));

            int quantity = _objModel.getAddedQuantity();
            double unitPrice = Double.parseDouble(_objModel.getSellingPrice());

            double totalPrice = quantity * unitPrice;
            holder.totalPriceTV.setText(Util.priceFormat(totalPrice));

        }


        return convertView;
    }

    class ViewHolder {
        TextView orderNameTV;
        TextView unitPriceTV;
        TextView totalPriceTV;
        TextView quantityTV;
       // TextView receiptNo;
    }

}


