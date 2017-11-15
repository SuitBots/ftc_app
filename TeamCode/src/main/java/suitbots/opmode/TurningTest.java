package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.AutoBase;
import suitbots.Robot;

@TeleOp(name = "Turning Test")
public class TurningTest extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap, telemetry);
        Controller c = new Controller(gamepad1);

        while (! isStarted()) {
            telemetry.addData("Gyro Calibrated?", robot.isGyroCalibrated() ? "YES" : "no.");
            telemetry.addData("Runtime", getRuntime());
            telemetry.update();
        }

        waitForStart();

        while(opModeIsActive()) {
            c.update();
            if (c.AOnce()) {
                turnRad(Math.PI / 2.0);
            } else if (c.BOnce()) {
                turnRad(- Math.PI / 2.0);
            } else if (c.XOnce()) {
                turnRad(Math.PI);
            } else if (c.YOnce()) {
                turnRad(- Math.PI);
            }
        }
    }

    @Override
    protected double forwardDir() {
        return 0;
    }
}
