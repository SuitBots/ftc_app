package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.DriverHelper;
import suitbots.Robot;

/**
 * Created by Samantha on 9/30/2017.
 */

@TeleOp(name = "TELEOP-2.5", group = "Tournament")
public class MecanumTeleop extends OpMode {
    private Robot robot = null;
    private Controller g1, g2;
    private boolean debug_mode = false;
    private boolean near;


    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry, false);
        robot.disableDriveEncoders();
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

    private void runCollector(Controller g) {
        if (g.A()) {
            robot.collect();
        } else if (g.B()) {
            robot.release();
        } else {
            robot.stoparms();
        }
    }

    private void g1Loop(Controller g) {

        if (g.A()) {
            robot.release();
            robot.drive(Math.PI, .2, 0.0);
        } else {
            DriverHelper.drive(g1, robot);
            if (g.leftBumper()) {
                robot.collect();
            } else if (g.rightBumper()) {
                robot.release();
            } else {
                robot.stoparms();
            }
        }
    }

    private void g2Loop(Controller g) {
        if (g.YOnce()) {
            robot.putUpSoas();
        }
        if (g.XOnce()) {
            robot.resetLiftEncoder();
        }


        /*
        dpad up = extender out
        dpad down = extender in
        a = clamp close
        b = clamp open
        dpad left = pivot up
        dpad right = pivot down
         */

        if(g.dpadUp()) {
            robot.moveExtender(-1);
        }
        else if (g.dpadDown()){
            robot.moveExtender(1);
        } else {
            robot.moveExtender(0.0);
        }

        if(g.AOnce()){
            robot.clampClosed();
        } else if(g.BOnce()){
            robot.clampOpen();
        }

        if(g.dpadLeft()){
            robot.movePivot(.5);
        }
        else if(g.dpadRight()){
            robot.movePivot(-.5);
        } else{
            robot.movePivot(0);
        }

        // runCollector(g);

        if (g.rightBumperOnce()) {
            robot.indexLiftUp();
        } else if (g.leftBumperOnce()) {
            robot.indexLiftDown();
        } else {
            robot.moveLift(g.right_trigger - g.left_trigger);
        }
    }

    @Override
    public void loop() {
        telemetry.addData("Left glyph:", robot.glyphLeftVolt());
        telemetry.addData("Right glyph:", robot.glyphRightVolt());
        telemetry.addData("Has Glyph?", robot.hasGlyph());

        g1.update();
        g2.update();
        g2Loop(g2);
        g1Loop(g1);
        telemetry.update();

    }
}
