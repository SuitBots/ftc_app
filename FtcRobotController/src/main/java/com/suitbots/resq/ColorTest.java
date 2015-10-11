package com.suitbots.resq;

import com.qualcomm.hardware.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class ColorTest extends OpMode {
    private ColorSensor color_sensor = null;

    @Override public void init() {
        color_sensor = hardwareMap.colorSensor.get("colorfore");
        assert(color_sensor instanceof ModernRoboticsI2cColorSensor);
    }

    @Override public void start() { color_sensor.enableLed(true); }
    @Override public void stop() { color_sensor.enableLed(false); }

    @Override public void loop() {
        if (gamepad1.left_bumper) { color_sensor.enableLed(true); }
        if (gamepad1.right_bumper) { color_sensor.enableLed(false); }

        telemetry.addData("Red", color_sensor.red());
        telemetry.addData("Green", color_sensor.green());
        telemetry.addData("Blue", color_sensor.blue());
        telemetry.addData("Alpha", color_sensor.alpha());
        telemetry.addData("Device", color_sensor.getDeviceName());
        telemetry.addData("Connection", color_sensor.getConnectionInfo());
    }
}
