package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.suitbots.util.Controller;

import suitbots.DriverHelper;
import suitbots.Robot;

@Disabled
public class HasGlyphTest extends OpMode {
    private Robot robot;
    private Controller c;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
        c = new Controller(gamepad1);
    }

    @Override
    public void loop() {
        telemetry.update();
        c.update();
        DriverHelper.drive(c, robot);
    }
}
