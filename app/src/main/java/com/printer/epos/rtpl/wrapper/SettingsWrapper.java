package com.printer.epos.rtpl.wrapper;

/**
 * Created by android-pc3 on 1/4/15.
 */
public class SettingsWrapper {
    String rowTitle;
    String rowDetail;

   /* public SettingsData(String rowTitle, String rowDetail) {
        this.rowTitle = rowTitle;
        this.rowDetail = rowDetail;
    }*/

    public String getRowTitle() {
        return rowTitle;
    }

    public void setRowTitle(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    public String getRowDetail() {
        return rowDetail;
    }

    public void setRowDetail(String rowDetail) {
        this.rowDetail = rowDetail;
    }
}
