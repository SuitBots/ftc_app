package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Samantha on 9/2/2017.
 */

@TeleOp (name = "Gyro Test")

public abstract class GyroTest extends OpMode {
    private Robot robot;
    @Override
    public void init_loop() {
        robot = new Robot(hardwareMap);
        while (!robot.isGyroCalibrated()) {
            telemetry.addLine("Gyro is not calibrated");
        }
        telemetry.addLine("GYRO IS CALIBRATED!!");
    }

    @Override
    public void loop() {
        telemetry.addData("Heading", robot.getGyro());
        telemetry.addData("Light", robot.getLight());
        telemetry.addData("Is on line", robot.isAboveWhiteLine());
        telemetry.update();
    }
}
