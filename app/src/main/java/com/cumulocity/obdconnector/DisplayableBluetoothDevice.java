package com.cumulocity.obdconnector;

import android.bluetooth.BluetoothDevice;

public class DisplayableBluetoothDevice {

    private final String name;
    private final String address;

    public DisplayableBluetoothDevice(String name, String address) {
       this.name = name;
       this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return name + " - " + address;
    }
}
