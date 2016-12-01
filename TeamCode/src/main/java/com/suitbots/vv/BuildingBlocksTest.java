package com.suitbots.vv;

public class BuildingBlocksTest extends AutonomousBase {
    @Override
    public void runOpMode() throws InterruptedException {
        while (! isStarted()) {
            telemetry.addData("Gyro", robot.isCalibrating() ? "Calibrating" : "CALIBRATED");
            telemetry.update();
            idle();
        }

        robot.onStart();

        Controller c = new Controller(gamepad1);
        while(opModeIsActive()) {
            int distance = 10; // cm
            if (c.A()) distance *= 2;
            if (c.B()) distance *= 5;

            robot.updateSensorTelemetry();
            telemetry.addData("Distance", distance);
            telemetry.update();

            if (c.dpadUpOnce()) {
                driveDirectionCM(0.0, distance);
            } else if (c.dpadDownOnce()) {
                driveDirectionCM(Math.PI, distance);
            } else if (c.dpadLeftOnce()) {
                driveDirectionCM(3.0 * Math.PI / 2.0, distance);
            } else if (c.dpadRightOnce()) {
                driveDirectionCM(Math.PI / 2.0, distance);
            } else if (c.XOnce()) {
                robot.resetGyro();
                turnToHeading(distance);
            } else if (c.YOnce()) {
                robot.resetGyro();
                turnToHeading(- distance);
            }

            final double lx = c.left_stick_x, ly = c.left_stick_x;
            final double angle = Math.atan2(lx, ly);
            final double velocity = Math.sqrt(lx * lx + ly * ly);
            robot.drive(angle, velocity, c.right_stick_x);


        }

        robot.onStop();
    }
}
