/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages.impl;

import com.cumulocity.obdconnector.messages.ObdMessage;
import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractObdMessage implements ObdMessage {

    private static final String ID_PREFIX = "M";
    private static final String SEPARATOR = ",";

    private final ObdCommand command;
    private final boolean includeTime;

    public AbstractObdMessage(ObdCommand command) {
        this(command, false);
    }

    public AbstractObdMessage(ObdCommand command, boolean includeTime) {
        this.command = command;
        this.includeTime = includeTime;
    }

    @Override
    public String getCommandId() {
        return command.getCommandPID();
    }

    @Override
    public String getMessage(InputStream input, OutputStream output) throws IOException, InterruptedException {
        command.run(input, output);
        String result = command.getCalculatedResult();
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(ID_PREFIX);
        messageBuilder.append(getCommandId());
        messageBuilder.append(SEPARATOR);
        messageBuilder.append(getCurrentIsoTime());
        messageBuilder.append(SEPARATOR);
        messageBuilder.append(result);
        return messageBuilder.toString();
    }

    private String getCurrentIsoTime() {
        if (includeTime) {
            return "";
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return getCommandId();
    }
}
