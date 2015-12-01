package com.suitbots.resq;

public class MeterDash extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        while (opModeIsActive()) {
            isaac5.sendSensorTelemetry();
            telemetry.addData("Time", time);
            if (gamepad1.a) {
                driveForwardUntilWhiteTape(isaac5, 1.0);
            } else if (gamepad1.b) {
                driveMeters(isaac5, -1.0);
            } else if (gamepad1.x) {
                driveForwardUntilWhiteTape(isaac5, 0.5);
            } else if (gamepad1.y) {
                driveMeters(isaac5, -0.5);
            }
            isaac5.stop();
        }
    }
}
