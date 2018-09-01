/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;

public class FuelPressureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Fuel pressure";

    public FuelPressureMessage() {
        super(new FuelPressureCommand());
    }

    public FuelPressureMessage(boolean includeTime) {
        super(new FuelPressureCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
