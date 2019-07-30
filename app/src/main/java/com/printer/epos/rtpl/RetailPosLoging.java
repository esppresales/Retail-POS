package com.printer.epos.rtpl;

import android.os.Environment;

import com.printer.epos.rtpl.Utility.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by android-pc3 on 28/5/15.
 */
public class RetailPosLoging {

    private static RetailPosLoging loging = null;
    private static File logFile = null;
    protected static boolean DEBUG = false;
    protected final static String FILE_PATH = Environment.getExternalStorageDirectory()+"/logging.txt";


    private RetailPosLoging()
    {
        createLogFile();
    }
    public static RetailPosLoging getInstance()
    {
        if(loging == null)
            loging = new RetailPosLoging();
        return loging;
    }
    public void registerLog(String url, int statusCode, Map<String,Object> param, String response)
    {
        if(DEBUG)
            writeLog(url, statusCode, param, response);
    }

    public void registerLog(String url, int statusCode, Map<String,Object> param, String response,Exception ex)
    {
        if(DEBUG)
            writeLog(url, statusCode, param, response, ex);
    }

    public void registerLog(String className, Exception ex)
    {
        if(DEBUG)
            writeLog(className, ex);
    }


   /* public void registerLog(String className, String value)
    {
        if(DEBUG)
            writeLog(className, value);
    }*/
    private static void createLogFile()
    {
        logFile = new File(FILE_PATH);
        if(logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
            }
        }

    }

    private static void writeLog(String url, int statusCode, Map<String,Object> param, String response)
    {
        StringBuilder params = new StringBuilder();

        if(param != null){
            for(Map.Entry<String,Object> map : param.entrySet())
                params.append(map.getValue()).append(",");
        }

        String logString = Util.getCurrentTimeStamp()+"\n\n"+"Status is:"+statusCode+" Url is: "+url+" "+"Parameters are: "+params.toString()+"Response is: "+response+"\n\n";

        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logString);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        } catch (IOException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        }
    }

    private static void writeLog(String url, int statusCode, Map<String,Object> param, String response,Exception ex)
    {
        StringBuilder params = new StringBuilder();

        if(param != null){
            for(Map.Entry<String,Object> map : param.entrySet())
                params.append(map.getValue()).append(",");
        }


        String logString = Util.getCurrentTimeStamp()+"\n\n"+"Status is:"+statusCode+"Url is: "+url+" "+"Parameters are: "+params.toString()+"Response is: "+response
                +"Exception is: "+ex.getMessage()+"\n\n";

        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logString);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        } catch (IOException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        }
    }

    private static void writeLog(String className, Exception ex)
    {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        String logString = Util.getCurrentTimeStamp()+"\n\n"+"Class name: "+className
                +" Exception is: "+stackTrace+"\n\n";

        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logString);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        } catch (IOException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        }
    }

    private static void writeLog(String className, String data)
    {
        String logString = Util.getCurrentTimeStamp()+"\n\n"+"Class name: "+className
                +" Data is: "+data+"\n\n";

        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logString);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        } catch (IOException e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(RetailPosLoging.class.getName(), e);
        }
    }
}
