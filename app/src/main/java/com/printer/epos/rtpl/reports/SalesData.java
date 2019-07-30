package com.printer.epos.rtpl.reports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-pc3 on 21/4/15.
 */
public class SalesData {

    @SerializedName("employee_data")
    @Expose
    private List<EmployeeData> employeeData = new ArrayList<EmployeeData>();
    @SerializedName("total_transaction_data")
    @Expose
    private TotalTransactionData totalTransactionData;

    /**
     * @return The employeeData
     */
    public List<EmployeeData> getEmployeeData() {
        return employeeData;
    }

    /**
     * @param employeeData The employee_data
     */
    public void setEmployeeData(List<EmployeeData> employeeData) {
        this.employeeData = employeeData;
    }

    /**
     * @return The totalTransactionData
     */
    public TotalTransactionData getTotalTransactionData() {
        return totalTransactionData;
    }

    /**
     * @param totalTransactionData The total_transaction_data
     */
    public void setTotalTransactionData(TotalTransactionData totalTransactionData) {
        this.totalTransactionData = totalTransactionData;
    }


    public class EmployeeData {
        @SerializedName("employee_name")
        @Expose
        private String employeeName;
        @SerializedName("employee_total_sales")
        @Expose
        private String employeeTotalSales;
        @SerializedName("employee_no_of_transactions")
        @Expose
        private String employeeNoOfTransactions;

        /**
         * @return The employeeName
         */
        public String getEmployeeName() {
            return employeeName;
        }

        /**
         * @param employeeName The employee_name
         */
        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        /**
         * @return The employeeTotalSales
         */
        public String getEmployeeTotalSales() {
            return employeeTotalSales;
        }

        /**
         * @param employeeTotalSales The employee_total_sales
         */
        public void setEmployeeTotalSales(String employeeTotalSales) {
            this.employeeTotalSales = employeeTotalSales;
        }

        /**
         * @return The employeeNoOfTransactions
         */
        public String getEmployeeNoOfTransactions() {
            return employeeNoOfTransactions;
        }

        /**
         * @param employeeNoOfTransactions The employee_no_of_transactions
         */
        public void setEmployeeNoOfTransactions(String employeeNoOfTransactions) {
            this.employeeNoOfTransactions = employeeNoOfTransactions;
        }
    }

    public class TotalTransactionData {

        @SerializedName("total_sales")
        @Expose
        private String totalSales;
        @SerializedName("total_transactions")
        @Expose
        private String totalTransactions;

        /**
         * @return The totalSales
         */
        public String getTotalSales() {
            return totalSales;
        }

        /**
         * @param totalSales The total_sales
         */
        public void setTotalSales(String totalSales) {
            this.totalSales = totalSales;
        }

        /**
         * @return The totalTransactions
         */
        public String getTotalTransactions() {
            return totalTransactions;
        }

        /**
         * @param totalTransactions The total_transactions
         */
        public void setTotalTransactions(String totalTransactions) {
            this.totalTransactions = totalTransactions;
        }
    }
}
