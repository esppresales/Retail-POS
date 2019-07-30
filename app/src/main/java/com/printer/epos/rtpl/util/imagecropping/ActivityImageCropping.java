package com.printer.epos.rtpl.util.imagecropping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.util.Utilities;


/**
 * {@link ActivityImageCropping} send an e-mail with some debug information
 * to the developer.
 *
 * @author Shashi
 */
public class ActivityImageCropping extends AppCompatActivity {

    private Uri selectedImageUri;
    private int ratio;
    CropImageView cropImageView;
    int minFrameSize;
    public static final String RETURN_IMAGE_PATH = "RETURN_IMAGE_PATH";
    public static final String SELECTED_IMAGE_URI = "SELECTED_IMAGE_URI";
    public static final String RATIO_OF_CROPPING = "RATIO_OF_CROPPING";
    public static final String MIN_FRAME_SIZE = "MIN_FRAME_SIZE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_cropping_activity);
        Bundle bundle = getIntent().getExtras();


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        LinearLayout header = (LinearLayout) findViewById(R.id.header);
        header.getLayoutParams().height = (int) (deviceHeight * .1f);

        if (bundle != null) {
            selectedImageUri = bundle.getParcelable(SELECTED_IMAGE_URI);
            ratio = bundle.getInt(RATIO_OF_CROPPING);
            minFrameSize = bundle.getInt(MIN_FRAME_SIZE, 120);
        }

        if (selectedImageUri == null) {
            finish();
        }

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        Bitmap photo = Utilities.getInstance().getBitmapFromUri(this, selectedImageUri);
        if (photo == null) {
            Toast.makeText(this, "Getting error while reading image", Toast.LENGTH_SHORT).show();
            finish();
        }

        cropImageView.setImageBitmap(photo);

        switch (ratio) {
            case 0:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_FIT_IMAGE);
                break;
            case 1:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                break;
            case 2:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                break;
            case 3:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
                break;
            case 4:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                break;
            case 5:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                break;
            case 6:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
                break;
            case 7:
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_CUSTOM);
                break;
            case 8:
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
                break;
        }
        cropImageView.setMinFrameSizeInPx(Utilities.getInstance().getValueInDP(minFrameSize, this));

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataOnFinish(null);
            }
        });

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataOnFinish(cropImageView.getCroppedBitmap());
            }
        });
    }

//    MenuItem action_cancel, action_done,
//            action_ratio_fit_image, action_ratio_4_3,
//            action_ratio_3_4, action_ratio_1_1,
//            action_ratio_16_9, action_ratio_9_16,
//            action_ratio_free, action_circle;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.cropping_menu, menu);
//        action_ratio_fit_image = menu.findItem(R.id.action_ratio_fit_image);
//        action_ratio_4_3 = menu.findItem(R.id.action_ratio_4_3);
//        action_ratio_3_4 = menu.findItem(R.id.action_ratio_3_4);
//        action_ratio_1_1 = menu.findItem(R.id.action_ratio_1_1);
//        action_ratio_16_9 = menu.findItem(R.id.action_ratio_16_9);
//        action_ratio_9_16 = menu.findItem(R.id.action_ratio_9_16);
//        action_ratio_free = menu.findItem(R.id.action_ratio_free);
//        action_circle = menu.findItem(R.id.action_circle);
//        action_cancel = menu.findItem(R.id.action_cancel);
//        action_done = menu.findItem(R.id.action_done);
//        if (ratio > 0) {
//            action_ratio_fit_image.setVisible(false);
//            action_ratio_4_3.setVisible(false);
//            action_ratio_3_4.setVisible(false);
//            action_ratio_1_1.setVisible(false);
//            action_ratio_16_9.setVisible(false);
//            action_ratio_9_16.setVisible(false);
//            action_ratio_free.setVisible(false);
//            action_circle.setVisible(false);
//        }
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == action_cancel.getItemId())
//            setDataOnFinish(null);
//        else if (item.getItemId() == action_done.getItemId())
//            setDataOnFinish(cropImageView.getCroppedBitmap());
//        else if (item.getItemId() == action_ratio_fit_image.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_FIT_IMAGE);
//        else if (item.getItemId() == action_ratio_4_3.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
//        else if (item.getItemId() == action_ratio_3_4.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
//        else if (item.getItemId() == action_ratio_1_1.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
//        else if (item.getItemId() == action_ratio_16_9.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
//        else if (item.getItemId() == action_ratio_9_16.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16);
//        else if (item.getItemId() == action_ratio_free.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
//        else if (item.getItemId() == action_circle.getItemId())
//            cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
//        return super.onOptionsItemSelected(item);
//    }

    private void setDataOnFinish(Bitmap originalBitmap) {
        if (originalBitmap != null) {
            Bitmap bitmap = Utilities.getInstance().getCompressedBitmap(originalBitmap, 1080f * 2, 720f * 2);
            Intent intent = new Intent();
            intent.putExtra(RETURN_IMAGE_PATH, Utilities.getInstance().saveImage(this, bitmap));
            setResult(RESULT_OK, intent);
        }
        onBackPressed();
    }
}
