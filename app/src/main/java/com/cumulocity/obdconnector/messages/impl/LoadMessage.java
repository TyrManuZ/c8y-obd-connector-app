/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;

public class LoadMessage extends AbstractObdMessage {

    private static final String DISPLAY_NAME = "Engine load";

    public LoadMessage() {
        super(new LoadCommand());
    }

    public LoadMessage(boolean includeTime) {
        super(new LoadCommand(), includeTime);
    }

    @Override
    public String toString() {
        return DISPLAY_NAME;
    }
}
