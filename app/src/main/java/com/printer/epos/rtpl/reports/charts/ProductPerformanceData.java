package com.printer.epos.rtpl.reports.charts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-pc3 on 28/4/15.
 */
public class ProductPerformanceData {

    @SerializedName("product_performance")
    @Expose
    private List<ProductPerformance> productPerformance = new ArrayList<ProductPerformance>();
    @SerializedName("top_product_details")
    @Expose
    private TopProductDetails topProductDetails;

    /**
     * @return The productPerformance
     */
    public List<ProductPerformance> getProductPerformance() {
        return productPerformance;
    }

    /**
     * @param productPerformance The product_performance
     */
    public void setProductPerformance(List<ProductPerformance> productPerformance) {
        this.productPerformance = productPerformance;
    }

    /**
     * @return The topProductDetails
     */
    public TopProductDetails getTopProductDetails() {
        return topProductDetails;
    }

    /**
     * @param topProductDetails The top_product_details
     */
    public void setTopProductDetails(TopProductDetails topProductDetails) {
        this.topProductDetails = topProductDetails;
    }


    public class ProductPerformance {
        @SerializedName("product_name")
        @Expose
        private String productName;
        @SerializedName("quantity_sold")
        @Expose
        private String quantitySold;

        /**
         * @return The productName
         */
        public String getProductName() {
            return productName;
        }

        /**
         * @param productName The product_name
         */
        public void setProductName(String productName) {
            this.productName = productName;
        }

        /**
         * @return The quantitySold
         */
        public String getQuantitySold() {
            return quantitySold;
        }

        /**
         * @param quantitySold The quantity_sold
         */
        public void setQuantitySold(String quantitySold) {
            this.quantitySold = quantitySold;
        }
    }

    public class TopProductDetails {
        @SerializedName("total_items_sold")
        @Expose
        private Integer totalItemsSold;
        @SerializedName("top_selling_product")
        @Expose
        private String topSellingProduct;
        @SerializedName("top_product_price")
        @Expose
        private String topProductPrice;

        /**
         * @return The totalItemsSold
         */
        public Integer getTotalItemsSold() {
            return totalItemsSold;
        }

        /**
         * @param totalItemsSold The total_items_sold
         */
        public void setTotalItemsSold(Integer totalItemsSold) {
            this.totalItemsSold = totalItemsSold;
        }

        /**
         * @return The topSellingProduct
         */
        public String getTopSellingProduct() {
            return topSellingProduct;
        }

        /**
         * @param topSellingProduct The top_selling_product
         */
        public void setTopSellingProduct(String topSellingProduct) {
            this.topSellingProduct = topSellingProduct;
        }

        /**
         * @return The topProductPrice
         */
        public String getTopProductPrice() {
            return topProductPrice;
        }

        /**
         * @param topProductPrice The top_product_price
         */
        public void setTopProductPrice(String topProductPrice) {
            this.topProductPrice = topProductPrice;
        }

    }
}
