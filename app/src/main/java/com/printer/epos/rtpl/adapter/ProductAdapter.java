package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Home;
import com.printer.epos.rtpl.OrderPreviewFragment;
import com.printer.epos.rtpl.ProductFragment;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Puneet Gupta on 2/20/2015.
 */
public class ProductAdapter extends BaseAdapter {
    List<ProductWrapper> data;
    List<ProductWrapper> filteredList;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;

    EventBus bus = EventBus.getDefault();

    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public ProductAdapter(Fragment fragment,
                          List<ProductWrapper> data) {
//        super(fragment.getActivity(), R.layout.adapter_product_list_item);
        // TODO Auto-generated constructor stub
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.data = data;
        this.filteredList = data;

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
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

    //    @Override
    public ProductWrapper getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_product_list_item, parent, false);
            holder = new ViewHolder();
            holder.RelativeText = (RelativeLayout) convertView.findViewById(R.id.RelativeText);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
            holder.overflowMenuButton = (Button) convertView.findViewById(R.id.overflow_menu);

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

            RelativeLayout.LayoutParams overflowMenuButton_param = (RelativeLayout.LayoutParams) holder.overflowMenuButton.getLayoutParams();
            overflowMenuButton_param.width = (int) (deviceHeight * .04f);
            overflowMenuButton_param.height = (int) (deviceHeight * .04f);
            overflowMenuButton_param.rightMargin = (int) (deviceWidth * .02f);
            holder.overflowMenuButton.setLayoutParams(overflowMenuButton_param);

            if (!UiController.mSavePreferences.get_roleId().equals("1"))
                holder.overflowMenuButton.setVisibility(View.INVISIBLE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductWrapper _objModel = getItem(position);
        final int temp_position = position;

        holder.title.setText(_objModel.getProductName());
        holder.price.setText(UiController.sCurrency + " " + Util.priceFormat(_objModel.getProductCost()));
        holder.quantity.setText("Qty " + _objModel.getQuantity());

        try {
            holder.RelativeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewOverFlowMenu(temp_position);
                }
            });
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                ((ProductFragment)mFragment).zoomImageFromThumb(holder.img, holder.img.getDrawable());
                }
            });
            holder.quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewOverFlowMenu(temp_position);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ProductAdapter.class.getName(), ex);
        }

        if (_objModel.getProductImage() != null) {
            imageLoader.displayImage(_objModel.getProductImage(),
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

        try {

            holder.overflowMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.overflow_menu:
                            PopupMenu popup = new PopupMenu(mContext, v);
                            popup.getMenuInflater().inflate(R.menu.menu_product_list,
                                    popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.view:
                                            viewOverFlowMenu(temp_position);
                                            break;
                                        case R.id.edit:
                                            editOverFlowMenu(temp_position);
                                            break;
                                        case R.id.delete:
                                            deleteOverFlowMenu(temp_position);
                                            break;
                                        default:
                                            break;
                                    }
                                    return true;
                                }
                            });
                            Util.hideSoftKeypad(mContext);
                            break;
                        default:
                            break;
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ProductAdapter.class.getName(), e);
        }


        //holder.deliveryCost.setText(gSTInstance.getTime(_objModel.getTime()));

        return convertView;
    }

    class ViewHolder {
        RelativeLayout RelativeText;
        ImageView img;
        TextView title;
        TextView price;
        TextView quantity;
        Button overflowMenuButton;
    }


    public void viewOverFlowMenu(int position) {
//        ProductDetailFragment newFragment = new ProductDetailFragment();
//        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();


        Bundle arguments = new Bundle();
        bus.postSticky(getItem(position));
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "31");
//        newFragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container, newFragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(mFragment.getActivity() instanceof Home){
            ((Home) mFragment.getActivity()).changeFragment(FragmentUtils.ProductDetailFragment, arguments, true, false);
        }

    }

    public void editOverFlowMenu(int position) {
//        AddProductFragment newFragment = new AddProductFragment();
//        FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();


        Bundle arguments = new Bundle();
        bus.postSticky(getItem(position));
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "4");
//        newFragment.setArguments(arguments);
//        transaction.replace(R.id.item_detail_container, newFragment);
//        transaction.addToBackStack(DashboardFragment.class.toString());

        // Commit the transaction
//        transaction.commit();

        if(mFragment.getActivity() instanceof Home){
            ((Home) mFragment.getActivity()).changeFragment(FragmentUtils.AddProductFragment, arguments, true, false);
        }
    }

    public void deleteOverFlowMenu(final int position) {
        new CustomDialog().showTwoButtonAlertDialog(mContext, "", mContext.getString(R.string.product_delete_alert), mContext.getString(R.string.ok_button),
                mContext.getString(R.string.cancel), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                    @Override
                    public void onPositiveClick() {

                        ProductWrapper.deleteProduct(getItem(position).getId(), mContext, mFragment);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });

    }

    public void getFilter(String query) {

        filteredList = filterList(data, query);

        if (filteredList != null && filteredList.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private List<ProductWrapper> filterList(
            List<ProductWrapper> originalList, String query) {
        List<ProductWrapper> newList = new ArrayList<ProductWrapper>();
        if (Validation.isValidData(query)) {
            for (ProductWrapper item : originalList) {
                if (item.getProductName().toLowerCase().contains(query.toLowerCase())) {
                    newList.add(item);
                }
            }
        } else
            newList = originalList;
        return newList;
    }
}
