package suitbots;

/**
 * Created by Samantha on 9/21/2017.
 */
import com.qualcomm.robotcore.eventloop.SyncdDevice;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.Callable;


public abstract class AutoBase extends LinearOpMode  {
    protected Robot robot;

    public void initialize(HardwareMap hm, Telemetry telemetry) {
        robot = new Robot(hm, telemetry);
    }

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

    private void turnToAngleDeg(double degrees) throws InterruptedException {
        degrees = Math.toRadians(degrees);
        turnToAngleRad(degrees);
    }
    private void turnToAngleRad(double radians) throws InterruptedException {
        while(opModeIsActive()) {
            double diff = angleDifference(robot.getHeadingRadians(), radians);
            if (MAX_HEADING_SLOP >= Math.abs(diff)) break;
            double speed = speedForTurnDistance(diff);
            robot.drive(0.0, 0.0, diff > 0 ? -speed : speed);
            idle();
        }
        robot.stopDriveMotors();
    }

    protected void turnRad(final double r) throws InterruptedException {
        robot.resetGyro();
        turnToAngleRad(r);
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
            //robot.updateSensorTelemetry();
            telemetry.update();
            idle();
        }
        robot.stopDriveMotors();
        robot.resetDriveMotorModes();
        robot.clearEncoderDrivePower();
    }

    protected abstract double forwardDir();

    public void knockForward() throws InterruptedException {
        driveDirectionTiles(0, .15, .25);
    }
    public void knockBackward() throws InterruptedException {
        driveDirectionTiles(Math.PI, .15, .25);
    }
}
