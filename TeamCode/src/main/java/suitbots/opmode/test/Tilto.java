package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.opmode.AutoBase;

@Disabled
@TeleOp(name = "Drive until Tilt", group = "Single")
public class Tilto extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        final Controller c = new Controller(gamepad1);

        while (opModeIsActive()) {
            c.update();

            if (c.dpadUpOnce()) {
                driveWithPowerUntilTilt(.5, .5, 10.0);
            } else if (c.dpadDownOnce()) {
                driveWithPowerUntilTilt(-.5, -.5, 10.0);
            } else {
                debugDrive(c);
            }
        }
    }
}
