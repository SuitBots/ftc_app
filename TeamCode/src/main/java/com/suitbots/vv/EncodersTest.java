package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.configuration.ConfigurationType;


@TeleOp(name = "Encoders Test", group = "Test")
public class EncodersTest extends OpMode {
    private MecanumRobot robot;
    private Controller g1;

    @Override
    public void init() {
        robot = new MecanumRobot(hardwareMap, telemetry);
        g1 = new Controller(gamepad1);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Gyro", robot.isCalibrating() ? "Calibrating" : "CalibratED");
        telemetry.update();
    }

    @Override
    public void start() {
        robot.onStart();
    }

    @Override
    public void stop() {
        robot.onStop();
    }

    @Override public void loop() {
        g1.update();
        if (robot.driveMotorsBusy()) {
            telemetry.addData("Busy", "Yes");
            telemetry.addData("Distance", "N/A");
        } else {
            telemetry.addData("Busy", "No");
            int distance = 25;
            if (g1.A()) distance *= 2;
            if (g1.B()) distance *= 2;

            telemetry.addData("Distance", distance);

            if (g1.dpadUpOnce()) {
                robot.encoderDriveForward(distance);
            } else if (g1.dpadDownOnce()) {
                robot.encoderDriveBackward(distance);
            } else if (g1.dpadLeftOnce()) {
                robot.encoderDriveLeft(distance);
            } else if (g1.dpadRightOnce()) {
                robot.encoderDriveRight(distance);
            } else {
                double lx = g1.left_stick_x, ly = - g1.left_stick_y;
                double v = Math.sqrt(lx * lx + ly * ly);
                robot.drive(Math.atan2(lx, ly), v, g1.right_stick_x);
            }
        }

        telemetry.update();
    }
}
