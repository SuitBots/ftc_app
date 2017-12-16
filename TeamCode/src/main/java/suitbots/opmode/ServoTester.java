package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.Robot;

@TeleOp(name = "SERVO TEST")
public class ServoTester extends OpMode {
    private Robot robot;
    private Controller controller;
    private double x = 0, y = 0;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
        controller = new Controller(gamepad1);
    }

    @Override
    public void loop() {
        controller.update();

        if (controller.dpadDownOnce()) { y -= .05; }
        if (controller.dpadUpOnce()) { y += .05; }
        if (controller.dpadLeftOnce()) { x -= 0.05; }
        if (controller.dpadRightOnce()) { x += 0.05; }

        telemetry.addData("X/Y", String.format("%.2f, %.2f", x, y));
        telemetry.update();

        robot.DEBUG_setSwing(x, y);
    }
}
