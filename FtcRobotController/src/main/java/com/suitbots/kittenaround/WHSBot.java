package com.suitbots.kittenaround;

/**
 * Created by cp on 9/12/15.
 */
public class WHSBot extends Isaac5i {
    public void loop() {
        double left = gamepad1.left_stick_y;
        double right = gamepad1.right_stick_y;

        setDriveMotorPowers(left, right);
    }
}
