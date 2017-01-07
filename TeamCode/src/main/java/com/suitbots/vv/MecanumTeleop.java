package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Suit Bots on 11/11/2016.
 */

@TeleOp(name = "S" + // Why, Kevin? Why?
        "half Dogron", group = "Tournament")
public class MecanumTeleop extends OpMode {
    private MecanumRobot robot = null;
    private Controller g1, g2;
    // Single driver mode.
    private boolean single_player_mode = false;

    @Override
    public void init() {
        robot = new MecanumRobot(hardwareMap, telemetry);
        // robot.disableEncoders();
        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
    }

    @Override
    public void init_loop() {
        g1.update();
        if (g1.A() && g1.B()) {
            if (g1.dpadUpOnce()) {
                single_player_mode = ! single_player_mode;
            }
        }
        telemetry.addData("Gyro", robot.isCalibrating() ? "Calibrating" : "CalibratED");
        telemetry.addData("Single Player Mode", single_player_mode ? "On" : "Off");
        telemetry.update();
    }

    @Override
    public void start() {
        robot.onStart();
    }

    @Override
    public void stop() {
        robot.stopDriveMotors();
    }

    private void g1Loop() {
        g1.update();
        DriveHelper.drive(g1, robot);

        robot.setHarvesterPower(g1.left_trigger - g1.right_trigger);

        if (g1.XOnce()) {
            robot.toggleBackServo();
        }
        if (g1.YOnce()) {
            robot.toggleFrontServo();
        }

    }

    private void g2Loop() {
        g2.update();
        if (g2.XOnce()) {
            robot.toggleBackServo();
        }

        if (g2.YOnce()) {
            robot.toggleFrontServo();
        }

        double flipper = Math.pow(g2.left_trigger - g2.right_trigger, 3.0);
        if (0.1 < Math.abs(flipper)) {
            robot.setFlipperPower(flipper);
        } else {
            robot.stopFlipperIfItIsNotFlipping();
        }

        if (g2.rightBumperOnce()) {
            robot.fire();
        }

        robot.setDispenser(!g2.leftBumper());
    }

    @Override
    public void loop() {
        robot.loop();
        robot.updateSensorTelemetry();
        g1Loop();
        if (! single_player_mode) {
            g2Loop();
        }
        telemetry.update();
    }
}
