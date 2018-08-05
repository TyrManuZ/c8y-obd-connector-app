package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

public class EngineCoolantTemperatureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Engine coolant temperature";

    public EngineCoolantTemperatureMessage() {
        super(new EngineCoolantTemperatureCommand());
    }

    public EngineCoolantTemperatureMessage(boolean includeTime) {
        super(new EngineCoolantTemperatureCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
