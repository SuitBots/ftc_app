package com.suitbots.resq;

public class Isaac5Teleop extends Isaac5 {

    private void drive() {
            setDriveMotorSpeeds(gamepad1.left_stick_y,
                                gamepad1.right_stick_y);
    }

    private void arm() {
        // Either controller can operate the arm.
        if (gamepad1.dpad_up) { armUp(); }
        else if (gamepad1.dpad_down) { armDown(); }
        else  {armStop();}
    }

    @Override
    public void loop() {
        drive();
        arm();
    }
}
