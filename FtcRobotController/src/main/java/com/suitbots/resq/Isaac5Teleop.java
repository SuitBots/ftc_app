package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Isaac5Teleop extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        isaac5.deactivateSensors();
        boolean hasMovedArm = false;
        waitForStart();
        while (opModeIsActive()) {
            // Wheels
            isaac5.sendSensorTelemetry();
            isaac5.setDriveMotorSpeeds(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

            if (gamepad1.x) {
                isaac5.moveDumperArmToThrowPosition();
                hasMovedArm = true;
            } else if (hasMovedArm) {
                isaac5.resetDumperArm();
                hasMovedArm = false;
            }

            if (gamepad1.y) {
                isaac5.zeroMotorEncoders();
            }

            if(gamepad1.a) {
                isaac5.resetHeading();
            }

            // nudge controls
            if (gamepad1.dpad_up) {
                isaac5.setDriveMotorSpeeds(0.25, 0.25);
            } else if (gamepad1.dpad_down) {
                isaac5.setDriveMotorSpeeds(-0.25, -0.25);
            } else if (gamepad1.dpad_left) {
                isaac5.setDriveMotorSpeeds(-0.25, 0.25);
            } else if (gamepad1.dpad_right) {
                isaac5.setDriveMotorSpeeds(0.25, -0.25);
            }

            isaac5.setTapeMotor(gamepad1.left_trigger - gamepad1.right_trigger);
        }
        isaac5.stop();
    }
}
