package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.AutoBase;
import suitbots.DriverHelper;

@Disabled
@TeleOp(name = "Velocity Drive")
public class VelocityDriveTest extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap, telemetry);
        Controller c = new Controller(gamepad1);
        waitForStart();

        while (opModeIsActive()) {
            robot.loop();
            c.update();
            if (c.XOnce()) {
                driveUntilStop(0.0, .25);
            } else if (c.YOnce()) {
                driveUntilStop(0.0, .5);
            } else if (c.AOnce()) {
                driveUntilStop(0.0, .75);
            } else if (c.BOnce()) {
                driveUntilStop(0.0, 1.0);
            } else {
                DriverHelper.drive(c, robot);
            }
            telemetry.addData("Velocity", robot.absoluteVelocity());
            telemetry.update();
        }
    }

    @Override
    protected double forwardDir() {
        return 0;
    }
}
