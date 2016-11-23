package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp(name = "Turn-o-matic")
public class TurnOMatic extends LinearOpMode {
    TankRobot robot = new TankRobot();
    public void runOpMode() throws InterruptedException {
        robot.initHardware(hardwareMap);
        robot.calibrateGyro();
        telemetry.addData("Gyro", "Calibrating...");
        telemetry.update();

        while (robot.isGyroCalibrating()) {
            Thread.sleep(100);
        }
        telemetry.addData("Gyro", "Calibrated.");
        telemetry.update();

        Controller g1 = new Controller(gamepad1);
        waitForStart();
        while (opModeIsActive()) {
            g1.update();
            if (g1.dpadRightOnce()) {
                turn(45);
            } else if (g1.dpadLeftOnce()) {
                turn(-45);
            } else if (g1.dpadUpOnce()) {
                fwd(50);
            } else if (g1.A() && g1.B()) {
                for (int i = 0; i < 4; ++i) {
                    fwd(50);
                    turn(90);
                }
            } else {
                robot.setDriveMotors(- g1.left_stick_y, -g1.right_stick_y);
            }
        }
    }

    static final double WHEEL_CM = 10.16 * Math.PI;
    static final int TICKS_PER_REV = 1140;

    private void fwd(int _cm) throws InterruptedException {
        double cm = (double) _cm;
        int ticks = (int)(TICKS_PER_REV * cm / WHEEL_CM);
        robot.resetDriveEncoders();
        robot.pushRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.setDriveMotorsEncoderTarget(ticks);

        final double P = .5;

        robot.setDriveMotors(P, P);
        while (opModeIsActive() && robot.motorsAreBusy()) {
            idle();
        }
        robot.setDriveMotors(0.0, 0.0);
        robot.popRunMode();
    }

    private void turn(int angle) throws InterruptedException {
        robot.pushRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.resetGyro();

        final double SPEED = .1;

        robot.setDriveMotors(0 < angle ? SPEED : -SPEED, 0 < angle ? -SPEED : SPEED);

        while(opModeIsActive() && (Math.abs(robot.getHeading()) < Math.abs(angle))) {
            idle();
        }

        telemetry.addData("Heading", robot.getHeading());
        telemetry.update();

        robot.setDriveMotors(0.0, 0.0);
        robot.popRunMode();
    }
}
