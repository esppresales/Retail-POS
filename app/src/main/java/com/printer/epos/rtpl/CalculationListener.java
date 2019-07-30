package com.printer.epos.rtpl;

/**
 * Created by ranosys-abhi1 on 5/6/15.
 */
public interface CalculationListener {

    void setGrossAndDiscountAmountValue();
    void setCouponAndRedeemedAmount(String statusText);
    void setTaxAndNetAmount();
    void setDefaultValues();
    void setDiscountAndTaxPercentage();
}
