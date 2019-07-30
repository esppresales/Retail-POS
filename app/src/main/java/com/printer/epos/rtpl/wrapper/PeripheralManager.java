package com.printer.epos.rtpl.wrapper;

import android.content.Context;
import android.util.Log;

import com.epson.eposdevice.ConnectListener;
import com.epson.eposdevice.CreateDeviceListener;
import com.epson.eposdevice.DeleteDeviceListener;
import com.epson.eposdevice.Device;
import com.epson.eposdevice.DisconnectListener;
import com.epson.eposdevice.EposCallbackCode;
import com.epson.eposdevice.EposException;
import com.epson.eposdevice.display.Display;
import com.epson.eposdevice.printer.Printer;
import com.epson.eposdevice.printer.ReceiveListener;
import com.epson.eposdevice.scanner.DataListener;
import com.epson.eposdevice.scanner.Scanner;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;

/**
 * Created by ashishasolanki on 18/05/15.
 */


public class PeripheralManager implements ConnectListener, CreateDeviceListener,
        DeleteDeviceListener, ReceiveListener, DataListener, DisconnectListener {

    protected Device mDevice;
    private Context mContext;


    private Printer mPrinter;
    private Scanner mBarcodeScanner;
    private Scanner mMsrScanner;
    private Display mDisplay;


    private String barcodeDeviceId;
    private String msrDeviceId;
    private String displayDeviceId;
    private String printerDeviceId;

    /** Max line of display */
    private final int DISPLAY_MAX_CHARS = 20;


    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    private boolean connected;

    private PeripheralCallback callback;


    private static PeripheralManager instance = null;

    protected PeripheralManager(Context context) {
        // Exists only to defeat instantiation.
        mContext = context;
        createDevice();
    }

    public static PeripheralManager getInstance(Context context) {
        if (instance == null) {
            instance = new PeripheralManager(context);
        }
        return instance;
    }

//    public PeripheralManager(Context context) {
//    }


    private void createDevice() {
        try {
            mDevice = new Device(mContext);
        } catch (EposException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
        }
    }


    public void connectPrinter(String ipAddress) {

        if (mDevice != null && !mDevice.isConnected()) {
            try {
                mDevice.disconnect();
            }
            catch (Exception e){
                //Just tried to disconnect, exception may occur
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
            try {
                mDevice.connect(ipAddress, this);
                mDevice.setDisconnectEventCallback(this);
            } catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);

            }
        } /*else {
            createDevice();
        }*/
    }


    public void displayAlignedText(String item, Double value) {
        String valueInString = Util.priceFormat(value);
        displayAlignedText(item, valueInString);
    }

    public void displayAlignedText(String item, String value) {
        if(mDisplay != null)
        {
            int valueStrLen = value.length();
            int cursorPos = DISPLAY_MAX_CHARS - valueStrLen;
            if (1 > cursorPos) {
                cursorPos = 1;
            }
            mDisplay.clearCommandBuffer();
            try {
                // clear window
                mDisplay.clearWindow();

                // add item
                mDisplay.setCursorPosition(1, 1);
                if (DISPLAY_MAX_CHARS < item.length()) {
                    mDisplay.addText(item.substring(0, DISPLAY_MAX_CHARS));
                } else {
                    mDisplay.addText(item);
                }

                // add value
                mDisplay.setCursorPosition(cursorPos, 1);
                mDisplay.addText(" ");
                mDisplay.addText(value);
                mDisplay.setCursorType(Display.CURSOR_NONE);

                mDisplay.sendData();
            } catch (EposException e) {
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
                mDisplay.clearCommandBuffer();
            }
            mDisplay.clearCommandBuffer();
        }

    }


    /**
     * Display is reset.
     */
    public void resetDisplay() {

        if(mDisplay != null){

            final int number = 1;
            final int x = 1;
            final int y = 1;
            final int width = 20;
            final int height = 2;

            try {
                mDisplay.reset();
                mDisplay.createWindow(number, x, y, width, height, Display.SCROLL_OVERWRITE);
                mDisplay.setCurrentWindow(1);
                mDisplay.setCursorType(Display.CURSOR_NONE);
                mDisplay.sendData();
            } catch (EposException e) {
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
            mDisplay.clearCommandBuffer();
        }

    }


    public void displayShopName(String shopName)
    {
        if (mDisplay != null) {
            try {
                mDisplay.clearWindow();
                mDisplay.addMarquee(shopName, Display.MARQUEE_PLACE, 100, 1000, 0, Display.LANG_EN);
                mDisplay.setCursorType(Display.CURSOR_NONE);
                mDisplay.sendData();
            } catch (EposException e) {
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            } finally {
                mDisplay.clearCommandBuffer();
            }

        }
    }


    public void connectDevice(DeviceType type, String deviceID) {

        switch (type) {
            case BARCODE_SCANNER:
                connectBarcodeScanner(deviceID);
                break;
            case DISPLAY:
                connectDisplay(deviceID);
                break;
            case KEYBOARD:
                break;
            case MSR:
                connectMSR(deviceID);
                break;
            case PRINTER:
                createPrinter(deviceID);
                break;
            default:
                break;
        }

    }

    public void openCashDrawer() {
        if (mPrinter != null) {
            try {
                mPrinter.addPulse(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
                mPrinter.sendData();
                mPrinter.clearCommandBuffer();
            } catch (EposException e) {
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);

            }

        }

    }

    private void createPrinter(String deviceID) {
        printerDeviceId = deviceID;
        if (mPrinter == null && mDevice != null && mDevice.isConnected()) {
            try {
                mDevice.createDevice(deviceID, Device.DEV_TYPE_PRINTER, Device.FALSE, Device.FALSE, this);
            } catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }

    }

    private void connectBarcodeScanner(String deviceID) {
        barcodeDeviceId = deviceID;
        mBarcodeScanner = null;
        if (mDevice != null) {
            try {
                mDevice.createDevice(deviceID, Device.DEV_TYPE_SCANNER, Device.FALSE, Device.FALSE, this);
            } catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }
    }

    private void connectMSR(String deviceID) {
        msrDeviceId = deviceID;
        if (mMsrScanner == null && mDevice != null) {
            try {
                mDevice.createDevice(deviceID, Device.DEV_TYPE_SCANNER, Device.FALSE, Device.FALSE, this);
            } catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }
    }

    private void connectDisplay(String deviceID) {
        displayDeviceId = deviceID;
        if (mDisplay == null && mDevice != null) {
            try {
                mDevice.createDevice(deviceID, Device.DEV_TYPE_DISPLAY, Device.FALSE, Device.FALSE, this);
            } catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }
    }


    @Override
    public void onConnect(String s, int i) {
        if (i == EposCallbackCode.SUCCESS) {
            setConnected(true);
            if (callback != null) {
                callback.onConnectPrinter();
            }
        } else {
            setConnected(false);
            if (callback != null) {
                callback.onDisconnectPrinter();
            }

        }
    }

    @Override
    public void onDisconnect(String s) {
        setConnected(false);
        if (callback != null) {
            callback.onDisconnectPrinter();
        }

    }


    @Override
    public void onCreateDevice(String ipAddress, String deviceID, int deviceType,
                               Object deviceObject,final int code) {
        if (code == EposCallbackCode.SUCCESS) {
            switch (deviceType) {
                case Device.DEV_TYPE_PRINTER:
                    mPrinter = (Printer) deviceObject;
                    mPrinter.setReceiveEventCallback(this);
                    break;
                case Device.DEV_TYPE_SCANNER:
                    if(deviceID.equals(barcodeDeviceId)){
                        mBarcodeScanner = (Scanner) deviceObject;
                        mBarcodeScanner.setDataEventCallback(this);
                    }
                    else if(deviceID.equals(msrDeviceId))
                    {
                        mMsrScanner = (Scanner) deviceObject;
                        mMsrScanner.setDataEventCallback(this);
                    }

                    break;
                case Device.DEV_TYPE_DISPLAY:
                    mDisplay = (Display)deviceObject;
                    SavePreferences mSavePreferences = UiController.getInstance().getSavePreferences();;
                    this.displayShopName(mSavePreferences.getReceiptName());
                    break;
                default:
                    break;
            }
        } else {
            Log.d(PeripheralManager.class.getName(),"Error in create device, error code is :"+code);
        }
    }

    @Override
    public void onScanData(String ipAddress, String deviceId, String input) {

        if (barcodeDeviceId != null &&
                deviceId.trim().toLowerCase().equals(barcodeDeviceId.trim().toLowerCase())) {
            if (callback != null) {
                callback.onBarcodeScanData(input);
            }

        } else if (msrDeviceId != null && deviceId.trim().toLowerCase().equals(msrDeviceId.trim().toLowerCase())) {
            callback.onMSRScanData(input);

        }

    }

    @Override
    public void onDeleteDevice(String s, String s1, int i) {
        //device deleted
    }

    @Override
    public void onPtrReceive(String ipAddress, String deviceId,
                             int success, int code,int status,
                             int battery) {

        String message = "";
        switch(code)
        {
            case EposCallbackCode.ERR_AUTOMATICAL:
                message = "An automatically recoverable error occurred, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_BATTERY_LOW:
                message = "No remaining battery, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_COVER_OPEN:
                message = "A cover open error occurred, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_CUTTER:
                message = "An autocutter error occurred, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_MECHANICAL:
                message = "A mechanical error occurred, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_EMPTY:
                message = "No roll paper, detected. Please retry after inserting new roll";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_UNRECOVERABLE:
                message = "An unrecoverable error occurred, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_FAILURE:
                message = "The request document contains a syntax error, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_NOT_FOUND:
                message = "The printer with the specified device ID does not exist, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_SYSTEM:
                message = "An error occurred on the printing system, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_PORT:
                message = "An error was detected on the communication port, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.ERR_TIMEOUT:
                message = "A print timeout occurred, please try again";
                callback.onPrinterStatusReceived(message,code,status);
                break;
            case EposCallbackCode.SUCCESS:
                callback.onPrinterStatusReceived("",code,status);
                break;
            default:
                break;
        }

    }

    public Printer getPrinter() {
        return mPrinter;
    }

    public PeripheralCallback getCallback() {
        return callback;
    }

    public void setCallback(PeripheralCallback callback) {
        this.callback = callback;
    }

    public void deleteDevices(){
        //delete barcodeScanner
        if(mDevice != null && mBarcodeScanner != null){
            try {
                mDevice.deleteDevice(mBarcodeScanner,this);
                mBarcodeScanner = null;
            }catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }

        //delete msr
        if(mDevice != null && mMsrScanner != null){
            try {
                mDevice.deleteDevice(mMsrScanner,this);
                mMsrScanner = null;
            }catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }

        //delete DMD
        if(mDevice != null && mDisplay != null){
            try {
                mDevice.deleteDevice(mDisplay,this);
                mDisplay = null;
            }catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(PeripheralManager.class.getName(), e);
            }
        }

    }
}
