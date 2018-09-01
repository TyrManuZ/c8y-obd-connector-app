/*
 * Copyright (c) 2018. Tobias Sommer
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */

package com.cumulocity.obdconnector.obd;

import android.text.TextUtils;
import android.util.Log;

import com.cumulocity.obdconnector.obd.commands.AvailablePidsCommand_61_80;
import com.cumulocity.obdconnector.obd.commands.AvailablePidsCommand_81_A0;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_01_20;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_21_40;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_41_60;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ObdCommands {

    private static final String TAG = "ObdCommands";
    private static final String DEFAULT_AVAILABLE_PIDS = "00000000000000000000000000000000";

    private final InputStream inputStream;
    private final OutputStream outputStream;

    // Available commands
    private final AvailablePidsCommand_01_20 availablePidsCommand_01_20;
    private final AvailablePidsCommand_21_40 availablePidsCommand_21_40;
    private final AvailablePidsCommand_41_60 availablePidsCommand_41_60;
    private final AvailablePidsCommand_61_80 availablePidsCommand_61_80;
    private final AvailablePidsCommand_81_A0 availablePidsCommand_81_a0;

    // Protocol commands
    private final EchoOffCommand echoOffCommand;
    private final LineFeedOffCommand lineFeedOffCommand;
    private final TimeoutCommand timeoutCommand;
    private final SelectProtocolCommand selectProtocolCommand;
    private final HeadersOffCommand headersOffCommand;

    // Temperature commands
    private final AmbientAirTemperatureCommand ambientAirTemperatureCommand;
    private final EngineCoolantTemperatureCommand engineCoolantTemperatureCommand;
    private final AirIntakeTemperatureCommand airIntakeTemperatureCommand;
    private final OilTempCommand oilTempCommand;

    // Fuel commands
    private final AirFuelRatioCommand airFuelRatioCommand;
    private final ConsumptionRateCommand consumptionRateCommand;
    private final FuelLevelCommand fuelLevelCommand;

    // Engine commands
    private final RPMCommand rpmCommand;
    private final SpeedCommand speedCommand;

    private final DistanceMILOnCommand traveledCommand;


    public ObdCommands(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        availablePidsCommand_01_20 = new AvailablePidsCommand_01_20();
        availablePidsCommand_21_40 = new AvailablePidsCommand_21_40();
        availablePidsCommand_41_60 = new AvailablePidsCommand_41_60();
        availablePidsCommand_61_80 = new AvailablePidsCommand_61_80();
        availablePidsCommand_81_a0 = new AvailablePidsCommand_81_A0();

        echoOffCommand = new EchoOffCommand();
        lineFeedOffCommand = new LineFeedOffCommand();
        timeoutCommand = new TimeoutCommand(125);
        selectProtocolCommand = new SelectProtocolCommand(ObdProtocols.AUTO);
        headersOffCommand = new HeadersOffCommand();

        ambientAirTemperatureCommand = new AmbientAirTemperatureCommand();
        engineCoolantTemperatureCommand = new EngineCoolantTemperatureCommand();
        airIntakeTemperatureCommand = new AirIntakeTemperatureCommand();
        oilTempCommand = new OilTempCommand();

        airFuelRatioCommand = new AirFuelRatioCommand();
        consumptionRateCommand = new ConsumptionRateCommand();
        fuelLevelCommand = new FuelLevelCommand();

        rpmCommand = new RPMCommand();
        speedCommand = new SpeedCommand();
        traveledCommand = new DistanceMILOnCommand();
    }

    public void echoOff() throws IOException, InterruptedException {
        echoOffCommand.run(inputStream, outputStream);
    }

    public void lineFeedOff() throws IOException, InterruptedException {
        lineFeedOffCommand.run(inputStream, outputStream);
    }

    public void setTimeout() throws IOException, InterruptedException {
        timeoutCommand.run(inputStream, outputStream);
    }

    public void selectProtocol() throws IOException, InterruptedException {
        selectProtocolCommand.run(inputStream, outputStream);
    }

    public int getRPM() throws IOException, InterruptedException {
        rpmCommand.run(inputStream, outputStream);
        return rpmCommand.getRPM();
    }

    public float getFuelConsumption() throws IOException, InterruptedException {
        consumptionRateCommand.run(inputStream, outputStream);
        return consumptionRateCommand.getLitersPerHour();
    }

    public int getSpeed() throws  IOException, InterruptedException {
        speedCommand.run(inputStream, outputStream);
        return speedCommand.getMetricSpeed();
    }

    public int getDistanceTraveled() throws  IOException, InterruptedException {
        traveledCommand.run(inputStream, outputStream);
        return traveledCommand.getKm();
    }

    public float getFuelLevel() throws IOException, InterruptedException {
        fuelLevelCommand.run(inputStream, outputStream);
        return fuelLevelCommand.getFuelLevel();
    }

    public String getAvailableCommands() throws IOException, InterruptedException {
        String allAvailabilities = "1";
        try {
            availablePidsCommand_01_20.run(inputStream, outputStream);
            allAvailabilities = allAvailabilities.concat(hexToBinary(availablePidsCommand_01_20.getCalculatedResult()));
        } catch (NoDataException e) {
            allAvailabilities = allAvailabilities.concat(DEFAULT_AVAILABLE_PIDS);
        }
        try {
            availablePidsCommand_21_40.run(inputStream, outputStream);
            allAvailabilities = allAvailabilities.concat(hexToBinary(availablePidsCommand_21_40.getCalculatedResult()));
        } catch (NoDataException e) {
            allAvailabilities = allAvailabilities.concat(DEFAULT_AVAILABLE_PIDS);
        }
        try {
            availablePidsCommand_41_60.run(inputStream, outputStream);
            allAvailabilities = allAvailabilities.concat(hexToBinary(availablePidsCommand_41_60.getCalculatedResult()));
        } catch (NoDataException e) {
            allAvailabilities = allAvailabilities.concat(DEFAULT_AVAILABLE_PIDS);
        }
        try {
            availablePidsCommand_61_80.run(inputStream, outputStream);
            allAvailabilities = allAvailabilities.concat(hexToBinary(availablePidsCommand_61_80.getCalculatedResult()));
        } catch (NoDataException e) {
            allAvailabilities = allAvailabilities.concat(DEFAULT_AVAILABLE_PIDS);
        }
        try {
            availablePidsCommand_81_a0.run(inputStream, outputStream);
            allAvailabilities = allAvailabilities.concat(hexToBinary(availablePidsCommand_81_a0.getCalculatedResult()));
        } catch (NoDataException e) {
            allAvailabilities = allAvailabilities.concat(DEFAULT_AVAILABLE_PIDS);
        }
        return allAvailabilities;
    }

    private String hexToBinary(String hex) {
        long i = Long.parseLong(hex, 16);
        String bin = Long.toBinaryString(i);
        return bin;
    }
}
