package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.opmode.AutoBase;

@TeleOp(name = "wall drive")
public class WallDriveTest extends AutoBase {
    @Override
    public void runOpMode() {
        initialize();

        final Controller c = new Controller(gamepad1);

        while(opModeIsActive()) {
            c.update();

            if (c.XOnce()) {
                driveUntilNearTheWall(4, 12.0);
            } else {
                debugDrive(c);
            }

            telemetry.addData("Distance", getInchesFromWall());
            telemetry.update();
        }
    }
}
