package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Samantha on 9/2/2017.
 */
//
//@TeleOp (name = "Gyro Test")
//
//public class GyroTest extends OpMode {
//    private Robot robot;
//    @Override
//    public void init() {
//        robot = new Robot(hardwareMap, telemetry);
//        if (!robot.isGyroCalibrated()) {telemetry.addLine("Gyro is not calibrated");}
//        else {telemetry.addLine("GYRO IS CALIBRATED!!");}
//        telemetry.update();
//    }
//
//    @Override
//    public void loop() {
//        telemetry.addData("Heading", robot.getGyro());
//        telemetry.addData("Light", robot.getLight());
//        telemetry.addData("Is on line", robot.isAboveWhiteLine());
//        telemetry.update();
//    }
//}
