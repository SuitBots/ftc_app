package com.suitbots.resq;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

abstract public class BuildingBlocks extends LinearOpMode {

    private static final int STOP_DISTANCE = 45;
    private static final double SLOW_SPEED = 1.0;
    private static final double SLIGHTLY_SLOWER_SPEED = 0.30;

    // Wait this many cycles for the gyro to reset
    private static final int MAX_HARDWARE_WAIT = 100;

    // The angle is close enough
    // TODO: THIS NEEDS TO BE TUNED FURTHER.
    private static final int CLOSE_ENOUGH_TO_ZERO = 1;

    private static final double WHEEL_DIAMETER_INCH = 4.25;
    private static final double METERS_PER_INCH = 0.0254;
    private static final double WHEEL_RADIUS_M = (WHEEL_DIAMETER_INCH * METERS_PER_INCH) * Math.PI;
    private static final double GEAR_REDUCTION = 16.0 / 22.0;
    private static final double EFFECTIVE_WHEEL_RADIUS_M = WHEEL_RADIUS_M * GEAR_REDUCTION;
    private static final double ENCODER_TICKS_PER_REVOLUTION = 1120.0;

    private static final double ENCODER_TICKS_PER_METER =
            ENCODER_TICKS_PER_REVOLUTION / EFFECTIVE_WHEEL_RADIUS_M;

    // Stop before the distance sensor thinks you're too close.
    void stopppppppp(Isaac5 isaac5, double max_meters) throws InterruptedException {
        int target_ticks = Math.abs(ticksForDistance(max_meters));
        isaac5.zeroMotorEncoders();

        final double power = .5;

        debug("Driving");

        boolean quit = false;
        while(opModeIsActive() && !quit)
        {
            isaac5.setDriveMotorSpeeds(power, power);

            int distance = isaac5.getDistance();
            quit = distance > STOP_DISTANCE;

            int ticks = Math.abs(isaac5.getEncoderAverage());
            int ticks_remaining = target_ticks - ticks;

            quit |= ticks_remaining < 0;

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

    int ticksForDistance(double meters) {
        // hey, don't forget that these encoders run backwards. Woo.
        return (int) (ENCODER_TICKS_PER_METER * meters);
    }

    void debug(String msg) {
        telemetry.addData("Debug", msg);
    }

    public void driveMeters(Isaac5 isaac5, double meters) throws InterruptedException {
        driveMeters(isaac5, meters, 0.5);
    }

    // Drive forward some number of meters.
    public void driveMeters(Isaac5 isaac5, double meters, double power) throws InterruptedException {
        isaac5.zeroMotorEncoders();

        int target_ticks = ticksForDistance(meters);

        boolean quit = false;
        // RUN_TO_POSITION always ignores sign in power.
        power *= signd(meters);
        waitOneFullHardwareCycle();
        while(opModeIsActive() && !quit) {
            isaac5.setDriveMotorSpeeds(power, power);
            isaac5.sendSensorTelemetry();
            final int ticks = isaac5.getEncoderAverage();
            quit = Math.abs(target_ticks) <= Math.abs(ticks);
        }
        isaac5.stop();
    }

    public void driveForwardUntilWhiteTape(Isaac5 isaac5, double max_meters) throws InterruptedException {
        int target_ticks = Math.abs(ticksForDistance(max_meters));

        isaac5.zeroMotorEncoders();

        boolean quit = false;

        final double power = signd(max_meters) * 0.25;

        debug("Driving");

        isaac5.setDriveMotorSpeeds(power, power);
        while(opModeIsActive() && !quit) {
            int ticks = Math.abs(isaac5.getEncoderAverage());
            int ticks_remaining = target_ticks - ticks;
            quit = (ticks_remaining <= 0) || isaac5.isOnWhiteLine();
            waitOneFullHardwareCycle();
        }
        debug("Done.");
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
    }

    // Motor speed for turning
    private static final double TURN_SPEED = 0.5;
    private static final int TURN_TOLEARANCE = 5;
    // Rotate this many degrees and then stop.
    public void rotateDegrees(Isaac5 isaac5, int desiredDegrees) throws InterruptedException {
        // Sorry. You can't just spin around.
        desiredDegrees %= 360;

        if (CLOSE_ENOUGH_TO_ZERO >= Math.abs(desiredDegrees)) {
            return;
        }

        resetGyro(isaac5);

        double power = TURN_SPEED;

        boolean quit = false;
        while(opModeIsActive() && !quit) {
            if (0 < desiredDegrees) { // turning right, so heading should get smaller
                isaac5.setDriveMotorSpeeds(power, -power);
            } else { // turning left, so heading gets bigger.
                isaac5.setDriveMotorSpeeds(-power, power);
            }

            final int currentHeading = - isaac5.getHeadingRaw();
            final int headingDiff = Math.abs(desiredDegrees - currentHeading);

            telemetry.addData("Headings", String.format("Target: %d, Current: %d", desiredDegrees, currentHeading));

            quit = headingDiff <= TURN_TOLEARANCE;
            waitOneFullHardwareCycle();
        }

        isaac5.stop();
    }

    public void rotateOnWhiteLine(Isaac5 isaac5, int degrees) throws InterruptedException {
        isaac5.activateSensors();
        resetGyro(isaac5);

        boolean quit = false;
        while (opModeIsActive() && !quit) {

        }
        isaac5.stop();
    }

    public static final int CLIMBER_WAIT_TIME_MS = 1000;
    /// Actuate the servo and wait appropriately for the climbers to fall
    public void dumpClimbers(Isaac5 isaac5) throws InterruptedException {
        isaac5.moveDumperArmToThrowPosition();
        Thread.sleep(CLIMBER_WAIT_TIME_MS);
        isaac5.resetDumperArm();
    }
}
