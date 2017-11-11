package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.suitbots.util.Controller;

import suitbots.AutoBase;
import suitbots.Robot;

/**
 * Created by Suit Bots on 11/11/2017.
 */

@Autonomous(name = "Jewel Auto")
public class JewelAutonomous extends AutoBase {
    boolean redAlliance = true;

    @Override
    public void runOpMode() throws InterruptedException {
        Controller c = new Controller(gamepad1);
        initialize(hardwareMap, telemetry);

        while (! isStarted()) {
            c.update();
            if (c.AOnce()) redAlliance = ! redAlliance;
            telemetry.addData("Alliance (a)", redAlliance ? "RED" : "BLUE");
            telemetry.addData("Time", getRuntime());
            if(robot.isGyroCalibrated()){
                telemetry.addData("Ready?", "YES.");
            }else {
                telemetry.addData("Ready?","no");
            }
            telemetry.update();
        }

        robot.putDownSoas();
        robot.grabBlock();
        sleep(1000);

        int identifier = robot.detectJewelColour();
        final boolean jewelIsRed = 1 == identifier;

        telemetry.addData("Color (x to continue)", jewelIsRed ? "RED" : "BLUE");
        telemetry.addData("Alliance", redAlliance ? "RED" : "BLUE");
        telemetry.update();
        while (true) {
            if(gamepad1.x) break;
            idle();
        }

        if (jewelIsRed == redAlliance) {
            knockForward();
        }else{
            knockBackward();
        }

        robot.putUpSoas();
        sleep(2000);
        driveDirectionTiles(forwardDir(), 2, 0.5);
        driveDirectionTiles(forwardDir() + (redAlliance ? 3 : 1) * Math.PI / 2.0, .5, .5);
    }

    @Override
    protected double forwardDir() {
        return redAlliance ? 0 : Math.PI;
    }
}
