package com.cumulocity.obdconnector.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.cumulocity.obdconnector.ApplicationProperties;
import com.cumulocity.obdconnector.DisplayableBluetoothDevice;
import com.cumulocity.obdconnector.R;
import com.cumulocity.obdconnector.obd.ObdService;

public class PollingConfigurationActivity extends AppCompatActivity {

    private static final String TAG = "PollingConfigActivity";

    private ApplicationProperties properties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polling_configuration);

        properties = new ApplicationProperties(this);

        final Switch gpsActiveSwitch = findViewById(R.id.gps_active_setting);
        final EditText gpsMinIntervalEdit = findViewById(R.id.gps_min_interval_setting);
        final EditText gpsMinDistanceEdit = findViewById(R.id.gps_min_distance_setting);
        final EditText obdInterval = findViewById(R.id.obd_interval_setting);

        gpsActiveSwitch.setChecked(properties.useGps());
        gpsMinIntervalEdit.setText(String.valueOf(properties.getMinInterval()));
        gpsMinDistanceEdit.setText(String.valueOf(properties.getMinDistance()));
        obdInterval.setText(String.valueOf(properties.getObdInterval()));

        final Button startButton = findViewById(R.id.start_service);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "try start service");

                SharedPreferences.Editor editor = properties.edit();
                editor.putBoolean(ApplicationProperties.GPS_ACTIVE_SETTING, gpsActiveSwitch.isChecked());
                editor.putLong(ApplicationProperties.GPS_MIN_INTERVAL_SETTING, Long.valueOf(gpsMinIntervalEdit.getEditableText().toString()));
                editor.putFloat(ApplicationProperties.GPS_MIN_DISTANCE_SETTING, Float.valueOf(gpsMinDistanceEdit.getEditableText().toString()));
                editor.putInt(ApplicationProperties.OBD_POLLING_INTERVAL_MS, Integer.valueOf(obdInterval.getEditableText().toString()));
                editor.commit();

                Intent startIntent = new Intent(getApplicationContext(), ObdService.class);
                startIntent.setAction(ObdService.SERVICE_START);
                startService(startIntent);
            }
        });
    }
}
