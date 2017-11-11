package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

/**
 * Created by Suit Bots on 10/10/2017.
 */

@Autonomous(name = "AutonomousRR")
public abstract class AutonomousRR extends AutoBase  {
    private Robot robot = null;
    private Controller g1, g2;
    private boolean near;

    @Override
    public void runOpMode() throws InterruptedException {
        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);

        boolean redAlliance = true;

        while (! isStarted()) {
            g1.update();
            telemetry.addData("Alliance (x)", redAlliance ? "RED" : "BLUE");

            if (g1.XOnce()) redAlliance = ! redAlliance;

            telemetry.addData("Location", near ? "Near":"Far");

            if(g1.BOnce()){
                near = true;
            } else if(g1.BOnce()) {
                near = false;
            }
            telemetry.addData("Location (b)", near ? "Near":"Far");
            telemetry.update();
        }


        robot = new Robot(hardwareMap, telemetry);

        telemetry.addData("State", "Waiting for start");
        telemetry.update();
        waitForStart();
        telemetry.addData("State", "NOT Waiting for Start");
        telemetry.update();

        robot.putDownSoas();
        sleep(1000);

        int identifier = robot.getRGBA();
        final boolean jewelIsRed = 1 == identifier;

        if (jewelIsRed == redAlliance) {
            telemetry.addData("State", "MINE");
            knockForward();
        }else{
            knockBackward();
        }

        //robot.drive(0, 0, robot.jewelIsRed() == redAlliance ? .8 : -.8);
        sleep(750);
        robot.stopDriveMotors();

        robot.putUpSoas();
        sleep(1000);

        //robot.encoderDriveTiles(0.0,1.5); //should go straight

        //turnToAngleDeg(90.0);


   }
}