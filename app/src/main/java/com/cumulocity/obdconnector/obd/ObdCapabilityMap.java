package com.cumulocity.obdconnector.obd;

import com.cumulocity.obdconnector.messages.ObdMessage;
import com.cumulocity.obdconnector.messages.impl.AbsoluteLoadMessage;
import com.cumulocity.obdconnector.messages.impl.AbstractObdMessage;
import com.cumulocity.obdconnector.messages.impl.AirFuelRatioMessage;
import com.cumulocity.obdconnector.messages.impl.AirIntakeTemperatureMessage;
import com.cumulocity.obdconnector.messages.impl.AmbientAirTemperatureMessage;
import com.cumulocity.obdconnector.messages.impl.BarometricPressureMessage;
import com.cumulocity.obdconnector.messages.impl.ConsumptionRateMessage;
import com.cumulocity.obdconnector.messages.impl.EngineCoolantTemperatureMessage;
import com.cumulocity.obdconnector.messages.impl.FuelLevelMessage;
import com.cumulocity.obdconnector.messages.impl.FuelPressureMessage;
import com.cumulocity.obdconnector.messages.impl.FuelRailPressureMessage;
import com.cumulocity.obdconnector.messages.impl.LoadMessage;
import com.cumulocity.obdconnector.messages.impl.OilTemperatureMessage;
import com.cumulocity.obdconnector.messages.impl.RpmMessage;
import com.cumulocity.obdconnector.messages.impl.RuntimeMessage;
import com.cumulocity.obdconnector.messages.impl.SpeedMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObdCapabilityMap {

    private final Map<String, ObdMessage> availableMessages;

    public ObdCapabilityMap() {
        this.availableMessages = new HashMap<>();
        addMessage(new RpmMessage());
        addMessage(new SpeedMessage());
        addMessage(new AbsoluteLoadMessage());
        addMessage(new AirFuelRatioMessage());
        addMessage(new AirIntakeTemperatureMessage());
        addMessage(new AmbientAirTemperatureMessage());
        addMessage(new BarometricPressureMessage());
        addMessage(new ConsumptionRateMessage());
        addMessage(new EngineCoolantTemperatureMessage());
        addMessage(new FuelLevelMessage());
        addMessage(new FuelPressureMessage());
        addMessage(new FuelRailPressureMessage());
        addMessage(new LoadMessage());
        addMessage(new OilTemperatureMessage());
        addMessage(new RuntimeMessage());

    }

    public List<ObdMessage> getAvailableMessages(String binaryAvailableCommands) {
        List<ObdMessage> availableMessages = new ArrayList<>();
        for(int i = 0; i < binaryAvailableCommands.length(); i++) {
            char currentPosition = binaryAvailableCommands.charAt(i);
            boolean pidAllowed;
            if (currentPosition == '1') {
                pidAllowed = true;
            } else {
                pidAllowed = false;
            }
            if (pidAllowed) {
                ObdMessage message = this.availableMessages.get(toHex(i));
                if (message != null) {
                    availableMessages.add(message);
                }
            }
        }
        return availableMessages;
    }

    public ObdMessage getObdMessage(String pid) {
        return availableMessages.get(pid);
    }

    private String toHex(int i) {
        return String.format("%02X", i);
    }

    private void addMessage(ObdMessage message) {
        this.availableMessages.put(message.getCommandId(), message);
    }
}
