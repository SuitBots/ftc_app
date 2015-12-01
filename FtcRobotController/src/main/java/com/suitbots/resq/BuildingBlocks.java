package com.suitbots.resq;

import android.os.PowerManager;
import android.text.InputFilter;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

abstract public class BuildingBlocks extends LinearOpMode {


    private static final int SLOW_DOWN_DISTANCE = 45;
    private static final int STOP_DISTANCE = 35;
    private static final double SLOW_SPEED = 0.5;
    private static final double SLIGHTLY_SLOWER_SPEED = 0.35;

    // Wait this many cycles for the gyro to reset
    private static final int MAX_HARDWARE_WAIT = 100;
    // The angle is close enough
    private static final int CLOSE_ENOUGH_TO_ZERO = 2;
    // Motor speed for turning
    private static final double TURN_SPEED = 0.4;
    private static final int ENCODER_TICKS_PER_METER = 2800; // Tuned 11/24
    // margin of error for motor tick similarity
    private static final int EPISLON_TICKS = ENCODER_TICKS_PER_METER / 100;
    // scale up motor speeds to compensate
    private static double POWER_SCALE_UP = 1.25;
    private static final int ENCODER_TICKS_PER_DEGREE = 12; // TODO: TUNE


    // Stop before the distance sensor thinks you're too close.
    // From Stopppppp
    void stopppppppp(Isaac5 isaac5)
    {
        boolean quit = false;
        while(opModeIsActive() && !quit)
        {
            int distance = isaac5.getDistance();
            if (distance > STOP_DISTANCE) {
                quit = true;
                isaac5.stop();
            } else if (distance > SLOW_DOWN_DISTANCE) {
                isaac5.setDriveMotorSpeeds(SLIGHTLY_SLOWER_SPEED, SLIGHTLY_SLOWER_SPEED);
            } else {
                isaac5.setDriveMotorSpeeds(SLOW_SPEED, SLOW_SPEED);
            }
        }
        isaac5.stop();
    }

    /// Get the sign of a double. Returns +/- 1.0
    private double signd(double x) {
        if (x < 0.0) {
            return -1.0;
        } else {
            return 1.0;
        }
    }


    private static int mod(int a, int n) {
        return (a % n + n) % n;
    }

    private static int angleDiff(int a, int b) {
        int diff = b - a;
        return mod(diff + 180, 360) - 180;
    }

    // To make sure we don't drift off course, make sure the right motor and left
    // motor are cycling roughly the same number of times.
    // TODO: Use the gyro sensor for this problem.
    private void equalizeEncoders(Isaac5 isaac5, double power) {
        int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
        int RightEncoder = Math.abs(isaac5.getRightEncoder());

        double left_power = power, right_power = power;

        if (LeftEncoder - RightEncoder > EPISLON_TICKS) {
            right_power *= POWER_SCALE_UP;
        } else if (RightEncoder - LeftEncoder > EPISLON_TICKS) {
            left_power *= POWER_SCALE_UP;
        }

        isaac5.setDriveMotorSpeeds(left_power, right_power);
    }

    // Drive forward some number of meters.
    // From MeterDash
    public void driveMeters(Isaac5 isaac5, double meters) throws InterruptedException {
        isaac5.zeroMotorEncoders();

        int target_ticks = Math.abs((int) (ENCODER_TICKS_PER_METER * meters));

        boolean quit = false;
        while(opModeIsActive() && !quit){
            isaac5.sendSensorTelemetry();

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);

            double power = signd(meters) *
                    (ticks_remaining < (ENCODER_TICKS_PER_METER / 10) ?
                            SLIGHTLY_SLOWER_SPEED : SLOW_SPEED);

            telemetry.addData("Ticks", String.format("%d %d", ticks_remaining, target_ticks));

            // Let's assume that it takes us a little bit to stop
            quit = ticks_remaining <= ENCODER_TICKS_PER_METER / 50;

            telemetry.addData("Meter", String.format("%d %f %d %d",
                    target_ticks, power, LeftEncoder, RightEncoder));

            equalizeEncoders(isaac5, power);
        }
        isaac5.stop();
    }

    public void driveForwardUntilWhiteTape(Isaac5 isaac5, double max_meters) {
        int target_ticks = Math.abs((int) (ENCODER_TICKS_PER_METER * max_meters));

        isaac5.activateSensors();

        isaac5.zeroMotorEncoders();
        boolean quit = false;
        while(opModeIsActive() && !quit) {
            isaac5.sendSensorTelemetry();

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);

            double power = (ticks_remaining < (ENCODER_TICKS_PER_METER / 10) ?
                            SLIGHTLY_SLOWER_SPEED : SLOW_SPEED);

            telemetry.addData("Ticks", String.format("%d %d", ticks_remaining, target_ticks));

            equalizeEncoders(isaac5, power);

            // Let's assume that it takes us a little bit to stop
            quit = ticks_remaining <= ENCODER_TICKS_PER_METER / 50;
            quit |= isaac5.isOnWhiteLine();
        }
        isaac5.stop();
        isaac5.deactivateSensors();
    }


    private void resetGyro(Isaac5 isaac5) throws InterruptedException {
        isaac5.resetHeading();
        int count = 0;
        while (count++ < MAX_HARDWARE_WAIT &&
                CLOSE_ENOUGH_TO_ZERO < Math.abs(isaac5.getHeadingRaw())) {
            waitOneFullHardwareCycle();
        }

        telemetry.addData("Hardware Wait", count);

    }

    // Rotate this many degrees and then stop.
    // From LeftAndRight
    public void rotateDegrees(Isaac5 isaac5, int degrees) throws InterruptedException {
        // Sorry. You can't just spin around.
        degrees %= 360;

        if (CLOSE_ENOUGH_TO_ZERO >= Math.abs(degrees)) {
            return;
        }

        resetGyro(isaac5);
        isaac5.zeroMotorEncoders();

        final int max_ticks = Math.abs(ENCODER_TICKS_PER_DEGREE * degrees);

        boolean quit = false;
        while(opModeIsActive() && !quit) {
            waitOneFullHardwareCycle();
            int heading = isaac5.getHeadingRaw();
            int diff = Math.abs(degrees - heading);
            quit = diff <= CLOSE_ENOUGH_TO_ZERO;

            telemetry.addData("Heading", heading);
            telemetry.addData("Degrees", degrees);

            double power = diff > 15 ? TURN_SPEED : (TURN_SPEED / 2.0);

            if (0 < degrees) { // turning right, so heading should get smaller
                isaac5.setDriveMotorSpeeds(power, -power);
            } else { // turning left, so heading gets bigger.
                isaac5.setDriveMotorSpeeds(-power, power);
            }

            // fail safe: also stop if the encoders say to. In case the gyro misbhaves.
            quit |= max_ticks <= Math.abs(isaac5.getLeftEncoder());
        }

        isaac5.stop();

        telemetry.addData("Final", String.format("%d %d", degrees, -isaac5.getHeadingRaw()));

    }

    public static final int CLIMBER_WAIT_TIME_MS = 500;
    /// Actuate the servo and wait appropriately for the climbers to fall
    public void dumpClimbers(Isaac5 isaac5) throws InterruptedException {
        isaac5.moveDumperArmToThrowPosition();
        sleep(CLIMBER_WAIT_TIME_MS);
        isaac5.resetDumperArm();
        sleep(CLIMBER_WAIT_TIME_MS);
    }
}
