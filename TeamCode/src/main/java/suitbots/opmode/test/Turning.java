package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.external.Func;

import suitbots.opmode.AutoBase;

@TeleOp(name = "Turning", group = "Single")
public class Turning extends AutoBase {
    private double angle = 0.0;
    @Override
    public void runOpMode() {
        initialize();
        waitForStart();

        telemetry.addLine("Angles")
                .addData("Z", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format("%.2f", getRotationZ());
                    }
                })
                .addData("Z Raw", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format("%.2f", getRotationZRaw());
                    }
                })
        .addData("Z Off", new Func<String>() {
            @Override
            public String value() {
                return String.format("%.2f", getRotationZOffset());
            }
        })
        ;
        telemetry.addLine("Help")
                .addData("A", "45 Degrees")
                .addData("B", "90 Degrees")
                .addData("X", "180 Degrees")
                .addData("Y", "0 in")
                .addData("Dleft", "Left")
                .addData("Dright", "Right")
                .addData("Target", new Func<Double>() {
                    @Override
                    public Double value() {
                        return angle;
                    }
                });

        final Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            updateOrientation();
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

            if (c.rightBumper()) {
                resetGyro();
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
        telemetry.update();
    }
}
