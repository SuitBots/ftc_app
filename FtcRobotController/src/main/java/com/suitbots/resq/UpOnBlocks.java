package com.suitbots.resq;

/**
 * Run I5 through its paces without any joystick intervention
 */
public class UpOnBlocks extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry, this);
        waitForStart();

        isaac5.activateSensors();

        while (opModeIsActive()) {
            isaac5.activateSensors();
            isaac5.sendSensorTelemetry();
        }
    }
}
