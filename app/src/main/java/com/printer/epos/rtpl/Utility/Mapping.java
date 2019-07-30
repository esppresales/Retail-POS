package com.printer.epos.rtpl.Utility;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ranosys-puneet on 24/3/15.
 */
public class Mapping {

    public static String createUrl(HashMap<String, Object> map) {
        //http://api.smartprix.com/simple/v1?type=product&key=&id=2179&indent=1

        ArrayList<String> url = new ArrayList<String>();
        List<String> list = new ArrayList<String>(map.keySet());
        List<Object> vList = new ArrayList<Object>(map.values());

        for(int i =0; i<list.size() ; i++) {

            url.add(list.get(i) + "=" +map.get(list.get(i)));
        }


        return TextUtils.join("&", url);
    }
}
