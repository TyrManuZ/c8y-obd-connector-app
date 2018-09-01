/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.pressure.FuelRailPressureCommand;

public class FuelRailPressureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Fuel rail pressure";

    public FuelRailPressureMessage() {
        super(new FuelRailPressureCommand());
    }

    public FuelRailPressureMessage(boolean includeTime) {
        super(new FuelRailPressureCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
