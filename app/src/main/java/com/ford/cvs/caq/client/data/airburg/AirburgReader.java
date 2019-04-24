package com.ford.cvs.caq.client.data.airburg;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ford.cvs.caq.client.ble.BluetoothLeService;
import com.ford.cvs.caq.client.ble.SampleGattAttributes;
import com.ford.cvs.caq.client.data.DataReaderListener;
import com.ford.cvs.caq.client.data.EnvData;
import com.ford.cvs.caq.client.data.ServerDataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuchong on 6/7/2016.
 */
public class AirburgReader extends ServerDataReader {

    private final String TAG = "AirburgReader";

    private String deviceAddress;
    BluetoothLeService mBluetoothLeService;
    private Lock lockObj = new ReentrantLock();
    Condition dataAvailableCond = lockObj.newCondition();
    private boolean mConnected = false;
    byte[] allData = null;
    private BluetoothGattCharacteristic ppp =
            new BluetoothGattCharacteristic(UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"),
                    BluetoothGattCharacteristic.PROPERTY_READ,
                    BluetoothGattCharacteristic.PERMISSION_READ);

    private Context contextWrapper;

    private BluetoothGattCharacteristic pm25Characteristic = ppp;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize(deviceAddress)) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                return;
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public AirburgReader(Context context, String deviceAddress, DataReaderListener... callback) {
        super(1 * 1000, callback);

        this.deviceAddress = deviceAddress;
        this.contextWrapper = context;

        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                List<BluetoothGattService> services = mBluetoothLeService.getSupportedGattServices(deviceAddress);
                findPm25Characters(services);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                lockObj.lock();
                try {
                    allData = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    dataAvailableCond.signal();
                } finally {
                    lockObj.unlock();
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    @Override
    protected Result doRequestData() {

        allData = null;

        lockObj.lock();
        if (pm25Characteristic == null || mBluetoothLeService == null) {
            return new Result("still not connected to device or characteristic still not initialized.", Result.CODE_FAIL);
        }
        mBluetoothLeService.readCharacteristic(deviceAddress, pm25Characteristic);

        try {
            dataAvailableCond.await(10, TimeUnit.SECONDS); //Assume data will return in 10 second.
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lockObj.unlock();
        }

        if (allData == null || allData.length < 12) {
            return new Result("no data return", Result.CODE_FAIL);
        }

        int value = ((int)allData[10] * 16) + (int) allData[11];

        EnvData envData = new EnvData();
        envData.setPm25(value);
        envData.setTime(System.currentTimeMillis());
        this.notifyNewData(envData);


        return new Result("PM 25 value is " + value, Result.CODE_NORMAL);
    }

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private void findPm25Characters(List<BluetoothGattService> gattServices) {
//        Log.d(TAG, "try to find pm25 characteristic from " + gattServices);

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "unknown service";//getResources().getString(R.string.unknown_service);
        String unknownCharaString = "unknown characteristic";//getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            Log.d(TAG, "Service: " + uuid);

            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);

                Log.d(TAG, "Characteristic: " + uuid);

                gattCharacteristicGroupData.add(currentCharaData);
                if ("0000fff2-0000-1000-8000-00805f9b34fb".equalsIgnoreCase(uuid.toString())) {
                    pm25Characteristic = gattCharacteristic;
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

    }

    public void cancel() {
        super.cancel();

        mBluetoothLeService.disconnect(deviceAddress);

        this.contextWrapper.unregisterReceiver(mGattUpdateReceiver);
        this.contextWrapper.unbindService(mServiceConnection);
    }

}
