package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.opmode.AutoBase;

@TeleOp(name = "Lift", group = "Single")
public class Lifto extends AutoBase {
    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        double distance = 0.0;


        final Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            c.update();

            if (c.A()) {
                distance = 50.0;
            } else if (c.B()) {
                distance = 100.0;
            } else if (c.X()) {
                distance = 170.0;
            } else if (c.Y()) {
                distance = 0.0;
            }

            if (c.dpadUpOnce()) {
                runLiftMotor(distance);
                distance = 0.0;
            } else if (c.dpadDownOnce()) {
                runLiftMotor(-distance);
                distance = 0.0;
            } else {
                lift.setPower(gamepad1.left_trigger - gamepad1.right_trigger);
                debugDrive(c);
            }

            help(distance);
        }
    }

    private void help(final double distance) {
        int newerDist = (int) Math.floor(145.6 * (distance / 8.0));
        telemetry.addData("A", "50mm");
        telemetry.addData("B", "100mm");
        telemetry.addData("X", "170mm");
        telemetry.addData("Y", "0 mm");
        telemetry.addData("Distance", distance);
        telemetry.addData("New position", newerDist);
        telemetry.addData("Dup", "up");
        telemetry.addData("Ddown", "down");
        telemetry.addData("lift position", lift.getCurrentPosition());
        telemetry.update();
    }
}
