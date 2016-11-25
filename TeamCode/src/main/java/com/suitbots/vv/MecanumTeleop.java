package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import java.util.Locale;

/**
 * Created by Suit Bots on 11/11/2016.
 */

@TeleOp(name = "S" +
        "half Dogron")
public class MecanumTeleop extends OpMode {
    private MecanumRobot robot = null;
    private Controller g1, g2;

    @Override
    public void init() {
        robot = new MecanumRobot(hardwareMap, telemetry);
        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
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
        robot.stop();
    }

    protected void g1Loop() {
        g1.update();

        final double lx = g1.left_stick_x;
        final double ly = - g1.left_stick_y;
        final double rx = g1.right_stick_x;

        final double speed = Math.sqrt(lx * lx + ly * ly);
        final double translation = Math.atan2(lx, ly);

        robot.drive(translation, speed, rx);
        robot.setHarvesterPower(g1.left_trigger - g1.right_trigger);

        if (g1.XOnce()) {
            robot.toggleBackServo();
        }

        if (g1.YOnce()) {
            robot.toggleFrontServo();
        }
    }

    protected void g2Loop() {
        g2.update();

        double flipper = g2.left_trigger - g2.right_trigger;
        if (0.1 < Math.abs(flipper)) {
            robot.setFlipperPower(flipper);
        } else {
            robot.stopFlipperIfItIsNotFlipping();
        }

        if (g2.rightBumperOnce()) {
            robot.fire();
        }

        robot.setDispenser(! g2.leftBumper());
    }

    @Override
    public void loop() {
        robot.loop();
        robot.updateSensorTelemetry();
        g1Loop();
        g2Loop();
        telemetry.update();
    }
}
