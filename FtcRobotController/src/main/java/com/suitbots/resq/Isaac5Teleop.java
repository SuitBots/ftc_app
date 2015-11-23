package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Isaac5Teleop extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        isaac5.deactivateSensors();
        waitForStart();
        while (opModeIsActive()) {
            // Wheels
            isaac5.sendSensorTelemetry();
            isaac5.setDriveMotorSpeeds(gamepad1.left_stick_y, gamepad1.right_stick_y);

            if (gamepad1.x) {
                isaac5.moveClimberArmToThrowPosition();
            } else {
                isaac5.resetClimberArm();
            }

            if (gamepad1.y) {
                isaac5.zeroMotorEncoders();
            }
        }
        isaac5.stop();
    }
}
