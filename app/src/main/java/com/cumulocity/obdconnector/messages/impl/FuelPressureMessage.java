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
