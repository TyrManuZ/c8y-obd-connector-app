package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;

public class AbsoluteLoadMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Engine load absolute";

    public AbsoluteLoadMessage() {
        super(new AbsoluteLoadCommand());
    }

    public AbsoluteLoadMessage(boolean includeTime) {
        super(new AbsoluteLoadCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
