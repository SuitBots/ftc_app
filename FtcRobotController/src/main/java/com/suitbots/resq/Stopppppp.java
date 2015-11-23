package com.suitbots.resq;

public class Stopppppp extends BuildingBlocks {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                stopppppppp(isaac5);
            }
        }
    }
}
