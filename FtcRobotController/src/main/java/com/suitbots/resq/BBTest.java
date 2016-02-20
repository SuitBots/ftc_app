package com.suitbots.resq;

public class BBTest extends BuildingBlocks {
    private void act(String action) {
        telemetry.addData("Action", action);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry, this);
        isaac5.calibrateGyro();
        isaac5.activateSensors();

        waitForStart();

        boolean arm_up = false;

        while (opModeIsActive()) {

            isaac5.setDriveMotorSpeeds(- gamepad1.left_stick_y, - gamepad1.right_stick_y);

            if (gamepad1.dpad_left) {
                act("Rotate Left");
                rotateDegrees(isaac5, -45);
            } else if (gamepad1.dpad_right) {
                act("Rotate Right");
                rotateDegrees(isaac5, 45);
            } else if (gamepad1.dpad_up) {
                act("Fwd 1 square");
                driveMeters(isaac5, .6);
            } else if (gamepad1.dpad_down) {
                act("Rev 1 square");
                driveMeters(isaac5, -.6);
            } else if (gamepad1.x) {
                act("White tape");
                driveForwardUntilWhiteTape(isaac5, .6);
            } else if (gamepad1.y) {
                act("Stop!");
                stopppppppp(isaac5, .6);
            }

            if (gamepad1.right_bumper) {
                if (! arm_up) {
                    isaac5.moveDumperArmToThrowPosition();
                    arm_up = true;
                }
            } else {
                if (arm_up) {
                    isaac5.resetDumperArm();
                    arm_up = false;
                }
            }

            if (gamepad1.a) {
                isaac5.activateSensors();
            } else {
                isaac5.deactivateSensors();
            }

            isaac5.sendSensorTelemetry();

            waitOneFullHardwareCycle();
        }
    }
}
