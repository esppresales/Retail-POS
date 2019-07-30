package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
 * Created by android-sristi on 6/4/15.
 */
public class ReturnOrderListAdapter extends ArrayAdapter<OrderWrapper.ProductDetail> {
    List<OrderWrapper.ProductDetail> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;
    private ValueChangeListener mListener;
    private int countCheck = 0;
    private String receiptNo;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public ReturnOrderListAdapter(Fragment fragment,
                                  List<OrderWrapper.ProductDetail> data, String receiptNo, ValueChangeListener listener) {
        super(fragment.getActivity(), R.layout.adapter_return_order_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        mListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_return_order_list_item, parent, false);
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.customCB);
            holder.orderNameTV = (TextView) convertView.findViewById(R.id.orderNameTV);
            holder.receiptNoTV = (TextView) convertView.findViewById(R.id.receiptNoTV);
            holder.unitPriceTV = (TextView) convertView.findViewById(R.id.unitPriceTV);
            holder.totalPriceTV = (TextView) convertView.findViewById(R.id.totalPriceTV);
            holder.quantityTV = (EditText) convertView.findViewById(R.id.quantityTV);

            holder.checkBox.setChecked(false);

          /*  RelativeLayout.LayoutParams checkBoxParam = (RelativeLayout.LayoutParams) holder.checkBox.getLayoutParams();
            checkBoxParam.rightMargin = (int) (deviceWidth * .01f);
            //checkBoxParam.width = (int) (deviceWidth * .04f);
            // checkBoxParam.height = (int) (deviceWidth * .04f);
            holder.checkBox.setLayoutParams(checkBoxParam);

            Drawable leftDrawable = mContext.getResources().getDrawable(R.drawable.check_box_selector);
            leftDrawable.setBounds(0, 0, (int) (deviceWidth * .03f), (int) (deviceWidth * .03f));
            holder.checkBox.setCompoundDrawables(leftDrawable, null, null, null);


            RelativeLayout.LayoutParams orderNameTVParam = (RelativeLayout.LayoutParams) holder.orderNameTV.getLayoutParams();
            orderNameTVParam.rightMargin = (int) (deviceWidth * .02f);
            orderNameTVParam.width = (int) (deviceWidth * .54f);
            holder.orderNameTV.setLayoutParams(orderNameTVParam);

            RelativeLayout.LayoutParams unitPriceTVParam = (RelativeLayout.LayoutParams) holder.unitPriceTV.getLayoutParams();
            unitPriceTVParam.rightMargin = (int) (deviceWidth * .02f);
            unitPriceTVParam.width = (int) (deviceWidth * .1f);
            holder.unitPriceTV.setLayoutParams(unitPriceTVParam);

            RelativeLayout.LayoutParams totalPriceTVParam = (RelativeLayout.LayoutParams) holder.totalPriceTV.getLayoutParams();
            totalPriceTVParam.rightMargin = (int) (deviceWidth * .2f);
            totalPriceTVParam.width = (int) (deviceWidth * .1f);
            holder.totalPriceTV.setLayoutParams(totalPriceTVParam);

            RelativeLayout.LayoutParams quantityTVParam = (RelativeLayout.LayoutParams) holder.quantityTV.getLayoutParams();
            quantityTVParam.rightMargin = (int) (deviceWidth * .02f);
            quantityTVParam.width = (int) (deviceWidth * .07f);
            quantityTVParam.height = (int) (deviceWidth * .05f);
            holder.quantityTV.setLayoutParams(quantityTVParam);

            convertView.setPadding((int) (deviceWidth * .02f), (int) (deviceWidth * .02f), (int) (deviceWidth * .02f), (int) (deviceWidth * .02f));*/
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OrderWrapper.ProductDetail _objModel = data.get(position);

        holder.orderNameTV.setText(_objModel.getName());
        holder.unitPriceTV.setText(Util.priceFormat(_objModel.getProductPrice()));
        if(!TextUtils.isEmpty(receiptNo)){
            holder.receiptNoTV.setText(receiptNo);
        }

        int quantity = Integer.parseInt(_objModel.getQty());
        int returnQuantity = 0;

        if (Validation.isValidData(_objModel.getReturnQty()))
            returnQuantity = Integer.parseInt(_objModel.getReturnQty());

        holder.quantityTV.setText("" + (quantity - returnQuantity));
        double unitPrice = Double.parseDouble(_objModel.getProductPrice());

        if ((quantity - returnQuantity) == 0) {
            holder.orderNameTV.setText(_objModel.getName() + " (Returned) ");
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else
            holder.checkBox.setVisibility(View.VISIBLE);

        final double totalPrice = (quantity - returnQuantity) * unitPrice;
        holder.totalPriceTV.setText("" + Util.priceFormat(totalPrice));
        holder.checkBox.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(null);
       // holder.checkBox.setChecked(_objModel.isReturned());

       /* if(data.get(position).isReturned())
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);*/


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int position = (Integer) compoundButton.getTag();
                if (b) {
                    holder.quantityTV.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                    holder.quantityTV.setFocusable(false);
                    ++countCheck;
                    data.get(position).setReturned(true);
                    mListener.onValueChanged(totalPrice, countCheck);
                } else {
                    holder.quantityTV.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                    holder.quantityTV.setFocusable(false);
                    data.get(position).setReturned(false);
                    --countCheck;
                    mListener.onValueChanged((-totalPrice), countCheck);
                }
            }
        });

        holder.quantityTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        return convertView;
    }

    class ViewHolder {
        CheckBox checkBox;
        TextView receiptNoTV;
        TextView orderNameTV;
        TextView unitPriceTV;
        TextView totalPriceTV;
        EditText quantityTV;
    }

    public interface ValueChangeListener {
        public void onValueChanged(double value, int countCheck);
    }

}

