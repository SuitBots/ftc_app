package com.suitbots.kittenaround;

/**
 * A very simple, single-joystick tank drive for Isaac 5.
 */
public class IsaacTheTankTeleop extends Isaac5i {

    @Override
    public void loop() {
        telemetry.addData("Text", "*** Robot Data***");
        sensorTelemetry();

        if (gamepad1.dpad_up) { driveForward(); }
        else if (gamepad1.dpad_down) { driveBackward(); }
        else if (gamepad1.dpad_left) { turnLeft(); }
        else if (gamepad1.dpad_right) { turnRight(); }
        else {
            float drive_x = gamepad1.left_stick_x;
            float drive_y = gamepad1.left_stick_y;

            float left_speed = drive_y + drive_x;
            float right_speed = drive_y - drive_x;

            telemetry.addData("Drive", String.format("Left: %.2f, Right: %.2f", left_speed, right_speed));

            setDriveMotorPowers(left_speed, right_speed);
        }
    }

}
