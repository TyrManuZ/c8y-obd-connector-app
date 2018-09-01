/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;

public class AirFuelRatioMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Air/Fuel ratio";

    public AirFuelRatioMessage() {
        super(new AirFuelRatioCommand());
    }

    public AirFuelRatioMessage(boolean includeTime) {
        super(new AirFuelRatioCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
