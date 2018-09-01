/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;

public class AirIntakeTemperatureMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Air intake temperature";

    public AirIntakeTemperatureMessage() {
        super(new AirIntakeTemperatureCommand());
    }

    public AirIntakeTemperatureMessage(boolean includeTime) {
        super(new AirIntakeTemperatureCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
