package com.printer.epos.rtpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.epson.eposdevice.display.Display;
import com.epson.eposdevice.printer.Printer;
import com.epson.eposdevice.scanner.Scanner;
import com.printer.epos.rtpl.Utility.FileUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.util.ImageCompress;
import com.printer.epos.rtpl.util.Utilities;
import com.printer.epos.rtpl.util.imagecropping.ActivityImageCropping;
import com.printer.epos.rtpl.util.imagecropping.CropImageView;
import com.printer.epos.rtpl.wrapper.MultiplePrintManager;
import com.printer.epos.rtpl.wrapper.PeripheralCallback;
import com.printer.epos.rtpl.wrapper.PeripheralManager;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ranosys-puneet on 23/3/15.
 */
public class BaseFragment extends Fragment implements PeripheralCallback {

//    File ROOT_DIR;
//    File CACHE_DIR;

    public File file;

    public final static int CAPTURE_PHOTO = 100;
    public final static int SELECT_PHOTO = 101;
    public final static int CROP_IMAGE = 1001;


    private static final int GALLERY_REQUEST = 1002;
    private static final int CAMERA_REQUEST = 1005;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 1003;
    public static final int CROP_REQUEST = 1009;
    private Uri mCapturedImageURI;
    public File image_asset;

    protected static Printer mPrinter = null;
    protected static Scanner mScanner = null;
    protected static Display mDisplay = null;

    private final String TAG = BaseFragment.class.getName();
    protected static boolean isPrinterCreated;
    protected static boolean isScannerCreated;
    protected static boolean isDisplayCreated;
    Bitmap bitmap = null;
    private static boolean isConnected = false;

    protected PeripheralManager peripheralManager;
    private final CharSequence[] items = {"Take Photo", "Choose from Library",
            "Cancel"};


    String date_of_birth_string;
    public void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    openCamera();
                } else if (options[item].equals("Choose from Gallery")){
                    openGallary();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void openCamera() {
        createFile();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(camera, CAPTURE_PHOTO);
    }

    public void openGallary() {
        file = null;
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, UiController.APP_NAME), SELECT_PHOTO);
        } else {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, UiController.APP_NAME), SELECT_PHOTO);
        }
    }

    private void createFile() {
        file = null;
        if (!UiController.getInstance().getCACHE_DIR().exists()) {
            UiController.getInstance().getCACHE_DIR().mkdirs();
        }
        file = new File(UiController.getInstance().getCACHE_DIR(), System.currentTimeMillis() + "-image.jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), e);
            }
        }
    }

    public File tempFile = null;
    public Uri getImageBitmapFromIntentData(Intent data) {
        ImageCompress image = new ImageCompress();
//        File tempFile = null;
        String tempFilePath = "";
        if (file == null || !file.exists()) {
            if (data != null && data.getData() != null)
                tempFilePath = image.compressImage(getActivity(), data.getData().toString());
        } else {
            tempFilePath = image.compressImage(getActivity(), file.getPath());

            try{
                file.delete();
            }catch (Exception e){
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), e);
            }
        }

        if (tempFilePath == null || tempFilePath.equals("")) {
            return null;
        }
        tempFile = new File(tempFilePath);

        if (tempFile == null) {
            Toast.makeText(getActivity(), "Error while reading Image File null", Toast.LENGTH_SHORT).show();
            return null;
        }
        return Uri.fromFile(tempFile);
    }

    Uri uri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            switch (requestCode) {
                case CAPTURE_PHOTO:
                case SELECT_PHOTO:
                    Intent captureImage = new Intent(getActivity(), ActivityImageCropping.class);
                    uri = getImageBitmapFromIntentData(data);
                    captureImage.putExtra(ActivityImageCropping.SELECTED_IMAGE_URI, uri);
                    captureImage.putExtra(ActivityImageCropping.RATIO_OF_CROPPING, CropImageView.CropMode.RATIO_FREE.getId());
                    captureImage.putExtra(ActivityImageCropping.MIN_FRAME_SIZE, 150);
                    startActivityForResult(captureImage, CROP_IMAGE);
                    break;

                case CROP_IMAGE:
//                    new File(uri.toString()).delete();
                    Bundle extras = data.getExtras();
//                    Bitmap bitmap = extras.getParcelable(ActivityImageCropping.RETURN_IMAGE_PATH);
                    try {
                        String filePath = extras.getString(ActivityImageCropping.RETURN_IMAGE_PATH);
                        Bitmap b = Utilities.getInstance().getBitmapFromPath(filePath);
                        setImageView(b, filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), e);
                    }
                    break;
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    int REQUEST_CROP_ICON = 100;

    public void showPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Your Option!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraClick();
                } else if (items[item].equals("Choose from Library")) {
                    galleryClick();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    void cameraClick() {
        // dialog.dismiss();
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        intent.putExtra("return-data", false);

        startActivityForResult(intent, Crop.REQUEST_PICK);
    }

    void galleryClick() {
        // dialog.dismiss();
        if (Build.VERSION.SDK_INT < 19) {
            try {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GALLERY_REQUEST);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), ex);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Crop.REQUEST_PICK);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = null;
