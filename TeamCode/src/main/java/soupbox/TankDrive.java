package soupbox;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Drive like a tank. The left stick controls the left wheels.
 * The right stick controls the right wheels.
 */
@TeleOp(name = "Tank")
public class TankDrive extends OpMode {
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        robot.loop();
        final double l = Math.pow(-gamepad1.left_stick_y, 3.0);
        final double r = Math.pow(-gamepad1.right_stick_y, 3.0);
        robot.setMotors(l, l, r, r);
    }
}
