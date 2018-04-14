package suitbots;

/**
 * Created by Samantha on 9/21/2017.
 */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;


public abstract class AutoBase extends LinearOpMode  {
    protected Robot robot;
    protected Controller c;
    protected void debug(String wat) {
        telemetry.addData("What", wat);
        telemetry.update();
        while (opModeIsActive()) {
            c.update();
            if (c.XOnce()) {
                break;
            }
        }
    }



    public void initialize(HardwareMap hm, Telemetry telemetry) {
        robot = new Robot(hm, telemetry);
        robot.resetEncoders();
        c = new Controller(gamepad1);
    }

    protected void snooze(int ms) throws InterruptedException {
        if (opModeIsActive()) {
            sleep(ms);
        }
    }

    private static final double SAFE_TURN_SPEED = .1;
    private static final double FAST_TURN_SPEED = .35;
    private static final double LUCDACRIS_TURN_SPEED = .35;
    private static final double FAST_TURN_THRESHOLD = Math.PI / 6.0;
    private static final double LUDACRIS_TURN_THRESHOLD = Math.PI / 3.0;

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
        if (angle > LUDACRIS_TURN_THRESHOLD) {
            return LUCDACRIS_TURN_SPEED;
        }
        if (angle > FAST_TURN_THRESHOLD) {
            return FAST_TURN_SPEED;
        }
        return SAFE_TURN_SPEED;
    }

    private static final double MAX_HEADING_SLOP = Math.PI / 40.0;

    protected void turnToAngleRad(double radians) throws InterruptedException {
        while(opModeIsActive()) {
            robot.loop();
            double diff = angleDifference(robot.getHeadingRadians(), radians);
            if (MAX_HEADING_SLOP >= Math.abs(diff)) break;
            double speed = speedForTurnDistance(diff);
            robot.drive(0.0, 0.0, diff > 0 ? -speed : speed);
            idle();
        }
        robot.stopDriveMotors();
    }

    protected void turnRad(double radians) throws InterruptedException {
        robot.resetGyro();
        turnToAngleRad(radians);
    }

    protected static double avg(double[] xs) {
        double total = 0.0;
        for (double x : xs) total += x;
        return total / xs.length;
    }
    static final int VELOCITY_BUFFER_SIZE = 32;
    protected void driveUntilStop(double directionRadians, double speed) throws InterruptedException {
        double velocity[] = new double[VELOCITY_BUFFER_SIZE];
        int vc = 0;
        double maximum = 0.0;
        robot.drive(directionRadians, speed, 0.0);
        while(opModeIsActive()) {
            robot.loop();
            velocity[vc++ % VELOCITY_BUFFER_SIZE] = robot.absoluteVelocity();
            final double current = avg(velocity);
            if (current > maximum) {
                maximum = current;
            }
            if (current < (maximum / 2.0)) {
                break;
            }
        }
        robot.stopDriveMotors();
    }

    protected void driveDirectionTiles(double directionRadians, double tiles, double power) throws InterruptedException {
        driveDirectionTiles(directionRadians, tiles, power, Double.MAX_VALUE);
    }

    protected void driveDirectionTiles(double directionRadians, double tiles, double power, double maxTime) throws InterruptedException {
        final double t0 = getRuntime();
        robot.setEncoderDrivePower(power);
        robot.encoderDriveTiles(directionRadians, tiles);
        while (opModeIsActive() && robot.driveMotorsBusy() && (maxTime > (getRuntime() - t0))) {
            robot.loop();
            telemetry.update();
            idle();
        }
        robot.stopDriveMotors();
        robot.resetDriveMotorModes();
        robot.clearEncoderDrivePower();
    }


    protected abstract double forwardDir();

    public static final double TURN_ANGLE = Math.PI / 20.0;
    public void knockForward() throws InterruptedException {
        turnRad(- TURN_ANGLE);
        robot.putUpSoas();
        snooze(500);
        turnRad(TURN_ANGLE);
    }
    public void knockBackward() throws InterruptedException {
        turnRad(TURN_ANGLE);
        robot.putUpSoas();
        snooze(500);
        turnRad(- TURN_ANGLE);
    }

    public double getVelocity() {
        return robot.absoluteVelocity();
    }

    public double maxVelocity(double previousVelocity, double newVelocity) {
        if(previousVelocity > newVelocity){
           return previousVelocity;
        }else{
            return newVelocity;
        }
    }

    public boolean checkVelocity(double maxVelocity, double lastVelocity) {
        return maxVelocity >= lastVelocity;
    }

}
