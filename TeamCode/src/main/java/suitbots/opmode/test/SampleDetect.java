package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import suitbots.opmode.AutoBase;

@TeleOp(name = "Sample Detect", group = "Single")
public class SampleDetect extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        while (opModeIsActive()) {
            announceMinearalPositions();
            telemetry.update();
        }
    }
}
