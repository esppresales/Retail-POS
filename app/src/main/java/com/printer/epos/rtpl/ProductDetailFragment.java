package com.printer.epos.rtpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.CategoryWrapper;
import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.util.List;

import de.greenrobot.event.EventBus;


public class ProductDetailFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).setTitleText("Product Detail");
        ((Home) getActivity()).setEnabledButtons(false, true, false, false);
    }

    private SavePreferences mSavePreferences;

    private TextView productCategoryET;


    private ProductWrapper wrapper;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        ImageLoader mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(false).cacheInMemory(false)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();


        ScrollView formScroll = (ScrollView) rootView.findViewById(R.id.formScroll);
        RelativeLayout.LayoutParams form_param = (RelativeLayout.LayoutParams) formScroll.getLayoutParams();
        form_param.leftMargin = (int) (deviceWidth * .02f);
        form_param.rightMargin = (int) (deviceWidth * .02f);
        form_param.bottomMargin = (int) (deviceHeight * .03f);
        formScroll.setLayoutParams(form_param);

        ImageView img = (ImageView) rootView.findViewById(R.id.img);
//        RelativeLayout.LayoutParams img_param = (RelativeLayout.LayoutParams) img.getLayoutParams();
//        img_param.height = (int) (deviceWidth * .25f);
//        img_param.width = (int) (deviceWidth * .25f);
//        img_param.topMargin = (int) (deviceHeight * .04f);
//        img_param.leftMargin = (int) (deviceWidth * .02f);
//        img.setLayoutParams(img_param);

        rootView.findViewById(R.id.rl_image).setPadding((int) (deviceHeight * .04f),
                (int) (deviceHeight * .04f), 0, 0);

        LinearLayout linLeft = (LinearLayout) rootView.findViewById(R.id.lin_Left);
        RelativeLayout.LayoutParams linLeft_param = (RelativeLayout.LayoutParams) linLeft.getLayoutParams();
        linLeft_param.width = (int) (deviceWidth * .25f);
        linLeft_param.leftMargin = (int) (deviceWidth * .02f);
        linLeft.setLayoutParams(linLeft_param);

        TextView product_name = (TextView) rootView.findViewById(R.id.product_name);
        product_name.setPadding(0, (int) (deviceHeight * .04f), 0, 0);

        TextView productNameET = (TextView) rootView.findViewById(R.id.product_nameET);
        productNameET.setPadding(0, 0, 0, 0);

        TextView barcode = (TextView) rootView.findViewById(R.id.barcode);
        barcode.setPadding(0, (int) (deviceHeight * .04f), 0, 0);

        TextView barcodeET = (TextView) rootView.findViewById(R.id.barcodeET);
        barcodeET.setPadding(0, 0, 0, 0);

        TextView product_category = (TextView) rootView.findViewById(R.id.product_category);
        product_category.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        productCategoryET = (TextView) rootView.findViewById(R.id.product_categoryET);
        productCategoryET.setPadding(0, 0, 0, 0);
        setProductCategory();

        TextView product_price = (TextView) rootView.findViewById(R.id.product_price);
        product_price.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView productPriceET = (TextView) rootView.findViewById(R.id.product_priceET);
        productPriceET.setPadding(0, 0, 0, 0);

        TextView stock_location = (TextView) rootView.findViewById(R.id.stock_location);
        stock_location.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView stockLocationET = (TextView) rootView.findViewById(R.id.stock_locationET);
        stockLocationET.setPadding(0, 0, 0, 0);

        TextView quantity = (TextView) rootView.findViewById(R.id.quantity);
        quantity.getLayoutParams().width = (int) (deviceWidth * .2f);
        quantity.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView quantityET = (TextView) rootView.findViewById(R.id.quantityET);
        quantityET.getLayoutParams().width = (int) (deviceWidth * .2f);
        quantityET.setPadding(0, 0, 0, 0);

        TextView min_stock_alert_quantity = (TextView) rootView.findViewById(R.id.min_stock_alert_quantity);
        min_stock_alert_quantity.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView minStockAlertQuantityET = (TextView) rootView.findViewById(R.id.min_stock_alert_quantityET);
        minStockAlertQuantityET.setPadding(0, 0, 0, 0);

        TextView selling_price = (TextView) rootView.findViewById(R.id.selling_price);
        selling_price.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView selling_priceET = (TextView) rootView.findViewById(R.id.selling_priceET);
        selling_priceET.setPadding(0, 0, 0, 0);

        TextView product_description = (TextView) rootView.findViewById(R.id.product_description);
        product_description.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .03f), 0, 0);

        TextView productDescriptionET = (TextView) rootView.findViewById(R.id.product_descriptionET);
        productDescriptionET.setPadding((int) (deviceWidth * .02f), 0, 0, (int) (deviceHeight * .03f));

        TextView supplier = (TextView) rootView.findViewById(R.id.supplier);
        supplier.setPadding(0, (int) (deviceHeight * .03f), 0, 0);

        TextView supplierET = (TextView) rootView.findViewById(R.id.supplierET);
        supplierET.setPadding(0, 0, 0, 0);


        wrapper = EventBus.getDefault().removeStickyEvent(ProductWrapper.class);

        if (wrapper != null) {


            productNameET.setText(wrapper.getProductName());
            productDescriptionET.setText(wrapper.getProductDescription());
            productPriceET.setText(Util.priceFormat(wrapper.getProductCost()));
            selling_priceET.setText(Util.priceFormat(wrapper.getSellingPrice()));
            barcodeET.setText(wrapper.getBarcode());
            minStockAlertQuantityET.setText(wrapper.getMinStockAlertQuantity());
            quantityET.setText(wrapper.getQuantity());
            supplierET.setText(wrapper.getSupplier());
            stockLocationET.setText(wrapper.getStockLocation());
            productCategoryET.setText(wrapper.getProductCategory());


            if (wrapper.getProductImage() != null) {
                mImageLoader.displayImage(wrapper.getProductImage(),
                        img, options1, new ImageLoadingListener() {

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

        }

        return rootView;
    }

    void setProductCategory() {

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {

                for (CategoryListWrapper mCategoryWrapper : data) {

                    if (wrapper != null && mCategoryWrapper.getId().equals(wrapper.getProductCategory())) {
                        productCategoryET.setText(mCategoryWrapper.getName());
                    }
                }
            }
        }.getProductCategoryList(getActivity());
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backButton)
            backClick();

    }

    void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }
}
