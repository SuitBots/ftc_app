package com.suitbots.resq;

import android.os.Build;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class MeterDash extends BuildingBlocks {
    @Override
    public void runOpMode()throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        while (opModeIsActive()) {
            isaac5.sendSensorTelemetry();
            if (gamepad1.a) {
                driveForwardMeters(isaac5, 1.0);
            } else if (gamepad1.b) {
                driveForwardMeters(isaac5, -1.0);
            } else if (gamepad1.x) {
                driveForwardMeters(isaac5, 0.5);
            } else if (gamepad1.y) {
                driveForwardMeters(isaac5, -0.5);
            }

            isaac5.stop();
        }
    }
}