//        UiController.mDevice.setDisconnectEventCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        peripheralManager = PeripheralManager.getInstance(getActivity());
        peripheralManager.setCallback(this);
        peripheralManager.connectPrinter(/*"192.168.10.168"*/new SavePreferences(getActivity()).get_ip());

        MultiplePrintManager.getMultiplePrinterManager().setCallback(this);

        if (peripheralManager.isConnected())
            ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_online);

//        if (!UiController.mDevice.isConnected())
//            searchAndConnect("192.168.10.168"/*new SavePreferences(getActivity()).get_ip()*/);
//
//        if (isConnected)
//            ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_online);
//        else
//            ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_offline);

    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }*/

    /*@Override
        public void onActivityResult(int requestCode, int resultCode, Intent result) {
            if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            }
        }
    */

    private void beginCrop(Intent data) {

        APP_THUMBNAIL_PATH_SD_CARD = FileUtils.getUniqueTimeValue();
        String fullPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + APP_PATH_SD_CARD
                + APP_THUMBNAIL_PATH_SD_CARD;

        if (data != null) {
            Crop.of(data.getData(), Uri.fromFile(new File(fullPath))).withMaxSize(100, 100).start(getActivity(), this);
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(mCapturedImageURI,
                    projection, null, null, null);
            int column_index_data = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index_data);
            cursor.close();
            Crop.of(Uri.fromFile(new File(path)), Uri.fromFile(new File(fullPath))).withMaxSize(100, 100).start(getActivity(), this);
        }

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            setImageView(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void setImageView(Uri uri) {

    }

    protected void setImageView(Bitmap bitmap, Intent data) {

    }

    protected void setImageView(Bitmap bitmap, String filePath) {

    }


    /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data != null) {
                mCapturedImageURI = data.getData();
            }

            if ((requestCode == GALLERY_REQUEST || requestCode == GALLERY_KITKAT_INTENT_CALLED)
                    && resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY_REQUEST) {
                    mCapturedImageURI = data != null ? data.getData() : null;
                    String[] column = {MediaStore.Images.Media.DATA};
                    assert mCapturedImageURI != null;
                    Cursor c = getActivity().getContentResolver().query(mCapturedImageURI,
                            column, null, null, null);
                    c.moveToFirst();
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    path = FileUtils.getPath(getActivity(), mCapturedImageURI);
                    c.close();
                    Log.d("Uri Got Path", "" + path);
                    performCrop(path);

                } else {

                    String path = FileUtils.getPath(getActivity(), mCapturedImageURI);
                    mCapturedImageURI = Uri.fromFile(new File(path));

                    performCrop(path);
                }
            } else if (requestCode == CAMERA_REQUEST
                    && resultCode == Activity.RESULT_OK) {
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(mCapturedImageURI,
                        projection, null, null, null);
                int column_index_data = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index_data);
                cursor.close();
                performCrop(path);

            }


        } catch (Exception e) {
            RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), e);
            e.printStackTrace();
        }
    }
