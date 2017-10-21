package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import suitbots.sensor.FlexSensor;

@TeleOp(name = "Flex")
public class FlexSensorTest extends OpMode {
    private FlexSensor flex;
    private int flexCount;

    @Override
    public void init() {
        flex = new FlexSensor(hardwareMap.analogInput.get("flex"), .3);
    }

    @Override
    public void init_loop() {
        super.init_loop();
        flex.init_loop();
        telemetry.addData("Flex sensor ready", flex.hasBaseline() ? "YES" : "no.");
        telemetry.update();
    }

    @Override
    public void loop() {
        flex.loop();
        if (flex.triggeredOnce()) {
            ++flexCount;
        }
        telemetry.addData("Flex Sensor", flex.voltage());
        telemetry.addData("Flex Count", flexCount);
        flex.dump(telemetry);
        telemetry.update();
    }
}
