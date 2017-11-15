package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.AutoBase;

@TeleOp(name = "Glyph Placing Test")
public class GlyphPlacingTest extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap, telemetry);
        Controller c = new Controller(gamepad1);


        double rs = -.5;
        double ls = 0.5;
        long t = 1000;

        while (opModeIsActive()) {
            c.update();

            telemetry.addData("Right (dpad u/d)", rs);
            telemetry.addData("Left (dpad r/l)", ls);
            telemetry.addData("Time (bumpers)", t);
            telemetry.update();

            if (c.dpadUpOnce()) { rs += .1; }
            else if (c.dpadDownOnce()) { rs -= .1; }
            else if (c.dpadLeftOnce()) { ls -= .1; }
            else if (c.dpadRightOnce()) { ls += .1; }
            else if (c.leftBumperOnce()) { t -= 100; }
            else if (c.rightBumper()) { t += 100; }
            else if (c.AOnce()) { throwGlyph(t, ls, rs); }
        }
    }

    private void throwGlyph(final long time, final double leftPower, final double rightPower) {
        robot.setArmMotors(leftPower, rightPower);
        sleep(time);
        robot.stoparms();
    }

    @Override
    protected double forwardDir() {
        return 0;
    }
}
