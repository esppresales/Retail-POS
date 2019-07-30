package com.printer.epos.rtpl.wrapper;

import android.util.Log;

import com.epson.eposdevice.CreateDeviceListener;
import com.epson.eposdevice.Device;
import com.epson.eposdevice.EposCallbackCode;
import com.epson.eposdevice.EposException;
import com.epson.eposdevice.printer.Printer;
import com.epson.eposdevice.printer.ReceiveListener;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.wrapper.settingswrapper.PrinterDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranosys-abhi1 on 2/7/15.
 */
public class MultiplePrintManager implements CreateDeviceListener,ReceiveListener {

    private static MultiplePrintManager manager = null;
    private static List<PrinterBuilder> printerList = new ArrayList<>();
    private PeripheralCallback callback;

    private MultiplePrintManager()
    {

    }

    public static MultiplePrintManager getMultiplePrinterManager()
    {
        if(manager == null) {
            manager = new MultiplePrintManager();
            return manager;
        }
        else
            return manager;
    }

    public void createPrinter(String deviceID, Printer printer, PeripheralManager peripheralManager) {
        if (printer == null && peripheralManager.mDevice != null && peripheralManager.mDevice.isConnected()) {
            try {
                peripheralManager.mDevice.createDevice(deviceID, Device.DEV_TYPE_PRINTER, Device.FALSE, Device.FALSE, this);
            } catch (EposException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(MultiplePrintManager.class.getName(), e);
            }
        }

    }

    @Override
    public void onCreateDevice(String ipAddress, String deviceID, int deviceType,
                               Object deviceObject,final int code) {
        if (code == EposCallbackCode.SUCCESS) {
            switch (deviceType) {
                case Device.DEV_TYPE_PRINTER:
                    PrinterBuilder builder = new PrinterBuilder();
                    builder.setPrinter((Printer) deviceObject);
                    builder.setPrinterId(deviceID);
                    builder.getPrinter().setReceiveEventCallback(this);
                    printerList.add(builder);
                    break;
                default:
                    break;
            }
        } else {
            Log.d(MultiplePrintManager.class.getName(), "Error in create device, error code is :" + code);
        }
    }

    public List<PrinterBuilder> getPrinterList()
    {
        return printerList;
    }

    public void createNetworkPrinters(PeripheralManager peripheralManager)
    {
        for(int i=1;i< Util.printerDetailsList.size();i++)
        {
            PrinterDetails details = Util.printerDetailsList.get(i);
            if(details.getIsEnabled().equals("1"))
                MultiplePrintManager.getMultiplePrinterManager().createPrinter(details.getPrinterName(),null,peripheralManager);
        }
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

    public void setCallback(PeripheralCallback callback) {
        this.callback = callback;
    }

    public class PrinterBuilder
    {
        private Printer printer;
        private String printerId;

        public String getPrinterId() {
            return printerId;
        }

        public void setPrinterId(String printerId) {
            this.printerId = printerId;
        }

        public Printer getPrinter() {
            return printer;
        }

        public void setPrinter(Printer printer) {
            this.printer = printer;
        }
    }

}
