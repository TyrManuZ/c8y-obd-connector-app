/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.cumulocity.obdconnector.ApplicationProperties;
import com.cumulocity.obdconnector.DisplayableBluetoothDevice;
import com.cumulocity.obdconnector.R;
import com.cumulocity.obdconnector.messages.ObdMessage;
import com.cumulocity.obdconnector.obd.ObdCapabilityMap;
import com.cumulocity.obdconnector.obd.ObdCommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cumulocity.obdconnector.ApplicationProperties.BLUETOOTH_DEVICE_SETTINGS;
import static com.cumulocity.obdconnector.ApplicationProperties.OBD_MESSAGES;

public class ObdConfigurationActivity extends AppCompatActivity {

    private static final String TAG = "OBDConfigActivity";

    private ApplicationProperties properties;
    private ObdCapabilityMap obdCapabilities;
    private Button scanButton;
    private Spinner bluetoothDeviceSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ObdConfigurationActivity creating...");
        setContentView(R.layout.obd_configuration_screen);
        obdCapabilities = new ObdCapabilityMap();
        properties = new ApplicationProperties(this);

        bluetoothDeviceSelector = findViewById(R.id.selector_bluetooth_device);
        bluetoothDeviceSelector.setAdapter(getDisplayableBluetoothDevices());

        scanButton = findViewById(R.id.scan_obd);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayableBluetoothDevice selectedDevice = (DisplayableBluetoothDevice) bluetoothDeviceSelector.getSelectedItem();
                ObdCapabilitiesTask task = new ObdCapabilitiesTask();
                task.execute(selectedDevice.getAddress());
            }
        });
    }

    private ArrayAdapter<DisplayableBluetoothDevice> getDisplayableBluetoothDevices() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            List<DisplayableBluetoothDevice> displayedBluetoothDevices = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                displayedBluetoothDevices.add(new DisplayableBluetoothDevice(device.getName(), device.getAddress()));
            }
            Log.d(TAG, displayedBluetoothDevices.toString());
            return new ArrayAdapter<>(this, R.layout.bluetooth_device_list, displayedBluetoothDevices);
        } catch (Exception e) {
            Log.e(TAG, "Error",e);
            return null;
        }

    }

    private class ObdCapabilitiesTask extends AsyncTask<String, Integer, String> {

        private static final String TAG = "ObdCapabilitiesTask";

        private final ProgressBar loadingAvailableCommandsBar;
        private final LinearLayout obdConfigurationLayout;
        private final ListView availableCommands;
        private final Button nextScreenButton;
        private BluetoothSocket mmSocket;
        private List<ObdMessage> supportedMessages;

        protected ObdCapabilitiesTask() {
            obdConfigurationLayout = findViewById(R.id.obd_configuration_layout);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            loadingAvailableCommandsBar = (ProgressBar) inflater.inflate(R.layout.spinning_progress_bar, obdConfigurationLayout, false);
            availableCommands = (ListView) inflater.inflate(R.layout.obd_command_list, obdConfigurationLayout, false);
            nextScreenButton = (Button) inflater.inflate(R.layout.next_screen_button, obdConfigurationLayout, false);
        }

        @Override
        protected void onPreExecute() {
            obdConfigurationLayout.removeView(scanButton);
            obdConfigurationLayout.removeView(bluetoothDeviceSelector);
            obdConfigurationLayout.addView(loadingAvailableCommandsBar);
        }

        @Override
        protected String doInBackground(String... strings) {
            return scanForAvailableObdCommands(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplication(), "Could not connect to OBD", Toast.LENGTH_LONG).show();
                loadingAvailableCommandsBar.setVisibility(View.GONE);
                obdConfigurationLayout.addView(bluetoothDeviceSelector);
                obdConfigurationLayout.addView(scanButton);
            } else {
                loadingAvailableCommandsBar.setVisibility(View.GONE);
                supportedMessages = obdCapabilities.getAvailableMessages(result);
                ArrayAdapter<ObdMessage> messageAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.obd_command_entry, supportedMessages);
                availableCommands.setAdapter(messageAdapter);
                Log.d(TAG, "Add list to view");
                obdConfigurationLayout.addView(availableCommands);

                nextScreenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveObdConfiguration();
                        Log.d(TAG, "intent for polling screen");
                        Intent intent = new Intent(getApplicationContext(), PollingConfigurationActivity.class);
                        startActivity(intent);
                    }
                });
                Log.d(TAG, "Add button to view");
                obdConfigurationLayout.addView(nextScreenButton);
            }
            if (mmSocket != null) {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Cloud not close Bluetooth socket", e);
                }
            }
        }

        private void saveObdConfiguration() {
            Set<String> selectedObdMessages = new HashSet<>();
            for(int i = 0; i < availableCommands.getChildCount(); i++) {
                if (((Switch)availableCommands.getChildAt(i)).isChecked()) {
                    selectedObdMessages.add(supportedMessages.get(i).getCommandId());
                }
            }
            DisplayableBluetoothDevice selectedDevice = (DisplayableBluetoothDevice) bluetoothDeviceSelector.getSelectedItem();
            SharedPreferences.Editor editor = properties.edit();
            editor.putString(BLUETOOTH_DEVICE_SETTINGS, selectedDevice.getAddress());
            editor.putStringSet(OBD_MESSAGES, selectedObdMessages);
            editor.commit();
        }

        private String scanForAvailableObdCommands(String deviceAddress) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice selectedDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

            Log.d(TAG, selectedDevice.getAddress());
            mmSocket = null;

            try {
                mmSocket = selectedDevice.createRfcommSocketToServiceRecord(selectedDevice.getUuids()[0].getUuid());
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mBluetoothAdapter.cancelDiscovery();

            try {
                Log.d(TAG, "Try connect to bluetooth device");
                mmSocket.connect();
                Log.d(TAG, "Connected to bluetooth device");
                ObdCommands obdCommands = new ObdCommands(mmSocket.getInputStream(), mmSocket.getOutputStream());
                obdCommands.echoOff();
                obdCommands.lineFeedOff();
                obdCommands.setTimeout();
                obdCommands.selectProtocol();
                return obdCommands.getAvailableCommands();
            } catch (IOException connectException) {
                Log.w(TAG, "Cannot establish bluetooth connection");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Problem sending bluetooth commands", e);
                return null;
            }
        }

    }
}
