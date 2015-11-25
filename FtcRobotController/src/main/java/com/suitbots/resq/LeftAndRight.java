package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class LeftAndRight extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        while (opModeIsActive()) {
            isaac5.sendSensorTelemetry();
            if (gamepad1.a) {
                turnLeftDegrees(isaac5, 45);
            } else if (gamepad1.b) {
                turnRightDegrees(isaac5, 45);
            } else if (gamepad1.x) {
                turnLeftDegrees(isaac5, 90);
            } else if (gamepad1.y) {
                turnRightDegrees(isaac5, 90);
            }

            isaac5.stop();
        }
    }
}
