package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.suitbots.util.Controller;

import suitbots.Robot;

/**
 * Created by robot on 4/12/18.
 */
@TeleOp(name = "Clamp Test")
public class ClampTest extends OpMode {
    private Controller controller;
    private Servo servo;


    @Override
    public void init() {
        servo = hardwareMap.servo.get("clamp");
        controller = new Controller(gamepad1);
    }
    @Override
    public void loop() {
        controller.update();
        if (controller.AOnce()) {
            servo.setPosition(0.0);
        } else if (controller.BOnce()) {
            servo.setPosition(.25);
        } else if (controller.XOnce()) {
            servo.setPosition(.75);
        } else if (controller.YOnce()) {
            servo.setPosition(1.);
        }
        telemetry.addData("Current Position", servo.getPosition());
        telemetry.update();
    }
}
