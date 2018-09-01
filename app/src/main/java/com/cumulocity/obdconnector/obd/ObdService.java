package com.cumulocity.obdconnector.obd;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cumulocity.obdconnector.ApplicationProperties;
import com.cumulocity.obdconnector.BluetoothListenerThread;
import com.cumulocity.obdconnector.messages.ObdMessage;
import com.cumulocity.obdconnector.mqtt.CumulocityConfiguration;
import com.cumulocity.obdconnector.GpsLocationListener;
import com.cumulocity.obdconnector.activities.WelcomeActivity;
import com.cumulocity.obdconnector.R;
import com.cumulocity.obdconnector.mqtt.CumulocityConfigurationBuilder;
import com.cumulocity.obdconnector.mqtt.MqttHelper;
import com.cumulocity.obdconnector.mqtt.MqttMessengerService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ObdService extends Service {
    private static final int NOTIFICATION_ID = 1;

    public static final String SERVICE_START = "StartService";
    public static final String SERVICE_STOP = "StopService";

    public static boolean isServiceRunning = false;
    private BluetoothListenerThread bluetoothListenerThread;
    private LocationManager mLocationManager;
    private GpsLocationListener gpsLocationListener;
    private boolean includeGps = false;
    private ApplicationProperties properties;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "service creation");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "toggle service");
        if (intent.getAction().equals(SERVICE_START)) {
            properties = new ApplicationProperties(this);
            includeGps = properties.useGps();
            CumulocityConfiguration cumulocityConfiguration = new CumulocityConfigurationBuilder()
                    .withUri(properties.getUri())
                    .withTenant(properties.getTenant())
                    .withUsername(properties.getUsername())
                    .withPassword(properties.getPassword())
                    .withSsl(properties.useSsl())
                    .build();
            startServiceWithNotification(
                    properties.getBluetoothDevice(),
                    properties.getObdInterval(),
                    cumulocityConfiguration
            );
        } else {
            stopMyService();
        }
        return START_STICKY;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        Log.d("Service", "service destroyed");
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void startServiceWithNotification(String deviceAddress, int intervalMs, CumulocityConfiguration cumulocityConfiguration) {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
        notificationIntent.setAction("MqttAction");  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_name);

        String CHANNEL_ID = "c8y";// The id of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Cumulocity", importance);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }


        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("OBD service running...")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
                .setChannelId(CHANNEL_ID)
                .build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
        Log.d("Service", "start the service");
        startForeground(NOTIFICATION_ID, notification);

        // Resolve OBD commands

        ObdCapabilityMap obdCapabilities = new ObdCapabilityMap();
        Set<String> selectedObdMessages = properties.getSelectedObdMessages();
        Set<ObdMessage> obdMessages = new HashSet<>();
        for(String selectedObdMessage : selectedObdMessages) {
            ObdMessage supportedObdMessage = obdCapabilities.getObdMessage(selectedObdMessage);
            if(supportedObdMessage != null) {
                obdMessages.add(supportedObdMessage);
            }
        }

        // MQTT

        MqttHelper mqttHelper = new MqttHelper(getApplicationContext(), cumulocityConfiguration, deviceAddress);
        MqttMessengerService messengerService = new MqttMessengerService(mqttHelper);

        // Bluetooth

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice selectedDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

        Log.d("Service", selectedDevice.getAddress());
        BluetoothSocket mmSocket = null;

        try {
            mmSocket = selectedDevice.createRfcommSocketToServiceRecord(selectedDevice.getUuids()[0].getUuid());
        } catch (IOException e) {
            Log.e("Service", "Socket's create() method failed", e);
        }
        mBluetoothAdapter.cancelDiscovery();

        try {
            Log.d("Service", "Try connect to bluetooth device");
            mmSocket.connect();
            Log.d("Service", "Connected to bluetooth device");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Service", "Could not close the client socket", closeException);
            }
            return;
        }

        bluetoothListenerThread = new BluetoothListenerThread(mmSocket, messengerService, obdMessages, intervalMs);
        bluetoothListenerThread.start();

        if (includeGps) {
            gpsLocationListener = new GpsLocationListener(messengerService);
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, properties.getMinInterval(), properties.getMinDistance(), gpsLocationListener);
        }
    }

    void stopMyService() {
        Log.d("Service", "stop the service");
        if (bluetoothListenerThread != null && bluetoothListenerThread.isAlive()) {
            bluetoothListenerThread.cancel();
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(gpsLocationListener);
            } catch (Exception e) {

            }
        }
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}