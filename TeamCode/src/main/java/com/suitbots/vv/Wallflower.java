package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@Disabled
@Autonomous(name = "Wallflower")
public class Wallflower extends AutonomousBase {
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        robot.resetGyro();
        turn(-45, false);
        final double WALL_DISTANCE = 10.0;
        final double P = .4;
        double l = P, r = P;
        robot.setDriveMotors(l, r);
        while (opModeIsActive() && 0 < Math.abs(robot.getHeading())) {
            final double d = robot.getAcousticRangeCM();
            if (d < WALL_DISTANCE) {
                l = P * 1.5;
                r = P / 1.5;
            } else if(d > WALL_DISTANCE) {
                l = P / 1.5;
                r = P * 1.5;
            } else {
                l = r = P;
            }
            robot.setDriveMotors(l, r);
        }
        robot.setDriveMotors(0.0, 0.0);
    }
}
