package com.printer.epos.rtpl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.adapter.AddOrderListAdapter;
import com.printer.epos.rtpl.adapter.ProductListAdapter;
import com.printer.epos.rtpl.adapter.SelectedProductCache;
import com.printer.epos.rtpl.wrapper.ProductOrderWrapper;
import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddOrderItemFragment extends NewOrderFragment implements View.OnClickListener {

    private Dialog addItemPopup;
    private int deviceWidth;

    private int deviceHeight;

    private List<ProductOrderWrapper> orderList = getOrderList();
    private List<ProductWrapper> productListForPopup = getProductList();

    private View rootView;


    public AddOrderItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_order_item, container, false);
        setupUI(rootView);
        ImageView addItem = (ImageView) rootView.findViewById(R.id.addItemButton);
        mOrderListView = (ListView) rootView.findViewById(R.id.list);
        addItem.setOnClickListener(this);

        TextView mEmptyTextView = (TextView) rootView.findViewById(R.id.emptyTextView);
        TextView totalPriceTv = (TextView) rootView.findViewById(R.id.totalPriceTV);

        totalPriceTv.setText("Total Price ("+UiController.sCurrency+")");
        mEmptyTextView.setText("No Item Added.");

        //For simulation of barcode reader operation

        /*totalPriceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductViaBarcode("123123123");
            }
        });
        */
        mOrderListView.setEmptyView(mEmptyTextView);
        SelectedProductCache.clearProcuctCacheData();

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        ProductWrapper.getProductList(null, getActivity(), this);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (addItemPopup != null && addItemPopup.isShowing()) {
            addItemPopup.cancel();
        }
    }

    public void setProductList(List<ProductWrapper> data) {
        productListForPopup = data;
        createAddItemDialog();

        List<ProductOrderWrapper> orderOnHold = getOnHoldItems(data);
        if(orderOnHold != null && orderOnHold.size() != 0)
        {

            double total = 0;
            for (ProductOrderWrapper aa : orderOnHold) {
                total = total + aa.getTotalPrice();
            }
            onTotalProductPriceReceived(total, false);

            orderList = new ArrayList<ProductOrderWrapper>(orderOnHold);
            setOrderList(orderList);

            //removeItemFromPopup();

            mOrderAdapter = new AddOrderListAdapter(this, orderList);
            mOrderListView.setAdapter(mOrderAdapter);

        }
        else{
            if (orderList != null) {
                double total = 0;
                for (ProductOrderWrapper aa : orderList) {
                    total = total + aa.getTotalPrice();
                }
                onTotalProductPriceReceived(total, false);
                //grossAmountCalculation(total, false);
            } else {
                orderList = new ArrayList<ProductOrderWrapper>();
            }

            mOrderAdapter = new AddOrderListAdapter(this, orderList);
            mOrderListView.setAdapter(mOrderAdapter);
        }

    }

    private Dialog getDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_item_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(
                context.getResources().getDrawable(
                        R.drawable.dilaog_circular_corner));

        WindowManager.LayoutParams params = dialog.getWindow()
                .getAttributes();

        params.width = (int) (deviceWidth * .55f);
        params.height = (int) (deviceHeight * .7f);

        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private void createAddItemDialog()
    {
        addItemPopup = getDialog(getActivity());
        TextView cancelButton = (TextView) addItemPopup.findViewById(R.id.cancelDialog);
        TextView addItemTitle = (TextView) addItemPopup.findViewById(R.id.addItemTV);
        TextView productNameTV = (TextView) addItemPopup.findViewById(R.id.productNameTV);
        TextView productPriceTV = (TextView) addItemPopup.findViewById(R.id.productPriceHTV);
        LinearLayout ListHeading = (LinearLayout) addItemPopup.findViewById(R.id.ListHeading);
        final EditText searchET = (EditText) addItemPopup.findViewById(R.id.searchProductET);
        ListView mProductListView = (ListView) addItemPopup.findViewById(R.id.productList);

        int margin = (int) (deviceHeight * 0.03f);
        int topmargin = (int) (deviceHeight * 0.03f);

        RelativeLayout.LayoutParams addItemTitleParam = (RelativeLayout.LayoutParams) addItemTitle.getLayoutParams();
        addItemTitleParam.topMargin = topmargin;
        addItemTitleParam.bottomMargin = topmargin;
        addItemTitle.setLayoutParams(addItemTitleParam);

        RelativeLayout.LayoutParams cancelButtonParam = (RelativeLayout.LayoutParams) cancelButton.getLayoutParams();
        cancelButtonParam.rightMargin = margin;
        cancelButton.setLayoutParams(cancelButtonParam);

        RelativeLayout.LayoutParams productNameTVParam = (RelativeLayout.LayoutParams) productNameTV.getLayoutParams();
        productNameTVParam.rightMargin = margin;
        productNameTVParam.topMargin = topmargin;
        productNameTVParam.leftMargin = margin;
        productNameTV.setLayoutParams(productNameTVParam);

        RelativeLayout.LayoutParams searchETParam = (RelativeLayout.LayoutParams) searchET.getLayoutParams();
        searchETParam.height = (int) (deviceHeight * 0.07f);
        searchETParam.bottomMargin = margin;
        searchET.setLayoutParams(searchETParam);
        searchET.setPadding(margin / 2, 0, 0, 0);

        RelativeLayout.LayoutParams mProductListViewParam = (RelativeLayout.LayoutParams) mProductListView.getLayoutParams();
        mProductListViewParam.bottomMargin = margin;
        mProductListView.setLayoutParams(mProductListViewParam);

        RelativeLayout.LayoutParams ListHeadingParam = (RelativeLayout.LayoutParams) ListHeading.getLayoutParams();
        ListHeadingParam.leftMargin = margin;
        ListHeadingParam.rightMargin = margin;
        ListHeading.setLayoutParams(ListHeadingParam);

        productPriceTV.setText("Price ( " + UiController.sCurrency + " )");

        searchET.clearFocus();
        Util.hideSoftKeypad(getActivity());

        mProductAdapter = new ProductListAdapter(this, productListForPopup);
        mProductListView.setAdapter(mProductAdapter);

        final HashMap<String,Integer> detail=new HashMap<>();
        for(int k=0;k<productListForPopup.size();k++){
            detail.put(productListForPopup.get(k).getProductName(),k);
        }

        mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (addItemPopup.isShowing()) {
                    addItemPopup.cancel();
                }
                boolean added = false;


                if (!mProductAdapter.getItem(position).getQuantity().trim().equals("0")) {
                    ProductOrderWrapper _wrapper;
                    _wrapper = mProductAdapter.getItem(position);
                    _wrapper.setAdded(true);
                    _wrapper.setAddedQuantity(1);
                    _wrapper.setTotalPrice(Double.parseDouble(_wrapper.getSellingPrice()));

                    if(null != orderList) {
                        for (ProductOrderWrapper model : orderList) {
                            if (_wrapper.getId().trim().equals(model.getId().trim())) {
                                added = true;
                            }
                        }
                    }
                    if (!added) {
                        orderList.add(_wrapper);
                        setOrderList(orderList);

                        mOrderAdapter.notifyDataSetChanged();

                        //   SelectedProductCache.setProductCacheData(productListForPopup.get(position));

                        /*if(detail.containsKey(_wrapper.getProductName())){
                            int pos=detail.get(_wrapper.getProductName());
                            SelectedProductCache.setProductCacheData(productListForPopup.get(pos));
                            productListForPopup.remove(pos);
                            mProductAdapter.notifyDataSetChanged();
                        }
                        else{
                            SelectedProductCache.setProductCacheData(productListForPopup.get(position));
                            productListForPopup.remove(position);
                            mProductAdapter.notifyDataSetChanged();
                        }*/



                        try {
                            recentlyAddedItemName = orderList.get(orderList.size() - 1).getProductName();
                            recentlyAddedItemPrice = orderList.get(orderList.size() - 1).getSellingPrice();
                            onTotalProductPriceReceived(Double.parseDouble(_wrapper.getSellingPrice()), true);
                            //grossAmountCalculation(Double.parseDouble(_wrapper.getSellingPrice()), true);
                        } catch (Exception ex) {
                            RetailPosLoging.getInstance().registerLog(AddOrderItemFragment.class.getName(), ex);
                            ex.printStackTrace();
                        }
                    } else {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product already added",
                                "Please select another product.", "OK", android.R.drawable.ic_dialog_alert, null);
                    }
                } else {
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), getActivity().getString(R.string.exceeded_quantity_limit),
                            getActivity().getString(R.string.exceeded_quantity_limit_message).replace("%s", mProductAdapter.getItem(position).getProductName()), "OK", android.R.drawable.ic_dialog_alert, null);
                }
                searchET.clearFocus();
                searchET.setText("");

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                searchET.clearFocus();
                searchET.setText("");
                if (addItemPopup.isShowing()) {
                    addItemPopup.cancel();
                }

            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mProductAdapter.getFilter(searchET.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.addItemButton:
                addItemPopupShow();
                break;
            default:
                break;
        }
    }

    private void addItemPopupShow() {
        if (addItemPopup != null && !addItemPopup.isShowing()) {
            createAddItemDialog();
            addItemPopup.show();
            Util.hideSoftKeypad(getActivity());
        }
    }

    public void addProductBackToList(String id) {
        for (int i = 0; i < SelectedProductCache.getProductCacheData().size(); i++) {
            ProductWrapper wrapper = SelectedProductCache.getProductCacheData().get(i);
            if (wrapper.getId().equals(id)) {
                productListForPopup.add(wrapper);
                mProductAdapter.addItem(wrapper);
                SelectedProductCache.getProductCacheData().remove(wrapper);
            }
        }
    }

    protected void addProductViaBarcode(String data)
    {
        if (!searchProductWithBarcode(data))
            Crouton.makeText(getActivity(),"Product not found with following barcode: " + data, Style.INFO).show();

    }

    private boolean searchProductWithBarcode(String barcode) {
        boolean flag = false;

        for (int i = 0; i < productListForPopup.size(); i++) {
            ProductWrapper wrapper = productListForPopup.get(i);
            if (wrapper.getBarcode().equals(barcode)) {

                ProductOrderWrapper orderWrapper = searchInItemList(barcode);
                if(orderWrapper != null && orderWrapper.getBarcode().equals(barcode))
                {
                        mOrderAdapter.increaseItemCount(orderWrapper);
                        return true;
                }
                else {
                    addBarcodeScannedProduct(wrapper);
                    flag = true;
                    break;
                }
            }
            else
                flag = false;
        }
        return flag;
    }

    private ProductOrderWrapper searchInItemList(String barcode)
    {
        ProductOrderWrapper orderWrapper = null;
        for (ProductOrderWrapper model : orderList) {
            if (barcode.trim().equals(model.getBarcode().trim())) {
                orderWrapper = model;
                break;
            }
            else{
                orderWrapper = null;
            }
        }

        return orderWrapper;

    }

    private void addBarcodeScannedProduct(ProductWrapper _wrapper) {
        boolean added = false;
        if (!_wrapper.getQuantity().trim().equals("0")) {

            _wrapper.setAdded(true);
            _wrapper.setAddedQuantity(1);
            _wrapper.setTotalPrice(Double.parseDouble(_wrapper.getSellingPrice()));

            for (ProductOrderWrapper model : orderList) {
                if (_wrapper.getId().trim().equals(model.getId().trim())) {
                    added = true;

                }
            }
            if (!added) {
                orderList.add(_wrapper);
                setOrderList(orderList);
                mOrderAdapter.notifyDataSetChanged();

                SelectedProductCache.setProductCacheData(_wrapper);

               // productListForPopup.remove(_wrapper);
               // mProductAdapter.notifyDataSetChanged();

                try {
                    recentlyAddedItemName = orderList.get(orderList.size() - 1).getProductName();
                    recentlyAddedItemPrice = orderList.get(orderList.size() - 1).getSellingPrice();
                    onTotalProductPriceReceived(Double.parseDouble(_wrapper.getSellingPrice()), true);
                    //grossAmountCalculation(Double.parseDouble(_wrapper.getSellingPrice()), true);
                } catch (Exception ex) {
                    RetailPosLoging.getInstance().registerLog(AddOrderItemFragment.class.getName(), ex);
                    ex.printStackTrace();
                }
            } else {
                new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product already added",
                        "Please select another product.", "OK", android.R.drawable.ic_dialog_alert, null);
            }
        } else {
            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product is no more available.",
                    "Available product quantity should be greater than zero.", "OK", android.R.drawable.ic_dialog_alert, null);
        }
    }



    private void removeItemFromPopup() {

        for(int i=0;i<orderList.size();i++)
        {
            for(int j = 0; j < productListForPopup.size();i++)
            {
                if(productListForPopup.get(j).getId().equals(orderList.get(i).getId()))
                {
                    SelectedProductCache.setProductCacheData(productListForPopup.get(j));
                    productListForPopup.remove(j);
                }

            }
        }
        mProductAdapter.notifyDataSetChanged();
    }


}