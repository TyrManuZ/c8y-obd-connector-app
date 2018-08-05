package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;

public class AmbientAirTemperatureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Ambient air temperature";

    public AmbientAirTemperatureMessage() {
        super(new AmbientAirTemperatureCommand());
    }

    public AmbientAirTemperatureMessage(boolean includeTime) {
        super(new AmbientAirTemperatureCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
