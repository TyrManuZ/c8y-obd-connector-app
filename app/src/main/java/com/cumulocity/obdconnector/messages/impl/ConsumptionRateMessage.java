/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;

public class ConsumptionRateMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Fuel consumption rate";

    public ConsumptionRateMessage() {
        super(new ConsumptionRateCommand());
    }

    public ConsumptionRateMessage(boolean includeTime) {
        super(new ConsumptionRateCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
