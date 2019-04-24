package com.ford.cvs.caq.client.data;

import com.smartdevicelink.proxy.rpc.OnVehicleData;

/**
 * Created by liuchong on 21/7/2016.
 */
public interface VehicleDataListener {

    public void onVehicleData(OnVehicleData onVehicleData);
    public void forceClose();

}
