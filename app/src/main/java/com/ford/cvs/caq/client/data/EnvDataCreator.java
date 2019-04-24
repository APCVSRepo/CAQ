package com.ford.cvs.caq.client.data;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchong on 11/5/2016.
 */
public class EnvDataCreator {

    public static EnvData parseHanvonData(String hanvonJsonString) {
        EnvData data = new EnvData();
        try {
            JSONObject wrap = new JSONObject(hanvonJsonString);
            JSONObject jsb = new JSONObject(wrap.getString("data"));
            data.cho = jsb.has(HanvonKey.JS_F_CHO) ? (float) jsb.getDouble(HanvonKey.JS_F_CHO) : 0;
            data.wet = jsb.has(HanvonKey.JS_F_WET) ? (float) jsb.getDouble(HanvonKey.JS_F_WET) : 0;
            data.pm1d0 = jsb.has(HanvonKey.JS_F_PM1d0) ? (float) jsb.getDouble(HanvonKey.JS_F_PM1d0) : 0;
            data.pm10 = jsb.has(HanvonKey.JS_F_PM10) ? (float) jsb.getDouble(HanvonKey.JS_F_PM10) : 0;
            data.pm25 = jsb.has(HanvonKey.JS_F_PM25) ? (float) jsb.getDouble(HanvonKey.JS_F_PM25) : 0;
            data.temp = jsb.has(HanvonKey.JS_F_TEMPETURE) ? (float) jsb.getDouble(HanvonKey.JS_F_TEMPETURE) : 0;
            data.time = jsb.has(HanvonKey.JS_L_MILLISECOND) ? jsb.getLong(HanvonKey.JS_L_MILLISECOND) : 0;

            //TODO: init date

        } catch (JSONException e) {
            return null;
        }

        return data;
    }

