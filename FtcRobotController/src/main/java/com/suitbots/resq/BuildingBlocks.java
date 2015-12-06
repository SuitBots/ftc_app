package com.suitbots.resq;

import android.app.UiAutomation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

abstract public class BuildingBlocks extends LinearOpMode {

    private static final int SLOW_DOWN_DISTANCE = 45;
    private static final int STOP_DISTANCE = 35;
    private static final double SLOW_SPEED = 1.0;
    private static final double SLIGHTLY_SLOWER_SPEED = 0.30;

    // Wait this many cycles for the gyro to reset
    private static final int MAX_HARDWARE_WAIT = 100;

    // The angle is close enough
    // TODO: THIS NEEDS TO BE TUNED FURTHER.
    private static final int CLOSE_ENOUGH_TO_ZERO = 1;
    // Motor speed for turning
    private static final double TURN_SPEED = 0.3;
    private static final int ENCODER_TICKS_PER_METER = 2800; // Tuned 11/24
    // margin of error for motor tick similarity
    private static final int EPISLON_TICKS = ENCODER_TICKS_PER_METER / 100;
    // scale up motor speeds to compensate
    private static double POWER_SCALE_UP = 1.25;
    private static final int ENCODER_TICKS_PER_DEGREE = 12; // TODO: TUNE


    public void stopSleep(Isaac5 isaac5) throws InterruptedException {
        isaac5.stop();
        isaac5.setDriveMotorSpeeds(.0, .0);
        waitOneFullHardwareCycle();
        isaac5.enableRedLED(true);
        Thread.sleep(1500);
        isaac5.enableRedLED(false);
    }

    // Stop before the distance sensor thinks you're too close.
    // From Stopppppp
    void stopppppppp(Isaac5 isaac5, double max_meters)
    {
        int target_ticks = Math.abs((int) (ENCODER_TICKS_PER_METER * max_meters));
        isaac5.zeroMotorEncoders();
        isaac5.resetHeading();

        boolean quit = false;
        while(opModeIsActive() && !quit)
        {
            int distance = isaac5.getDistance();
            if (distance > STOP_DISTANCE) {
                quit = true;
                isaac5.stop();
            } else {
                correctTracking(isaac5, SLIGHTLY_SLOWER_SPEED);
            }

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);


            quit |= ticks_remaining <= ENCODER_TICKS_PER_METER / 50;

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

/*
    private static int mod(int a, int n) {
        return (a % n + n) % n;
    }

    private static int angleDiff(int a, int b) {
        int diff = b - a;
        return mod(diff + 180, 360) - 180;
    }
    */

    // To make sure we don't drift off course, make sure the raw heading is
    // still at zero.
    private void correctTracking(Isaac5 isaac5, double power) {
        int heading = isaac5.getHeadingRaw();

        double left_power = power;
        double right_power = power;

        /*
        if (heading < 0) {
            left_power *= POWER_SCALE_UP;
        } else if (heading > 0) {
            right_power *= POWER_SCALE_UP;
        }
        */

        isaac5.setDriveMotorSpeeds(left_power, right_power);
    }

    public void driveMeters(Isaac5 isaac5, double meters) throws InterruptedException {
        driveMeters(isaac5, meters, 10.0);
    }

    // Drive forward some number of meters.
    // From MeterDash
    public void driveMeters(Isaac5 isaac5, double meters, double max_time) throws InterruptedException {
        isaac5.resetHeading();
        isaac5.zeroMotorEncoders();

        int target_ticks = Math.abs((int) (ENCODER_TICKS_PER_METER * meters));

        double start_time = getRuntime();
        boolean quit = false;
        while(opModeIsActive() && !quit){
            isaac5.sendSensorTelemetry();

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);

            double power = signd(meters) * SLOW_SPEED;

            // Let's assume that it takes us a little bit to stop
            quit = ticks_remaining <= ENCODER_TICKS_PER_METER / 50;
            quit |= max_time <= (getRuntime()- start_time);

            correctTracking(isaac5, power);
        }
        isaac5.stop();
    }

    public void driveForwardUntilWhiteTape(Isaac5 isaac5, double max_meters) throws InterruptedException {
        driveForwardUntilWhiteTape(isaac5, max_meters, 10.0);
    }

    public void driveForwardUntilWhiteTape(Isaac5 isaac5, double max_meters, double max_time) {
        int target_ticks = Math.abs((int) (ENCODER_TICKS_PER_METER * max_meters));

        isaac5.activateSensors();

        isaac5.resetHeading();
        isaac5.zeroMotorEncoders();

        boolean quit = false;

        double start_time = getRuntime();

        while(opModeIsActive() && !quit) {
            isaac5.sendSensorTelemetry();

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);

            double power = SLIGHTLY_SLOWER_SPEED;

            telemetry.addData("Ticks", String.format("%d %d", ticks_remaining, target_ticks));

            correctTracking(isaac5, power);

            quit = ticks_remaining <= ENCODER_TICKS_PER_METER / 50 || isaac5.isOnWhiteLine();
            quit |= max_time < (getRuntime() - start_time);
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
            int heading = - isaac5.getHeadingRaw();

            if (degrees > 0) {
                quit = heading >= (degrees - 3);
            } else {
                quit = heading <= (degrees + 3);
            }

            telemetry.addData("Heading", heading);
            telemetry.addData("Degrees", degrees);

            double power = TURN_SPEED;

            if (0 < degrees) { // turning right, so heading should get smaller
                isaac5.setDriveMotorSpeeds(power, -power);
            } else { // turning left, so heading gets bigger.
                isaac5.setDriveMotorSpeeds(-power, power);
            }

        }

        isaac5.stop();
    }

    public static final int CLIMBER_WAIT_TIME_MS = 2000;
    /// Actuate the servo and wait appropriately for the climbers to fall
    public void dumpClimbers(Isaac5 isaac5) throws InterruptedException {
        isaac5.moveDumperArmToThrowPosition();
        Thread.sleep(CLIMBER_WAIT_TIME_MS);
        isaac5.resetDumperArm();
        Thread.sleep(CLIMBER_WAIT_TIME_MS);
    }
}
