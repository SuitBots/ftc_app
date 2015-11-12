package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class LeftAndRight extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);
        telemetry.addData("Status", "Waiting for start.");
        isaac5.calibrateGyro();
        waitForStart();
        telemetry.addData("Status", "Started!");

        telemetry.addData("Status", "Rotating +180");
        rotateDegrees(isaac5, 180);

        while(! gamepad1.a) {
            Thread.sleep(50);
        }

        telemetry.addData("Status", "Rotating -180");
        rotateDegrees(isaac5, -180);
        telemetry.addData("Status", "Done");
    }

    private boolean inside(int begin, int end, int heading) {
        if (begin < end) {
            return begin <= heading && heading <= end;
        } else {
            return !(end <= heading && heading <= begin);
        }
    }

    void rotateDegrees(Isaac5 isaac5, int degrees) throws InterruptedException {
        int starting_heading = isaac5.getHeading();
        int final_heading = (starting_heading + degrees) % 360;

        if (degrees > 0) {
            isaac5.turnLeft();
        } else {
            isaac5.turnRight();
        }

        while (opModeIsActive()) {
            int heading = isaac5.getHeading();
            telemetry.addData("Headings", String.format("H: %d, S: %d, F: %d",
                    heading, starting_heading, final_heading));
            if (degrees < 0) {
                if (! inside(final_heading, starting_heading, heading)) {
                    break;
                }
            } else {
                if (! inside(starting_heading, final_heading, heading)) {
                    break;
                }
            }
            Thread.sleep(100);
        }

        isaac5.stop();
    }
}
