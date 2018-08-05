package com.cumulocity.obdconnector.obd.commands;

import com.github.pires.obd.commands.protocol.AvailablePidsCommand;

public class AvailablePidsCommand_81_A0 extends AvailablePidsCommand {

    private static final String NAME = "Available PIDs 81-A0";

    public AvailablePidsCommand_81_A0() {
        super("01 80");
    }

    public AvailablePidsCommand_81_A0(AvailablePidsCommand_81_A0 other) {
        super(other);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
