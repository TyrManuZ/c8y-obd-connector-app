package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;

public class RuntimeMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Engine runtime";

    public RuntimeMessage() {
        super(new RuntimeCommand());
    }

    public RuntimeMessage(boolean includeTime) {
        super(new RuntimeCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
