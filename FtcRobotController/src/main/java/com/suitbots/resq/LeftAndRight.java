package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class LeftAndRight extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        boolean quit = false;
        while (opModeIsActive() && !quit) {
            // isaac5.sendSensorTelemetry();

            int angle = 90;
            if (gamepad1.left_bumper) { angle /= 2; }
            if (gamepad1.right_bumper) { angle /= 2; }

            if (gamepad1.dpad_left) { rotateDegrees(isaac5, -angle); }
            if (gamepad1.dpad_right) { rotateDegrees(isaac5, angle); }

            quit = gamepad1.x && gamepad1.y;

            isaac5.stop();
        }
    }
}
