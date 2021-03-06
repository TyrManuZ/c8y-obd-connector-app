/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;

public class FuelLevelMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Current fuel level";

    public FuelLevelMessage() {
        super(new FuelLevelCommand());
    }

    public FuelLevelMessage(boolean includeTime) {
        super(new FuelLevelCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
