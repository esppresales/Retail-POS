package com.printer.epos.rtpl.wrapper.settingswrapper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class SettingsData {

    @SerializedName("discount_details")
    @Expose
    private DiscountDetails discountDetails;
    @SerializedName("tax_details")
    @Expose
    private TaxDetails taxDetails;
    @SerializedName("loyaltyPoint_details")
    @Expose
    private LoyaltyPointDetails loyaltyPointDetails;

    @SerializedName("peripheral_details")
    @Expose
    private PeripheralDetails peripheralDetails;
    @SerializedName("receiptHeader_details")
    @Expose
    private ReceiptHeaderDetails receiptHeaderDetails;
    @SerializedName("currency_details")
    @Expose
    private CurrencyDetails currencyDetails;
    @SerializedName("printer_details")
    @Expose
    private List<PrinterDetails> printerDetails = new ArrayList<PrinterDetails>();


    /**
     * @return The discountDetails
     */
    public DiscountDetails getDiscountDetails() {
        return discountDetails;
    }

    /**
     * @param discountDetails The discount_details
     */
    public void setDiscountDetails(DiscountDetails discountDetails) {
        this.discountDetails = discountDetails;
    }

    /**
     * @return The taxDetails
     */
    public TaxDetails getTaxDetails() {
        return taxDetails;
    }

    /**
     * @param taxDetails The tax_details
     */
    public void setTaxDetails(TaxDetails taxDetails) {
        this.taxDetails = taxDetails;
    }

    /**
     * @return The loyaltyPointDetails
     */
    public LoyaltyPointDetails getLoyaltyPointDetails() {
        return loyaltyPointDetails;
    }

    /**
     * @param loyaltyPointDetails The loyaltyPoint_details
     */
    public void setLoyaltyPointDetails(LoyaltyPointDetails loyaltyPointDetails) {
        this.loyaltyPointDetails = loyaltyPointDetails;
    }

    /**
     * @return The printerDetails
     */
    public List<PrinterDetails> getPrinterDetails() {
        return printerDetails;
    }

    /**
     * @param printerDetails The printer_details
     */
    public void setPrinterDetails(List<PrinterDetails> printerDetails) {
        this.printerDetails = printerDetails;
    }


    /**
     * @return The peripheralDetails
     */
    public PeripheralDetails getPeripheralDetails() {
        return peripheralDetails;
    }

    /**
     * @param peripheralDetails The peripheral_details
     */
    public void setPeripheralDetails(PeripheralDetails peripheralDetails) {
        this.peripheralDetails = peripheralDetails;
    }

    /**
     * @return The receiptHeaderDetails
     */
    public ReceiptHeaderDetails getReceiptHeaderDetails() {
        return receiptHeaderDetails;
    }

    /**
     * @param receiptHeaderDetails The receiptHeader_details
     */
    public void setReceiptHeaderDetails(ReceiptHeaderDetails receiptHeaderDetails) {
        this.receiptHeaderDetails = receiptHeaderDetails;
    }

    /**
     * @return The currencyDetails
     */
    public CurrencyDetails getCurrencyDetails() {
        return currencyDetails;
    }

    /**
     * @param currencyDetails The currency_details
     */
    public void setCurrencyDetails(CurrencyDetails currencyDetails) {
        this.currencyDetails = currencyDetails;
    }


}
