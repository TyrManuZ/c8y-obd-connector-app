/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.mqtt;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class CumulocitySmartRestBulkMessage {

    private final List<String> messages;

    public  CumulocitySmartRestBulkMessage() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    @Override
    public String toString() {
        return TextUtils.join("\r\n", messages);
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }
}
