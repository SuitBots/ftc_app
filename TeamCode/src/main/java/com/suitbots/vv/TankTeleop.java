package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;

@Disabled
@TeleOp(name = "Teleop: Tank", group = "Teleops")
public class TankTeleop extends OpMode {
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

        double lp = - g1.left_stick_y;
        double rp = - g1.right_stick_y;

        robot.setDriveMotors(lp * scale, rp * scale);
        robot.setSpinner(g1.left_trigger - g1.right_trigger);
    }
}
