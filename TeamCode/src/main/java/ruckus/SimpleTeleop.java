package ruckus;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class SimpleTeleop extends OpMode {
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
    }

    @Override
    public void loop() {
        robot.drive(gamepad1.left_stick_y, gamepad1.right_stick_y);
    }
}
