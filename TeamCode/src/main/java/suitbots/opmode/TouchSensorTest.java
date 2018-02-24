package suitbots.opmode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.suitbots.util.Controller;

import suitbots.Robot;

@Disabled
@TeleOp(name = "TOUCH TEST")
public class TouchSensorTest extends OpMode  {
    private TouchSensor touch;

    @Override
    public void init() {
        touch = hardwareMap.touchSensor.get("glyph");
    }
    @Override
    public void loop() {
        telemetry.addData("Touched", touch.getValue());
        telemetry.update();
    }
}
