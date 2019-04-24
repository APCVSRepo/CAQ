package com.ford.cvs.caq.client.data;

/**
 * Created by liuchong on 21/7/2016.
 */
public class VehicleDataSet {
//    boolean gps;
//    boolean speed;
//    boolean rpm;
//    boolean fuelLevel;
//    boolean fuelLevel_State;
//    boolean instantFuelConsumption, boolean externalTemperature, boolean prndl, boolean tirePressure,
// boolean odometer, boolean beltStatus, boolean bodyInformation, boolean deviceStatus, boolean driverBraking

    public static final long GPS    = 1;
    public static final long SPEED  = 2;
    public static final long RPM    = 4;
    public static final long FULE_LEVEL  = 8;
    public static final long FULE_LEVEL_STATUS  = 16;
    public static final long INSTANT_FULE_COMSUMPUTION  = 32;
    public static final long EXTERNAL_TEMP  = 64;
    public static final long EXTERNAL_PRNDL  = 128;
    public static final long TIRE_PRESSURE  = 256;
    public static final long ODOMETER  = 512;
    public static final long BELT_STATUS = 1024;
    public static final long BODY_INFO = 2048;
    public static final long DEVICE_STATUS = 4096;
    public static final long DRIVER_BREAKING = 4096 * 2;

    private long demand;

    public VehicleDataSet() {
        this.demand = GPS | SPEED | RPM | FULE_LEVEL |  FULE_LEVEL_STATUS | INSTANT_FULE_COMSUMPUTION |
                EXTERNAL_PRNDL |  EXTERNAL_TEMP | TIRE_PRESSURE |  ODOMETER |  BELT_STATUS |  BODY_INFO
                |  DEVICE_STATUS | DRIVER_BREAKING;
    }

    public VehicleDataSet(long targetData) {
        this.demand = targetData;
    }

    public boolean needGps() {
        return (demand & GPS) == GPS;
    }

    public boolean needSpeed() {
        return (demand & SPEED) == SPEED;
    }

    public boolean needRPM() {
        return (demand & RPM) == RPM;
    }


}
