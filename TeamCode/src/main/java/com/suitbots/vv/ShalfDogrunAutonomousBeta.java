package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

/**
 * Created by Suit Bots on 11/22/2016.
 */
public abstract class ShalfDogrunAutonomousBeta extends LinearOpMode {
    private MecanumRobot robot;

    public enum Alliance {
        RED, BLUE
    }

    public abstract Alliance getAlliance();

    protected double forwardDir() {
        if(Alliance.RED == getAlliance()) {
            return Math.PI * 2.0;
        } else {
            return Math.PI;
        }
    }

    public double leftDir() {
        return 3.0 * Math.PI / 2.0;
    }

    public double diagonalDirection() {
        return (leftDir() + forwardDir()) / 2.0;
    }


    @Autonomous(name = "Shalf RED")
    public static class Red extends ShalfDogrunAutonomousBeta {
        public Alliance getAlliance() { return Alliance.RED; }
    }

    @Autonomous(name = "Shalf BLUE")
    public static class Blue extends ShalfDogrunAutonomousBeta {
        public Alliance getAlliance() { return Alliance.BLUE; }
    }

    public ShalfDogrunAutonomousBeta() {

    }

   @Override
   public void runOpMode() throws InterruptedException {
       robot = new MecanumRobot(hardwareMap, telemetry);

       int ccount = 0;
       while (robot.isCalibrating()) {
           telemetry.addData("Gyro", String.format(Locale.US, "%d", ccount++));
           telemetry.update();
           Thread.sleep(100, 0);
       }
       robot.resetGyro();
       telemetry.addData("Gyro", "CalibratED");
       telemetry.update();

       waitForStart();
       driveDiagonalToTheWall();
       driveForwardToWhiteLine();
       pressButton();
       driveBackToWhiteLine();
       pressButton();
       rotate();
       shoot();
   }

    public static double DISTANCE_TO_WALL_CM = 15.0;
    protected void driveDiagonalToTheWall() throws InterruptedException {
        while(DISTANCE_TO_WALL_CM < robot.distanceToWallCM()) {
            robot.drivePreservingDirection(diagonalDirection(), .75);
            idle();
        }
        robot.stop();
    }
    protected void driveForwardToWhiteLine() throws InterruptedException {
        while(robot.isNotOnWhiteLine()) {
            robot.drivePreservingDirection(forwardDir(), .75);
            idle();
        }
        robot.stop();
    }

    Alliance getBeaconColor() {
        return robot.colorSensorIsBlue() ? Alliance.BLUE : Alliance.RED;
    }

    protected void pressButton() throws InterruptedException {
        final boolean back_button = getBeaconColor() == getAlliance();

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }

        // drive left
        // drive right

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }
        robot.stop();
    }

    protected void driveBackToWhiteLine() throws InterruptedException {
        double t0 = time;
        double x = 0.5;
        while(x<t0){
            robot.drivePreservingDirection(forwardDir()-Math.PI, .75);
        }
        while(robot.isNotOnWhiteLine()) {
            robot.drivePreservingDirection(forwardDir()-Math.PI, .75);
            idle();
        }
        robot.stop();
    }
    protected void rotate(){
        robot.resetGyro();
       while(robot.gyro1.equals(forwardDir()-Math.PI)){
            robot.drive(0,0,90);
       }
        robot.stop();
    }
    protected void shoot(){
        //find the thingy for flipper encoder value
        while(){
            robot.setFlipperPower(1.0);
        }
        robot.stop();
    }
}
