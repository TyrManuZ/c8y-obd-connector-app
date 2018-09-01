/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.SpeedCommand;

public class SpeedMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Speed";

    public SpeedMessage() {
        super(new SpeedCommand());
    }

    public SpeedMessage(boolean includeTime) {
        super(new SpeedCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
