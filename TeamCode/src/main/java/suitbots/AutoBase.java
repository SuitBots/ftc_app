package suitbots;

/**
 * Created by Samantha on 9/21/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.concurrent.Callable;


public abstract class AutoBase extends LinearOpMode  {
    protected Robot robot;

//    protected void initRobot() {
//        initRobot(true);
//    }


    public void onStart() {
        robot.onStart();
    }

    public void onStop() {
        robot.onStop();
    }

    protected void snooze(int ms) throws InterruptedException {
        if (opModeIsActive()) {
            sleep(ms);
        }
    }

    private static final double SAFE_TURN_SPEED = .1;
    private static final double FAST_TURN_SPEED = .15;
    private static final double STUPID_TURN_SPEED = .3;
    private static final int FAST_TURN_THRESHOLD = 30;
    private static final int STUPID_TURN_THRESHOLD = 60;



    private double angleDifference(double from, double to) {
        if (from < 0) from += 2*Math.PI;
        if (to < 0) to += 2*Math.PI;

        double diff = to - from;

        if (diff < -Math.PI) {
            diff += 2*Math.PI;
        } else if (diff > Math.PI) {
            diff = - (2*Math.PI - diff);
        }

        return diff;
    }

    private static double speedForTurnDistance(double angle) {
        angle = Math.abs(angle);
        if (angle > STUPID_TURN_THRESHOLD) {
            return STUPID_TURN_SPEED;
        }
        if (angle > FAST_TURN_THRESHOLD) {
            return FAST_TURN_SPEED;
        }
        return SAFE_TURN_SPEED;
    }

    private static final int MAX_HEADING_SLOP = 1;

    protected void turnToAngleDeg(double degrees) throws InterruptedException {
        degrees = Math.toRadians(degrees);
        turnToAngleRad(degrees);
    }
    protected void turnToAngleRad(double radians) throws InterruptedException {
        while(opModeIsActive()) {
            double diff = angleDifference(robot.getHeadingRadians(), radians);
            if (MAX_HEADING_SLOP >= Math.abs(diff)) break;
            double speed = speedForTurnDistance(diff);
            robot.drive(0.0, 0.0, diff > 0 ? -speed : speed);
            idle();
        }
        robot.stopDriveMotors();
    }

//
//    protected void turn(int degrees) throws InterruptedException {
//        robot.resetGyro();
//        turnToAngle(- degrees);
//    }

//    protected void driveDirectionTiles(double directionRadians, double tiles) throws InterruptedException {
//        driveDirectionTiles(directionRadians, tiles, .35);
//    }
//
//    protected void driveDirectionTilesFast(double directionRadians, double tiles) throws InterruptedException {
//        driveDirectionTiles(directionRadians, tiles, .65);
//    }

//    protected void driveDirectionTiles(double directionRadians, double tiles, double power) throws InterruptedException {
//        robot.setEncoderDrivePower(power);
//        robot.encoderDriveTiles(directionRadians, tiles);
//        while (opModeIsActive() && robot.driveMotorsBusy()) {
//            robot.updateSensorTelemetry();
//            telemetry.update();
//            robot.loop();
//            idle();
//        }
//        robot.stopDriveMotors();
//        robot.resetDriveMotorModes();
//        robot.clearEncoderDrivePower();
//    }

//    private void fire(long timeout) throws InterruptedException {
//        robot.fire();
//        final long t0 = System.currentTimeMillis();
//        while (robot.isFlipping()) {
//            // robot.loop();
//            final long t1 = System.currentTimeMillis();
//            if (timeout < (t1 - t0)) {
//                break;
//            }
//        }
//        robot.setFlipperPower(0.0);
//    }

   // Let's make some autonomous routines orientation-agnostic.
    protected abstract double forwardDir();
    protected double pressersDir() {
        return 3.0 * Math.PI / 2.0;
    }
    protected double antiPressersDir() { return Math.PI / 2.0; }
    protected double leftForwardDir() {
        return (pressersDir() + forwardDir()) / 2.0;
    }
    protected double backwardDir() {
        return forwardDir() + Math.PI;
    }

    private static final double WHITE_LINE_SPEED = .2;
//    protected void driveToWhiteLine(double dir) throws InterruptedException {
//        driveToWhiteLine(dir, WHITE_LINE_SPEED);
//    }
//
//    protected void driveToWhiteLineSlow(double dir) throws InterruptedException {
//        driveToWhiteLine(dir, WHITE_LINE_SPEED / 2.0);
//    }

    private static final double LINE_LIGHT_READING_MIN = 4.0;
    private static final double LINE_READING_SCALE_FACTOR = 3.0;
//    private void driveToWhiteLine(double dir, double speed) throws InterruptedException {
//        robot.drive(dir, speed, 0.0);
//        final double line_limit = LINE_LIGHT_READING_MIN; // robot.getAverageLightMeter() * LINE_READING_SCALE_FACTOR;
//        while(opModeIsActive() && robot.getLineLightReading() < LINE_LIGHT_READING_MIN) {
//            robot.loop();
//            idle();
//        }
//        robot.stopDriveMotors();
//
//    }

    private static final double SNEAKY_SPEED = .2;
    private static final double SNEAKY_SCALE = 0.01;
//    protected void sneakToBeacons() throws InterruptedException {
//        while(! robot.touchSensorPressed()){
//            final double orientation = robot.getHeading();
//            robot.drive(pressersDir(), SNEAKY_SPEED, orientation * SNEAKY_SCALE);
//            idle(); // If this all of the sudden stops working, drop the idle!!
//        }
//        robot.stopDriveMotors();
//    }

//    protected void sneakToBeaconAssumingCenteredRobot() {
//        while (! robot.touchSensorPressed()) {
//
//        }
//    }

//    private static final double SLOW_LINE_SPEED = .1;
//    protected void driveForwardToWhiteLine() throws InterruptedException {
//        // Let's assume that we're going to overshoot the first time
//        driveToWhiteLine(forwardDir(), SLOW_LINE_SPEED);
//    }

//    private BeaconFinder finder = null;
//
//    public BeaconFinder.Status beaconLoop() {
//        if (null == finder) {
//            finder = new BeaconFinder(robot, vision, telemetry);
//        }
//        return finder.loop();
//    }
//
//    public void alignToVisionTarget() throws InterruptedException {
//        int rot = (int) vision.getOrientation();
//        turn(rot);
//    }

//    public void averageColor() {
//         private double c =0; //c=count
//        for(){
//            c++;
//        }
//    }
}
