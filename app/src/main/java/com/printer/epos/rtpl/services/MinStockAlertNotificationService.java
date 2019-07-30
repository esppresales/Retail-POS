package com.printer.epos.rtpl.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.reports.StockInHandData;
import com.printer.epos.rtpl.reports.StockInHandWrapper;

import java.util.List;

@SuppressWarnings("ALL")
public class MinStockAlertNotificationService extends Service {

    private List<StockInHandData> mDataList;

   /* public MinStockAlertNotificationService() {
        super("MinStockAlertNotificationService");
    }*/

   /* @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MinStockAlert", "onBind");

    }
*/
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("MinStockAlert", "onStart");
        onBind(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            try {
                StockInHandWrapper wrapper = new StockInHandWrapper() {
                    @Override
                    protected void onProductListReceived(StockInHandWrapper wrapper) {
                        super.onProductListReceived(wrapper);
                        mDataList = wrapper.getData();
                        int id = 0;
                        for (StockInHandData data : mDataList) {
                            String minQuantity = data.getMinStockAlertQty();
                            String productName = data.getName();

                            createNotificationAlert(id++, productName, minQuantity);
                        }

                    }
                };
                wrapper.getProductList(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                RetailPosLoging.getInstance().registerLog(MinStockAlertNotificationService.class.getName(), ex);
            }
        }
        return null;
    }

   /* @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                StockInHandWrapper wrapper = new StockInHandWrapper() {
                    @Override
                    protected void onProductListReceived(StockInHandWrapper wrapper) {
                        super.onProductListReceived(wrapper);
                        mDataList = wrapper.getData();
                        int id = 0;
                        for (StockInHandData data : mDataList) {
                            String minQuantity = data.getMinStockAlertQty();
                            String productName = data.getName();

                            createNotificationAlert(id++, productName, minQuantity);
                        }

                    }
                };
                wrapper.getProductList(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }*/

    private void createNotificationAlert(int id, String productName, String productQuantity) {

        Notification.Builder n = new Notification.Builder(getApplicationContext())
                .setContentTitle("Minimum Stock Alert")
                .setContentText("Product Name: " + productName + " " + "Product Quantity: " + productQuantity)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, n.build());

    }
}
