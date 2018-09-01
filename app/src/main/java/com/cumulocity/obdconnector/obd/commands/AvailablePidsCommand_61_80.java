/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.obd.commands;

import com.github.pires.obd.commands.protocol.AvailablePidsCommand;

public class AvailablePidsCommand_61_80 extends AvailablePidsCommand {

    private static final String NAME = "Available PIDs 81-A0";

    public AvailablePidsCommand_61_80() {
        super("01 60");
    }

    public AvailablePidsCommand_61_80(AvailablePidsCommand_61_80 other) {
        super(other);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
