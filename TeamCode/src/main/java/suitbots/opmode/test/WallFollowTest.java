package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.ConfigVars;
import suitbots.opmode.AutoBase;

@TeleOp(name = "Wall Follow", group = "Single")
public class WallFollowTest extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();

        waitForStart();

        final Controller c = new Controller(gamepad1);

        while (opModeIsActive()) {
            if (c.XOnce()) {
                driveRightWall(ConfigVars.INCHES_FROM_WALL, 24.0);
            }
            debugDrive(c);
        }
    }
}
