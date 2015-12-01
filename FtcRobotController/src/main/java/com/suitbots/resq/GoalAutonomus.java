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

    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        isaac5.calibrateGyro();

        waitForStart();

        Alliance alliance = getAlliance();
        int turn_angle = alliance == Alliance.BLUE ? 45 : -45;

        driveMeters(isaac5, 1.0 * SQUARE_SIZE);
        rotateDegrees(isaac5, turn_angle);
        driveForwardUntilWhiteTape(isaac5, 2.5 * SQUARE_SIZE * SQRT_2);
        rotateDegrees(isaac5, turn_angle);
        stopppppppp(isaac5);

        // Only dump the climbers if we can see *some* light.
        // Otherwise, leave that for teleop.
        if (COLOR_THRESHOLD <= isaac5.getRedFore() || COLOR_THRESHOLD < isaac5.getBlueFore()) {
            dumpClimbers(isaac5);

            boolean left_is_red = COLOR_THRESHOLD <= isaac5.getRedFore();

            if ((Alliance.RED == alliance) == left_is_red) {
                isaac5.setDriveMotorSpeeds(0.5, 0.0);
            } else {
                isaac5.setDriveMotorSpeeds(0.0, 0.5);
            }
            Thread.sleep(250);

            isaac5.stop();
        }
    }
}
