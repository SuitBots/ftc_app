package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Headless extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry, this);
        isaac5.calibrateGyro();
        waitForStart();

        while(opModeIsActive()) {
            isaac5.activateSensors();
            isaac5.sendSensorTelemetry();
            telemetry.addData("On Line", isaac5.isOnWhiteLine() ? "YES" : "NO");
            waitOneFullHardwareCycle();
        }
    }
}
