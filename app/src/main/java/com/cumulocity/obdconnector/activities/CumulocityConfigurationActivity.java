/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cumulocity.obdconnector.ApplicationProperties;
import com.cumulocity.obdconnector.AsyncResponse;
import com.cumulocity.obdconnector.R;
import com.cumulocity.obdconnector.mqtt.BootstrapMqttClient;
import com.cumulocity.obdconnector.mqtt.CumulocityConfiguration;
import com.cumulocity.obdconnector.mqtt.CumulocityConfigurationBuilder;

import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_PASSWORD_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_SSL_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_TENANT_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_URI_SETTING;
import static com.cumulocity.obdconnector.ApplicationProperties.CUMULOCITY_USER_SETTING;

public class CumulocityConfigurationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "C8YConfigActivity";
    private static final int REQUEST_CODE_PHONE_STATE = 1;
    private static final int REQUEST_CODE_LOCATION = 2;

    private ApplicationProperties properties;

    private String cumulocityUri;
    private String cumulocityTenant;
    private String cumulocityUser;
    private String cumulocityPassword;
    private boolean cumulocitySslActive;
    private String imei;

    private Button saveButton;
    private Button bootstrapButton;
    private TextView connectedTenant;
    private EditText uriSelector;
    private Switch sslSelector;
    private TextView deviceIdLabel;
    private TextView deviceIdValue;
    private LinearLayout mainLayout;
    private ProgressBar waitingForBootstrap;

    private BootstrapMqttClient bootstrapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cumulocity_settings);
        properties = new ApplicationProperties(this);

        this.cumulocityUri = properties.getUri();
        this.cumulocityTenant = properties.getTenant();
        this.cumulocityUser = properties.getUsername();
        this.cumulocityPassword = properties.getPassword();
        this.cumulocitySslActive = properties.useSsl();

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_PHONE_STATE);
        } else {
            this.imei = getImei();
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }

        uriSelector = findViewById(R.id.c8y_uri_setting);
        uriSelector.setText(cumulocityUri);
        sslSelector = findViewById(R.id.c8y_ssl_setting);
        sslSelector.setChecked(cumulocitySslActive);
        connectedTenant = findViewById((R.id.active_tenant_label));
        bootstrapButton = findViewById(R.id.bootstrap_btn);
        saveButton = findViewById(R.id.next_screen);
        deviceIdLabel = findViewById(R.id.device_id_label);
        deviceIdValue = findViewById(R.id.device_id_value);
        deviceIdValue.setText(imei);
        LayoutInflater inflater = LayoutInflater.from(this);
        waitingForBootstrap = (ProgressBar) inflater.inflate(R.layout.spinning_progress_bar, null, false);

        mainLayout = (LinearLayout)findViewById(R.id.connection_settings_main_layout);

        // if full credentials available
        if (!TextUtils.isEmpty(cumulocityTenant) && !TextUtils.isEmpty(cumulocityUser) && !TextUtils.isEmpty(cumulocityPassword)) {
            bootstrapButton.setText(R.string.clear_credentials);
            uriSelector.setEnabled(false);
            bootstrapButton.setOnClickListener(new ClearCredentials());
            connectedTenant.setText(cumulocityTenant);
        } else {
            saveButton.setEnabled(false);
            uriSelector.setEnabled(true);
            bootstrapButton.setOnClickListener(new Bootstrap());
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = properties.edit();
                editor.putString(CUMULOCITY_URI_SETTING, uriSelector.getEditableText().toString());
                editor.putBoolean(CUMULOCITY_SSL_SETTING, sslSelector.isChecked());
                editor.commit();

                Log.d(TAG, "intent for obd screen");
                Intent intent = new Intent(getApplicationContext(), ObdConfigurationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bootstrapClient != null) {
            bootstrapClient.cancel(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.imei = getImei();
                    deviceIdValue.setText(imei);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // make gps available
                } else {
                    // disable completely
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    private class ClearCredentials implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            clearCredentials();
            Button currentView = (Button) view;
            currentView.setOnClickListener(new Bootstrap());
            currentView.setText(R.string.bootstrap);
            saveButton.setEnabled(false);
            connectedTenant.setText(R.string.not_connected);
            uriSelector.setEnabled(true);
        }

        private void clearCredentials() {
            SharedPreferences.Editor editor = properties.edit();
            editor.remove(CUMULOCITY_TENANT_SETTING);
            editor.remove(CUMULOCITY_USER_SETTING);
            editor.remove(CUMULOCITY_PASSWORD_SETTING);
            editor.commit();
        }
    }

    private class Bootstrap implements  View.OnClickListener, AsyncResponse {

        @Override
        public void onClick(View view) {
            Log.d(TAG, imei);
            mainLayout.addView(waitingForBootstrap);
            CumulocityConfiguration config = new CumulocityConfigurationBuilder()
                    .withUri(cumulocityUri)
                    .withSsl(cumulocitySslActive)
                    .buildBootstrap();

            bootstrapClient = new BootstrapMqttClient(getApplicationContext(), this, config, imei);
            bootstrapClient.execute();
        }

        @Override
        public void taskCompleted() {
            waitingForBootstrap.setVisibility(View.GONE);
            saveButton.setEnabled(true);
            bootstrapButton.setText(R.string.clear_credentials);
            connectedTenant.setText(properties.getTenant());
            uriSelector.setEnabled(false);
            bootstrapButton.setOnClickListener(new ClearCredentials());
        }
    }

}