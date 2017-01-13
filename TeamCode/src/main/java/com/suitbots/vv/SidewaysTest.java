package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp(name = "Drive Sideways")
public class SidewaysTest extends AutonomousBase {
    @Override
    public double forwardDir() {
        return 0.0;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initRobot();
        waitForStart();
        Controller c = new Controller(gamepad1);
        double speed = 1.0;
        while (opModeIsActive()) {
            c.update();
            if (c.AOnce()) {
                speed /= 2.0;
            }
            if (c.BOnce()) {
                speed *= 2.0;
            }
            if (c.XOnce()) {
                speed += .1;
            }
            if (c.YOnce()) {
                speed -= .1;
            }
            telemetry.addData("Speed", speed);
            telemetry.update();
            if (c.dpadLeftOnce()) {
                driveDirectionTiles(Math.PI / 2.0, .5, speed);
            } else if (c.dpadRightOnce()) {
                driveDirectionTiles(3.0 * Math.PI / 2.0, .5, speed);
            } else {
                DriveHelper.drive(c, robot);
            }
        }
    }
}
