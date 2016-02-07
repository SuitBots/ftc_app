package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Isaac5Teleop extends LinearOpMode {

    // Teleop:
    // joysticks: left y: left wheels, right y: right wheels
    // x:         dump the climbers
    // y:         send sensor telemetry
    // a:         winch forward
    // b:         winch back
    // l bumper:  tape out
    // r bumper:  tape back
    // triggers:  tape out/back (left/right)
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry, this);
        isaac5.deactivateSensors();
        boolean hasMovedArm = false;
        waitForStart();
        while (opModeIsActive()) {
            // Wheels
            isaac5.setDriveMotorSpeeds(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

            // For hill climbing.
            if (gamepad1.dpad_up) {
                isaac5.setDriveMotorSpeeds(1.0, 1.0);
            }

            if (gamepad1.left_bumper) {
                isaac5.setTapeMotor(1.0);
            } else if(gamepad1.right_bumper) {
                isaac5.setTapeMotor(-1.0);
            } else {
                isaac5.setTapeMotor(gamepad1.left_trigger - gamepad1.right_trigger);
            }

            if (gamepad1.a) {
                isaac5.setWinchMotor(1.0);
            } else if (gamepad1.b) {
                isaac5.setWinchMotor(-1.0);
            } else {
                isaac5.setWinchMotor(0.0);
            }

            if (gamepad1.y) {
                isaac5.moveFlapUp();
            } else if(gamepad1.x){
                isaac5.moveFlapDown();
            } else{
                isaac5.stopFlap();
            }

        }
        isaac5.stop();
    }
}
