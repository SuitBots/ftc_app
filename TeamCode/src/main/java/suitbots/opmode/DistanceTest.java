package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;

@TeleOp(name = "Distance Test")
public class DistanceTest extends OpMode {
    private AnalogInput a1;

    @Override
    public void init() {
        a1 = hardwareMap.analogInput.get("a1");
    }

    @Override
    public void loop() {
        telemetry.addData("A1", a1.getVoltage());
        telemetry.update();
    }
}
