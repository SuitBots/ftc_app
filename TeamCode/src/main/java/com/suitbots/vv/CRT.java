package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@Disabled
@TeleOp(name = "CRT", group = "Test")
public class CRT extends LinearOpMode {
    private LazyCR back, front;

    private void toggle(LazyCR servo, long wait) throws InterruptedException {
        servo.setPower(-1.0);
        sleep(wait);
        servo.setPower(0.0);
        sleep(250);
        servo.setPower(1.0);
        sleep(wait);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        back = new LazyCR(hardwareMap.crservo.get("pb"));
        front = new LazyCR(hardwareMap.crservo.get("pf"));

        Controller c = new Controller(gamepad1);

        waitForStart();

        while (opModeIsActive()) {
            c.update();

            if (gamepad1.a) {
                back.setPower(1.0);
            } else if (gamepad1.b) {
                back.setPower(-1.0);
            } else {
                back.setPower(0.0);
            }

            if (gamepad1.x) {
                front.setPower(1.0);
            } else if (gamepad1.y) {
                front.setPower(-1.0);
            } else {
                front.setPower(0.0);
            }

            if (c.leftBumperOnce()) {
                toggle(front, 1000);
            }
            if (c.rightBumperOnce()) {
                toggle(back, 1500);
            }
        }
    }
}
