package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.OrderWrapper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-sristi on 3/4/15.
 */
public class OrderDetailListAdapter extends ArrayAdapter<OrderWrapper.ProductDetail> {
    List<OrderWrapper.ProductDetail> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;
    private String receiptNo;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public OrderDetailListAdapter(Fragment fragment,
                                  List<OrderWrapper.ProductDetail> data, String receiptNo) {
        super(fragment.getActivity(), R.layout.adapter_order_preview_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        this.receiptNo = receiptNo;

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
    public OrderWrapper.ProductDetail getItem(int position) {
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
            holder.receiptNoTV = (TextView) convertView.findViewById(R.id.receiptNoTV);
            holder.unitPriceTV = (TextView) convertView.findViewById(R.id.unitPriceTV);
            holder.totalPriceTV = (TextView) convertView.findViewById(R.id.totalPriceTV);
            holder.quantityTV = (TextView) convertView.findViewById(R.id.quantityTV);

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

        OrderWrapper.ProductDetail _objModel = data.get(position);
        if (_objModel != null) {
          //  if(!TextUtils.isEmpty(_objModel.getName())) {
                holder.orderNameTV.setText(!TextUtils.isEmpty(_objModel.getName())?_objModel.getName():"");
           // }
            if(!TextUtils.isEmpty(receiptNo)) {
                holder.receiptNoTV.setVisibility(View.VISIBLE);
                holder.receiptNoTV.setText(receiptNo);
            }else{
                holder.receiptNoTV.setVisibility(View.GONE);
            }
            //if(!TextUtils.isEmpty(_objModel.getProductPrice())) {
                holder.unitPriceTV.setText(!TextUtils.isEmpty(_objModel.getProductPrice()) ? Util.priceFormat(_objModel.getProductPrice()) : "");
            //}

            int quantity = Integer.parseInt(_objModel.getQty());
            int returnQuantity = 0;

            if (Validation.isValidData(_objModel.getReturnQty()))
                returnQuantity = Integer.parseInt(_objModel.getReturnQty());

            double unitPrice = Double.parseDouble(_objModel.getProductPrice());

            double totalPrice = (quantity - returnQuantity) * unitPrice;
            holder.totalPriceTV.setText(Util.priceFormat(totalPrice));
            holder.quantityTV.setText("" + (quantity - returnQuantity));

            if ((quantity - returnQuantity) == 0)
                holder.orderNameTV.setText(_objModel.getName() + " (Returned) ");

        }
        return convertView;
    }

    class ViewHolder {
        TextView orderNameTV;
        TextView unitPriceTV;
        TextView receiptNoTV;
        TextView totalPriceTV;
        TextView quantityTV;
    }

}

