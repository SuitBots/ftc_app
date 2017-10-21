package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.suitbots.util.Controller;

/**
 * Created by Samantha on 10/21/2017.
 */

@TeleOp(name = "Data Gather-er", group = "Tournament")
public class DataCollectionTeleOp extends OpMode {
    private ColorSensor jewelColorDetector;
    private Controller g1, g2;
    private boolean debug_mode = false;
    private boolean near;


    public void getColor() {
        jewelColorDetector.alpha();
    }

    @Override
    public void init() {
        jewelColorDetector = hardwareMap.colorSensor.get("jewelColorDetector");
        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
    }




    @Override
    public void loop() {
//        if(g1.XOnce()){
//
//        }else if(g1.BOnce()){
//
//        }
        telemetry.addData("Color Alpha - ",jewelColorDetector.alpha());
        telemetry.addData("Color Blue - ",jewelColorDetector.blue());
        telemetry.addData("Color Red - ",jewelColorDetector.red());
        telemetry.addData("Color Green - ",jewelColorDetector.green());

        g1.update();
        g2.update();
        telemetry.update();
    }
}
