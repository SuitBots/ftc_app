package com.suitbots.resq;

/**
 * Created by cp on 11/27/15.
 */
public class Square extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();
        isaac5.calibrateGyro();

        for (int i = 0; i < 4; ++i) {
            driveMeters(isaac5, .5);
            rotateDegrees(isaac5, 90);
        }
        isaac5.stop();
    }
}
