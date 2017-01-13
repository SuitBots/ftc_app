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
    private boolean debug_mode = false;

    @Override
    public void init() {
        robot = new MecanumRobot(hardwareMap, telemetry);

        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
    }

    @Override
    public void init_loop() {
        g1.update();
        if (g1.AOnce()) {
            debug_mode = ! debug_mode;
        }
        telemetry.addData("Debug? (a)", debug_mode ? "on" : "off");
        telemetry.addData("Ready?", "YES.");
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

    private void g1Loop(Controller g) {
        g1.update();
        DriveHelper.drive(g, robot);

        robot.setHarvesterPower(g.left_trigger - g.right_trigger);

        if (g.XOnce()) {
            robot.toggleBackServo();
        }
        if (g.YOnce()) {
            robot.toggleFrontServo();
        }

    }

    private void g2Loop(Controller g) {
        g.update();
        if (g.XOnce()) {
            robot.toggleBackServo();
        }

        if (g.YOnce()) {
            robot.toggleFrontServo();
        }

        double flipper = Math.pow(g.left_trigger - g.right_trigger, 3.0);
        if (0.1 < Math.abs(flipper)) {
            robot.setFlipperPower(flipper);
        } else {
            robot.stopFlipperIfItIsNotFlipping();
        }

        if (g.rightBumperOnce()) {
            robot.fire();
        }

        robot.setDispenser(!g.leftBumper());
    }

    @Override
    public void loop() {
        robot.loop();
        g1Loop(g1);
        g2Loop(g2);
        if (debug_mode) {
            robot.updateSensorTelemetry();
            telemetry.update();
        }
    }
}
