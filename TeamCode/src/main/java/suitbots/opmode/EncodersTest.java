package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.Robot;

@TeleOp(name = "Encoder Test", group = "Diagnostic")
public class EncodersTest extends LinearOpMode {
    private Robot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap, telemetry);
        Controller c = new Controller(gamepad1);

        waitForStart();
        robot.resetEncoders();

        while(opModeIsActive()) {
            c.update();

            robot.announceEncoders();
            telemetry.update();

            if (c.AOnce()) {
                robot.setMotorSpeeds(1.0, 1.0, 1.0, 1.0);
                sleep(5000);
                robot.stopDriveMotors();
            }
            if (c.BOnce()) {
                robot.setMotorSpeeds(-1.0, -1.0, -1.0, -1.0);
                sleep(5000);
                robot.stopDriveMotors();
            }
            if (c.XOnce()) {
                robot.resetEncoders();
            }
        }
    }
}
