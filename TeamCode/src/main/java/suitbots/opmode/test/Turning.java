package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.opmode.AutoBase;

@TeleOp(name = "Turning", group = "Single")
public class Turning extends AutoBase {
    @Override
    public void runOpMode() {
        initialize();
        waitForStart();

        double angle = 0.0;

        final Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            c.update();

            if (c.A()) {
                angle = 45.0;
            } else if (c.B()) {
                angle = 90.0;
            } else if (c.X()) {
                angle = 180.0;
            } else if (c.Y()) {
                angle = 0.0;
            }

            if (c.dpadLeftOnce()) {
                turnDegrees(- angle);
                angle = 0.0;
            } else if (c.dpadRightOnce()) {
                turnDegrees(angle);
                angle = 0.0;
            } else {
                debugDrive(c);
            }

            help(angle);
        }
    }

    private void help(final double angle) {
        telemetry.addData("A", "45 Degrees");
        telemetry.addData("B", "90 Degrees");
        telemetry.addData("X", "180 Degrees");
        telemetry.addData("Y", "0 in");
        telemetry.addData("Angle", angle);
        telemetry.addData("Dleft", "Left");
        telemetry.addData("Dright", "Right");
        telemetry.update();
    }
}
