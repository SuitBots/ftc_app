package com.suitbots.resq;

/**
 * Created by Samantha on 11/23/2015.
 */

public abstract class GoalAutonomus extends BuildingBlocks {
    enum Alliance {
        RED, BLUE
    }

    abstract Alliance getAlliance();

    public static final double SQUARE_SIZE = 0.6; // meters
    public static final double SQRT_2 = Math.sqrt(2.0);

    private void pause() throws InterruptedException {
        Thread.sleep(500);
    }

    private static int COLOR_THRESHOLD = 10;

    private void state(String msg) {
        telemetry.addData("State", msg);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        Alliance alliance = getAlliance();
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        isaac5.enableBlueLED(alliance == Alliance.BLUE);
        isaac5.enableRedLED(alliance == Alliance.RED);
        isaac5.calibrateGyro();
        isaac5.activateSensors();

        int turn_angle = alliance == Alliance.BLUE ? 45 : -45;

        telemetry.addData("Alliance", Alliance.RED == alliance ? "RED" : "BLUE");
        telemetry.addData("Turn Angle", turn_angle);

        waitForStart();


        // Start up against the wall to ensure angle. One square forward

        state("Drive 1");
        driveMeters(isaac5, SQUARE_SIZE);
        stopSleep(isaac5);
        // Turn towards the beacon line
        state("Rotate 1");
        rotateDegrees(isaac5 ,turn_angle);
        stopSleep(isaac5);
        // fast drive the first diagonal square

        state("Drive 2");
        driveMeters(isaac5, 2.0 * SQUARE_SIZE * SQRT_2);
        stopSleep(isaac5);
        // then slow down and stop for the tape
        state("Tape Search");
        driveForwardUntilWhiteTape(isaac5, SQUARE_SIZE * SQRT_2);
        stopSleep(isaac5);
        // Face the beacon

        state("Rotate 2");
        rotateDegrees(isaac5, turn_angle);
        stopSleep(isaac5);
        // Drive just up to the beacon
        state("Distance Stop");
        stopppppppp(isaac5, SQUARE_SIZE / 2.0);
        stopSleep(isaac5);
        // Save on batteries?
        isaac5.deactivateSensors();

        // Climbers out.
        // NOTE: Move this inside the COLOR_THRESHOLD block if it turns out that
        // we don't always get to the beacon exactly.
        state("Climber Dump");
        dumpClimbers(isaac5);

        state("Button Press");
        // If you see a light, figure out which button to press and press it
        if (COLOR_THRESHOLD <= isaac5.getRedFore() || COLOR_THRESHOLD < isaac5.getBlueFore()) {
            boolean left_is_red = COLOR_THRESHOLD <= isaac5.getRedFore();

            boolean go_left = (Alliance.RED == alliance) == left_is_red;

            // To make sure we actually hit the button, give it a few good whacks.
            final int NUM_WHACKS = 3;
            final int CYCLE_TIME_MS = 500;
            for (int i = 0; i < NUM_WHACKS; ++i) {
                isaac5.setDriveMotorSpeeds(go_left ? 0.5 : 0.0,
                                           go_left ? 0.0 : 0.5);
                Thread.sleep(CYCLE_TIME_MS);
                isaac5.setDriveMotorSpeeds(go_left ? -.5 : 0.0,
                        go_left ? 0.0 : -.5);
                Thread.sleep(CYCLE_TIME_MS / 2);
            }

            isaac5.stop();
        }

        // Now roll on over to the parking zone.
        state("Back up");
        driveMeters(isaac5, -0.2);
        state("Rotate 3");
        rotateDegrees(isaac5, 2 * turn_angle);
        state("Parking drive.");
        driveMeters(isaac5, SQUARE_SIZE);
        isaac5.stop();
        isaac5.enableBlueLED(false);
        isaac5.enableRedLED(false);
    }
}
