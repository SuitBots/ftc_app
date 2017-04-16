package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp(name = "Shooter Test")
public class Gunner extends AutonomousBase {

    @Override
    public double forwardDir() {
        return Math.PI;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initRobot(false);
        waitForStart();
        Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            c.update();
            if (c.XOnce()) {
                shoot(2);
            }
            if (c.YOnce()) {
                shoot(1);
            }
            if (c.AOnce()) {
                robot.toggleDispenser();
            }
        }
    }
}
