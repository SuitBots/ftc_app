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
    private Controller g1;
    private ModernRoboticsI2cGyro gyro;
    private ModernRoboticsI2cRangeSensor range;
    private OpticalDistanceSensor line;
    private ColorSensor color;

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

    }

    @Override
    public void stop() {
        robot.stop();
    }

    @Override
    public void loop() {
        g1.update();

        final double lx = g1.left_stick_x;
        final double ly = - g1.left_stick_y;
        final double rx = g1.right_stick_x;

        telemetry.addData("Input", String.format(Locale.US, "%.2f\t%.2f\t%.2f", lx, ly, rx));
        telemetry.addData("Gyro1", String.format(Locale.US, "%.2f", gyro));
        telemetry.addData("Range1", String.format(Locale.US, "%.2f", range));
        telemetry.addData("Line1", String.format(Locale.US, "%.2f", line));
        telemetry.addData("Color1", String.format(Locale.US, "%.2f", color));

        final double speed = Math.sqrt(lx * lx + ly * ly);
        final double translation = Math.atan2(lx, ly);
        final double rotation = rx;

        robot.drive(translation, speed, rotation);
        robot.setHarvesterPower(g1.left_trigger - g1.right_trigger);
        robot.setFlipperPower(g1.A()? 1 : 0);

        if (g1.XOnce()) {
            robot.toggleBackServo();
        }

        if (g1.BOnce()) {
            robot.toggleFrontServo();
        }
    }
}
