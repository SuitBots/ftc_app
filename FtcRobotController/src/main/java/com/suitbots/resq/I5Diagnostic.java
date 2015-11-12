package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * A teleop-style class for testing sensor and encoder values.
 */
public class I5Diagnostic extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        while(opModeIsActive()) {
            isaac5.sendSensorTelemetry();
            isaac5.setDriveMotorSpeeds(gamepad1.left_stick_y, gamepad1.right_stick_y);
            // isaac5.setArmMotorSpeed(gamepad1.left_trigger - gamepad1.right_trigger);

            if (gamepad1.a) {
                isaac5.activateSensors();
            }
            if (gamepad1.b) {
                isaac5.deactivateSensors();
            }
        }
    }
}
