package com.suitbots.vv;

/**
 * Created by Suit Bots on 11/10/2016.
 */

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;

@TeleOp(name = "Mecanum: Test", group = "Teleops")
public class MecanumTest {
    public class TankTeleop extends OpMode {
        private TankRobot robot;
        private Controller g1;


        public void init() {
            robot = new TankRobot();
            robot.initHardware(hardwareMap);
            g1 = new Controller(gamepad1);
        }
        //Right = rotation
        //Left = dirrection

        public void loop() {
            g1.update();
            //if(){}
            //
            double scale = 1.0 * (g1.leftBumper() ? .5 : 1.) * (g1.rightBumper() ? .5 : 1.);

            double l1 = -g1.left_stick_y;
            double r1 = -g1.right_stick_y;

            robot.setDriveMotors(l1 * scale, r1 * scale);
            robot.setSpinner(g1.left_trigger - g1.right_trigger);
        }
    }
}

