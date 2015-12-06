package com.suitbots.resq;

public class ForwardAutonomous extends BuildingBlocks {

    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();
        isaac5.activateSensors();

        final double SQUARE_SIZE_METERS = 0.6;
        final double SQRT_2 = Math.sqrt(2.0);

        driveMeters(isaac5, 2.5 * SQRT_2 * SQUARE_SIZE_METERS, 4.0);
        driveForwardUntilWhiteTape(isaac5, 1.0 * SQRT_2 * SQUARE_SIZE_METERS, 2.0);

        isaac5.stop();
    }
}
