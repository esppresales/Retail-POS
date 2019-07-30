package com.printer.epos.rtpl.reports.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.reports.StockInHandData;

import java.util.List;


/**
 * Created by android-pc3 on 27/4/15.
 */
public class StockInHandAdapter extends ArrayAdapter<StockInHandData> {

    private final List<StockInHandData> data;
    private final Context mContext;
    private final int deviceWidth;
    private final int deviceHeight;

    private final ImageLoader imageLoader = ImageLoader.getInstance();
    private final DisplayImageOptions options1;

    public StockInHandAdapter(Context context, List<StockInHandData> data) {
        super(context, R.layout.adapter_product_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = context;
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
    public StockInHandData getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_product_list_item, parent, false);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
            holder.overflow = (Button) convertView.findViewById(R.id.overflow_menu);

            holder.overflow.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams img_param = (RelativeLayout.LayoutParams) holder.img.getLayoutParams();
            img_param.topMargin = (int) (deviceHeight * .01f);
            img_param.bottomMargin = (int) (deviceHeight * .01f);
            img_param.rightMargin = (int) (deviceWidth * .02f);
            img_param.width = (int) (deviceWidth * .1f);
            img_param.height = (int) (deviceWidth * .1f);
            holder.img.setLayoutParams(img_param);

            RelativeLayout.LayoutParams title_param = (RelativeLayout.LayoutParams) holder.title.getLayoutParams();
            title_param.rightMargin = (int) (deviceWidth * .02f);
            holder.title.setLayoutParams(title_param);

            RelativeLayout.LayoutParams price_param = (RelativeLayout.LayoutParams) holder.price.getLayoutParams();
            price_param.rightMargin = (int) (deviceWidth * .02f);
            holder.price.setLayoutParams(price_param);

            RelativeLayout.LayoutParams quantity_param = (RelativeLayout.LayoutParams) holder.quantity.getLayoutParams();
            quantity_param.rightMargin = (int) (deviceWidth * .04f);
            holder.quantity.setLayoutParams(quantity_param);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StockInHandData _objModel = getItem(position);

        holder.title.setText(_objModel.getName());
        holder.price.setText(_objModel.getCostPrice());
        holder.quantity.setText("Qty " + _objModel.getQuantity());

        if (_objModel.getImage() != null) {
            imageLoader.displayImage(_objModel.getImage(),
                    holder.img, options1, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View img,
                                                      Bitmap bmp) {
                            // TODO Auto-generated method stub
                            if (img instanceof ImageView) {
                                ((ImageView) img).setImageBitmap(bmp);

                            }
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            // TODO Auto-generated method stub

                        }
                    });

        }

        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView title;
        TextView price;
        TextView quantity;
        Button overflow;


    }

}
