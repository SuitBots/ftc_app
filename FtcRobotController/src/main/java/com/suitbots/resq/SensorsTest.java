package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Created by cp on 11/30/15.
 */
public class SensorsTest extends LinearOpMode {
    @Override public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        isaac5.calibrateGyro();

        waitForStart();

        while(opModeIsActive()) {
            isaac5.sendSensorTelemetry();
            if(gamepad1.x) {
                isaac5.activateSensors();
            } else {
                isaac5.deactivateSensors();
            }

            isaac5.setDriveMotorSpeeds(- gamepad1.left_stick_y,
                                       - gamepad1.right_stick_y);
        }
    }
}
