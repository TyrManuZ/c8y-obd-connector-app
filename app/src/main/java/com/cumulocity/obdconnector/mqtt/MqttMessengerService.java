package com.cumulocity.obdconnector.mqtt;

import com.cumulocity.obdconnector.mqtt.MqttHelper;

public class MqttMessengerService {

    public static final String SMARTREST_DEFAULT_TOPIC = "s/ud";
    public static final String SMARTREST_STATIC_TOPIC = "s/us";

    private final MqttHelper mqttHelper;

    public MqttMessengerService(MqttHelper mqttHelper) {
        this.mqttHelper = mqttHelper;
    }

    public void sendBulkMessage(CumulocitySmartRestBulkMessage message) {
        mqttHelper.publishData(SMARTREST_DEFAULT_TOPIC, message.toString());
    }

    public void sendGpsLocation(double latitude, double longitude) {
        mqttHelper.publishData(SMARTREST_STATIC_TOPIC, "402," + String.valueOf(latitude) + "," + String.valueOf(longitude));
    }

    public void sendAvailableCommands(String commands) {
        mqttHelper.publishData(SMARTREST_DEFAULT_TOPIC, "PIDS,," + commands);
    }
}
