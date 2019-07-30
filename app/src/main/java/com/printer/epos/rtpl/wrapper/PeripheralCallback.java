package com.printer.epos.rtpl.wrapper;

/**
 * Created by ashishasolanki on 18/05/15.
 */
public interface PeripheralCallback {


    public void onBarcodeScanData(String data);
    public void onMSRScanData(String data);
    public void onDisplayReadData(String data);
    public void onConnectPrinter();
    public void onDisconnectPrinter();
    public void onPrinterStatusReceived(String message,int code, int status);
}
