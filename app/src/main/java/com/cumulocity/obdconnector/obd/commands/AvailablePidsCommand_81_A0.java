/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

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