    /**
     * Try to parse the binary data.
     *
     * @param lastStr the last data not parsed yet.
     * @param data partial data read from hardware.
     * @param baselineValues the baseline values for chemisense.
     * @return
     *      Pair with EnvData and the string that not parsed.
     *
     */
    public static Pair<EnvData, String> parseChemisense(String lastStr, byte[] data, ArrayList<String>baselineValues) {
        Pair<EnvData, String> result;

        String str = lastStr == null ? new String() : lastStr;

        // CHEMSENSE13
        for (int i = 0; i < data.length; i++) {
            if (data[i] != 0) {
                str += Character.toString((char) ((int) data[i]));
            }
        }
        if (str.length() < 100) {
            return new Pair<>(null, str); //Should be invalid data.
        }

        str = str.replace("\r\n", ",");        // new line is at every channel reading. Get rid of it.
        str = str.replace("\n", ",");
        str = str.replace("\n", ",");
        int beginIndex;
        int endIndex;

        //        1: tVOC
        //        6: Temperature
        //        12: PM2.5
        //        13: Selected VOCs (not included in tVOC)
        //        22: NO2
        //        24: CO
        //        25: Formaldehyde
        //        80: Humidity

        if (str.indexOf("1,") == 0) {
            beginIndex = 0;
        }
        else {
            beginIndex = str.indexOf(",1,");
        }

        if (str.lastIndexOf("1,") == 0) {
            endIndex = 0;
        }
        else {
            endIndex = str.lastIndexOf(",1,");
        }

        if (beginIndex == -1 || endIndex == -1 || beginIndex + 1 >= endIndex || endIndex < beginIndex || endIndex > str.length()) {
            return new Pair<>(null, str);     // keep waiting, invalid data?
        }

        EnvData envData = null;
        String usableStr = str.substring(beginIndex, endIndex); // get rid of first comma
        if (usableStr.indexOf(",") == 0)
            usableStr = usableStr.substring(1, usableStr.length());
        str = str.substring(endIndex + 2, str.length()); // Keep the remaining for parsing later.


        // Now we should have data starting from channel 1 and ending at channel 1
        String[] myStr = usableStr.split(",");
        List<String> allChannels = new ArrayList<String>();
        List<String> allData = new ArrayList<String>();
        String strChannels = "";

        int parseState = 1;
        for (int i = 0; i < myStr.length; i++) {
            // state machine

            //remove channel 8
            if (myStr[i].compareTo("8") == 0) {
                if (i < myStr.length - 2)
                    i += 2;
                else
                    break;
            }

            switch (parseState) {
                case 1:
                    if (myStr[i].compareTo("12") == 0) { // Last channel. Now we write.
                        strChannels = strChannels + myStr[i] + "\n";
                        allChannels.add(myStr[i]);

                        strChannels = "";
                        allChannels.removeAll(allChannels);

                        parseState = 3; // Load last channel data
                    } else {
                        strChannels = strChannels + myStr[i] + ",";
                        allChannels.add(myStr[i]);

                        parseState = 2; // Load channel data
                    }
                    break;

                case 2: // we will read value of channel.
                    //                    strData = strData + myStr[i] +",";
                    allData.add(myStr[i]);

                    parseState = 1;
                    break;

                case 3:
                    // Write line
                    //                    strData = strData + myStr[i] +"," + System.currentTimeMillis() + "," + GPSString + "\n";
                    allData.add(myStr[i]);

                    try {
                        envData = convertPPM(allData, baselineValues);
                        envData.time = System.currentTimeMillis();

                    } catch (Exception ex) {
                        envData = null;
                    }
//                    envData.co = convertAQI.convertCO(envData.co, envData.getTemp(), envData.getWet());
//                    envData.pm25 = convertAQI.convertPM(envData.pm25, envData.getTemp());

                    String strData = "";
                    for (String s : allData) {
                        strData = strData + s + ",";
                    }

                    strData = strData + System.currentTimeMillis() + "," + baselineValues.get(7) + "," + "" + "\n";

                    try {
                        strData = "";
                        allData.removeAll(allData);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    parseState = 1; // Back to reading channels

                    break;
            }
        }

        return new Pair<>(envData, str);
    }

    private static EnvData convertPPM(List<String>data, ArrayList<String>baselineValues){
        double value, baseline;
        double ppm;

        EnvData envData = null;
        if (data.size() > 0) {
            envData = new EnvData();

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).length() <= 0)
                    break;
                value = Float.parseFloat(data.get(i));
                baseline = Float.parseFloat(baselineValues.get(i));
                switch (i) {
//                        public static String[] channelNames = {"tVOC", "NO2", "FCOH", "CO", "Temp", "HUM", "VOCs", "PM2.5"};
                    case 0: // tVOC
//                        updatedData.add(data.get(i));
                        envData.tvoc = value;
                        break;
                    case 1: // NO2- no ppm known
                        ppm = (value - baseline) / (-97.465);
                        if (ppm < 0)
                            ppm = Double.valueOf(0);
                        envData.no2 = ppm;
//                        updatedData.add(Integer.toString(ppm.intValue()));
                        break;
                    case 2: // FCOH
                        ppm = (value - baseline) / (480.75);
                        if (ppm < 0)
                            ppm = Double.valueOf(0);
                        envData.cho = ppm;
//                        updatedData.add(Integer.toString(ppm.intValue()));
                        break;
                    case 3: // CO
                        ppm = (value - baseline) / (28.378);
                        if (ppm < 0)
                            ppm = Double.valueOf(0);
                        envData.co =  ppm;
//                        updatedData.add(Integer.toString(ppm.intValue()));
                        break;
                    case 4: // temp - leave as is
                        envData.temp = value;
//                        updatedData.add(data.get(i));
                        break;
                    case 5: // humidity - leave as is
                        envData.wet = value;
//                        updatedData.add(data.get(i));
                        break;
                    case 6: // VOCs - no ppm known
                        envData.voc = value;
//                        updatedData.add(data.get(i));
                        break;
                    case 7: // PM2.5
                        ppm = (value - baseline) * 3.88;
                        if (ppm < 0)
                            ppm = Double.valueOf(0);
                        envData.pm25 = ppm;
//                        updatedData.add(Integer.toString(ppm.intValue()));
                        break;
                }
            }
        }
        return envData;
    }

    public static String toCSV(EnvData data) {
        StringBuffer bf = new StringBuffer();
        bf.append(data.getPm25()).append(EnvData.TAB)
                .append(data.getPm10()).append(EnvData.TAB)
                .append(data.getPm1d0()).append(EnvData.TAB)
                .append(data.getCho()).append(EnvData.TAB)
                .append(data.getTemp()).append(EnvData.TAB)
                .append(data.getWet()).append(EnvData.TAB)
                .append(data.getTime());

        return bf.toString();
    }

    public static List<HanvonDevice> parseHanvonDevice(String deviceListJson) {
        List<HanvonDevice> deviceList = new ArrayList<HanvonDevice>();
        try {
            JSONArray array = new JSONArray(deviceListJson);
            for (int i=0; i<array.length(); i++) {
                JSONObject jsb = (JSONObject) array.get(i);
                HanvonDevice device = new HanvonDevice();
                device.devId = jsb.getString(HanvonKey.JS_S_DEVICE_ID);
                device.name = jsb.getString(HanvonKey.JS_S_NAME);
                device.series = jsb.getString(HanvonKey.JS_S_SERIES);

                //TODO: parse date
                deviceList.add(device);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deviceList;
    }

    public static EnvData parseMoji(String jsonResponse) {
        EnvData data = new EnvData();

        return data;
    }
}
