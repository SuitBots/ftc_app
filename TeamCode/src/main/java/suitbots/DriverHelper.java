package suitbots;

import com.suitbots.util.Controller;

/**
 * Created by Samantha on 9/26/2017.
 */

public class DriverHelper {
    // assumes that the controller is updated
    public static void drive(Controller g, Robot robot) {
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

        if (0.05 < g.left_trigger) {
            v_theta /= (1.0 + g.left_trigger);
            v_rotation /= (1.0 + g.left_trigger);
        }

        if (0.05 < g.right_trigger) {
            v_theta /= (1.0 + g.right_trigger);
            v_rotation /= (1.0 + g.right_trigger);
        }

        robot.drive(theta, v_theta, v_rotation);
    }
}
