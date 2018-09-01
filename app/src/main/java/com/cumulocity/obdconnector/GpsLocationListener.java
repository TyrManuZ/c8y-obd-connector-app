/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.cumulocity.obdconnector.mqtt.MqttMessengerService;

public class GpsLocationListener implements LocationListener {

    private final MqttMessengerService messengerService;

    public GpsLocationListener(MqttMessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @Override
    public void onLocationChanged(Location location) {
        messengerService.sendGpsLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
