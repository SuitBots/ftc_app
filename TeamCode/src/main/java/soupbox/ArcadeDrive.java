package soupbox;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Arcade-style drive. The left stick controls both left
 * and right wheels, leaving the right stick free for
 * other uses.
 */
@TeleOp(name = "Arcade")
public class ArcadeDrive extends OpMode {
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        robot.loop();
        final double x = Math.pow(gamepad1.left_stick_x, 3.0);
        final double y = Math.pow(-gamepad1.left_stick_y, 3.0);

        final double l = y + x;
        final double r = y - x;

        robot.setMotors(l, l, r, r);
    }
}
