package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import suitbots.AutoBase;

/**
 * Created by Suit Bots on 11/11/2017.
 */
@Disabled
@Autonomous(name = "TEST")
public class TESTAUTO extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Waiting", "yes");
        telemetry.update();
        waitForStart();
        int counter = 0;
        while (opModeIsActive()) {
            telemetry.addData("Count", counter++);
            telemetry.update();

        }
    }

    @Override
    protected double forwardDir() {
        return 0;
    }
}
