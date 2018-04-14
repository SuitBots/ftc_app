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
        if (g.YOnce()) {
            robot.putUpSoas();
        }
    }

    private void g2Loop(Controller g) {
        if (g.AOnce()) {
            robot.resetLiftEncoder();
        }

        robot.moveExtender(g.left_trigger / 2.0 - g.right_trigger);

        if(g.XOnce()){
            robot.clampClosed();
        } else if(g.YOnce()){
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
            if (g.dpadUp()) {
                robot.moveLift(1.0);
            } else if (g.dpadDown()) {
                robot.moveLift(-1.0);
            } else {
                robot.moveLift(0.0);
            }
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
