package com.printer.epos.rtpl.Utility;

/**
 * Created by android-pc3 on 23/3/15.
 */
public interface EditCategoryListener {
    public void onCategorySaved(String title, String description);

    public void onCategoryUpdate(String id, String title, String description, String markAsDelete);
}
