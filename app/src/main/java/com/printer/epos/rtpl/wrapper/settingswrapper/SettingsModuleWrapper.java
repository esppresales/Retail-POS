package com.printer.epos.rtpl.wrapper.settingswrapper;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by android-pc3 on 16/4/15.
 */
public class SettingsModuleWrapper {
    private SettingsData data;
    private RefreshSettingsListener mSettings;

    /**
     * @return The data
     */
    public SettingsData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(SettingsData data) {
        this.data = data;
    }

    public void getSettings(Context context, RefreshSettingsListener refreshSettings) {
        mSettings = refreshSettings;
        new WebServiceCalling().callWS(context, UiController.appUrl + "settings", null, new WebServiceHandler() {

            @Override
            public void onSuccess(String response) {
                System.out.println(response);

                Reader reader = new InputStreamReader(Util.getResponseAsInputStream(response));
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<SettingsModuleWrapper>() {
                    }.getType();
                    SettingsModuleWrapper setingsWrapper = gson.fromJson(reader, listType);
                    mSettings.onSettingsReceived(setingsWrapper);
                } catch (Exception e) {
                    System.out.println("Exception is: " + e.toString());
                    RetailPosLoging.getInstance().registerLog(SettingsModuleWrapper.class.getName(), e);
                }

            }

            @Override
            public void onError(String error) {
                System.out.println("" + error);
            }
        });
    }

    public interface RefreshSettingsListener {
        public void onSettingsReceived(SettingsModuleWrapper wrapper);

    }
}
