package suitbots;

import com.suitbots.util.Controller;

/**
 * Created by Samantha on 9/26/2017.
 */

public class DriverHelper {
    // assumes that the controller is updated
    static void drive(Controller g, Robot robot) {
        double theta = 0.0, v_theta = 0.0, v_rotation = 0.0;
        final double dpad_speed = 0.3;

        if (g.dpadUp()) {
            theta = 0.0;
            v_theta = dpad_speed;
        } else if (g.dpadDown()) {
            theta = Math.PI;
            v_theta = dpad_speed;
        } else if (g.dpadLeft()) {
            theta = 3.0 * Math.PI / 2.0;
            v_theta = dpad_speed;
        } else if (g.dpadRight()) {
            theta = Math.PI / 2.0;
            v_theta = dpad_speed;
        } else {
            final double lx = g.left_stick_x;
            final double ly = - g.left_stick_y;

            theta = Math.atan2(lx, ly);
            v_theta = Math.sqrt(lx * lx + ly * ly);
            v_rotation = g.right_stick_x;
        }

        // If A or B are pressed, rotate drive motion by 90 degrees for more effective
        // teleop button pushing.
        if (g.leftBumper()) {
            v_theta /= 2.0;
        }

        if (g.rightBumper()) {
            v_theta /= 2.0;
        }

        robot.drive(theta, v_theta, v_rotation);
    }
}
