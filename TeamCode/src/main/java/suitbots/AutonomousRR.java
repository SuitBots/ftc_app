package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

/**
 * Created by Suit Bots on 10/10/2017.
 */

@Autonomous(name = "AutonomousRR")
public class AutonomousRR extends LinearOpMode {
    private Robot robot = null;
    private Controller g1, g2;

    @Override
    public void runOpMode() throws InterruptedException {
        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);

        boolean redAlliance = true;

        while (! isStarted()) {
            g1.update();
            telemetry.addData("Alliance (x)", redAlliance ? "RED" : "BLUE");
            telemetry.update();

            if (g1.XOnce()) redAlliance = ! redAlliance;
        }

        robot = new Robot(hardwareMap, telemetry);

        telemetry.addData("State", "Waiting for start");
        telemetry.update();
        waitForStart();
        telemetry.addData("State", "NOT Waiting for Start");
        telemetry.update();

        robot.putDownSoas();
        sleep(1000);

        robot.drive(0, 0, robot.jewelIsRed() == redAlliance ? .8 : -.8);
        sleep(750);
        robot.stopDriveMotors();

        robot.putUpSoas();
   }
}