package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Suit Bots on 11/22/2016.
 */
public abstract class ShalfDogronAutonomousBeta extends LinearOpMode {
    private MecanumRobot robot;

    public enum Alliance {
        RED, BLUE
    }

    public abstract Alliance getAlliance();

    @Autonomous(name = "Shalf RED")
    public static class Red extends ShalfDogronAutonomousBeta {
        public Alliance getAlliance() { return Alliance.RED; }
    }

    @Autonomous(name = "Shalf BLUE")
    public static class Blue extends ShalfDogronAutonomousBeta {
        public Alliance getAlliance() { return Alliance.BLUE; }
    }

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

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new MecanumRobot(hardwareMap, telemetry);

        while (robot.isCalibrating()) {
            telemetry.addData("Gyro", "Calibrating");
            telemetry.update();
            idle();
        }
        telemetry.addData("Gyro", "CalibratED");
        telemetry.update();
        robot.resetGyro();

        waitForStart();

        robot.onStart();

        setPhase("Diag to wall");
        driveDiagonalToTheWall();
        trueUp();
        /*
        setPhase("To white line");
        driveForwardToWhiteLine();
        trueUp();
        achieveWallDistance(DISTANCE_TO_WALL_CM, 500);
        setPhase("Press buttons 1");
        pressButton();
        setPhase("Back to line");
        driveBackToWhiteLine();
        trueUp();
        achieveWallDistance(DISTANCE_TO_WALL_CM, 500);
        setPhase("Press buttons 2");
        pressButton();
        setPhase("Rotate");
        rotate();
        setPhase("Shoot");
        shoot();
        */
        setPhase("Done");
    }

    public static double DISTANCE_TO_WALL_CM = 15.0;
    protected void driveDiagonalToTheWall() throws InterruptedException {
        while(DISTANCE_TO_WALL_CM < robot.distanceToWallCM()) {
            robot.drivePreservingDirection(diagonalDirection(), 1.0);
            idle();
        }
        robot.stop();
    }

    public static final double WHITE_LINE_SPEED = 1.0;
    protected void driveToWhiteLine(double dir) throws InterruptedException {
        driveToWhiteLine(dir, WHITE_LINE_SPEED);
    }

    public static final double LINE_LIGHT_READING_MIN = 3.0;
    protected void driveToWhiteLine(double dir, double speed) throws InterruptedException {
        while(opModeIsActive() && robot.getLineLightReading() < LINE_LIGHT_READING_MIN) {
            double direction = dir;
            double distance = robot.distanceToWallCM();

            // While we're driving to the line, make sure we preserve our distance to the wall.
            if (distance > DISTANCE_TO_WALL_CM) {
                direction = (2.0 * direction + leftDir()) / 3.0;
            } else if (distance < DISTANCE_TO_WALL_CM) {
                direction = (2.0 * direction - leftDir()) / 3.0;
            }

            robot.drivePreservingDirection(direction, speed);
            idle();
        }
    }

    protected void driveForwardToWhiteLine() throws InterruptedException {
        // Let's assume that we're going to overshoot the first time
        driveToWhiteLine(forwardDir());
        driveToWhiteLine(forwardDir() - Math.PI, .3);
    }

    public static final long INITIAL_BACKUP_TIME = 1000;
    protected void driveBackToWhiteLine() throws InterruptedException {
        // Before we start looking for the white line, let's get off the one we're currently on.
        long t0 = System.currentTimeMillis();
        while(opModeIsActive() && INITIAL_BACKUP_TIME > (System.currentTimeMillis() - t0)) {
            robot.drivePreservingDirection(forwardDir() - Math.PI, .5);
        }
        robot.stop();
        trueUp();
        // Let's assume that we're going to overshoot the first time
        driveToWhiteLine(forwardDir() - Math.PI);
        driveToWhiteLine(forwardDir(), .3);
    }

    Alliance getBeaconColor() {
        return robot.colorSensorIsBlue() ? Alliance.BLUE : Alliance.RED;
    }

    public static final double FUDGE_FACTOR = .5;
    protected void achieveWallDistance(double distance, long timeout_ms) {
        final long t0 = System.currentTimeMillis();
        while (opModeIsActive() && timeout_ms > (System.currentTimeMillis() - t0)) {
            double diff = distance - robot.distanceToWallCM();
            if (FUDGE_FACTOR < Math.abs(diff)) {
                break;
            }

            double direction = diff > 0.0 ? leftDir() : leftDir() - Math.PI;

            robot.drivePreservingDirection(direction, 0.4);
        }
        robot.stop();
    }

    public static final double BEACON_PRESSING_MOVE_CM = 4.0;
    protected void pressButton() throws InterruptedException {
        final boolean back_button = getBeaconColor() == getAlliance();

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }

        achieveWallDistance(DISTANCE_TO_WALL_CM - BEACON_PRESSING_MOVE_CM, 1000);
        achieveWallDistance(DISTANCE_TO_WALL_CM, 2000);

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }
        robot.stop();
    }

    // Correct for any heading drift during a previous stage
    public static final int ALLOWABLE_HEADING_DRIFT = 2;
    protected void trueUp() {
        while (ALLOWABLE_HEADING_DRIFT < Math.abs(robot.getHeading())) {
            robot.drivePreservingDirection(0.0, 0.0);
        }
        robot.stop();
    }

    public static final int DESIRED_RELATIVE_HEADING = -90;
    protected void rotate() throws InterruptedException {
        while (opModeIsActive() && ALLOWABLE_HEADING_DRIFT < Math.abs(DESIRED_RELATIVE_HEADING - robot.getHeading())) {
            robot.drive(0.0, 0.0, -.4);
        }
        robot.stop();
    }

    protected void shoot() throws InterruptedException {
        robot.fire();
        robot.toggleDispenser();
        while (! robot.isDoneFlipping()) {
            idle();
        }
        robot.fire();
        while (robot.isDoneFlipping()) {
            idle();
        }
        telemetry.update();
    }

    protected void setPhase(String phase) {
        telemetry.addData("Phase", phase);
        telemetry.update();
    }
}
