/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ObdMessage {

    String getMessage(InputStream input, OutputStream output) throws IOException, InterruptedException;

    String getCommandId();
}
