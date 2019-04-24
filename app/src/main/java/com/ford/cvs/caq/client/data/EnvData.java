package com.ford.cvs.caq.client.data;

import java.util.Date;

/**
 * Created by liuchong on 11/5/2016.
 */
public class EnvData {

//    "tVOC", "NO2", "FCOH", "CO", "Temp", "HUM", "VOCs", "PM2.5"}

    //NO2

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public void setPm1d0(double pm1d0) {
        this.pm1d0 = pm1d0;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setCho(double cho) {
        this.cho = cho;
    }

    public void setWet(double wet) {
        this.wet = wet;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public void setVoc(double voc) {
        this.voc = voc;
    }

    public void setTvoc(double tvoc) {
        this.tvoc = tvoc;
    }

    public void setTime(long time) {
        this.time = time;
    }

    //NH3
    //CO
    //FCOH
    //Temp
    //Humidity
    //VOCs volatile organic compounds
    //PM2.5
    //ts
    // Baseline
    //Lon
    //Lat

    Date updateDate;
    double pm25;
    double pm10;
    double pm1d0;
    double temp;
    double cho;
    double wet;
    double co;
    double no2;
    double voc;
    double tvoc;
    long time;

    public Date getUpdateDate() {
        return updateDate;
    }

    public double getPm25() {
        return pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public double getPm1d0() {
        return pm1d0;
    }

    public double getTemp() {
        return temp;
    }

    public double getCho() {
        return cho;
    }

    public double getWet() {
        return wet;
    }

    public long getTime() { return time; }

    public double getCo() {return co;}

    public double getNo2() {return no2;}

    public double getVOC() {return voc;}

    public double getTVOC() {return tvoc;}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PM25").append("-- ").append(pm25).append("\n")
                .append("WET ").append("-- ").append(wet).append("\n")
                .append("TEMP").append("-- ").append(temp).append("\n")
                .append("CHO ").append("-- ").append(cho).append("\n")
                .append("PM10").append("-- ").append(pm10).append("\n")
                .append("PM1d0").append("-- ").append(pm1d0).append("\n")
                .append("TimeStamp").append("-- ").append(time).append("\n");

        //TODO: add date.
        return sb.toString();
    }

    public static final String TAB = ",\t";

    private static final String HEADER_LINE =  "PM25" + TAB + "PM10" + TAB + "PM1d0" + TAB + "CHO" + TAB + "TEMP" + TAB + "WET" + TAB +"TS";
    public final static String getCSVHeaderLine() {
        return HEADER_LINE;
    }

}
