package com.printer.epos.rtpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.Validation;
import com.printer.epos.rtpl.adapter.ProductCategorySpinnerAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;
import com.printer.epos.rtpl.wrapper.CategoryWrapper;
import com.printer.epos.rtpl.wrapper.DeviceType;
import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.printer.epos.rtpl.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.printer.epos.rtpl.ItemDetailActivity}
 * on handsets.
 */
public class AddProductFragment extends BaseFragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private boolean alreadyExist = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddProductFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }


    private Home hContext;
    private static String imagePath;

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("OnStart");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setProductCategorySpinnerAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        peripheralManager.connectDevice(DeviceType.BARCODE_SCANNER,
                new SavePreferences(getActivity()).getBarcodeDeviceName());

        hContext.setEnabledButtons(false, true, true, false);
        hContext.backButton.setOnClickListener(this);
        hContext.saveButton.setOnClickListener(this);

        if (wrapper != null) {
            hContext.saveButton.setText("UPDATE");
            hContext.setTitleText("Update Product");
            update = true;
        } else {
            hContext.saveButton.setText(getString(R.string.save));
            hContext.setTitleText(getString(R.string.add_product));
            update = true;
        }


//        if (EposDeviceClient.getScanner() == null)
//            Toast.makeText(getActivity(), "Barcode device not connected with printer", Toast.LENGTH_LONG).show();
        /*if (mScanner == null)
            createDevice(new SavePreferences(getActivity()).getBarcodeDeviceName(), Device.DEV_TYPE_SCANNER, Device.FALSE);
        else
            Toast.makeText(getActivity(), "Device is already connected", Toast.LENGTH_LONG).show();*/
    }

    private SavePreferences mSavePreferences;

    private EditText productNameET, barcodeET, productPriceET, selling_priceET, stockLocationET, quantityET,
            minStockAlertQuantityET, productDescriptionET, supplierET;
    private Spinner productCategoryET;
    private ImageView img;


    private ImageLoader mImageLoader;
    private View rootView;
    private ProductWrapper wrapper;
    private boolean update = false;

    private DisplayImageOptions options1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        imagePath = null;

        if (getActivity() instanceof Home)
            hContext = (Home) getActivity();

        mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        mImageLoader = ImageLoader.getInstance();
        options1 = new DisplayImageOptions.Builder()
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

        img = (ImageView) rootView.findViewById(R.id.img);
        RelativeLayout.LayoutParams img_param = (RelativeLayout.LayoutParams) img.getLayoutParams();
        img_param.height = (int) (deviceHeight * .15f);
        img_param.width = (int) (deviceHeight * .15f);
        img_param.topMargin = (int) (deviceHeight * .02f);
        img.setLayoutParams(img_param);
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               // showPicker();
                selectImage();
            }
        });

        TextView product_name = (TextView) rootView.findViewById(R.id.product_name);
        product_name.getLayoutParams().width = (int) (deviceWidth * .36f);
        product_name.setPadding(0, (int) (deviceHeight * .04f), 0, 0);

        productNameET = (EditText) rootView.findViewById(R.id.product_nameET);
        RelativeLayout.LayoutParams productNameET_param = (RelativeLayout.LayoutParams) productNameET.getLayoutParams();
        productNameET_param.height = (int) (deviceHeight * .07f);
        productNameET_param.width = (int) (deviceWidth * .36f);
        productNameET.setLayoutParams(productNameET_param);
        productNameET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView barcode = (TextView) rootView.findViewById(R.id.barcode);
        barcode.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .04f), 0, 0);

        barcodeET = (EditText) rootView.findViewById(R.id.barcodeET);
        RelativeLayout.LayoutParams barcodeET_param = (RelativeLayout.LayoutParams) barcodeET.getLayoutParams();
        barcodeET_param.height = (int) (deviceHeight * .07f);
        barcodeET_param.width = (int) (deviceWidth * .36f);
        barcodeET_param.leftMargin = (int) (deviceWidth * .06f);
        barcodeET.setLayoutParams(barcodeET_param);
        barcodeET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        ImageButton barcodeButton = (ImageButton) rootView.findViewById(R.id.barcodeButton);
        RelativeLayout.LayoutParams barcodeButton_param = (RelativeLayout.LayoutParams) barcodeButton.getLayoutParams();
        barcodeButton_param.height = (int) (deviceHeight * .07f);
        barcodeButton_param.width = (int) (deviceHeight * .07f);
        barcodeButton_param.leftMargin = (int) (deviceWidth * .02f);
        barcodeButton.setLayoutParams(barcodeButton_param);

        TextView product_category = (TextView) rootView.findViewById(R.id.product_category);
        product_category.getLayoutParams().width = (int) (deviceWidth * .36f);
        product_category.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        productCategoryET = (Spinner) rootView.findViewById(R.id.product_categoryET);
        RelativeLayout.LayoutParams productCategoryET_param = (RelativeLayout.LayoutParams) productCategoryET.getLayoutParams();
        productCategoryET_param.height = (int) (deviceHeight * .07f);
        productCategoryET_param.width = (int) (deviceWidth * .36f);
        productCategoryET.setLayoutParams(productCategoryET_param);
        productCategoryET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);
        productCategoryET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.hideSoftKeypad(getActivity());
                return false;
            }
        });

        LinearLayout price_lin = (LinearLayout) rootView.findViewById(R.id.price_lin);
        price_lin.getLayoutParams().width = (int) (deviceWidth * .25f);

        TextView product_price = (TextView) rootView.findViewById(R.id.product_price);
        product_price.getLayoutParams().width = (int) (deviceWidth * .25f);
        product_price.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        productPriceET = (EditText) rootView.findViewById(R.id.product_priceET);
        LinearLayout.LayoutParams productPriceET_param = (LinearLayout.LayoutParams) productPriceET.getLayoutParams();
        productPriceET_param.height = (int) (deviceHeight * .07f);
        productPriceET_param.width = (int) (deviceWidth * .13f);
        productPriceET_param.leftMargin = (int) (deviceWidth * .06f);
        productPriceET.setLayoutParams(productPriceET_param);
        productPriceET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);
        productPriceET.addTextChangedListener(new TextWatcher(){
//            DecimalFormat dec = new DecimalFormat("0.00");
            @Override
            public void afterTextChanged(Editable s) {
                String str = productPriceET.getText().toString();
                DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
                DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
                char sep = symbols.getDecimalSeparator();

                int indexOFdec = str.indexOf(sep);

                if (indexOFdec >= 0) {
                    if (str.substring(indexOFdec, str.length() - 1).length() > 2) {
                        s.replace(0, s.length(), str.substring(0, str.length() - 1));
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$"))
//                {
//                    String userInput= ""+s.toString().replaceAll("[^\\d]", "");
//                    if (userInput.length() > 0) {
//                        Float in=Float.parseFloat(userInput);
//                        float percen = in/100;
//                        productPriceET.setText(""+dec.format(percen));
//                        productPriceET.setSelection(productPriceET.getText().length());
//                    }
//                }
            }
        });

        TextView selling_price = (TextView) rootView.findViewById(R.id.selling_price);
        selling_price.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        selling_priceET = (EditText) rootView.findViewById(R.id.selling_priceET);
        LinearLayout.LayoutParams selling_priceET_param = (LinearLayout.LayoutParams) selling_priceET.getLayoutParams();
        selling_priceET_param.height = (int) (deviceHeight * .07f);
        selling_priceET_param.width = (int) (deviceWidth * .13f);
        selling_priceET.setLayoutParams(selling_priceET_param);
        selling_priceET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);
        selling_priceET.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                String str = selling_priceET.getText().toString();
                DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
                DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
                char sep = symbols.getDecimalSeparator();

                int indexOFdec = str.indexOf(sep);

                if (indexOFdec >= 0) {
                    if (str.substring(indexOFdec, str.length() - 1).length() > 2) {
                        s.replace(0, s.length(), str.substring(0, str.length() - 1));
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        TextView stock_location = (TextView) rootView.findViewById(R.id.stock_location);
        stock_location.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        stockLocationET = (EditText) rootView.findViewById(R.id.stock_locationET);
        RelativeLayout.LayoutParams stockLocationET_param = (RelativeLayout.LayoutParams) stockLocationET.getLayoutParams();
        stockLocationET_param.height = (int) (deviceHeight * .07f);
        stockLocationET_param.width = (int) (deviceWidth * .36f);
        stockLocationET_param.leftMargin = (int) (deviceWidth * .06f);
        stockLocationET.setLayoutParams(stockLocationET_param);
        stockLocationET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView quantity = (TextView) rootView.findViewById(R.id.quantity);
        quantity.getLayoutParams().width = (int) (deviceWidth * .23f);
        quantity.setPadding((int) (deviceWidth * .06f), (int) (deviceHeight * .02f), 0, 0);

        quantityET = (EditText) rootView.findViewById(R.id.quantityET);
        RelativeLayout.LayoutParams quantityET_param = (RelativeLayout.LayoutParams) quantityET.getLayoutParams();
        quantityET_param.height = (int) (deviceHeight * .07f);
        quantityET_param.width = (int) (deviceWidth * .17f);
        quantityET_param.leftMargin = (int) (deviceWidth * .06f);
        quantityET.setLayoutParams(quantityET_param);
        quantityET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView min_stock_alert_quantity = (TextView) rootView.findViewById(R.id.min_stock_alert_quantity);
        min_stock_alert_quantity.setPadding((int) (deviceWidth * .02f), (int) (deviceHeight * .02f), 0, 0);

        minStockAlertQuantityET = (EditText) rootView.findViewById(R.id.min_stock_alert_quantityET);
        RelativeLayout.LayoutParams min_stock_alert_quantityET_param = (RelativeLayout.LayoutParams) minStockAlertQuantityET.getLayoutParams();
        min_stock_alert_quantityET_param.height = (int) (deviceHeight * .07f);
        min_stock_alert_quantityET_param.width = (int) (deviceWidth * .17f);
        min_stock_alert_quantityET_param.leftMargin = (int) (deviceWidth * .02f);
        minStockAlertQuantityET.setLayoutParams(min_stock_alert_quantityET_param);
        minStockAlertQuantityET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView product_description = (TextView) rootView.findViewById(R.id.product_description);
        product_description.getLayoutParams().width = (int) (deviceWidth * .36f);
        product_description.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        productDescriptionET = (EditText) rootView.findViewById(R.id.product_descriptionET);
        RelativeLayout.LayoutParams productDescriptionET_param = (RelativeLayout.LayoutParams) productDescriptionET.getLayoutParams();
        productDescriptionET_param.height = (int) (deviceHeight * .14f);
        productDescriptionET_param.width = (int) (deviceWidth * .36f);
        productDescriptionET.setLayoutParams(productDescriptionET_param);
        productDescriptionET.setPadding((int) (deviceWidth * .01f), (int) (deviceHeight * .01f), (int) (deviceWidth * .01f), (int) (deviceHeight * .01f));

        TextView supplier = (TextView) rootView.findViewById(R.id.supplier);
        supplier.getLayoutParams().width = (int) (deviceWidth * .36f);
        supplier.setPadding(0, (int) (deviceHeight * .02f), 0, 0);

        supplierET = (EditText) rootView.findViewById(R.id.supplierET);
        RelativeLayout.LayoutParams supplierET_param = (RelativeLayout.LayoutParams) supplierET.getLayoutParams();
        supplierET_param.height = (int) (deviceHeight * .07f);
        supplierET_param.width = (int) (deviceWidth * .36f);
        supplierET.setLayoutParams(supplierET_param);
        supplierET.setPadding((int) (deviceWidth * .01f), 0, (int) (deviceWidth * .01f), 0);

        TextView dollar = (TextView) rootView.findViewById(R.id.dollar);
        dollar.setText(" " + UiController.sCurrency);

        TextView dollar1 = (TextView) rootView.findViewById(R.id.dollar1);
        dollar1.setText(" " + UiController.sCurrency);




        wrapper = EventBus.getDefault().removeStickyEvent(ProductWrapper.class);

        if (wrapper != null) {

            productNameET.setText(wrapper.getProductName());
            productDescriptionET.setText(wrapper.getProductDescription());
            productPriceET.setText(Util.priceFormatForServer(wrapper.getProductCost()));
            selling_priceET.setText(Util.priceFormatForServer(wrapper.getSellingPrice()));
            barcodeET.setText(wrapper.getBarcode());
            minStockAlertQuantityET.setText(wrapper.getMinStockAlertQuantity());
            quantityET.setText(wrapper.getQuantity());
            supplierET.setText(wrapper.getSupplier());
            stockLocationET.setText(wrapper.getStockLocation());


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
                                    //((ImageView) img).setImageBitmap(new Util().getCircularBitmap(bmp));
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

    private HashMap<String, Object> valueKey;
    private ArrayList<CategoryWrapper.CategoryListWrapper> list;

    private void setProductCategorySpinnerAdapter() {

        new CategoryWrapper() {
            @Override
            public void getCategoryWrapperList(Context context, List<CategoryListWrapper> data) {

                List<CategoryListWrapper> tempList = new ArrayList<CategoryListWrapper>();
                for(CategoryListWrapper obj : data){
                    if (obj.getMarkAsDelete() != null && obj.getMarkAsDelete().equals("0"))
                        tempList.add(obj);
                }
                valueKey = new HashMap<String, Object>();
                list = new ArrayList<CategoryListWrapper>();
                CategoryListWrapper categoryListWrapper = new CategoryListWrapper();
                categoryListWrapper.setName("Select Product Category");
                list.add(0, categoryListWrapper);
                int selection = 0;
                for (CategoryListWrapper mCategorywrapper : tempList) {
                    valueKey.put(mCategorywrapper.getName(), mCategorywrapper.getId());

                    if (mCategorywrapper.getMarkAsDelete() != null && mCategorywrapper.getMarkAsDelete().equals("0"))
                        list.add(mCategorywrapper);

                    if (wrapper != null && mCategorywrapper.getId().equals(wrapper.getProductCategory())) {
                        selection = tempList.indexOf(mCategorywrapper);
                        selection = ++selection;
                    }
                }

                ProductCategorySpinnerAdapter categoryAdapter = new ProductCategorySpinnerAdapter(getActivity(), list);
                productCategoryET.setAdapter(categoryAdapter);
                productCategoryET.setSelection(selection);

                if(tempList.size() == 0)
                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product category is not available.",
                            "Please set the product category first.", "OK", android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                                @Override
                                public void onPositiveClick() {
                                    if (getActivity() instanceof Home) {
                                        ((Home) getActivity()).changeFragment(FragmentUtils.ProductCategoryFragment, null, true, false);
                                    }
                                }

                                @Override
                                public void onNegativeClick() {

                                }
                            });
            }
        }.getProductCategoryList(getActivity());
    }

    public void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton: {
                backClick();
                break;
            }
            case R.id.saveButton: {
                saveCLick();
                break;
            }

        }
    }

    private void saveCLick() {
        /*if (wrapper == null)
            ProductWrapper.getProductList(null, getActivity(), this);
        if (!alreadyExist) {
        */
        Util.hideSoftKeypad(this.getActivity());
        try {

            if (new Validation().checkValidation((ViewGroup) rootView.findViewById(R.id.form))) {

                boolean isError = false;
                if (Float.parseFloat(productPriceET.getText().toString()) > 10000000) {
                    productPriceET.setError(getString(R.string.error_less_then_1crore));
                    isError = true;
                } else if (Float.parseFloat(selling_priceET.getText().toString()) > 10000000) {
                    selling_priceET.setError(getString(R.string.error_less_then_1crore));
                    isError = true;
                }
                if (isError)
                    return;

                CategoryWrapper.CategoryListWrapper data = (CategoryWrapper.CategoryListWrapper)productCategoryET.getSelectedItem();
//                if (data.getMarkAsDelete() != null && data.getMarkAsDelete().equals("0")) {
                    if (Integer.parseInt(minStockAlertQuantityET.getText().toString()) <= Integer.parseInt(quantityET.getText().toString())) {
                        if (productCategoryET.getSelectedItemPosition() != 0) {
                            HashMap<String, Object> map = new HashMap<String, Object>();

                            map.put("name", productNameET.getText().toString());
                            map.put("barcode", barcodeET.getText().toString());
                            map.put("category_id", valueKey.get(data.getName()));
                            map.put("cost_price", Util.priceFormatForServer(productPriceET.getText().toString()));
                            map.put("selling_price",Util.priceFormatForServer(selling_priceET.getText().toString()));
                            map.put("stock_location", stockLocationET.getText().toString());
                            map.put("quantity", quantityET.getText().toString());
                            map.put("min_stock_alert_qty", minStockAlertQuantityET.getText().toString());
                            map.put("description", productDescriptionET.getText().toString());
                            map.put("supplier", supplierET.getText().toString());
                            map.put("added_by", mSavePreferences.get_id());



                            if (wrapper == null) {
                                if (file != null) {
                                    Log.i("filepath in product--->",""+file.getAbsolutePath());
                                    map.put("image", file);
                                    ProductWrapper.addProduct(map, getActivity(), this);
                                } else {
                                    new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product image is missing.",
                                            "Please add an image.", "OK", android.R.drawable.ic_dialog_alert, null);
                                }
                            } else if (img.getDrawable() != null) {
                                String imageBade64 = "";

                                if(file != null){
                                    imageBade64 = new Util().encodeTobase64(img.getDrawable());
                                }
                              //  map.put("barcode", barcodeET.getText().toString());
                                Log.i("Map-->",map.toString());
                                map.put("image", imageBade64);
                                /*ProductWrapper.getProductList(null, getActivity(), this);
                                if (!alreadyExist) {
                                */
                               // Log.i("prduct update==>","")
                                ProductWrapper.updateProduct(wrapper.id, map, getActivity(), this);
                                /*} else
                                    alreadyExist = false;
   */
                            }
                           // Log.i("Map-->",map.toString());
                        } else {
                            new CustomDialog().showOneButtonAlertDialog(getActivity(), "Product category is not selected.",
                                    "Please select the product category.", "OK", android.R.drawable.ic_dialog_alert, null);
                        }
                    } else {
                        new CustomDialog().showOneButtonAlertDialog(getActivity(), "Min stock alert quantity is more than quantity.",
                                "Min stock alert quantity should be less than quantity.", "OK", android.R.drawable.ic_dialog_alert, null);
                    }
//                } else {
//                    new CustomDialog().showOneButtonAlertDialog(getActivity(), null,
//                            "Selected category is disabled, please select another one.", "OK", android.R.drawable.ic_dialog_alert, null);
//                }
            }

        } catch (Exception ex)
        {

            RetailPosLoging.getInstance().registerLog(AddProductFragment.class.getName(), ex);
            ex.printStackTrace();
        }

        /*} else
            alreadyExist = false;
*/
    }

    public void setProductAdapter(List<ProductWrapper> data) {

        for (ProductWrapper wrapper : data) {
            String barcode = barcodeET.getText().toString().trim();
            if (barcode.length() > 0 && wrapper.getBarcode().equals(barcode)) {
                alreadyExist = true;
                barcodeET.setError("Barcode already exists");
                return;
            }
        }


     /*   mAdapter = new ProductAdapter(ProductFragment.this, data);
        list.setAdapter(mAdapter);

        ((Home) getActivity()).SearchBarET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {

                    mAdapter.getFilter(((Home) getActivity()).SearchBarET.getText().toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (mCapturedImageURI != null) {
//            outState.putString("cameraImageUri", mCapturedImageURI.toString());
//
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState.containsKey("cameraImageUri")) {
//            mCapturedImageURI = Uri.parse(savedInstanceState
//                    .getString("cameraImageUri"));
//        }
//    }



  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CROP_REQUEST
                && resultCode == Activity.RESULT_OK) {

            try {
                // Bundle extras = data.getExtras();
                // // get the cropped bitmap
                // bitmap = extras.getParcelable("data");
                // image_asset = new File(FileUtils.getPath(this,
                // mCapturedImageURI));

                // Bundle extras = data.getExtras();
                // get the cropped bitmap
                imagePath = data.getStringExtra(CropImage.IMAGE_PATH);
                // String imagePath = extras
                // .getString(CropActivity.CROPPED_IMAGE_PATH);

                // Uri picUri = Uri.parse(imagePath);

                bitmap = BitmapFactory.decodeFile(imagePath);

                if (bitmap != null) {
                    // load image into ImageView

                    //img.setImageBitmap(new Util().getCircularBitmap(bitmap));
                    img.setImageBitmap(bitmap);
                    // idCardBackPicture.setImageBitmap(bitmap);

                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(AddProductFragment.class.getName(), e);
                }
                if (null == image_asset) {
                    try {
                        image_asset = saveImageToExternalStorage(bitmap);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        RetailPosLoging.getInstance().registerLog(AddProductFragment.class.getName(), e1);
                        e1.printStackTrace();
                    }

                }
            }
        }

        else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }*/

    @Override
    protected void setImageView(Bitmap bitmap, String filePath) {
        super.setImageView(bitmap, filePath);
        img.setImageBitmap(bitmap);
        imagePath = filePath;
        file = new File(filePath);
    }


    @Override
    public void onBarcodeScanData(final String data) {
        super.onBarcodeScanData(data);
        if(getActivity() != null)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barcodeET.setText(data);
                }
            });
        }

    }
}
