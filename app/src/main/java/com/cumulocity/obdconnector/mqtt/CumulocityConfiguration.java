/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.mqtt;

public class CumulocityConfiguration {

    public static final String MQTT_SSL_PORT = "8883";
    public static final String MQTT_NO_SSL_PORT = "1883";

    private final String uri;
    private final String tenant;
    private final String username;
    private final String password;
    private final boolean ssl;

    public CumulocityConfiguration(String uri, String tenant, String username, String password, boolean ssl) {
        this.uri = uri;
        this.tenant = tenant;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
    }

    public String getUri() {
        return uri;
    }

    public String getTenant() {
        return tenant;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getMqttUsername() {
        return tenant + "/" + username;
    }

    public String getMqttUri() {
        if (ssl) {
            return "ssl://" + uri + ":" + MQTT_SSL_PORT;
        }
        return "tcp://" + uri + ":" + MQTT_NO_SSL_PORT;
    }

}
