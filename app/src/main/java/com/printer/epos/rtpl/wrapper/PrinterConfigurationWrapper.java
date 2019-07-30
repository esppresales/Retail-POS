package com.printer.epos.rtpl.wrapper;

/**
 * Created by android-pc3 on 13/5/15.
 */
public class PrinterConfigurationWrapper {

    public String printerSerial;
    public String printerName;
    public String isChecked;

    public String getPrinterSerial() {
        return printerSerial;
    }

    public void setPrinterSerial(String printerSerial) {
        this.printerSerial = printerSerial;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String isChecked() {
        return isChecked;
    }

    public void setChecked(String isChecked) {
        this.isChecked = isChecked;
    }
}
