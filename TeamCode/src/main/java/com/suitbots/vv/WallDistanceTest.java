package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@Disabled
@Autonomous(name = "Wall Test")
public class WallDistanceTest extends AutonomousBase {
    @Override
    public double forwardDir() { return 0.0; }

    @Override
    public void runOpMode() throws InterruptedException {
        Controller g = new Controller(gamepad1);
        initRobot();

        waitForStart();

        while (opModeIsActive()) {
            g.update();
            double distance = 15.0;
            if (g.A()) distance += 5.0;
            if (g.B()) distance += 5.0;
            if (g.X()) distance += 5.0;
            if (g.Y()) distance += 5.0;

            if (g.rightBumperOnce()) {
                achieveWallDistance(distance, AllianceColor.RED);
            }

            double lx = gamepad1.left_stick_x, ly = - gamepad1.left_stick_y;
            double v = Math.sqrt(lx * lx + ly * ly);
            robot.drive(Math.atan2(lx, ly), v, gamepad1.right_stick_x);

            telemetry.addData("Wall Distance", vision.getXOffset());
            telemetry.update();
        }

        onStop();
    }
}
