package soupbox;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Mecanum teleop (with an optional arcade mode)
 * * Left stick controls x/y translation.
 * * Right stick controls rotation about the z axis
 * * When arcade mode is enabled (press "a"), translation direction
 * becomes relative to the field as opposed to the robot. You can
 * reset the forward heading by pressing "x".
 */
@TeleOp(name = "Mecanum")
public class Mecanum extends OpMode {
    private Robot robot;
    private Controller controller;
    private boolean arcadeMode = false;
    private int gyroCalibratedCount = 0;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
        robot.runUsingEncoders();
        controller = new Controller(gamepad1);
    }

    @Override
    public void init_loop() {
        controller.update();
        if (controller.AOnce()) {
            arcadeMode = ! arcadeMode;
        }
        telemetry.addData("Gyro Ready?", robot.isGyroCalibrated() ? "YES" : "no.");
        telemetry.addData("Arcade Mode (a)", arcadeMode ? "YES" : "no.");
        telemetry.update();
    }

    @Override
    public void loop() {
        controller.update();
        robot.loop();

        if (controller.XOnce()) {
            robot.resetHeading();
        }
        if (controller.AOnce()) {
            arcadeMode = !arcadeMode;
        }
        telemetry.addData("Arcade Mode (a)", arcadeMode ? "YES" : "no.");
        telemetry.addData("Heading (reset: x)", robot.getHeadingDegrees());
        telemetry.update();

        final double x = Math.pow(controller.left_stick_x, 3.0);
        final double y = Math.pow(controller.left_stick_y, 3.0);

        final double rotation = Math.pow(controller.right_stick_x, 3.0);
        final double direction = Math.atan2(x, y) + (arcadeMode ? robot.getHeading() : 0.0);
        final double speed = Math.min(1.0, Math.sqrt(x * x + y * y));

        final double lf = speed * Math.sin(direction + Math.PI / 4.0) + rotation;
        final double rf = speed * Math.cos(direction + Math.PI / 4.0) - rotation;
        final double lr = speed * Math.cos(direction + Math.PI / 4.0) + rotation;
        final double rr = speed * Math.sin(direction + Math.PI / 4.0) - rotation;

        robot.setMotors(lf, lr, rf, rr);
    }
}
