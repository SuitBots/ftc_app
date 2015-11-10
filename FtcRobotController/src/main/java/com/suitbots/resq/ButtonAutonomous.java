package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class ButtonAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws java.lang.InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        waitForStart();
        isaac5.calibrateGyro();

        isaac5.goForward();
        Thread.sleep(500);
        isaac5.goBackward();
        Thread.sleep(500);
        isaac5.stop();

        while (opModeIsActive()) {
            isaac5.sendSensorTelemetry();
            Thread.sleep(100);
        }
    }
}
