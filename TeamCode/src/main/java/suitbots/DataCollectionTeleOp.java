package suitbots;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.suitbots.util.Controller;

import java.io.FileWriter;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Samantha on 10/21/2017.
 */
@Disabled
@TeleOp(name = "Data Gather-er", group = "Tournament")
public class DataCollectionTeleOp extends OpMode {
    private ColorSensor jewelColorDetector;
    private Controller g1, g2;
    private boolean debug_mode = false;
    private boolean near;
    private PrintWriter printf;
    public static final File ROOT_FOLDER = Environment.getExternalStorageDirectory();
    public static final File FIRST_FOLDER = new File(ROOT_FOLDER + "/FIRST/");
    private int countB = 1;
    private int countR = 1;


    public void getColor() {
        jewelColorDetector.alpha();
    }

    @Override
    public void init() {
        try {
            final File fp = new File(FIRST_FOLDER, "observations.txt");
            printf = new PrintWriter(new FileWriter(fp));
        } catch(IOException ioe){ }

        jewelColorDetector = hardwareMap.colorSensor.get("jewelColorDetector");
        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
    }

    @Override
    public void stop(){
        super.stop();
        printf.close();
    }

    @Override
    public void loop() {
        if(g1.X()){
            printf.printf("%d\t%d\t%d\t%d\t%s\n",
                    jewelColorDetector.red(),
                    jewelColorDetector.green(),
                    jewelColorDetector.blue(),
                    jewelColorDetector.alpha(),
                    "BLUE"
                    );
            telemetry.addData("Writing", "BLUE");
            telemetry.addData("Counter BLUE", countB++);
        } else if(g1.B()){
            printf.printf("%d\t%d\t%d\t%d\t%s\n",
                    jewelColorDetector.red(),
                    jewelColorDetector.green(),
                    jewelColorDetector.blue(),
                    jewelColorDetector.alpha(),
                    "RED");
            telemetry.addData("Writing", "RED");
            telemetry.addData("Counter RED", countR++);
        }
        telemetry.addData("Color Red - ",jewelColorDetector.red());
        telemetry.addData("Color Green - ",jewelColorDetector.green());
        telemetry.addData("Color Blue - ",jewelColorDetector.blue());
        telemetry.addData("Color Alpha - ",jewelColorDetector.alpha());

        g1.update();
        g2.update();
        telemetry.update();
    }
}
