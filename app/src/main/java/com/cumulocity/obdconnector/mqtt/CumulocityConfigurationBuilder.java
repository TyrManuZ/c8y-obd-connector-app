package com.cumulocity.obdconnector.mqtt;

public class CumulocityConfigurationBuilder {

    private static final String DEFAULT_URI = "mqtt.cumulocity.com";
    private static final boolean DEFAULT_SSL = true;

    private static final String BOOTSTRAP_TENANT = "management";
    private static final String BOOTSTRAP_USER = "devicebootstrap";
    private static final String BOOTSTRAP_PASSWORD = "Fhdt1bb1f";

    private String uri = DEFAULT_URI;
    private String tenant;
    private String username;
    private String password;
    private boolean ssl = DEFAULT_SSL;

    public CumulocityConfigurationBuilder withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public CumulocityConfigurationBuilder withTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public CumulocityConfigurationBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public CumulocityConfigurationBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public CumulocityConfigurationBuilder withSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public CumulocityConfiguration build() {
        return new CumulocityConfiguration(uri, tenant, username, password, ssl);
    }

    public CumulocityConfiguration buildBootstrap() {
        return new CumulocityConfiguration(uri, BOOTSTRAP_TENANT, BOOTSTRAP_USER, BOOTSTRAP_PASSWORD, ssl);
    }
}