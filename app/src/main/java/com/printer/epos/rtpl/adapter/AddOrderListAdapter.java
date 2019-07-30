package com.printer.epos.rtpl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.printer.epos.rtpl.NewOrderFragment;
import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.ProductOrderWrapper;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by android-sristi on 3/4/15.
 */
public class AddOrderListAdapter extends ArrayAdapter<ProductOrderWrapper> {
    List<ProductOrderWrapper> data;
    Context mContext;
    Fragment mFragment;
    int deviceWidth;
    int deviceHeight;

    EventBus bus = EventBus.getDefault();


    public ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options1;

    public AddOrderListAdapter(Fragment fragment,
                               List<ProductOrderWrapper> data) {
        super(fragment.getActivity(), R.layout.adapter_new_order_list_item, data);
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

//    protected void hideKeyboard(View view) {
//        InputMethodManager in = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        view.requestFocus();
//    }

    public void setupUI(final View view) {
        view.setFocusableInTouchMode(true);

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    hideKeyboard(view);
//                    Util.hideSoftKeypad(mContext);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_new_order_list_item, parent, false);
            setupUI(convertView);
            holder = new ViewHolder();
            holder.orderNameTV = (TextView) convertView.findViewById(R.id.orderNameTV);
            holder.unitPriceTV = (TextView) convertView.findViewById(R.id.unitPriceTV);
            holder.totalPriceTV = (TextView) convertView.findViewById(R.id.totalPriceTV);
            holder.quantityTV = (EditText) convertView.findViewById(R.id.quantityTV);
            holder.plusButton = (Button) convertView.findViewById(R.id.plusButton);
            holder.minusButton = (Button) convertView.findViewById(R.id.minusButton);
            holder.crossButton = (Button) convertView.findViewById(R.id.crossButton);

            /*RelativeLayout.LayoutParams orderNameTVParam = (RelativeLayout.LayoutParams) holder.orderNameTV.getLayoutParams();
            orderNameTVParam.rightMargin = (int) (deviceWidth * .02f);
            orderNameTVParam.width = (int) (deviceWidth * .36f);
            holder.orderNameTV.setLayoutParams(orderNameTVParam);

            RelativeLayout.LayoutParams unitPriceTVParam = (RelativeLayout.LayoutParams) holder.unitPriceTV.getLayoutParams();
            unitPriceTVParam.rightMargin = (int) (deviceWidth * .02f);
            unitPriceTVParam.width = (int) (deviceWidth * .1f);
            holder.unitPriceTV.setLayoutParams(unitPriceTVParam);

            RelativeLayout.LayoutParams totalPriceTVParam = (RelativeLayout.LayoutParams) holder.totalPriceTV.getLayoutParams();
            totalPriceTVParam.rightMargin = (int) (deviceWidth * .02f);
            totalPriceTVParam.width = (int) (deviceWidth * .1f);
            holder.totalPriceTV.setLayoutParams(totalPriceTVParam);

            RelativeLayout.LayoutParams quantityTVParam = (RelativeLayout.LayoutParams) holder.quantityTV.getLayoutParams();
            quantityTVParam.rightMargin = (int) (deviceWidth * .02f);
            quantityTVParam.width = (int) (deviceWidth * .07f);
            quantityTVParam.height = (int) (deviceWidth * .05f);
            holder.quantityTV.setLayoutParams(quantityTVParam);*/


           /* BitmapDrawable bd = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.minus_icon);
            LinearLayout.LayoutParams plusButtonParam = (LinearLayout.LayoutParams) holder.plusButton.getLayoutParams();
            //plusButtonParam.rightMargin = (int) (deviceWidth * .02f);
            plusButtonParam.width = *//*bd.getBitmap().getWidth()*//*(int) (deviceWidth * .03f);
            plusButtonParam.height = *//*bd.getBitmap().getHeight()*//*(int) (deviceWidth * .05f);
            holder.plusButton.setLayoutParams(plusButtonParam);

            LinearLayout.LayoutParams minusButtonParam = (LinearLayout.LayoutParams) holder.minusButton.getLayoutParams();
            //minusButtonParam.rightMargin = (int) (deviceWidth * .02f);
            minusButtonParam.width = *//*bd.getBitmap().getWidth()*//*(int) (deviceWidth * .05f);
            minusButtonParam.height = *//*bd.getBitmap().getHeight()*//*(int) (deviceWidth * .05f);
            holder.minusButton.setLayoutParams(minusButtonParam);*/

            LinearLayout.LayoutParams crossButtonParam = (LinearLayout.LayoutParams) holder.crossButton.getLayoutParams();
            //crossButtonParam.leftMargin = (int) (deviceWidth * .02f);
            crossButtonParam.width = (int) (deviceWidth * .025f);
            crossButtonParam.height = (int) (deviceWidth * .025f);
            holder.crossButton.setLayoutParams(crossButtonParam);


            //convertView.setPadding((int) (deviceWidth * .02f), (int) (deviceWidth * .02f), (int) (deviceWidth * .02f), (int) (deviceWidth * .02f));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ProductOrderWrapper _objModel = getItem(position);


        holder.orderNameTV.setText(_objModel.getProductName());
        holder.quantityTV.setText("" + _objModel.getAddedQuantity());
        holder.unitPriceTV.setText("" + _objModel.getSellingPrice());
        holder.totalPriceTV.setText("" + _objModel.getTotalPrice());

        holder.quantityTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (holder.quantityTV.getText().toString().trim().equals("")
                            || Integer.parseInt(holder.quantityTV.getText().toString()) < 1) {
                        holder.quantityTV.setText("1");
                        calculateTotal(_objModel, 1, holder);
//                        Util.hideSoftKeypad(mContext);
//                        return true;
                    }else {

                        if (Integer.parseInt(holder.quantityTV.getText().toString())
                                > Integer.parseInt(_objModel.getQuantity())) {
                            holder.quantityTV.setText(_objModel.getQuantity());

                            int addedQuantity = _objModel.getAddedQuantity();
                            if (addedQuantity >= 1) {
                                double unitPrice = Double.parseDouble(_objModel.getSellingPrice());

                                addedQuantity = Integer.parseInt(holder.quantityTV.getText().toString());
                                holder.quantityTV.setText("" + addedQuantity);
                                calculateTotal(_objModel, addedQuantity, holder);
                            }

                            new CustomDialog().showOneButtonAlertDialog(mContext, "Product is no more available.",
                                    "Product quantity should be equals to available quantity.", "OK", android.R.drawable.ic_dialog_alert, null);
                        } else {
                            int addedQuantity = _objModel.getAddedQuantity();
                            if (addedQuantity >= 1) {
                                double unitPrice = Double.parseDouble(_objModel.getSellingPrice());

                                addedQuantity = Integer.parseInt(holder.quantityTV.getText().toString());
                                calculateTotal(_objModel, addedQuantity, holder);
                            }
                        }
                    }
                    // }
//                    Util.hideSoftKeypad(mContext);
//                    return true;
                }
                return false;
            }
        });

        holder.quantityTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if (holder.quantityTV.getText().toString().trim().equals("")
                            || Integer.parseInt(holder.quantityTV.getText().toString()) < 1) {
                        holder.quantityTV.setText("1");
                        calculateTotal(_objModel, 1, holder);
                        return;
                    }

                    if (Integer.parseInt(holder.quantityTV.getText().toString())
                            > Integer.parseInt(_objModel.getQuantity())) {
                        holder.quantityTV.setText(_objModel.getQuantity());

                        int addedQuantity = _objModel.getAddedQuantity();
                        if (addedQuantity >= 1) {
                            double unitPrice = Double.parseDouble(_objModel.getSellingPrice());

                            addedQuantity = Integer.parseInt(holder.quantityTV.getText().toString());
                            holder.quantityTV.setText("" + addedQuantity);
                            calculateTotal(_objModel, addedQuantity, holder);
                        }

                        new CustomDialog().showOneButtonAlertDialog(mContext, "Product is no more available.",
                                "Product quantity should be equals to available quantity.", "OK", android.R.drawable.ic_dialog_alert, null);
                    } else {
                        int addedQuantity = _objModel.getAddedQuantity();
                        if (addedQuantity >= 1) {
                            double unitPrice = Double.parseDouble(_objModel.getSellingPrice());

                            addedQuantity = Integer.parseInt(holder.quantityTV.getText().toString());
                            calculateTotal(_objModel, addedQuantity, holder);
                        }
                    }
                }else{
                    System.out.println("get focus");
                }

            }
        });

        holder.unitPriceTV.setText("" + Util.priceFormat(_objModel.getSellingPrice()));
        holder.totalPriceTV.setText("" + Util.priceFormat(_objModel.getTotalPrice()));

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int addedQuantity = Integer.parseInt(holder.quantityTV.getText().toString());
                int quantity = Integer.parseInt(_objModel.getQuantity());
                if (addedQuantity == quantity) {
                    new CustomDialog().showOneButtonAlertDialog(mContext, mContext.getString(R.string.exceeded_quantity_limit),
                            mContext.getString(R.string.exceeded_quantity_limit_message).replace("%s", _objModel.getProductName()), "OK", android.R.drawable.ic_dialog_alert, null);
                    return;
                }

                holder.quantityTV.setText("" + ++addedQuantity);
                calculateTotal(_objModel, addedQuantity, holder);
                Util.hideSoftKeypad(mContext);
            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int addedQuantity = Integer.parseInt(holder.quantityTV.getText().toString());
                if (addedQuantity == 1)
                    return;
                holder.quantityTV.setText("" + --addedQuantity);
                calculateTotal(_objModel, addedQuantity, holder);
                Util.hideSoftKeypad(mContext);
            }
        });

        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomDialog().showTwoButtonAlertDialog(mContext, null,
                        "Do you want to delete this product.", "OK", "CANCEL",
                        android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                            @Override
                            public void onPositiveClick() {
                                data.remove(position);

                                try {
                                    calculateTotal(null, 0, holder);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    RetailPosLoging.getInstance().registerLog(AddOrderListAdapter.class.getName(), ex);
                                }
                                notifyDataSetChanged();

                                NewOrderFragment.setOrderList(data);

                               /* if (mFragment instanceof AddOrderFragment)
                                    ((AddOrderFragment) mFragment).addProductBackToList(_objModel.getId());
                                else if (mFragment instanceof OrderOnHoldFragment)
                                    ((OrderOnHoldFragment) mFragment).addProductBackToList(_objModel.getId());
                                else if (mFragment instanceof AddOrderItemFragment)
                                    ((AddOrderItemFragment) mFragment).addProductBackToList(_objModel.getId());*/

                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });
            }
        });

        return convertView;
    }

    public void addItem(ProductOrderWrapper wrapper) {
        data.add(wrapper);
        notifyDataSetChanged();
    }

    public void increaseItemCount(ProductOrderWrapper wrapper) {
        ProductOrderWrapper orderWrapper = getItem(getPosition(wrapper));

        int addedQuantity = orderWrapper.getAddedQuantity();
        int quantity = Integer.parseInt(orderWrapper.getQuantity());

        if (addedQuantity == quantity) {
            new CustomDialog().showOneButtonAlertDialog(mContext, "Product is no more available.",
                    "Product quantity should be equals to available quantity.", "OK", android.R.drawable.ic_dialog_alert, null);
            return;
        }


        calculateTotal(orderWrapper, ++addedQuantity, null);
        notifyDataSetChanged();
    }


    private void calculateTotal(ProductOrderWrapper _objModel, int addedQuantity, ViewHolder holder) {
        if (_objModel != null) {
            double unitPrice = Double.parseDouble(_objModel.getSellingPrice());
            double totalPrice = addedQuantity * unitPrice;
            if(holder != null) {
                _objModel.setTotalPrice(totalPrice);
                _objModel.setAddedQuantity(addedQuantity);
                _objModel.setTotalPrice(totalPrice);
                holder.totalPriceTV.setText(Util.priceFormat(totalPrice));
            }
            else
            {
                data.get(getPosition(_objModel)).setTotalPrice(totalPrice);
                data.get(getPosition(_objModel)).setAddedQuantity(addedQuantity);
                data.get(getPosition(_objModel)).setTotalPrice(totalPrice);
                notifyDataSetChanged();
            }

        }

        if (mFragment instanceof NewOrderFragment) {
            double total = 0;
            for (ProductOrderWrapper aa : data) {
                total = total + aa.getTotalPrice();
            }
            ((NewOrderFragment) mFragment).onTotalProductPriceReceived(total, false);
            //((AddOrderFragment) mFragment).grossAmountCalculation(total, false);
        }
    }

    class ViewHolder {
        TextView orderNameTV;
        TextView unitPriceTV;
        TextView totalPriceTV;
        EditText quantityTV;
        Button plusButton;
        Button minusButton;
        Button crossButton;
    }

}

