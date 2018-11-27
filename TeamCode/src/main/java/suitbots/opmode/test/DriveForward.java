package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.opmode.AutoBase;

@TeleOp(name = "Drive Straight", group = "Single")
@Disabled
public class DriveForward extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        double distance = 0.0;

        final Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            c.update();

            if (c.A()) {
                distance = 12.0;
            } else if (c.B()) {
                distance = 18.0;
            } else if (c.X()) {
                distance = 24.0;
            } else if (c.Y()) {
                distance = 0.0;
            }

            if (c.dpadUpOnce()) {
                driveInches(distance);
            } else if (c.dpadDownOnce()) {
                driveInches(- distance);
            } else {
                debugDrive(c);
            }

            help(distance);
        }
    }

    private void help(final double d) {
        telemetry.addData("A", "12in");
        telemetry.addData("B", "18in");
        telemetry.addData("X", "24in");
        telemetry.addData("Y", "0 in");
        telemetry.addData("Distance", d);
        telemetry.addData("Dup", "Foward");
        telemetry.addData("Ddown", "Backwards");
        telemetry.update();
    }
}
