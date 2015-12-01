package com.suitbots.resq;

import android.bluetooth.BluetoothClass;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

/**
 * Created by cp on 11/27/15.
 */
public class Switch extends OpMode {

    public static int SWITCH_CHANNEL = 0;

    DeviceInterfaceModule dim;
    @Override public void init() {
        dim = hardwareMap.deviceInterfaceModule.get("dim");
        dim.setDigitalChannelMode(SWITCH_CHANNEL, DigitalChannelController.Mode.INPUT);
    }

    @Override public void loop() {
        if (dim.getDigitalChannelState(SWITCH_CHANNEL)) {
            telemetry.addData("Switch", "ON");
        } else {
            telemetry.addData("Switch", "OFF");
        }

        dim.setLED(0, gamepad1.x || dim.getDigitalChannelState(SWITCH_CHANNEL));
        dim.setLED(0, gamepad1.x);
        dim.setLED(1, gamepad1.y);
    }
}
