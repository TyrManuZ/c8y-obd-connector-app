package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;

public class OilTemperatureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Oil temperature";

    public OilTemperatureMessage() {
        super(new OilTempCommand());
    }

    public OilTemperatureMessage(boolean includeTime) {
        super(new OilTempCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
