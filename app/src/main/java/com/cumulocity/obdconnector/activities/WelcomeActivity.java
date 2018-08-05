package com.cumulocity.obdconnector.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.cumulocity.obdconnector.DisplayableBluetoothDevice;
import com.cumulocity.obdconnector.R;
import com.cumulocity.obdconnector.obd.ObdService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ObdService.isServiceRunning) {
            setStartingView();
        } else {
            setStoppingView();
        }
    }

    private void setStartingView() {
        setContentView(R.layout.welcome_inactive_service);

        final Button nextButton = findViewById(R.id.next_screen);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Open C8Y settings");
                Intent intent = new Intent(getApplicationContext(), CumulocityConfigurationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setStoppingView() {
        setContentView(R.layout.welcome_active_service);
        final Button stopService = findViewById(R.id.stop_service);
        stopService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "try stop service");
                Intent startIntent = new Intent(getApplicationContext(), ObdService.class);
                startIntent.setAction(ObdService.SERVICE_STOP);
                startService(startIntent);
                setContentView(R.layout.welcome_inactive_service);
            }
        });
    }
}