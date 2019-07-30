package com.printer.epos.rtpl;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.epson.eposdevice.Device;
import com.epson.eposdevice.EposException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.util.TypefaceUtil;

import java.io.File;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ranosys-puneet on 11/3/15.
 */
public class UiController extends Application {

    public static final String APP_NAME = ".Epson";
    //    public static String appUrl = "http://ranosys.net/client/epson/api/web/v1/";
    //public static String appString = "/client/epson/api/web/v1/";
    public static String appString = "/pos/api/web/v1/";
    //    public static String appString = "/epsonpos/api/web/v1/";
    public static String appUrl = "";
    /**
     * Device object
     */
    public static Device mDevice = null;

    public static String sCurrency = null;

    public static String ACCESS_TOKEN = null;

    public AQuery aQuery;

    public SavePreferences getSavePreferences() {
        return mSavePreferences;
    }

    public static SavePreferences mSavePreferences;

    public static UiController getInstance() {
        return mInstance;
    }

    public static UiController mInstance;

    private ImageLoader mImageLoader;

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        ROOT_DIR = new File(Environment.getExternalStorageDirectory(), APP_NAME);
        CACHE_DIR = new File(ROOT_DIR, "Images");

        RetailPosLoging.DEBUG = true;

        mInstance = this;
        aQuery = new AQuery(getApplicationContext());
        mSavePreferences = new SavePreferences(this);

        context = getApplicationContext();

        sCurrency = mSavePreferences.getCurrencyName();
        ACCESS_TOKEN = mSavePreferences.get_accessToken();

        initializeDevice();

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Roboto-Regular.ttf");

        initImageLoader(getApplicationContext());

    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.MIN_PRIORITY)
                .denyCacheImageMultipleSizesInMemory().threadPoolSize(4)
                .memoryCacheSize(1 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)// Remove for //
                        // release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * This method is used to get instanse of AQuery
     *
     * @return

    public AQuery aQuery;



    public AQuery aQuery;


     */
    public AQuery getAQuery() {
        return aQuery;
    }

    /**
     * Initialize device
     *
     * @return : Propriety
     */
    private void initializeDevice() {
        // The instance of a Device class is initialized.
        try {
            mDevice = new Device(getApplicationContext());
        } catch (EposException e) {
            mDevice = null;
            RetailPosLoging.getInstance().registerLog(UiController.class.getName(), e);
        }

    }

    File ROOT_DIR;
    File CACHE_DIR;
    public File getCACHE_DIR() {
        return CACHE_DIR;
    }


}
