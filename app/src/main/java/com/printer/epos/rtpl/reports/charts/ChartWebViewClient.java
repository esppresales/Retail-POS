package com.printer.epos.rtpl.reports.charts;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by hp pc on 02-05-2015.
 */
public class ChartWebViewClient extends WebViewClient {

    private final ProgressDialog mDialog;

    protected ChartWebViewClient(Context context) {
        mDialog = new ProgressDialog(context, android.R.style.Theme_Holo_Dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setMessage("Loading Chart...");
        mDialog.show();

    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        System.out.println("on finish");
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }
}