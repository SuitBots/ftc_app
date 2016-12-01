package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


public abstract class AutonomousBase extends LinearOpMode {
    protected MecanumRobot robot;

    public AutonomousBase() {
        robot = new MecanumRobot(hardwareMap, telemetry);
    }

    protected void waitForEncoderDriveToFinish() throws InterruptedException {
        while (robot.driveMotorsBusy()) {
            idle();
        }
    }

    private static final int MAX_HEADING_SLOP = 2;
    private static final double SAFE_TURN_SPEED = .3;
    protected void turnToHeading(int heading) {
        while (MAX_HEADING_SLOP < Math.abs(heading - robot.getHeading())) {
            final int diff = heading - robot.getHeading();
            robot.drive(0.0, 0.0, diff > 0 ? SAFE_TURN_SPEED : - SAFE_TURN_SPEED);
            idle();
        }
        robot.stopDriveMotors();
    }

    protected void driveDirectionCM(double directionRadians, int cm) {
        robot.encoderDriveDirection(directionRadians, cm);
        while (robot.driveMotorsBusy()) {
            idle();
        }
        robot.stopDriveMotors();
        robot.resetDriveMotorModes();
    }

    protected void shoot(int n) throws InterruptedException {
        if (0 < n) {
            robot.fire();
            while (!robot.isDoneFlipping()) {
                idle();
            }
            if (1 < n) {
                robot.toggleDispenser();
                robot.fire();
                while (!robot.isDoneFlipping()) {
                    idle();
                }
                robot.toggleDispenser();
            }
        }
    }

}
