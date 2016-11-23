package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;

/**
 * Created by Suit Bots on 10/13/2016.
 */
@Disabled
@Autonomous(name="Shalf")
//extends AutonomousBase??
public class ShalfDogrunAutonomousAlpha extends LinearOpMode {
    private OpticalDistanceSensor line;
    private ModernRoboticsI2cRangeSensor range;
    TankRobot robot = new TankRobot();
    public void runOpMode() throws InterruptedException {

        DcMotor l1 = hardwareMap.dcMotor.get("l1");
        DcMotor r1 = hardwareMap.dcMotor.get("r1");
        line = hardwareMap.opticalDistanceSensor.get("line");
        r1.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();

            long t0 = System.currentTimeMillis();
            l1.setPower(1.0);
            r1.setPower(1.0);
            while (opModeIsActive()) {
                long t1 = System.currentTimeMillis();
                if (1300 < (t1 - t0)) {
                    break;
                }
                idle();
            }
            l1.setPower(0.0);
            r1.setPower(0.0);
        turn(45);
        if (true){
            l1.setPower(0.85);
            r1.setPower(0.85);
        }
        //arm hits beacon
        //change position x times and hit beacon
        //




    }
    private void turn(int angle) throws InterruptedException {
        robot.pushRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.resetGyro();

        final double SPEED = .1;

        robot.setDriveMotors(0 < angle ? SPEED : -SPEED, 0 < angle ? -SPEED : SPEED);

        while(opModeIsActive() && (Math.abs(robot.getHeading()) < Math.abs(angle))) {
            idle();
        }

//        telemetry.addData("Heading", robot.getHeading());
//        telemetry.update();

        robot.setDriveMotors(0.0, 0.0);
        robot.popRunMode();
    }
}
