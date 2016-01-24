package com.suitbots.resq;

public class BBTest extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        isaac5.calibrateGyro();
        isaac5.activateSensors();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_left) {
                rotateDegrees(isaac5, -45);
            } else if (gamepad1.dpad_right) {
                rotateDegrees(isaac5, 45);
            } else if (gamepad1.dpad_up) {
                driveMeters(isaac5, .6);
            } else if (gamepad1.dpad_down) {
                driveMeters(isaac5, -.6);
            }

            if (gamepad1.x) {
                driveForwardUntilWhiteTape(isaac5, .6);
            } else if (gamepad1.y) {
                stopppppppp(isaac5, .6);
            }

            waitOneFullHardwareCycle();
        }
    }
}
