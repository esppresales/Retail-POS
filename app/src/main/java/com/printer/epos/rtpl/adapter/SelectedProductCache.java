package com.printer.epos.rtpl.adapter;

import com.printer.epos.rtpl.wrapper.ProductWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by android-pc3 on 6/5/15.
 * Maintains a Temporary cache of Removed Product Items.
 */
public class SelectedProductCache {

    private static List<ProductWrapper> productCacheData = Collections.synchronizedList(new ArrayList<ProductWrapper>());

    public static List<ProductWrapper> getProductCacheData() {
        return productCacheData;
    }

    public static void setProductCacheData(ProductWrapper productCacheData) {
        SelectedProductCache.productCacheData.add(productCacheData);
    }

    public static void clearProcuctCacheData() {
        SelectedProductCache.productCacheData.clear();
    }

}
