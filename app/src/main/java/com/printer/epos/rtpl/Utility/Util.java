package com.printer.epos.rtpl.Utility;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import org.apache.commons.lang3.CharEncoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static List<PrinterDetails> printerDetailsList = new ArrayList<PrinterDetails>();

    public static Map<String, String> getSettingsItems(Context context) {
        Map<String, String> map = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        map.put(context.getString(R.string.setting_title_first), context.getString(R.string.setting_description_first));
        map.put(context.getString(R.string.setting_title_second), context.getString(R.string.setting_description_second));
        map.put(context.getString(R.string.setting_title_third), context.getString(R.string.setting_description_third));
        map.put(context.getString(R.string.setting_title_fourth), context.getString(R.string.setting_description_fourth));
        map.put(context.getString(R.string.setting_title_fifth), context.getString(R.string.setting_description_fifth));
        map.put(context.getString(R.string.setting_title_six), context.getString(R.string.setting_description_six));
        map.put(context.getString(R.string.setting_title_seven), context.getString(R.string.setting_description_seven));
        map.put(context.getString(R.string.setting_title_eight), context.getString(R.string.setting_description_eight));

        return map;
    }

    public static ArrayList<String> getReportsItems(Context context) {
        ArrayList<String> arrList = new ArrayList<String>();
        arrList.add(context.getString(R.string.reports_title_first));
        arrList.add(context.getString(R.string.reports_title_second));
        arrList.add(context.getString(R.string.reports_title_third));
        arrList.add(context.getString(R.string.reports_title_fourth));
        arrList.add(context.getString(R.string.reports_title_fifth));
        arrList.add(context.getString(R.string.reports_title_sixth));

        return arrList;
    }

    public String getUniqueTimeValue() {
        long currentDateTime = System.currentTimeMillis();
        Date currentDate = new Date(currentDateTime);
        SimpleDateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
        return df.format(currentDate);
    }

    public static void changeViewState(ViewGroup viewGroup, String value) {
        int count = viewGroup.getChildCount();
        if (count > 0) {
            for (int index = 0; index < count; index++) {
                View v = viewGroup.getChildAt(index);
                if (v instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) v;
                    String id = (String) checkBox.getTag();
                    if (id.equals(value)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                }
                if (v instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) v;
                    String id = (String) radioButton.getTag();
                    if (id.equals(value)) {
                        radioButton.setChecked(true);
                    } else {
                        radioButton.setChecked(false);
                    }
                }
                if (v instanceof Button) {
                    Button button = (Button) v;
                    String id = (String) button.getTag();
                    if (id.equals(value)) {
                        button.setSelected(true);
                    } else {
                        button.setSelected(false);
                    }
                }
            }
        }
    }

    public static void SetViewState(ViewGroup viewGroup, String value) {
        int count = viewGroup.getChildCount();
        if (count > 0) {
            for (int index = 0; index < count; index++) {
                View v = viewGroup.getChildAt(index);
                if (v instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) v;
                    String id = (String) checkBox.getTag();
                    if (id.equals(value)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                }
                if (v instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) v;
                    String id = (String) radioButton.getTag();
                    if (id.equals(value)) {
                        radioButton.setChecked(true);
                    } else {
                        radioButton.setChecked(false);
                    }
                }
                if (v instanceof Button) {
                    Button button = (Button) v;
                    String id = (String) button.getTag();
                    if (id.equals(value)) {
                        button.setSelected(true);
                    } else {
                        button.setSelected(false);
                    }
                }
            }
        }
    }

    /**
     * Method to get Extra small font size
     */
    public static int getAreaButtonTextSize(Context context, Typeface typeface) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        // int deviceWidth = displayMetrics.widthPixels;
        int deviceWidth = displayMetrics.widthPixels;

        return determineTextSize(typeface, (int) (0.23f * (.14f * deviceWidth)));

    }

    public static float calculateHeight(Paint.FontMetrics fm) {
        return fm.bottom - fm.top;
    }

    // determineTextSize method
    public static int determineTextSize(Typeface font, float allowableHeight) {

        Paint p = new Paint();
        p.setTypeface(font);

        int size = (int) allowableHeight;
        p.setTextSize(size);

        float currentHeight = calculateHeight(p.getFontMetrics());

        while (size != 0 && (currentHeight) > allowableHeight) {
            p.setTextSize(size--);
            currentHeight = calculateHeight(p.getFontMetrics());
        }

        if (size == 0) {

            return (int) allowableHeight;
        }

        return size;
    }

    public static boolean isDeviceOnline(Context context) {

        boolean isConnectionAvail = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo.isConnected();
        } catch (Exception e) {
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
        }
        return isConnectionAvail;
    }

    /**
     * To hide the soft key pad if open
     */
    public static void hideSoftKeypad(Context context) {
        Activity activity = (Activity) context;
        if(activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static InputStream getResponseAsInputStream(String request) {
        InputStream stream = null;

        try {
            stream = new ByteArrayInputStream(request.getBytes(CharEncoding.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
        }
        return stream;
    }

    public String encodeTobase64(Drawable image) {
        if (null != image) {
            Bitmap immagex = drawableToBitmap(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("LOOK", imageEncoded);
            return imageEncoded;
        } else
            return null;
    }

    public static String getPrintLogoEncodedImage(SavePreferences prefs, Context context)
    {
        File image = new File(prefs.getReceiptLogoFilePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        return new Util().encodeTobase64(drawable);
    }

    public static Bitmap getPrintLogoBitmap(Context context)
    {
        File image = new File(new SavePreferences(context).getReceiptLogoFilePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        // Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        Bitmap originalBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return returnRatioBitmap(originalBitmap, 512f);
    }

    private static Bitmap returnRatioBitmap(Bitmap originalBitmap, float maxWidth){
        if(originalBitmap != null) {
            if (originalBitmap.getWidth() <= maxWidth) {
                return originalBitmap;
            } else {
                float ratio = originalBitmap.getWidth() / maxWidth;
                return Bitmap.createScaledBitmap(originalBitmap, (int) Math.ceil(originalBitmap.getWidth() / ratio), (int) Math.ceil(originalBitmap.getHeight() / ratio), true);
            }
        }else{
            return null;
        }
    }

    public static String getCouponEncodedImage(SavePreferences prefs, Context context)
    {
        File image = new File(prefs.getReceiptCouponFilePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        return  new Util().encodeTobase64(drawable);
    }

    public static Bitmap getCouponBitmap(Context context)
    {
        File image = new File(new SavePreferences(context).getReceiptCouponFilePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap originalBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return returnRatioBitmap(originalBitmap, 512f);
    }


    public String encodeBitmapTobase64(Bitmap immagex) {
        if (null != immagex) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("LOOK", imageEncoded);
            return imageEncoded;
        } else
            return null;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {

        if (null != drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else
            return null;
    }

    public static void saveBitmapToFile(Context context,Bitmap bitmap,String fileName,int imageType)
    {
        if(bitmap != null){
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/RetailPos";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, fileName);
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);

                if(imageType == 0)
                    new SavePreferences(context).storeReceiptLogoFilePath(file.getAbsolutePath());
                else
                    new SavePreferences(context).storeReceiptCouponFilePath(file.getAbsolutePath());

                fOut.flush();
                fOut.close();
            } catch (FileNotFoundException e) {
                Log.d(Util.class.getName(),"Error is: "+e.toString());
                RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(Util.class.getName(),"Error is: "+e.toString());
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);

            }
        }

    }

    public static Bitmap getBitmap(String url)
    {
        URL logoUrl = null;
        Bitmap bitmap = null;
        try {
            logoUrl = new URL(url);
            bitmap = BitmapFactory.decodeStream(logoUrl.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.d(Util.class.getName(), "Error is: " + e.toString());
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            return null;

        }
        catch (FileNotFoundException e)
        {
            Log.d(Util.class.getName(),"Error is: "+e.toString());
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            return null;
        }
        catch (IOException e) {
            Log.d(Util.class.getName(), "Error is: " + e.toString());
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            return null;

        }

        return bitmap;

    }

    static boolean isCancel = false;

    public static void datePickerDialog(Context context, final EditText et) {

        Calendar calendar = Calendar.getInstance();
        if(!TextUtils.isEmpty(et.getText().toString())){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = df.parse(et.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            }

        }

         DatePickerDialog dateDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String temp = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;

                        et.setText(temp);
                    }

                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


        dateDialog.show();
        dateDialog.setCancelable(false);

    }

    public static void datePickerDialog(Context context, final EditText et, int setMaxDate) {
        Calendar calendar = Calendar.getInstance();
        if(!TextUtils.isEmpty(et.getText().toString())){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = df.parse(et.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            }

        }
        DatePickerDialog dateDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String temp = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;

                        et.setText(temp);


                    }

                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 0);
        dateDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        dateDialog.show();
        dateDialog.setCancelable(false);

    }

    public static String parseDate(String date) {
        String dateStr[] = date.split("\\s+");
        return dateStr[0];
    }

    public static boolean isDateAfter(String startDate, String endDate, EditText et) {
        try {
            String myFormatString = "yyyy-M-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.compareTo(startingDate) == 0 || date1.compareTo(startingDate) > 0)
                return true;
            else {
                et.setError("End date should be equal or greater then Start Date");
                return false;
            }
        } catch (Exception e) {
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            et.setError("End date should be equal or greater then Start Date");
            return false;
        }
    }

    public static boolean isDateAfter(String startDate, String endDate, Context context) {
        try {
            String myFormatString = "yyyy-M-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.compareTo(startingDate) == 0 || date1.compareTo(startingDate) > 0)
                return true;
            else {
                new CustomDialog().showOneButtonAlertDialog(context, null,
                        context.getResources().getString(R.string.date_validation_string), context.getResources().getString(R.string.ok_button), android.R.drawable.ic_dialog_alert, null);
                return false;
            }
        } catch (Exception e) {
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
            new CustomDialog().showOneButtonAlertDialog(context, null,
                    context.getResources().getString(R.string.date_validation_string), context.getResources().getString(R.string.ok_button), android.R.drawable.ic_dialog_alert, null);

            return false;
        }
    }


    public static boolean checkInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static String convertTolocalTime(String time)
    {
        if(time.equals("00:00"))
            return "Logged in";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        outputFormat.setTimeZone(TimeZone.getDefault());
        // Adjust locale and zone appropriately
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
        }
        return outputFormat.format(date);
    }

    public static String convertTolocalDate(String time)
    {
        if(time.equals("00:00"))
            return time;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        outputFormat.setTimeZone(TimeZone.getDefault());
        // Adjust locale and zone appropriately
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
        }
        return outputFormat.format(date);
    }


    public static String getCurrentTimeStamp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static String changeDateFormatWithTime(String date)  {

        Date initDate = null;
        String parsedDate = "";
        try {
            initDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            parsedDate = formatter.format(initDate);
            String[] time = getCurrentTimeStamp().split(" ");
            parsedDate = parsedDate +" "+ time[1];

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }




    public static String priceFormat(double value) {

        if (value == 0.00 || value == 0.0 || value == 0)
            return "0.00";
        DecimalFormat formatter = new DecimalFormat("#,###0.00");
        String format = formatter.format(value);

        if (format.equals(".00"))
            return "0.00";

        return format;
        //return String.format("%.2f", value);
    }

    public static String priceFormat(String value) {
        double amount = 0;
        if (value == null || TextUtils.isEmpty(value))
            return "0.00";

        try {
            amount = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
        }

        if (amount == 0.00 || amount == 0.0 || amount == 0 || amount == .00)
            return "0.00";
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String format = formatter.format(amount);

        if (format.equals(".00"))
            return "0.00";

        return format;
        /*return String.format("%.2f", value);*/
    }
    public static String priceFormatForServer(double value) {

        if (value == 0.00 || value == 0.0 || value == 0)
            return "0.00";
        DecimalFormat formatter = new DecimalFormat("####.00");
        String format = formatter.format(value);

        if (format.equals(".00"))
            return "0.00";

        return format;
        //return String.format("%.2f", value);
    }

    public static String priceFormatForServer(String value) {
        double amount = 0;
        if (value == null || TextUtils.isEmpty(value))
            return "0.00";

        try {
            amount = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Util.class.getName(), e);
        }

        if (amount == 0.00 || amount == 0.0 || amount == 0 || amount == .00)
            return "0.00";
        DecimalFormat formatter = new DecimalFormat("####.00");
        String format = formatter.format(amount);

        if (format.equals(".00"))
            return "0.00";

        return format;
        /*return String.format("%.2f", value);*/
    }

    public static String price(double value) {
        return String.format("%.2f", value);
    }

    public static String priceFormatTax(double value)
    {
        return new DecimalFormat("0.00").format(value);
    }

    public static boolean isValidPrice(EditText input_edit_text) {
        String value = input_edit_text.getText().toString();
        if (TextUtils.isEmpty(value)) {
            input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_price));
            return false;
        } else {
            //Pattern pattern = Pattern.compile("^\\d{0,9}(\\d\\.\\d?|\\.\\d)?\\d?$");
            Pattern pattern = Pattern.compile("^\\d{0,}(?:\\.\\d{1,2})?$");

            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                input_edit_text.setError(UiController.getInstance().getResources().getString(R.string.required_valid_price));
                return false;
            }

        }
        return true;
    }

    public static double roundUpToTwoDecimal(double value){
        double roundValue = 0.00;
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}
