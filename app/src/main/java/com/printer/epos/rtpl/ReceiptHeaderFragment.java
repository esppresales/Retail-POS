package com.printer.epos.rtpl;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.dialogs.SettingsWebClient;
import com.printer.epos.rtpl.wrapper.settingswrapper.ReceiptHeaderDetails;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptHeaderFragment extends BaseFragment implements View.OnClickListener {


    private EditText name;
    private EditText website;
    private EditText header1;
    private EditText header2;
    private EditText header3;
    private EditText message;
    private CheckBox addLogoCb;
    private CheckBox addCouponCb;
    private ImageView logoIv;
    private ImageView couponIv;

    private View rootView;
    private boolean isLogoImage;
    private SavePreferences mPrefs;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options1;


    public enum ImageType {
        LOGO, COUPON_IMAGE
    }

    public ReceiptHeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_receipt_header, container, false);

        name = (EditText) rootView.findViewById(R.id.name);
        website = (EditText) rootView.findViewById(R.id.header4);
        header1 = (EditText) rootView.findViewById(R.id.header1);
        header2 = (EditText) rootView.findViewById(R.id.header2);
        header3 = (EditText) rootView.findViewById(R.id.header33);
        message = (EditText) rootView.findViewById(R.id.message);
        addCouponCb = (CheckBox) rootView.findViewById(R.id.couponCheck);
        addLogoCb = (CheckBox) rootView.findViewById(R.id.logoCheck);

        logoIv = (ImageView) rootView.findViewById(R.id.logo);
        couponIv = (ImageView) rootView.findViewById(R.id.couponlogo);

        logoIv.setOnClickListener(this);
        couponIv.setOnClickListener(this);

//        setImageDimension(logoIv);
//        setImageDimension(couponIv);

        mPrefs = new SavePreferences(getActivity());

        mImageLoader = ImageLoader.getInstance();
        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false).cacheInMemory(false)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();

        setUpValues();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).setTitleText("Receipt Configuration");
        ((Home) getActivity()).setEnabledButtons(false, true, true, false);
        ((Home) getActivity()).backButton.setOnClickListener(this);
        ((Home) getActivity()).saveButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backButton:
                backClick();
                break;
            case R.id.saveButton:
                saveClick(view);
                break;
            case R.id.couponlogo:
                isLogoImage = false;
                selectImage();
                break;
            case R.id.logo:
                isLogoImage = true;
                selectImage();
                break;

            default:
                break;
        }
    }

    private void setUpValues()
    {
        name.setText(mPrefs.getReceiptName());
        header1.setText(mPrefs.getReceiptHeader1());
        header2.setText(mPrefs.getReceiptHeader2());
        header3.setText(mPrefs.getReceiptHeader3());
        website.setText(mPrefs.getReceiptWebsite());
        message.setText(mPrefs.getReceiptMessage());

        if(!TextUtils.isEmpty(mPrefs.getReceiptCouponUrl())){
            couponImagePath = mPrefs.getReceiptCouponUrl();
            setImage(couponIv, couponImagePath,ImageType.COUPON_IMAGE);
        }


        if(!TextUtils.isEmpty(mPrefs.getReceiptLogoUrl())){
            logoImagePath = mPrefs.getReceiptLogoUrl();
            setImage(logoIv, mPrefs.getReceiptLogoUrl(),ImageType.LOGO);
        }


        if(mPrefs.getReceiptCouponFlag() == 1)
            addCouponCb.setChecked(true);
        else
            addCouponCb.setChecked(false);

        if(mPrefs.getReceiptLogoFlag() == 1)
            addLogoCb.setChecked(true);
        else
            addLogoCb.setChecked(false);
    }
    private void backClick() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStackImmediate();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
        getActivity().onBackPressed();
    }

    public void saveClick(View v) {

        Util.hideSoftKeypad(getActivity());
        boolean flag = true;
        ReceiptHeaderDetails details = new ReceiptHeaderDetails();
        details.setName(name.getText().toString());
        details.setWebsite(website.getText().toString());
        details.setHeader1(header1.getText().toString());
        details.setHeader2(header2.getText().toString());
        details.setHeader3(header3.getText().toString());
        details.setMessage(message.getText().toString());



        if(addLogoCb.isChecked())
            details.setLogoUsed(1);
        else
            details.setLogoUsed(0);

        if(addCouponCb.isChecked())
            details.setCouponUsed(1);
        else
            details.setCouponUsed(0);


//        if(addLogoCb.isChecked()){
            if(!TextUtils.isEmpty(logoImagePath)){
                if(!logoImagePath.contains("http")) {
                    Log.i("logoImagePath", logoImagePath);
                    details.setLogoImage(logoImagePath);
                }
            }
//            else {
//                flag = false;
//                new CustomDialog().showOneButtonAlertDialog(getActivity(), "Logo image is missing.",
//                        "Please add an image.", "OK", android.R.drawable.ic_dialog_alert, null);
//            }
//        }

//        if(addCouponCb.isChecked()) {
            if(!TextUtils.isEmpty(couponImagePath)){
                if(!couponImagePath.contains("http")) {
                    Log.i("couponImagePath", couponImagePath);
                    details.setCouponImage(couponImagePath);
                }
            }
//            else {
//                flag = false;
//                new CustomDialog().showOneButtonAlertDialog(getActivity(), "Coupon image is missing.",
//                        "Please add an image.", "OK", android.R.drawable.ic_dialog_alert, null);
//            }
//        }

        if(flag) {
            HashMap<String, Object> map=SettingsWebClient.getReceiptHeaderMap(details);
            Log.i("Reciept map",""+map);
            SettingsWebClient.updateSettings(getActivity(), SettingsWebClient.getReceiptHeaderMap(details), null);
            backClick();
        }

    }

    private static String imagePath;
    private static String couponImagePath;
    private static String logoImagePath;

//    private void setImageDimension(ImageView iv)
//    {
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        int deviceWidth = displayMetrics.widthPixels;
//        int deviceHeight = displayMetrics.heightPixels;
//
//        RelativeLayout.LayoutParams img_param_1 = (RelativeLayout.LayoutParams) iv.getLayoutParams();
//        img_param_1.height = (int) (deviceHeight * .15f);
//        img_param_1.width = (int) (deviceHeight * .15f);
//        iv.setLayoutParams(img_param_1);
//
//    }

    private void setImage(ImageView iv, String url, final ImageType type)
    {
        mImageLoader.displayImage(url,
                iv, options1, new ImageLoadingListener() {

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



    protected void setImageView(Bitmap bitmap,String filePath)
    {
        //Uri uri=data.getData();
        imagePath = filePath;

        Log.i("isLogoImage--->",""+isLogoImage);
        // load image into ImageView
        if(isLogoImage) {
            logoImagePath = imagePath;
            //mPrefs.storeReceiptLogoFilePath(logoImagePath);
           // logoIv.setImageURI(uri);
            logoIv.setImageBitmap(bitmap);
        }
        else {
            couponImagePath = imagePath;
            //mPrefs.storeReceiptCouponFilePath(couponImagePath);
           // couponIv.setImageURI(uri);
            couponIv.setImageBitmap(bitmap);
        }


    }



}
