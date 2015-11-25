package com.suitbots.resq;

import android.os.Build;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Samantha on 11/23/2015.
 */

public abstract class Autonomus extends BuildingBlocks {
    enum Alliance {
        RED, BLUE
    }

    abstract Alliance getAlliance();

    @Override
    public void runOpMode()throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        double square_size = 0.6; // meters

        Alliance alliance = getAlliance();
        int turn_angle = alliance == Alliance.BLUE ? 45 : -45;

        driveForwardMeters(isaac5, 2.0 * square_size);
        rotateDegrees(isaac5, turn_angle);
        driveForwardMeters(isaac5, 1.5 * square_size * Math.sqrt(2.0));
        rotateDegrees(isaac5, turn_angle);
        stopppppppp(isaac5);

        isaac5.moveDumperArmToThrowPosition();
        Thread.sleep(500);
        isaac5.resetDumperArm();
        Thread.sleep(500);

        boolean left_is_red = 10 <= isaac5.getRedFore();

        if ((Alliance.RED == alliance) == left_is_red) {
            isaac5.setDriveMotorSpeeds(1.0, 0.0);
        } else {
            isaac5.setDriveMotorSpeeds(0.0, 1.0);
        }
        Thread.sleep(1000);
        isaac5.stop();
    }
}
