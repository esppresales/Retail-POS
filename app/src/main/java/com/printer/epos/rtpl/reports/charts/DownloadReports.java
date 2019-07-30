package com.printer.epos.rtpl.reports.charts;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.reports.DownloadReceiver;

/**
 * Created by android-pc3 on 29/4/15.
 */
public class DownloadReports {

    protected static void downloadCsvReport(Context context, String url, String fileName) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));

        request.addRequestHeader("access-token", new SavePreferences(context).get_accessToken());
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName + ".xls");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        context.registerReceiver(new DownloadReceiver(), new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        manager.enqueue(request);
    }
}
