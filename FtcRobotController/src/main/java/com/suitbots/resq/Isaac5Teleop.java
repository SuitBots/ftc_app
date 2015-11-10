package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Isaac5Teleop extends OpMode {

    private Isaac5 isaac5;

    private void drive() {
            isaac5.setDriveMotorSpeeds(gamepad1.left_stick_y,
                                       gamepad1.right_stick_y);
    }

    private void arm() {
        // Either controller can operate the arm.
        if      (gamepad1.dpad_up)   { isaac5.armUp(); }
        else if (gamepad1.dpad_down) { isaac5.armDown(); }
        else                         { isaac5.armStop(); }
    }

    @Override
    public void init() {
        isaac5 = new Isaac5(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drive();
        arm();
    }
}
