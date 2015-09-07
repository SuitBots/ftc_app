package com.suitbots.kittenaround;

/**
 * Created by cp on 9/7/15.
 */
public class Isaac5Diagnostic extends Isaac5i {
        @Override
        public void loop() {
            double lf = 0.0, lr = 0.0, rf = 0.0, rr = 0.0;
            double direction = gamepad1.left_bumper ? -1.0 : 1.0;

            if(gamepad1.x) {
                lf = direction;
            }
            if (gamepad1.y) {
                rf = direction;
            }
            if (gamepad1.a) {
                lr = direction;
            }
            if (gamepad1.b) {
                rr = direction;
            }

            setDriveMotorPowers(lf, lr, rf, rr);
        }
}
