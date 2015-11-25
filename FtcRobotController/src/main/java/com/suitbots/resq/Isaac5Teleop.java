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
            isaac5.setDriveMotorSpeeds(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

            if (gamepad1.x) {
                isaac5.moveDumperArmToThrowPosition();
            } else {
                isaac5.resetDumperArm();
            }

            if (gamepad1.y) {
                isaac5.zeroMotorEncoders();
            }


            // nudge controls
            if (gamepad1.left_bumper) {
                isaac5.setDriveMotorSpeeds(0.1, 0.1);
            } else if(gamepad1.right_bumper) {
                isaac5.setDriveMotorSpeeds(-.3, -.3);
            }

            isaac5.setTapeMotor(gamepad1.left_trigger - gamepad1.right_trigger);
        }
        isaac5.stop();
    }
}
