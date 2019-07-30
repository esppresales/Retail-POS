package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.ProductCategoryFragment;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.wrapper.CategoryWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by Puneet gupta on 2/20/2015.
 */
public class ProductCategoryAdapter extends BaseAdapter {
    List<CategoryWrapper.CategoryListWrapper> data;
    List<CategoryWrapper.CategoryListWrapper> filteredList;
    Context mContext;
    int deviceWidth;
    int deviceHeight;
    Fragment mFragment;
    EventBus bus = EventBus.getDefault();

    String currency;


    public ProductCategoryAdapter(Fragment fragment, Context mContext, List<CategoryWrapper.CategoryListWrapper> data) {
//        super(mContext, R.layout.adapter_product_list_item, data);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.data = data;
        this.filteredList = data;
        this.mFragment = fragment;

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

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
    public CategoryWrapper.CategoryListWrapper getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_product_category_list_item, parent, false);
            holder = new ViewHolder();
            holder.colorBar = (TextView) convertView.findViewById(R.id.colorBar);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.overflowMenuButton = (Button) convertView.findViewById(R.id.overflow_menu);

            holder.colorBar.getLayoutParams().height = (int) (deviceHeight * .015f);

            RelativeLayout.LayoutParams title_param = (RelativeLayout.LayoutParams) holder.title.getLayoutParams();
            title_param.height = (int) (deviceHeight * .1f);
            holder.title.setLayoutParams(title_param);
            //holder.title.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

            RelativeLayout.LayoutParams overflowMenuButton_param = (RelativeLayout.LayoutParams) holder.overflowMenuButton.getLayoutParams();
            overflowMenuButton_param.width = (int) (deviceHeight * .04f);
            overflowMenuButton_param.height = (int) (deviceHeight * .04f);
            //overflowMenuButton_param.rightMargin = (int) (deviceWidth * .02f);
            holder.overflowMenuButton.setLayoutParams(overflowMenuButton_param);
            // holder.overflowMenuButton.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);


            RelativeLayout.LayoutParams description_param = (RelativeLayout.LayoutParams) holder.description.getLayoutParams();
            description_param.height = (int) (deviceHeight * .28f);
            holder.description.setLayoutParams(description_param);
            holder.description.setPadding((int) (deviceWidth * .01f), (int) (deviceHeight * .02f),
                    (int) (deviceWidth * .01f), (int) (deviceHeight * .02f));

            holder.colorBar.setBackgroundColor(randomColor());

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CategoryWrapper.CategoryListWrapper _objModel = getItem(position);

        String status = _objModel.getMarkAsDelete().equals("1") ? "(Disabled)" : "";
        holder.title.setText(_objModel.getName() + " " + status);
        holder.description.setText(_objModel.getDescription());

        holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProductCategoryFragment) mFragment).viewCategory(_objModel);
            }
        });

        holder.overflowMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.overflow_menu:
                        PopupMenu popup = new PopupMenu(mContext, view);

                        if (_objModel.getMarkAsDelete().equals("1")) {
                            popup.getMenu().add(0, 1, 0, "Enable");
                            if (_objModel.getProductCount().equals("0")) {
                                popup.getMenu().add(0, 1, 0, "Delete");
                            }
                        } else {
                            popup.getMenu().add(0, 1, 0, "Disable");
                        }

                        popup.getMenu().removeItem(2);
                        popup.getMenu().removeItem(3);

                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                if (item.toString().equals("Enable") || item.toString().equals("Disable")) {
                                    if (_objModel.getMarkAsDelete().equals("1"))
                                        enableCategoryStatus(_objModel);
                                    else if (_objModel.getMarkAsDelete().equals("0"))
                                        disableCategoryStatus(_objModel);
                                } else if (item.toString().equals("Delete")) {
                                    deleteCategoryStatus(_objModel);
                                }
                                return true;
                            }
                        });
                        Util.hideSoftKeypad(mContext);

                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView colorBar;
        TextView title;
        TextView description;
        Button overflowMenuButton;
    }

    int randomColor() {
        Random r = new Random();
        int red = r.nextInt(256);
        int green = r.nextInt(256);
        int blue = r.nextInt(256);
        return Color.rgb(red, green, blue);
    }


    public void getFilter(String query) {

        filteredList = filterList(data, query);

        if (filteredList != null && filteredList.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private List<CategoryWrapper.CategoryListWrapper> filterList(
            List<CategoryWrapper.CategoryListWrapper> originalList, String query) {
        List<CategoryWrapper.CategoryListWrapper> newList = new ArrayList<CategoryWrapper.CategoryListWrapper>();
        if (Validation.isValidData(query)) {
            for (CategoryWrapper.CategoryListWrapper item : originalList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    newList.add(item);
                }
            }
        } else
            newList = originalList;
        return newList;
    }

    private void enableCategoryStatus(final CategoryWrapper.CategoryListWrapper _objModel) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        if (_objModel.getMarkAsDelete() != null && _objModel.getMarkAsDelete().equals("1")) {
            map.put("mark_as_delete", "0");
        }

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {
                _objModel.setMarkAsDelete("0");
                notifyDataSetChanged();
            }
        }.editProductCategory(mContext, UiController.appUrl + "categories/" + _objModel.getId(), map);
    }

    private void disableCategoryStatus(final CategoryWrapper.CategoryListWrapper _objModel) {

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {
                _objModel.setMarkAsDelete("1");
                notifyDataSetChanged();
            }
        }.deleteProductCategory(mContext, UiController.appUrl + "categories/" + _objModel.getId());
    }
    private void deleteCategoryStatus(final CategoryWrapper.CategoryListWrapper _objModel) {
        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {
                data.remove(_objModel);
                filteredList.remove(_objModel);
                notifyDataSetChanged();
            }
        }.deleteProductCategory(mContext, UiController.appUrl + "product-category/" + _objModel.getId());
    }
}