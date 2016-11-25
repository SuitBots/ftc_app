package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Arcade Teleop (Test)")
public class ArcadeTeleop extends OpMode {
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
        robot.resetGyro();
    }

    @Override
    public void loop() {
        g1.update();

        if (g1.A() && g1.B()) {
            robot.resetGyro();
        }

        double lx = g1.left_stick_x, ly = - g1.left_stick_y;
        double v = Math.sqrt(lx * lx + ly * ly);
        double theta = Math.atan2(lx, ly);
        double current = Math.toRadians(robot.getHeading());

        if (0.05 < (v + Math.abs(g1.right_stick_x))) {
            robot.drive(theta - current, v, g1.right_stick_x);
        }
    }

}