*/
    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                null, null, null);

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } finally {
            cursor.close();
        }
    }

    private final String APP_PATH_SD_CARD = "/EPOS/";
    private String APP_THUMBNAIL_PATH_SD_CARD;

    public File saveImageToExternalStorage(Bitmap image) {
        if (null == image)
            return null;

        APP_THUMBNAIL_PATH_SD_CARD = FileUtils.getUniqueTimeValue();
        String fullPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + APP_PATH_SD_CARD
                + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut;
            File file = new File(fullPath, "desiredFilename.png");
            file.createNewFile();
            fOut = new FileOutputStream(file);

            // 100 means no compression, the lower you go, the stronger the
            // compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                    file.getAbsolutePath(), file.getName(), file.getName());

            return file;

        } catch (Exception e) {
            RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), e);
            Log.e("saveToExternalStorage()", e.getMessage());
            return null;
        }
    }

    /**
     * Perform Crop Operation after getting image from Gallery and Camera
     */
    private void performCrop(String path) {
        try {
            // Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // // indicate image type and Uri
            // cropIntent.setDataAndType(mCapturedImageURI, "image/*");
            // // set crop properties
            // cropIntent.putExtra("crop", "true");
            // // indicate aspect of desired crop
            // cropIntent.putExtra("aspectX", 1);
            // cropIntent.putExtra("aspectY", 1);
            // // indicate output X and Y
            // cropIntent.putExtra("outputX", 256);
            // cropIntent.putExtra("outputY", 256);
            // // retrieve data on return
            // cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
           /* Intent cropIntent = new Intent(getActivity(), CropImage.class);
            cropIntent.putExtra(CropImage.IMAGE_PATH, path);
            cropIntent.putExtra(CropImage.SCALE, true);*/

            APP_THUMBNAIL_PATH_SD_CARD = FileUtils.getUniqueTimeValue();
            String fullPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + APP_PATH_SD_CARD
                    + APP_THUMBNAIL_PATH_SD_CARD;

            Crop.of(Uri.fromFile(new File(path)), Uri.fromFile(new File(fullPath))).withMaxSize(50, 50).start(getActivity());
            /*cropIntent.putExtra(CropImage.ASPECT_X, 3);
            cropIntent.putExtra(CropImage.ASPECT_Y, 4);*/
            // startActivityForResult(cropIntent, CROP_REQUEST);
        } catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), anfe);
            Toast.makeText(getActivity(), "No Crop Feature Available", Toast.LENGTH_LONG).show();
        }
    }


    public String changeDateFormat(String current_date) {
        if (!TextUtils.isEmpty(current_date) && !current_date.contains("/") && current_date.contains("-"))
            try {
                //    String current_date = "2014-11-25";   "yyyy-mm-dd"
                System.out.println("Date is : " + current_date);
                String bdate = current_date.substring(0, current_date.indexOf("-"));
                String bmonth = current_date.substring(current_date.indexOf("-") + 1, current_date.lastIndexOf("-"));
                String byear = current_date.substring(current_date.lastIndexOf("-") + 1);

                String month_name = null;

                switch (Integer.parseInt(bmonth)) {
                    case 1:
                        month_name = "Jan";
                        break;
                    case 2:
                        month_name = "Feb";
                        break;
                    case 3:
                        month_name = "Mar";
                        break;
                    case 4:
                        month_name = "Apr";
                        break;
                    case 5:
                        month_name = "May";
                        break;
                    case 6:
                        month_name = "June";
                        break;
                    case 7:
                        month_name = "July";
                        break;
                    case 8:
                        month_name = "Aug";
                        break;
                    case 9:
                        month_name = "Sep";
                        break;
                    case 10:
                        month_name = "Oct";
                        break;
                    case 11:
                        month_name = "Nov";
                        break;
                    case 12:
                        month_name = "Dec";
                        break;

                    default:
                        break;

                }
                return byear + "-" + month_name + "-" + bdate;
            } catch (Exception e) {
                RetailPosLoging.getInstance().registerLog(BaseFragment.class.getName(), e);
                e.printStackTrace();
                return null;
            }
        else
            return null;
    }

