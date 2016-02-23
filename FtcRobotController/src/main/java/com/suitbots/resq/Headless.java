package com.suitbots.resq;

public class Headless extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry, this);

        isaac5.activateSensors();

        waitForStart();

        driveForwardUntilWhiteTape(isaac5, .25);

        isaac5.deactivateSensors();
    }
}
