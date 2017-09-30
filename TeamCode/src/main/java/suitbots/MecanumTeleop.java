package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;


/**
 * Created by Samantha on 9/17/2017.
 */

@TeleOp(name = "Issac 7", group = "Tournament")
public class MecanumTeleop extends OpMode {
    private Robot robot = null;
    private Controller g1, g2;
    private boolean debug_mode = false;


    @Override
    public void init() {
        robot = new Robot(hardwareMap);

        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
    }

    @Override
    public void init_loop() {
        g1.update();
        if (g1.AOnce()) {
            debug_mode = ! debug_mode;
        }
        telemetry.addData("Debug? (a)", debug_mode ? "on" : "off");
        telemetry.addData("Ready?", "YES.");
        telemetry.update();
    }

    @Override
    public void start() {
        robot.onStart();
    }

    @Override
    public void stop() {
        robot.onStop();
    }

    @Override
    public void loop() {
        g1.update();
        g2.update();
        DriverHelper.drive(g1,robot);

        if (debug_mode) {
            robot.updateSensorTelemetry();
            telemetry.update();
        }
    }
}