//    @Override
//    public void onConnect(final String ipAddress, final int code) {
//
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    if (code == EposCallbackCode.SUCCESS) {
//                        Toast.makeText(getActivity(), "Device connected with printer: " + ipAddress, Toast.LENGTH_SHORT).show();
//                        isConnected = true;
//                        onPrinterConnected();
//                        ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_online);
//
//                    } else {
//                        ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_offline);
//                        Crouton.makeText(getActivity(), "Device is not connected with printer", Style.ALERT).show();
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public void onCreateDevice(String ipAddress, final String deviceId,
//                               final int deviceType, final Object deviceObject, final int code) {
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    if (code == EposCallbackCode.SUCCESS) {
//                        switch (deviceType) {
//                            case Device.DEV_TYPE_PRINTER:
//                                /*mPrinter = (Printer) deviceObject;
//                                mPrinter.setReceiveEventCallback(BaseFragment.this);*/
//                                EposDeviceClient.setPrinter((Printer) deviceObject);
//                                EposDeviceClient.getPrinter().setReceiveEventCallback(BaseFragment.this);
//                                isPrinterCreated = true;
//                                Log.i(TAG, "Printer Created: " + mPrinter);
//                                break;
//                            case Device.DEV_TYPE_SCANNER:
//                                /*mScanner = (Scanner) deviceObject;
//                                mScanner.setDataEventCallback(BaseFragment.this);*/
//                                EposDeviceClient.setScanner((Scanner) deviceObject);
//                                EposDeviceClient.getScanner().setDataEventCallback(BaseFragment.this);
//                                Toast.makeText(getActivity(), "Device Connected", Toast.LENGTH_SHORT).show();
//                                isScannerCreated = true;
//                                Log.i(TAG, "Scanner Created: " + mScanner);
//                                break;
//                            case Device.DEV_TYPE_DISPLAY:
//                                //mDisplay = (Display) deviceObject;
//                                EposDeviceClient.setDisplay((Display) deviceObject);
//                                isDisplayCreated = true;
//                                Log.i(TAG, "Display Created: " + mScanner);
//                                break;
//                            default:
//                                break;
//                        }
//                    } else {
//                        Log.i(TAG, "Device not created properly");
//                        Toast.makeText(getActivity(), "Device not connected properly", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public void onScanData(String ipAddress, final String deviceId, final String input) {
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    if (deviceId.equals(new SavePreferences(getActivity()).getBarcodeDeviceName()))
//                        onScanDataReceive(input, DeviceType.DEV_TYPE_BARCODE);
//                    else if (deviceId.equals(new SavePreferences(getActivity()).getMSRDeviceName()))
//                        onScanDataReceive(input, DeviceType.DEV_TYPE_MSR);
//                }
//            });
//        }
//    }

//    @Override
//    public void onDeleteDevice(String ipAddress, final String deviceId, final int code) {
//        if (code == EposCallbackCode.SUCCESS) {
//            mPrinter = null;
//            mScanner = null;
//        }
//    }

//    @Override
//    public void onPtrReceive(String ipAddress, String deviceId, final int success,
//                             final int code, final int status, final int battery) {
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    if (success == Printer.TRUE) {
//                        Log.i(TAG, "Printer true");
//
//                    } else {
//                        Log.i(TAG, "Printer event not received");
//                    }
//                }
//            });
//        }
//    }

//    protected void onScanDataReceive(String scanData, DeviceType type) {
//        Log.i(TAG, "Scanned Data is: " + scanData);
//    }

//    private void searchAndConnect(String ipAddress) {
//        if (!isConnected) {
//            try {
//                UiController.mDevice.connect(ipAddress, this);
//            } catch (EposException e) {
//                Toast.makeText(getActivity(), "Device is not connected with printer", Toast.LENGTH_LONG).show();
//            }
//        } else
//            Toast.makeText(getActivity(), "Already Connected", Toast.LENGTH_LONG).show();
//
//    }


    @Override
    public void onBarcodeScanData(String data) {

    }

    @Override
    public void onMSRScanData(String data) {

    }

    @Override
    public void onDisplayReadData(String data) {

    }

    @Override
    public void onConnectPrinter() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_online);
//                if (code == EposCallbackCode.SUCCESS) {
////                    Toast.makeText(getActivity(), "Device connected with printer: " + ipAddress, Toast.LENGTH_SHORT).show();
////                    isConnected = true;
////                    onPrinterConnected();
//
//                } else {
//                    ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_offline);
////                    Crouton.makeText(getActivity(), "Device is not connected with printer", Style.ALERT).show();
//                }
                }
            });
        }


    }

    @Override
    public void onDisconnectPrinter() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    if (getActivity() != null)
                        ((Home) getActivity()).mStatus.setImageResource(R.drawable.ic_offline);
                }
            });

        }

    }

    @Override
    public void onPrinterStatusReceived(String message, int code, int status) {

    }

    @Override
    public void onStop() {
        super.onStop();

        if(peripheralManager != null) {
            peripheralManager.deleteDevices();
        }
    }
}
