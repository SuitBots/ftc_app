package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.concurrent.Callable;


public abstract class AutonomousBase extends LinearOpMode  {
    protected MecanumRobot robot;
    protected VisionTargets vision;

    protected void initRobot() {
        initRobot(true);
    }

    protected void initRobot(boolean initialize_vision) {
        robot = new MecanumRobot(hardwareMap, telemetry);
        if (initialize_vision) {
            vision = new VisionTargets();
            vision.activate();
        }

    }

    public void onStart() {
        robot.onStart();
    }

    public void onStop() {
        if (vision != null) {
            vision.deactivate();
        }
        robot.onStop();
    }

    protected void snooze(int ms) throws InterruptedException {
        if (opModeIsActive()) {
            sleep(ms);
        }
    }


    private int angleDifference(int from, int to) {
        if (from < 0) from += 360;
        if (to < 0) to += 360;

        int diff = to - from;

        if (diff < -180) {
            diff += 360;
        } else if (diff > 180) {
            diff = - (360 - diff);
        }

        return diff;
    }

    private static final double SAFE_TURN_SPEED = .1;
    private static final double FAST_TURN_SPEED = .15;
    private static final double STUPID_TURN_SPEED = .3;
    private static final int FAST_TURN_THRESHOLD = 30;
    private static final int STUPID_TURN_THRESHOLD = 60;

    private static double speedForTurnDistance(int angle) {
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

    protected void turnUntilBeaconIsVisible(int degrees) throws InterruptedException {
        degrees = - degrees;
        robot.resetGyro();
        while(opModeIsActive() && ! vision.canSeeWall()) {
            int diff = angleDifference(robot.getHeading(), degrees);
            if (MAX_HEADING_SLOP >= Math.abs(diff)) break;
            double speed = speedForTurnDistance(diff);
            robot.drive(0.0, 0.0, diff > 0 ? -speed : speed);
            idle();
        }
        robot.stopDriveMotors();
    }

    protected void turnToAngle(int degrees) throws InterruptedException {
        while(opModeIsActive()) {
            int diff = angleDifference(robot.getHeading(), degrees);
            if (MAX_HEADING_SLOP >= Math.abs(diff)) break;
            double speed = speedForTurnDistance(diff);
            robot.drive(0.0, 0.0, diff > 0 ? -speed : speed);
            idle();
        }
        robot.stopDriveMotors();
    }

    protected void turn(int degrees) throws InterruptedException {
        robot.resetGyro();
        turnToAngle(- degrees);
    }

    protected void driveDirectionTiles(double directionRadians, double tiles) throws InterruptedException {
        driveDirectionTiles(directionRadians, tiles, .35);
    }

    protected void driveDirectionTilesFast(double directionRadians, double tiles) throws InterruptedException {
        driveDirectionTiles(directionRadians, tiles, .65);
    }

    protected void driveDirectionTiles(double directionRadians, double tiles, double power) throws InterruptedException {
        robot.setEncoderDrivePower(power);
        robot.encoderDriveTiles(directionRadians, tiles);
        while (opModeIsActive() && robot.driveMotorsBusy()) {
            robot.updateSensorTelemetry();
            telemetry.update();
            robot.loop();
            idle();
        }
        robot.stopDriveMotors();
        robot.resetDriveMotorModes();
        robot.clearEncoderDrivePower();
    }

    private void fire(long timeout) throws InterruptedException {
        robot.fire();
        final long t0 = System.currentTimeMillis();
        while (robot.isFlipping()) {
            robot.loop();
            final long t1 = System.currentTimeMillis();
            if (timeout < (t1 - t0)) {
                break;
            }
        }
        robot.setFlipperPower(0.0);
    }

    protected void shoot(int n) throws InterruptedException {
        if (1 > n) {
            return;
        }
        fire(800);
        if (2 > n) {
            return;
        }
        robot.toggleDispenser();
        sleep(500);
        fire(800);
    }

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
    protected void driveToWhiteLine(double dir) throws InterruptedException {
        driveToWhiteLine(dir, WHITE_LINE_SPEED);
    }

    protected void driveToWhiteLineBackSensor(double dir) throws InterruptedException {
        driveToWhiteLineBackSensor(dir, WHITE_LINE_SPEED);
    }

    protected void driveToWhiteLineBackSensorSlow(double dir) throws InterruptedException {
        driveToWhiteLineBackSensor(dir, WHITE_LINE_SPEED / 2.0);
    }

    protected void driveToWhiteLineBackSensor(double dir, double speed) throws InterruptedException {
        driveToWhiteLine(dir, speed, true);
    }

    protected void driveToWhiteLineSlow(double dir) throws InterruptedException {
        driveToWhiteLine(dir, WHITE_LINE_SPEED / 2.0);
    }

    private static final double LINE_LIGHT_READING_MIN = 4.0;
    protected void driveToWhiteLine(double dir, double speed) throws InterruptedException {
        driveToWhiteLine(dir, speed, true);
    }

    private double ods(boolean front) {
        return front ? robot.getLineLightReadingF() : robot.getLineLightReadingB();
    }

    private void driveToWhiteLine(double dir, double speed, boolean frontSensor) throws InterruptedException {
        robot.drive(dir, speed, 0.0);
        while(opModeIsActive() && ods(frontSensor) < LINE_LIGHT_READING_MIN) {
            robot.loop();
            idle();
        }
        robot.stopDriveMotors();

    }

    private final double SNEAKY_SPEED = .2;
    protected void sneakToBeacons(){
        while(! robot.touchSensorPressed()){
            final double orientation = robot.getHeading();
            robot.drive(pressersDir(), SNEAKY_SPEED, orientation / 50.0);
        }
        robot.stopDriveMotors();
    }

    private static final double SLOW_LINE_SPEED = .2;
    protected void driveForwardToWhiteLine() throws InterruptedException {
        // Let's assume that we're going to overshoot the first time
        driveToWhiteLine(forwardDir(), SLOW_LINE_SPEED);
    }

    /*
    // Assumes that you're parallel to the wall, range sensor facing it
    protected void achieveWallDistance(double distance, AllianceColor alliance) throws InterruptedException {
        for (int i = 0; i < 4; ++i) {
            if (vision.canSeeWall()) {
                break;
            }
            sleep(100);
        }
        while (vision.canSeeWall()) {
            robot.loop();
            final double d = vision.getXOffset();
            if (.5 >= Math.abs(d - distance)) {
                break;
            }
            final double direction = d > distance ? pressersDir() : (pressersDir() + Math.PI);
            robot.drive(direction, 0.1, 0.0);
            idle();
        }
        robot.stopDriveMotors();
    } */

    private BeaconFinder finder = null;

    public BeaconFinder.Status beaconLoop() {
        if (null == finder) {
            finder = new BeaconFinder(robot, vision, telemetry);
        }
        return finder.loop();
    }

    public void alignToVisionTarget() throws InterruptedException {
        int rot = (int) vision.getOrientation();
        turn(rot);
    }
}
