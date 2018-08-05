package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;

public class BarometricPressureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Barometric pressure";

    public BarometricPressureMessage() {
        super(new BarometricPressureCommand());
    }

    public BarometricPressureMessage(boolean includeTime) {
        super(new BarometricPressureCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
