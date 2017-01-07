package com.suitbots.vv;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Locale;
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
        robot.onStop();
        if (vision != null) {
            vision.deactivate();
        }
    }

    protected void snooze(int ms) throws InterruptedException {
        if (opModeIsActive()) {
            sleep(ms);
        }
    }

    private static final int MAX_HEADING_SLOP = 1;
    private static final double SAFE_TURN_SPEED = .1;
    private static final double FAST_TURN_SPEED = .3;
    private static final double STUPID_TURN_SPEED = .7;
    private static final int FAST_TURN_THRESHOLD = 30;

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

    private static double speedForTurnDistance(int angle) {
        angle = Math.abs(angle);
        if (angle > 60) {
            return STUPID_TURN_SPEED;
        }
        if (angle > FAST_TURN_THRESHOLD) {
            return FAST_TURN_SPEED;
        }
        return SAFE_TURN_SPEED;
    }

    protected void turnToAngle(int degrees) throws InterruptedException {
        while(opModeIsActive()) {
            int diff = angleDifference(robot.getHeading(), degrees);
            if (MAX_HEADING_SLOP >= Math.abs(diff)) break;
            double speed = speedForTurnDistance(diff);
            robot.drive(0.0, 0.0, diff > 0 ? -speed : speed);
            telemetry.addData("Heading", robot.getHeading());
            telemetry.addData("Diff", diff);
            telemetry.update();
            idle();
        }
        robot.stopDriveMotors();
    }

    protected void turn(int degrees) throws InterruptedException {
        robot.resetGyro();
        turnToAngle(degrees);
    }

    private void setState(String msg) {
        telemetry.addData("Base State", msg);
        telemetry.update();
    }

    protected void driveDirectionTiles(double directionRadians, double tiles) throws InterruptedException {
        driveDirectionInches(directionRadians, tiles * 24.0);
    }

    protected void driveDirectionInches(double directionRadians, double inches) throws InterruptedException {
        driveDirectionCM(directionRadians, 2.54 * inches);
    }

    protected void driveDirectionCM(double directionRadians, double cm) throws InterruptedException {
        robot.encoderDriveCM(directionRadians, cm);
        while (opModeIsActive() && robot.driveMotorsBusy()) {
            telemetry.update();
            idle();
        }
        robot.stopDriveMotors();
        robot.resetDriveMotorModes();
    }


    protected void waitTimeout(long ms, Callable<Boolean> test) throws InterruptedException {
        long done = System.currentTimeMillis() + ms;
        try {
            while (opModeIsActive() && done > System.currentTimeMillis() && !test.call()) {
                robot.loop();
                idle();
            }
        } catch(InterruptedException ie) {
            throw ie;
        } catch(Exception e) {
            // pass
        }
    }

    private void fire() throws InterruptedException {
        robot.fire();
        waitTimeout(1000, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                robot.loop();
                return !robot.isFlipping();
            }
        });
        robot.setFlipperPower(0.0);
    }

    protected void shoot(int n) throws InterruptedException {
        if (1 > n) {
            return;
        }
        fire();
        if (2 > n) {
            return;
        }
        robot.toggleDispenser();
        Thread.sleep(500);
        fire();
        robot.toggleDispenser();
    }

    // Let's make some autonomous routines orientation-agnostic.
    protected abstract double forwardDir();
    protected double leftDir() {
        return 3.0 * Math.PI / 2.0;
    }
    protected double leftForwardDir() {
        return (leftDir() + forwardDir()) / 2.0;
    }

    private static final double WHITE_LINE_SPEED = .3;
    private void driveToWhiteLine(double dir) throws InterruptedException {
        driveToWhiteLine(dir, WHITE_LINE_SPEED);
    }

    private static final double LINE_LIGHT_READING_MIN = 3.0;
    private void driveToWhiteLine(double dir, double speed) throws InterruptedException {
        while(opModeIsActive() && robot.getLineLightReading() < LINE_LIGHT_READING_MIN) {
            robot.drive(dir, speed, 0.0);
            idle();
        }
    }

    private static final double SLOW_LINE_SPEED = .2;
    protected void driveForwardToWhiteLine() throws InterruptedException {
        // Let's assume that we're going to overshoot the first time
        driveToWhiteLine(forwardDir(), SLOW_LINE_SPEED);
    }

    // Assumes that you're parallel to the wall, range sensor facing it
    protected void achieveWallDistance(double distance, AllianceColor alliance) throws InterruptedException {
        for (int i = 0; i < 4; ++i) {
            if (vision.canSeeWall()) {
                break;
            }
            sleep(100);
        }
        while (vision.canSeeWall()) {
            final double d = vision.getXOffset();
            if (.5 >= Math.abs(d - distance)) {
                break;
            }
            final double direction = d > distance ? leftDir() : (leftDir() + Math.PI);
            robot.drive(direction, 0.1, 0.0);
            idle();
        }
        robot.stopDriveMotors();

    }

    private BeaconFinder finder = null;

    public BeaconFinder.Status beaconLoop() {
        if (null == finder) {
            finder = new BeaconFinder(robot, vision, telemetry);
        }
        return finder.loop();
    }
}
