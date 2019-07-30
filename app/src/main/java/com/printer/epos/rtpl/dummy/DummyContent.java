package com.printer.epos.rtpl.dummy;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.UiController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static List<DummyItem> getItems() {
        ITEM_MAP.clear();
        ITEMS.clear();
        // Add 3 sample items.
        addItem(new DummyItem("1", "Dashboard", R.drawable.dashboard_icon));
        addItem(new DummyItem("2", "Customers", R.drawable.customers_icon));
        addItem(new DummyItem("3", "Orders", R.drawable.order_icon));
        addItem(new DummyItem("4", "Products", R.drawable.product_icon));
        addItem(new DummyItem("5", "Product Category", R.drawable.product_categories_icon));
        if (UiController.mSavePreferences.get_roleId().equals("1")) {
            addItem(new DummyItem("6", "Staff", R.drawable.staff_icon));
//        addItem(new DummyItem("7", "Time Tracking", R.drawable.time_tracking_icon));
            addItem(new DummyItem("7", "Reports", R.drawable.reports_icon));
            addItem(new DummyItem("8", "Settings", R.drawable.setting_icon));
        }

        addItem(new DummyItem("9", "Logout", R.drawable.logout_icon));
        addItem(new DummyItem("10", "Send Logs", 0));

        return ITEMS;
    }

    /* static {
         // Add 3 sample items.
         addItem(new DummyItem("1", "Dashboard", R.drawable.dashboard_icon));
         addItem(new DummyItem("2", "Customers", R.drawable.customers_icon));
         addItem(new DummyItem("3", "Orders", R.drawable.order_icon));
         addItem(new DummyItem("4", "Products", R.drawable.product_icon));
         addItem(new DummyItem("5", "Product Category", R.drawable.product_categories_icon));
         if(UiController.mSavePreferences.get_roleId().equals("1"))
         {
             addItem(new DummyItem("6", "Staff", R.drawable.staff_icon));
 //        addItem(new DummyItem("7", "Time Tracking", R.drawable.time_tracking_icon));
             addItem(new DummyItem("7", "Reports", R.drawable.reports_icon));
             addItem(new DummyItem("8", "Settings", R.drawable.setting_icon));
         }

         addItem(new DummyItem("9", "Logout", R.drawable.logout_icon));
     }
 */
    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;
        public int imageName;

        public DummyItem(String id, String content, int imageName) {
            this.id = id;
            this.content = content;
            this.imageName = imageName;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}

