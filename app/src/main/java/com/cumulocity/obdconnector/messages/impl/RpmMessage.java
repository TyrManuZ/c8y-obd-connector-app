package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.engine.RPMCommand;

public class RpmMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "RPM";

    public RpmMessage() {
        super(new RPMCommand());
    }

    public RpmMessage(boolean includeTime) {
        super(new RPMCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
