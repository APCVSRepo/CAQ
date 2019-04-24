package com.ford.cvs.caq.client.data;

/**
 * Created by liuchong on 11/5/2016.
 *
 * Define all the json keys for Hanvon server response.
 */
public class HanvonKey {
//    "devId": "M2-1456808671163", //设备ID
//            "pm25": 46.57, //pm2.5
//            "pm10": 66.92, //pm10
//            "pm1d0": 24.62, //pm1.0
//            "millisecond": 1460100019124, //时间戳，ms
//            "dataTime": "2016-04-08 15:20:19", //时间
//            "temp": 24.85, //温度
//            "cho": 0.03,//甲醛
//            "wet": 51.49//湿度

    //            "name": "M2 (4)", //设备名称
//            "series": "M2", //设备系列
//            "createTime": "2016-03-20 23:17:16"//设备绑定时间

    public static final String JS_S_DEVICE_ID = "devId";
    public static final String JS_F_PM25 = "pm25";
    public static final String JS_F_PM10 = "pm10";
    public static final String JS_F_PM1d0 = "pm1d0";
    public static final String JS_L_MILLISECOND = "millisecond";
    public static final String JS_L_DATATIME = "dataTime";
    public static final String JS_F_TEMPETURE = "temp";
    public static final String JS_F_CHO = "cho";
    public static final String JS_F_WET = "wet";

    public static final String JS_S_NAME = "name";
    public static final String JS_S_SERIES = "series";
    public static final String JS_S_CREATETIME = "createTime";
    public static final String JS_A_DEVICES = "devices";
    public static final String JS_S_SESSIONID = "sessionId";
}
