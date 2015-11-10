package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by cp on 11/8/15.
 */
public class LeftAndRight extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        waitForStart();
        isaac5.calibrateGyro();

        rotateDegrees(isaac5, 90);
        Thread.sleep(1000);
        rotateDegrees(isaac5, -90);
    }

    private boolean inside(int begin, int end, int current) {
        if (end < begin) {
            return !(end <= current && current <= begin);
        } else {
            return begin <= current && current <= end;
        }
    }

    void rotateDegrees(Isaac5 isaac5, int degrees) throws InterruptedException {
        int start = isaac5.getHeading();
        int finish = start + degrees;

        if (degrees > 0) {
            isaac5.turnLeft();
        } else {
            isaac5.turnRight();
        }

        while(opModeIsActive()) {
            int heading = isaac5.getHeading();
            boolean inside = (degrees > 0) // turning right?
                ? inside(start, finish, heading)
                : inside(finish, start, heading);
            if(! inside) { break; }
            Thread.sleep(20);
            isaac5.sendSensorTelemetry();
        }
        isaac5.stop();
    }
}
