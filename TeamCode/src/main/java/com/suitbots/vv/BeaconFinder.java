package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

public class BeaconFinder {
    private MecanumRobot robot;
    private VisionTargets vision;
    private Telemetry telemetry;

    public BeaconFinder(MecanumRobot robot_, VisionTargets vision_, Telemetry telemetry_) {
        robot = robot_;
        vision = vision_;
        telemetry = telemetry_;
    }

    public enum Status {
        CONTINUE, STOP, NO_DATA
    }

    private static final float FINAL_THRESHOLD = 1f;

    public Status loop() {
        return loop(false);
    }

    private double calcSpeed(double distance) {
        if (distance > 60.0) {
            return .5;
        }
        if (distance > 40.0) {
            return .35;
        }
        if (distance > 20.0) {
            return .25; // .25
        }
        if (distance > 1.0) {
            return .1; // .1
        }
        return 0.0;
    }

    private static final float DESIRED_X_DISTANCE_CM = 10f;

    public Status loop(boolean testing) {
        if (! vision.canSeeWall()) {
            robot.stopDriveMotors();
            return Status.NO_DATA;
        }

        float x = vision.getXOffset() - DESIRED_X_DISTANCE_CM;
        float y = - vision.getYOffset();
        double rot = vision.getOrientation();

        float thresh = FINAL_THRESHOLD * (testing ? 5f : 1f);

        if (Math.abs(x) < thresh &&
                Math.abs(y) < thresh &&
                Math.abs(rot) < thresh) {
            robot.stopDriveMotors();
            return Status.STOP;
        }

        double d = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(-x, y);

        double speed = calcSpeed(d);
        // todo: tune
        // double rspeed = Math.max(.1, Math.abs(rot) / 100.0) * rot < 0.0 ? -1.0 : 1.0;
        double rspeed = rot / 100.0;

        if (Math.abs(rot) < 1.0) {
            rspeed = 0.0;
        }

        if(testing) {
            telemetry.addData("Drive",
                    String.format(Locale.US, "%.2f, %.2f, %.2f",
                            Math.toDegrees(theta), speed, rspeed));
        } else {
            robot.drive(theta, speed, rspeed);
        }

        return Status.CONTINUE;
    }

    public double getOrientation() {
        return vision.canSeeWall() ? vision.getOrientation() : 0.0;
    }

    @Disabled
    @TeleOp(name = "Beacon Finder Test", group = "Test")
    public static class TestTeleop extends AutonomousBase {
        private BeaconFinder beacons;
        private Controller g1;
        private Status last_status = Status.STOP;

        @Override
        public double forwardDir() { return 0.0; }

        @Override
        public void runOpMode() throws InterruptedException {
            initRobot();
            g1 = new Controller(gamepad1);
            beacons = new BeaconFinder(robot, vision, telemetry);

            while (! isStarted()) {
                telemetry.addData("Ready", robot.isCalibrating() ? "no" : ">>> YES <<<");
                telemetry.update();
            }

            robot.onStart();

            while (opModeIsActive()) {
                g1.update();

                telemetry.addData("Target", String.format(Locale.US, "X: %.2f Y: %.2f θ: %.2f",
                        vision.getXOffset() / 2.54, vision.getYOffset() / 2.54, vision.getOrientation()));
                telemetry.addData("Status", last_status);
                telemetry.update();

                if (g1.XOnce()) {
                    while (opModeIsActive() && Status.CONTINUE == beacons.loop()) {
                        idle();
                    }
                    robot.stopDriveMotors();
                } else if (g1.A()) {
                    last_status = beacons.loop();
                } else {
                    DriveHelper.drive(g1, robot);
                }
            }
        }
    }
}
