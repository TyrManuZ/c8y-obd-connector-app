/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.cumulocity.obdconnector.messages.ObdMessage;
import com.cumulocity.obdconnector.mqtt.CumulocitySmartRestBulkMessage;
import com.cumulocity.obdconnector.mqtt.MqttMessengerService;
import com.cumulocity.obdconnector.obd.ObdCommands;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothListenerThread extends Thread {
    private static final String TAG = "BluetoothListenerThread";

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final MqttMessengerService messengerService;
    private final ObdCommands obdCommandLibrary;
    private final int intervalMs;
    private final Set<ObdMessage> obdMessages;


    public BluetoothListenerThread(BluetoothSocket socket, MqttMessengerService messengerService, Set<ObdMessage> obdMessages, int intervalMs) {
        this.intervalMs = intervalMs;
        this.obdMessages = obdMessages;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.messengerService = messengerService;

        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        obdCommandLibrary = new ObdCommands(mmInStream, mmOutStream);
    }

    public void run() {
        try {
            obdCommandLibrary.echoOff();
            obdCommandLibrary.lineFeedOff();
            obdCommandLibrary.setTimeout();
            obdCommandLibrary.selectProtocol();
            messengerService.sendAvailableCommands(obdCommandLibrary.getAvailableCommands());
            while (true) {
                CumulocitySmartRestBulkMessage bulkMessage = new CumulocitySmartRestBulkMessage();
                for (ObdMessage obdMessage : obdMessages) {
                    try {
                        bulkMessage.addMessage(obdMessage.getMessage(mmInStream, mmOutStream));

                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected", e);
                        break;
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Command was interrupted", e);
                    } catch (NoDataException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
                messengerService.sendBulkMessage(bulkMessage);
                Thread.sleep(intervalMs);
            }
        } catch (IOException e) {
            Log.d(TAG, "Input stream was disconnected", e);
        } catch (InterruptedException e) {
            Log.d(TAG, "Command was interrupted", e);
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
