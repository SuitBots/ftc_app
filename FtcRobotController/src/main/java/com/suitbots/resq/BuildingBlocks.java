package com.suitbots.resq;

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

    private static final double WHEEL_RADIUS_M = 0.1016 * Math.PI;
    private static final double ENCODER_TICKS_PER_REVOLUTION = 1120.0;

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
    void stopppppppp(Isaac5 isaac5, double max_meters) throws InterruptedException {
        int target_ticks = Math.abs((int) (ticksForDistance(max_meters)));
        isaac5.resetHeading();
        isaac5.zeroMotorEncoders();
        while (! isaac5.encodersAreZero()) {
            waitOneFullHardwareCycle();
        }

        double power = signd(max_meters);

        boolean quit = false;
        while(opModeIsActive() && !quit)
        {
            isaac5.setDriveMotorSpeeds(power, power);
            int distance = isaac5.getDistance();
            quit = distance > STOP_DISTANCE;

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);

            quit |= ticks_remaining < 0;

            if (! quit) {
                waitOneFullHardwareCycle();
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

    int ticksForDistance(double meters) {
        return (int) (ENCODER_TICKS_PER_REVOLUTION * meters / WHEEL_RADIUS_M);
    }

    // Drive forward some number of meters.
    // From MeterDash
    public void driveMeters(Isaac5 isaac5, double meters) throws InterruptedException {
        isaac5.resetHeading();
        isaac5.zeroMotorEncoders();

        int target_ticks = ticksForDistance(meters);

        isaac5.resetEncoders();

        while(! isaac5.encodersAreZero()) {
            waitOneFullHardwareCycle();
        }

        isaac5.setEncoderTargets(target_ticks);

        double start_time = getRuntime();
        boolean quit = false;
        while(opModeIsActive() && !quit){
            isaac5.sendSensorTelemetry();
            double power = signd(meters);
            isaac5.setDriveMotorSpeeds(power, power);
            quit = ! isaac5.motorsAreBusy();
            if (! quit) {
                waitOneFullHardwareCycle();
            }
        }
        isaac5.stop();
        isaac5.reetEncoderMode();
    }

    public void driveForwardUntilWhiteTape(Isaac5 isaac5, double max_meters) throws InterruptedException {
        driveForwardUntilWhiteTape(isaac5, max_meters, 10.0);
    }

    public void driveForwardUntilWhiteTape(Isaac5 isaac5, double max_meters, double max_time) throws InterruptedException {
        int target_ticks = Math.abs(ticksForDistance(max_meters));

        isaac5.activateSensors();
        isaac5.resetHeading();
        isaac5.zeroMotorEncoders();

        while (! isaac5.encodersAreZero()) {
            waitOneFullHardwareCycle();
        }

        boolean quit = false;

        final double start_time = getRuntime();
        final double power = signd(max_meters);

        while(opModeIsActive() && !quit) {
            isaac5.sendSensorTelemetry();

            int LeftEncoder = Math.abs(isaac5.getLeftEncoder());
            int RightEncoder = Math.abs(isaac5.getRightEncoder());

            int ticks_remaining = target_ticks - Math.max(LeftEncoder, RightEncoder);


            telemetry.addData("Ticks", String.format("%d %d", ticks_remaining, target_ticks));

            isaac5.setDriveMotorSpeeds(power, power);

            quit = ticks_remaining <= (target_ticks / 50) || isaac5.isOnWhiteLine();
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

    public void rotateOnWhiteLine(Isaac5 isaac5, int degrees) throws InterruptedException {
        isaac5.activateSensors();
        isaac5.resetHeading();
        telemetry.addData("Heading", "Resetting...");
        while(0 < Math.abs(isaac5.getHeadingRaw())) {
            waitOneFullHardwareCycle();
        }
        telemetry.addData("Heading", "Reset.");

        do {
            if (isaac5.isOnWhiteLine()) { // can still see the line, so turn
                telemetry.addData("Line Follow", "Turn");
                if (degrees < 0) {
                    isaac5.turnLeftSlowly();
                } else {
                    isaac5.turnRightSlowly();
                }
            } else { // drive forward until we can
                telemetry.addData("Line Follow", "Forward");
                isaac5.goForwardSlowly();
            }
            waitOneFullHardwareCycle();
        } while (opModeIsActive() && isaac5.getHeadingRaw() < Math.abs(degrees));
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
