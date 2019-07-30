package com.printer.epos.rtpl.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;

import com.printer.epos.rtpl.RetailPosLoging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Shashi on 31/12/15.
 */
public class Utilities {

    private static Utilities utils;

    public static Utilities getInstance() {
        if (utils == null)
            utils = new Utilities();
        return utils;
    }


    public Bitmap getBitmapFromPath(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        return BitmapFactory.decodeStream(new FileInputStream(f));
    }

    /**
     * Method use to get density pixel from int value.
     *
     * @param context       context of current activity.
     * @param selectedImage is Uri of the image, which you want BitMap.
     * @return converted value in Bitmap from URI
     */
    public Bitmap getBitmapFromUri(Context context, Uri selectedImage) {
        InputStream imageStream = null;
        Bitmap photo = null;
        try {
            imageStream = context.getContentResolver().openInputStream(selectedImage);
            photo = BitmapFactory.decodeStream(imageStream);
        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Utilities.class.getName(), e);
        }
        return photo;
    }

    /**
     * Method use to get density pixel from int value.
     *
     * @param value   to which you want it to convert into DP.
     * @param context context of current activity.
     * @return converted value in DP
     */
    public int getValueInDP(int value, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
    }


    public Bitmap getCompressedBitmap(Bitmap originalBitmap, float maxWidth, float maxHeight) {
        Bitmap compressedBitmap = null;
        int actualHeight = originalBitmap.getHeight();
        int actualWidth = originalBitmap.getWidth();
//      max Height and width values of the compressed image is taken as 816x612
//        float maxHeight = 816.0f;
//        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        try {
            if (actualWidth <= 0 || actualHeight <= 0) {
                return null;
            }
            compressedBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            return null;
        }

        float ratioX = actualWidth / (float) originalBitmap.getWidth();
        float ratioY = actualHeight / (float) originalBitmap.getHeight();
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(compressedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(originalBitmap, middleX - originalBitmap.getWidth() / 2, middleY - originalBitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        compressedBitmap = Bitmap.createBitmap(compressedBitmap, 0, 0, compressedBitmap.getWidth(), compressedBitmap.getHeight(), scaleMatrix, true);
        return compressedBitmap;
    }

    public String saveImage(Context context, Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("cacheImage", context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, System.currentTimeMillis() + ".png");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(Utilities.class.getName(), e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(Utilities.class.getName(), e);
            }
        }
        return mypath.getAbsolutePath();
    }


    @SuppressLint("NewApi")
    public String getRealPathFromURI(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getImageUrlWithAuthority(context, uri);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else {
            try {
                String filePath = "";
                Cursor cursor;
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion < 21) {
                    cursor = context.getContentResolver().query(uri, null, null, null, null);
                    if (cursor == null) {
                        filePath = uri.getPath();
                    } else {
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        filePath = cursor.getString(index);
                    }
                } else {
                    String wholeID = DocumentsContract.getDocumentId(uri);
                    String[] splited = wholeID.split(":");
                    String id = "";
                    if (splited.length == 2)
                        id = wholeID.split(":")[1];
                    else
                        id = wholeID.split(":")[0];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    cursor = context.getContentResolver().
                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(columnIndex);
                    }
                }
                if (cursor != null && !cursor.isClosed())
                    cursor.close();
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(Utilities.class.getName(), e);
                return null;
            }
        }

        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
//                return writeToTempImageAndGetPathUri(context, bmp).toString();
                return saveImage(context, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(Utilities.class.getName(), e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    RetailPosLoging.getInstance().registerLog(Utilities.class.getName(), e);
                }
            }
        }
        return null;
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
}
