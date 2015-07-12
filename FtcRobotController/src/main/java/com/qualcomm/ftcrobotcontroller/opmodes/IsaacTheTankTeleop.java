package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * A very simple, single-joystick tank drive for Isaac 5.
 */
public class IsaacTheTankTeleop extends Isaac5Basic {



    @Override public void start() {
        super.start();
    }

    @Override public void stop() {
        super.stop();
    }

    @Override public void loop() {
        telemetry.addData("Text", "*** Robot Data***");

        float drive_x = gamepad1.left_stick_x;
        float drive_y = gamepad1.left_stick_y;

        float left_speed = drive_y + drive_x;
        float right_speed = drive_y - drive_x;

        telemetry.addData("Drive!", String.format("Left: %.2f, Right: %.2f", left_speed, right_speed));

        setDriveMotorPowers(left_speed, right_speed);
    }
}
