package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Disabled
@TeleOp(name = "Sensor Test")
public class SensorsTest extends AutonomousBase {
    public void runOpMode() throws InterruptedException {
        initialize();
        Controller g1 = new Controller(gamepad1);

        waitForStart();

        while(opModeIsActive()) {
            g1.update();
            robot.setDriveMotors(- g1.left_stick_y, -g1.right_stick_y);

            telemetry.addData("Heading", robot.getHeading());
            telemetry.addData("Range/A", robot.getAcousticRangeCM());
            telemetry.addData("Range/O", robot.getRangeLightDetected());
            telemetry.addData("Line", robot.getLineReading());
            telemetry.update();
            if (g1.AOnce()) {
                robot.resetGyro();
            } else if (g1.dpadUpOnce()) {
                fwd(12);
            } else if (g1.dpadDownOnce()) {
                fwd(-12);
            } else if (g1.dpadLeftOnce()) {
                turn(-45);
            } else if (g1.dpadRightOnce()) {
                turn(45);
            }
        }
    }
}
