package com.cumulocity.obdconnector;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ApplicationProperties {

    private static final String DEFAULT_URI = "mqtt.cumulocity.com";
    private static final boolean DEFAULT_SSL = true;
    private static final boolean DEFAULT_GPS = false;
    private static final long DEFAULT_MIN_INTERVAL = 1000;
    private static final float DEFAULT_MIN_DISTANCE = 30;
    private static final int DEFAULT_OBD_INTERVAL = 1000;
    private static final String DEFAULT_EMPTY = "";

    public static final String CUMULOCITY_URI_SETTING = "c8y.uri";
    public static final String CUMULOCITY_TENANT_SETTING = "c8y.tenant";
    public static final String CUMULOCITY_USER_SETTING = "c8y.user";
    public static final String CUMULOCITY_PASSWORD_SETTING = "c8y.password";
    public static final String CUMULOCITY_SSL_SETTING = "c8y.ssl";

    public static final String BLUETOOTH_DEVICE_SETTINGS = "obd.device";
    public static final String OBD_POLLING_INTERVAL_MS = "obd.interval";
    public static final String OBD_MESSAGES = "obd.messages";

    public static final String GPS_ACTIVE_SETTING = "gps.active";
    public static final String GPS_MIN_INTERVAL_SETTING = "gps.interval";
    public static final String GPS_MIN_DISTANCE_SETTING = "gps.distance";

    private SharedPreferences sharedPref;

    public ApplicationProperties(Context ctx) {
        sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.settings_file), Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor edit() {
        return sharedPref.edit();
    }

    public String getUri() {
        return sharedPref.getString(CUMULOCITY_URI_SETTING, DEFAULT_URI);
    }

    public String getTenant() {
        return sharedPref.getString(CUMULOCITY_TENANT_SETTING, DEFAULT_EMPTY);
    }

    public String getUsername() {
        return sharedPref.getString(CUMULOCITY_USER_SETTING, DEFAULT_EMPTY);
    }

    public String getPassword() {
        return sharedPref.getString(CUMULOCITY_PASSWORD_SETTING, DEFAULT_EMPTY);
    }

    public boolean useSsl() {
        return sharedPref.getBoolean(CUMULOCITY_SSL_SETTING, DEFAULT_SSL);
    }

    public boolean useGps() {
        return sharedPref.getBoolean(GPS_ACTIVE_SETTING, DEFAULT_GPS);
    }

    public long getMinInterval() {
        return sharedPref.getLong(GPS_MIN_INTERVAL_SETTING, DEFAULT_MIN_INTERVAL);
    }

    public float getMinDistance() {
        return sharedPref.getFloat(GPS_MIN_DISTANCE_SETTING, DEFAULT_MIN_DISTANCE);
    }

    public String getBluetoothDevice() {
        return sharedPref.getString(BLUETOOTH_DEVICE_SETTINGS, null);
    }

    public int getObdInterval() {
        return sharedPref.getInt(OBD_POLLING_INTERVAL_MS, DEFAULT_OBD_INTERVAL);
    }

    public Set<String> getSelectedObdMessages() {
        return sharedPref.getStringSet(OBD_MESSAGES, new HashSet<String>());
    }

}
