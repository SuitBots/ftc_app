package com.suitbots.resq;

import com.qualcomm.robotcore.hardware.configuration.DeviceInfoAdapter;

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
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry, this);
        isaac5.enableBlueLED(alliance == Alliance.BLUE);
        isaac5.enableRedLED(alliance == Alliance.RED);
        isaac5.calibrateGyro();
        isaac5.activateSensors();

        int turn_angle = alliance == Alliance.BLUE ? 38 : -38;

        telemetry.addData("Alliance", Alliance.RED == alliance ? "RED" : "BLUE");
        telemetry.addData("Turn Angle", turn_angle);

        waitForStart();


        // Start up against the wall to ensure angle. One square forward

        driveMeters(isaac5, 2.4, 0.5);

        driveForwardUntilWhiteTape(isaac5, 0.25);

        // We don't go forward *quite* enough, so go a little more.
        //driveMeters(isaac5, 0.05, 0.5);

        rotateDegrees(isaac5, turn_angle);

        state("Approach the goal");
        stopppppppp(isaac5, 0.1);

        // Save on batteries?
        isaac5.deactivateSensors();

        if (0 < isaac5.getAlphaFore()) {
            dumpClimbers(isaac5);

            boolean left_is_red = isaac5.getRedFore() > Math.max(isaac5.getGreenFore(), isaac5.getBlueFore());
            boolean go_left = (Alliance.RED == alliance) == left_is_red;

            double left_speed = go_left ? -1.0 : 1.0;
            double right_speed = go_left ? 1.0 : -1.0;

            isaac5.setDriveMotorSpeeds(left_speed, right_speed);
            Thread.sleep(1000);
            isaac5.stop();

        }

        isaac5.stop();
        isaac5.enableBlueLED(false);
        isaac5.enableRedLED(false);
    }
}
