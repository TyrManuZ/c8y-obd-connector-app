/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cumulocity.obdconnector.ApplicationProperties;
import com.cumulocity.obdconnector.AsyncResponse;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_PASSWORD_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_TENANT_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_USER_SETTING;

public class BootstrapMqttClient extends AsyncTask<String, Integer, CumulocityConfiguration> {
    private static final String TAG = "BootstrapMqttClient";

    private static final String BOOTSTRAP_REQUEST_TOPIC = "s/ucr";
    private static final String BOOTSTRAP_SUBSCRIBE_TOPIC = "s/dcr";
    private static final String CREDENTIALS_RESPONSE_CODE = "70";

    private MqttAndroidClient mqttAndroidClient;
    private final AsyncResponse callback;
    private final CumulocityConfiguration cumulocityConfiguration;
    private final Context context;
    private final String imei;

    private String credentials;
    private AtomicBoolean hasCredentials;

    public BootstrapMqttClient(Context context, AsyncResponse callback, CumulocityConfiguration cumulocityConfiguration, String imei){
        this.context = context;
        this.callback = callback;
        this.cumulocityConfiguration = cumulocityConfiguration;
        this.imei = imei;
    }

    private void connect(String username, String password){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttAndroidClient.connect(mqttConnectOptions);
        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    private void requestCredentials() throws MqttException {
        mqttAndroidClient.publish(BOOTSTRAP_REQUEST_TOPIC, new byte[]{}, 0, false);
    }

    @Override
    protected void onPreExecute() {
        mqttAndroidClient = new MqttAndroidClient(context, cumulocityConfiguration.getMqttUri(), imei);
        hasCredentials = new AtomicBoolean(false);
        Log.i(TAG, "Connecting for bootstrap ...");
    }

    @Override
    protected void onPostExecute(CumulocityConfiguration result) {
        if (result == null) {
            Toast.makeText(context, "Problem during bootstrap", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Successfully connected to tenant: " + result.getTenant(), Toast.LENGTH_LONG).show();
            ApplicationProperties properties = new ApplicationProperties(context);
            SharedPreferences.Editor editor = properties.edit();
            editor.putString(CUMULOCITY_TENANT_SETTING, result.getTenant());
            editor.putString(CUMULOCITY_USER_SETTING, result.getUsername());
            editor.putString(CUMULOCITY_PASSWORD_SETTING, result.getPassword());
            editor.commit();
            callback.taskCompleted();
        }
    }

    @Override
    protected CumulocityConfiguration doInBackground(String... strings) {
        connect(cumulocityConfiguration.getMqttUsername(), cumulocityConfiguration.getPassword());
        while (!mqttAndroidClient.isConnected()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // interrupted
            }
        }
        try {
            mqttAndroidClient.subscribe(BOOTSTRAP_SUBSCRIBE_TOPIC, 2, new CredentialsListener());
            while(!hasCredentials.get()) {
                requestCredentials();
                Thread.sleep(1000);
            }
        } catch (MqttException e) {
            // Could not subscribe
        } catch (InterruptedException e) {
            // interrupted
        }
        disconnectClient();
        return processCredentials(credentials);
    }

    private CumulocityConfiguration processCredentials(String credentials) {
        String[] splittedCredentials = credentials.split(",");
        if (splittedCredentials.length != 4 || !splittedCredentials[0].equals(CREDENTIALS_RESPONSE_CODE)) {
            Log.e(TAG, "Could not bootstrap. Received message: " + credentials);
            return null;
        }
        return new CumulocityConfigurationBuilder()
                .withSsl(cumulocityConfiguration.isSsl())
                .withUri(cumulocityConfiguration.getUri())
                .withTenant(splittedCredentials[1])
                .withUsername(splittedCredentials[2])
                .withPassword(splittedCredentials[3])
                .build();
    }

    private void disconnectClient() {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.disconnect();
            } catch (MqttException e) {
                Log.w(TAG, "Could not gracefully disconnect bootstrap client");
            }
        }
    }

    private class CredentialsListener implements IMqttMessageListener {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            credentials = new String(message.getPayload());
            hasCredentials.set(true);
        }
    }
}
