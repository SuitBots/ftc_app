package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

/**
 * Created by Samantha on 9/30/2017.
 */

@TeleOp(name = "Mr. Bulldops", group = "Tournament")
public class MecanumTeleop extends OpMode {
    private Robot robot = null;
    private Controller g1, g2;
    private boolean debug_mode = false;
    private boolean near;


    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
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
        if(robot.isGyroCalibrated()){
            telemetry.addData("Ready?", "YES.");
        }else {
            telemetry.addData("Ready?","no");
        }
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

    private void g1Loop(Controller g) {
        if (g.YOnce()) {
            robot.putUpSoas();
        }
        if (g.XOnce()){
            robot.putDownSoas();
        }
    }

    private void g2Loop(Controller g) {
        if(g.A()) {robot.collect();}
        else if(g.B()) {robot.release();}
        else {robot.stoparms();}
        robot.moveLift(g.right_trigger - g.left_trigger);
    }

    @Override
    public void loop() {
        g1.update();
        g2.update();
        g1Loop(g1);
        g2Loop(g2);
        DriverHelper.drive(g1, robot);
    }
}
