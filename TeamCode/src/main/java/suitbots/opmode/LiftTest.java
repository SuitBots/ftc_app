package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.Robot;

@TeleOp(name = "Lift Test")
public class LiftTest extends OpMode {
    private Robot robot;
    private Controller controller;
    private int up, down;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
        controller = new Controller(gamepad1);
    }

    @Override
    public void loop() {
        controller.update();

        telemetry.addData("List Position", robot.getLiftIndex());
        telemetry.addData("Lift Target", robot.DEBUGgetLiftTarget());
        telemetry.addData("Lift Current", robot.DEBUGgetLiftCurrent());
        telemetry.addData("Lift Mode", robot.DEBUGgetLiftMode());
        telemetry.addData("up", up);
        telemetry.addData("down", down);
        telemetry.update();

        if (controller.dpadUpOnce()) {
            robot.indexLiftUp();
            ++up;
        } else if (controller.dpadDownOnce()) {
            robot.indexLiftDown();
            ++down;
        } else {
            robot.moveLift(controller.left_stick_y);
        }
    }
}
