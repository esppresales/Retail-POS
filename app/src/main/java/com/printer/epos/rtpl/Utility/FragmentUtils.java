package com.printer.epos.rtpl.Utility;

/**
 * Created by sabby on 5/1/16.
 */
public enum FragmentUtils {

    DashboardFragment(0),
    CustomerFragment(1),
    OrderListingFragment(2),
    ProductFragment(3),
    ProductCategoryFragment(4),
    StaffFragment(5),
    ReportsFragment(6),
    SettingsFragment(7),

    AddStaffFragment(8),
    AddProductFragment(9),
    NewOrderFragment(10),
    AddCustomerFragment(11),
    OrderPreviewFragment(12),
    ReturnOrderFragment(13),
    OrderDetailFragment(14),
    ReceiptHeaderFragment(15),
    PrinterConfigurationFragment(16),
    CouponCodeFragment(17),
    CustomerDetailFragment(18),
    ProductDetailFragment(19),
    StaffDetailFragment(20),
    StaffTimeTrackingFragment(21),
    SalesChartFragment(22),
    ProductPerformanceFragment(23),
    ProductThresholdFragment(24),
    StockInHandFragment(25),
    DaySalesFragment(26),
    ;

    private final int value;

    FragmentUtils(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

}
