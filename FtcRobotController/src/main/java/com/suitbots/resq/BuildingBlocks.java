package com.suitbots.resq;

import android.util.LruCache;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

abstract public class BuildingBlocks extends LinearOpMode {


    public static final int SLOW_DOWN_DISTANCE = 45;
    public static final int STOP_DISTANCE = 35;
    final double SLOW_SPEED = 0.5;
    final double SLOWER_SPEED = 0.2;
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
                isaac5.setDriveMotorSpeeds(SLOWER_SPEED, SLOWER_SPEED);
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

    // TODO: Tune this variable for the mars rover
    public static final int ENCODER_TICKS_PER_METER = 2800; // <- Brandon's Calculation
    // Drive forward some number of meters.
    // From MeterDash
    public void driveForwardMeters(Isaac5 isaac5, double meters) {
        isaac5.zeroMotorEncoders();

        int target_ticks = -(int) (ENCODER_TICKS_PER_METER * meters);

        double power = signd(meters) * 0.5;

        boolean quit = false;
        while(opModeIsActive() && !quit){
            isaac5.sendSensorTelemetry();
            int LeftEncoder = isaac5.getLeftEncoder();
            int RightEncoder = isaac5.getRightEncoder();

            if(power < 0.0) {
                quit = LeftEncoder > target_ticks && RightEncoder > target_ticks;
            } else if (power > 0.0) {
                quit = LeftEncoder < target_ticks && RightEncoder < target_ticks;
            }

            telemetry.addData("Meter", String.format("%d %f %d %d", target_ticks, power, LeftEncoder, RightEncoder));

            isaac5.setDriveMotorSpeeds(power, power);
        }
        isaac5.stop();
    }

    private static int mod(int a, int n) {
        return (a % n + n) % n;
    }

    private static int angleDiff(int a, int b) {
        int diff = b - a;
        return mod(diff + 180, 360) - 180;
    }

    // Rotate this many degrees and then stop.
    // From LeftAndRight
    public void rotateDegrees(Isaac5 isaac5, int degrees) {
        int starting_heading = isaac5.getHeading();
        int final_heading = (starting_heading + degrees) % 360;

        boolean quit = false;
        while (opModeIsActive() && !quit) {
            int heading = isaac5.getHeading();
            int remaining = angleDiff(heading, final_heading);

            double speed = (remaining < 10) ? .25 : .5;
            if (degrees > 0) {
                isaac5.setDriveMotorSpeeds(-speed, speed);
            } else {
                isaac5.setDriveMotorSpeeds(speed, -speed);
            }

            if (degrees < 0) {
                quit = remaining >= 0;
            } else {
                quit = remaining <= 0;
            }

            telemetry.addData("Headings", String.format("H: %d, S: %d, F: %d",
                    heading, starting_heading, final_heading));
        }

        isaac5.stop();
    }

    /// Helper function for turning left.
    public void turnLeftDegrees(Isaac5 isaac5, int degrees) {
        rotateDegrees(isaac5, degrees);
    }

    /// Helper function for turning right.
    void turnRightDegrees(Isaac5 isaac5, int degrees) {
        rotateDegrees(isaac5, -degrees);
    }

    public static final int CLIMBER_WAIT_TIME_MS = 1000;
    /// Actuate the servo and wait appropriately for the climbers to fall
    public void dumpClimbers(Isaac5 isaac5) throws InterruptedException {
        isaac5.moveDumperArmToThrowPosition();
        Thread.sleep(CLIMBER_WAIT_TIME_MS);
        isaac5.resetDumperArm();
    }
}
