package com.ford.cvs.caq.client.data;

/**
 * Created by CLIU130 on 6/5/2016.
 */
public interface DataReaderListener {
    public void onNewData(Object dataObj);
    public void onError(String msg);
}
