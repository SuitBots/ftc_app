package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.AutoBase;
import suitbots.Robot;

/**
 * Created by Jonah on 2/19/18.
 */

@Disabled
@TeleOp(name = "Strafe Test", group = "Tournament")
public class StrafeTest extends AutoBase{
    @Override
    public void runOpMode() throws InterruptedException {

        robot = new Robot(hardwareMap, telemetry);
        Controller c = new Controller(gamepad1);

        while (!isStarted()) {
            telemetry.addData("Runtime", getRuntime());
            telemetry.update();
        }

        waitForStart();
        double tiles = 0.1;

        while (opModeIsActive()) {
            c.update();
            if(c.dpadUpOnce()){
                tiles += 0.05;
            }
            if(c.dpadDownOnce()){
                tiles -= 0.05;
            }
            telemetry.addData("tiles", tiles);
            telemetry.update();

            if(c.dpadLeftOnce()){
                driveDirectionTiles(3*Math.PI/2,tiles,0.8);
            }
            if(c.dpadRightOnce()){
                driveDirectionTiles((Math.PI/2),tiles,0.8);
            }
        }
    }


    @Override
    protected double forwardDir() {
        return 0;
    }
    }