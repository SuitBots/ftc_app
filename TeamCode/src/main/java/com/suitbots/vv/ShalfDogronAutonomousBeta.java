package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Created by Suit Bots on 11/22/2016.
 */
public abstract class ShalfDogronAutonomousBeta extends AutonomousBase {
    private int particles_to_shoot = 2;

    private enum Alliance {
        RED, BLUE
    }

    protected abstract Alliance getAlliance();

    @Autonomous(name = "Shalf RED", group = "Tournament")
    public static class Red extends ShalfDogronAutonomousBeta {
        protected Alliance getAlliance() { return Alliance.RED; }
    }

    @Autonomous(name = "Shalf BLUE", group = "Tournament")
    public static class Blue extends ShalfDogronAutonomousBeta {
        protected Alliance getAlliance() { return Alliance.BLUE; }
    }

    private double forwardDir() {
        if(Alliance.RED == getAlliance()) {
            return Math.PI * 2.0;
        } else {
            return Math.PI;
        }
    }

    private double leftDir() {
        return 3.0 * Math.PI / 2.0;
    }
    private double diagonalDirection() {
        return (leftDir() + forwardDir()) / 2.0;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new MecanumRobot(hardwareMap, telemetry);

        Controller c = new Controller(gamepad1);
        while (! isStarted()) {
            c.update();
            if (c.AOnce()) {
                particles_to_shoot = (1 + particles_to_shoot) % 3;
            }
            telemetry.addData("Gyro", robot.isCalibrating() ? "Calibrating" : "CALIBRATED");
            telemetry.addData("Particles to shoot", particles_to_shoot);
            telemetry.update();
            idle();
        }

        robot.resetGyro();
        robot.onStart();

        /*
        if (Alliance.BLUE == getAlliance()) {
            setPhase("Shoot");
            shoot(particles_to_shoot);
        }
        */

        /*
        setPhase("Diag to wall");
        driveDiagonalToTheWall();
        trueUp();
        */
        setPhase("Safety Forward");
        driveDirectionCM(forwardDir(), 60);
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

        if (Alliance.RED == getAlliance()) {
            setPhase("Rotate");
            rotate();
            setPhase("Shoot");
            shoot(particles_to_shoot);

        }
        */
        setPhase("Done");

        robot.onStop();
    }

    private static double DISTANCE_TO_WALL_CM = 20.0;
    private void driveDiagonalToTheWall() throws InterruptedException {
        while(DISTANCE_TO_WALL_CM < robot.distanceToWallCM()) {
            telemetry.addData("Distance", robot.distanceToWallCM());
            telemetry.update();
            robot.drive(diagonalDirection(), 1.0, 0.0);
            idle();
        }
        robot.stopDriveMotors();
    }

    private static final double WHITE_LINE_SPEED = 1.0;
    private void driveToWhiteLine(double dir) throws InterruptedException {
        driveToWhiteLine(dir, WHITE_LINE_SPEED);
    }

    private static final double LINE_LIGHT_READING_MIN = 3.0;
    private void driveToWhiteLine(double dir, double speed) throws InterruptedException {
        while(opModeIsActive() && robot.getLineLightReading() < LINE_LIGHT_READING_MIN) {
            double direction = dir;
            double distance = robot.distanceToWallCM();
            robot.drive(direction, speed, 0.0);
            idle();
        }
    }

    protected void driveForwardToWhiteLine() throws InterruptedException {
        // Let's assume that we're going to overshoot the first time
        driveToWhiteLine(forwardDir());
        driveToWhiteLine(forwardDir() - Math.PI, .3);
    }

    private static final long INITIAL_BACKUP_TIME = 1000;
    protected void driveBackToWhiteLine() throws InterruptedException {
        // Before we start looking for the white line, let's get off the one we're currently on.
        driveDirectionCM(forwardDir() - Math.PI, 60);
        trueUp();
        // Let's assume that we're going to overshoot the first time
        driveToWhiteLine(forwardDir() - Math.PI);
        driveToWhiteLine(forwardDir(), .3);
    }

    private Alliance getBeaconColor() {
        return robot.colorSensorIsBlue() ? Alliance.BLUE : Alliance.RED;
    }

    private static final double FUDGE_FACTOR = .5;
    private void achieveWallDistance(double distance, long timeout_ms) {
        final long t0 = System.currentTimeMillis();
        while (opModeIsActive() && timeout_ms > (System.currentTimeMillis() - t0)) {
            double diff = distance - robot.distanceToWallCM();
            if (FUDGE_FACTOR < Math.abs(diff)) {
                break;
            }

            double direction = diff > 0.0 ? leftDir() : leftDir() - Math.PI;

            robot.drive(direction, 0.4, 0.0);
        }
        robot.stopDriveMotors();
    }

    private static final double BEACON_PRESSING_MOVE_CM = 4.0;
    private void pressButton() throws InterruptedException {
        final boolean back_button = getBeaconColor() == getAlliance();

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }

        driveDirectionCM(3.0 * Math.PI / 2.0, 5);
        driveDirectionCM(Math.PI / 2.0, 5);

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }
        robot.stopDriveMotors();
    }

    // Correct for any heading drift during a previous stage
    private static final int ALLOWABLE_HEADING_DRIFT = 2;
    private void trueUp() {
        turnToHeading(0);
    }

    private static final int DESIRED_RELATIVE_HEADING = -90;
    private static final double SAFE_ROTATION_SPEED = - 0.4;
    private void rotate() throws InterruptedException {
        turnToHeading(DESIRED_RELATIVE_HEADING);
    }


    private void setPhase(String phase) {
        telemetry.addData("Phase", phase);
        telemetry.update();
    }
}
