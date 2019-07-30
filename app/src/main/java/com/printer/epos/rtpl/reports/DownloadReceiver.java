package com.printer.epos.rtpl.reports;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DownloadReceiver extends BroadcastReceiver {
    public DownloadReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
            Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();

    }
}
