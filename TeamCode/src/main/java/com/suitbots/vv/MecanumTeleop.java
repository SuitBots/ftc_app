package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Suit Bots on 11/11/2016.
 */

@TeleOp(name = "S" + // Why, Kevin? Why?
        "half Dogron")
public class MecanumTeleop extends OpMode {
    private MecanumRobot robot = null;
    private Controller g1, g2;
    // Single driver mode.
    private boolean single_player_mode = false;

    @Override
    public void init() {
        robot = new MecanumRobot(hardwareMap, telemetry);
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

    private void drive(Controller g) {
        double theta = 0.0, v_theta = 0.0, v_rotation = 0.0;

        if (g.dpadUp()) {
            theta = 0.0;
            v_theta = 0.5;
        } else if (g.dpadDown()) {
            theta = Math.PI;
            v_theta = 0.5;
        } else if (g.dpadLeft()) {
            theta = 3.0 * Math.PI / 2.0;
            v_theta = 0.5;
        } else if (g.dpadRight()) {
            theta = Math.PI / 2.0;
            v_theta = 0.5;
        } else {
            final double lx = g.left_stick_x;
            final double ly = - g.left_stick_y;

            theta = Math.atan2(lx, ly);
            v_theta = Math.sqrt(lx * lx + ly * ly);
            v_rotation = g.right_stick_x;
        }

        if (0.05 < (v_theta + Math.abs(v_rotation))) {
            robot.drive(theta, v_theta, v_rotation);
        }
    }

    private void g1Loop() {
        g1.update();

        drive(g1);

        if (single_player_mode && g1.A()) {
            robot.setFlipperPower(g1.left_trigger - g1.right_trigger);
        } else {
            robot.setHarvesterPower(g1.left_trigger - g1.right_trigger);
        }

        if (single_player_mode) {
            if (g1.rightBumperOnce()) {
                robot.fire();
            }
            robot.setDispenser(! g1.leftBumperOnce());
            if (g1.XOnce()) {
                robot.toggleBackServo();
            }
            if (g1.YOnce()) {
                robot.toggleFrontServo();
            }
            if (g1.A()) {
                robot.setFlipperPower(0.5);
            } else if (g1.B()) {
                robot.setFlipperPower(-0.5);
            }

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

        double flipper = g2.left_trigger - g2.right_trigger;
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
