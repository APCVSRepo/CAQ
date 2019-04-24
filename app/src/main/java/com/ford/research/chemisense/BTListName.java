package com.ford.research.chemisense;

import android.bluetooth.BluetoothDevice;

/**
 * Created by omakke on 10/19/2015.
 */
public class BTListName {

    BluetoothDevice device;
    String name;
    private boolean isSelected = false;

    public BTListName(BluetoothDevice device){
        name = device.getName();
        this.device = device;
    }

    @Override
    public String toString(){
        return name;
    }

    public void select(){
        isSelected = true;
    }

    public void unselect(){
        isSelected = false;
    }

    public boolean isSelected(){
        return isSelected;
    }
}
