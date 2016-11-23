package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp(name = "Teleop: Arcade", group = "Teleops")
public class ArcadeTeleop extends OpMode {
    private TankRobot robot;
    private Controller g1;

    public void init() {
        robot = new TankRobot();
        robot.initHardware(hardwareMap);
        g1 = new Controller(gamepad1);
    }

    public void loop() {
        g1.update();

        double scale = 1.0 * (g1.leftBumper() ? .5 : 1.) * (g1.rightBumper() ? .5 : 1.);

        double lp = - g1.left_stick_y + g1.left_stick_x - g1.right_stick_y + g1.right_stick_x;
        double rp = - g1.left_stick_y - g1.left_stick_x - g1.right_stick_y - g1.right_stick_x;
        double m = Math.max(1.0, Math.max(Math.abs(lp), Math.abs(rp)));

        robot.setDriveMotors(lp * scale / m, rp * scale / m);
        robot.setSpinner(g1.left_trigger - g1.right_trigger);
    }
}
