package com.cumulocity.obdconnector.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ObdMessage {

    String getMessage(InputStream input, OutputStream output) throws IOException, InterruptedException;

    String getCommandId();
}
