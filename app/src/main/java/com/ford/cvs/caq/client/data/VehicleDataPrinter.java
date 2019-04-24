package com.ford.cvs.caq.client.data;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.smartdevicelink.proxy.rpc.GPSData;
import com.smartdevicelink.proxy.rpc.OnVehicleData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by liuchong on 21/7/2016.
 */
public class VehicleDataPrinter implements VehicleDataListener {

    private String targetFileDir;
    private String vehicleName;

    private List<String> dataQueue = new ArrayList<>();

    public VehicleDataPrinter(String filePath, String vehicleName) {
        this.targetFileDir = filePath;
        this.vehicleName = vehicleName;
    }

    public void forceClose() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                writeData(null, dataQueue);
                dataQueue.clear();
                Log.d("Louis", "finish write data.");
                Looper.loop();
            }
        });
        t.start();
    }

    @Override
    public void onVehicleData(OnVehicleData onVehicleData) {
        try {
            JSONObject jsb = onVehicleData.serializeJSON();

            dataQueue.add(System.currentTimeMillis() + "," + jsb.toString());

            if (dataQueue.size() >= 1000) { //Write for every 1000 record.
                final List<String> tmpList = new ArrayList<>(dataQueue);
                dataQueue.clear();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        writeData(null, tmpList);
                        Log.d("Louis", "finish write data.");
                        Looper.loop();
                    }
                });
                t.start();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean writeData(String headerLine, List<String> lines) {

        TimeZone time = TimeZone.getTimeZone("UTC");
        Calendar cal = Calendar.getInstance(time);
        cal.setTimeInMillis(System.currentTimeMillis());
        Date date = cal.getTime();
        SimpleDateFormat formator = new SimpleDateFormat("yyyy-MM-dd");

        String filePath = this.vehicleName + formator.format(date) + ".csv";
        try {

            File f = createFile(filePath);

            if (f == null || !f.exists()) {
                return false;
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
            if (headerLine != null) {
                writer.write(headerLine + "\n");
            }
            for (String l : lines) {
                writer.write(l + "\n");
            }
            writer.close();
            Log.d("Louis", "write data successfully to " + f.getAbsolutePath());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return false;
    }

    private File createFile(String filePath) {
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

        String firstFilePath = rawSecondaryStoragesStr + targetFileDir + filePath;

        File f = new File(rawSecondaryStoragesStr + targetFileDir, filePath);
        if (!f.exists()) {
            try {
                f.createNewFile();
                return f;
            } catch (IOException e) {
                Log.e("Louis", "fail to create file at " + firstFilePath);
                f = new File(Environment.getExternalStorageDirectory().getPath() + "/", filePath);

                try {
                    f.createNewFile();
                    return f;
                } catch (IOException e1) {
                    Log.e("Louis", "fail to create file at " + Environment.getExternalStorageDirectory().getPath() + "/" + filePath);
                }

                return null;
            }
        }

        return null;
    }

}
