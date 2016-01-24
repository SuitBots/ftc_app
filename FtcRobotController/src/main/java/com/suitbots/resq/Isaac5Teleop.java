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

            if (gamepad1.x && !hasMovedArm) {
                isaac5.moveDumperArmToThrowPosition();
                hasMovedArm = true;
            } else if (hasMovedArm) {
                isaac5.resetDumperArm();
                hasMovedArm = false;
            }

            isaac5.setTapeMotor(gamepad1.left_trigger - gamepad1.right_trigger);

            if (gamepad1.a) {
                isaac5.setWinchMotor(1.0);
            } else if (gamepad1.b) {
                isaac5.setWinchMotor(-1.0);
            } else {
                isaac5.setWinchMotor(0.0);
            }
        }
        isaac5.stop();
    }
}
