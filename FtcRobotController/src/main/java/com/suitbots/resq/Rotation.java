package com.suitbots.resq;

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;


public class Rotation extends LinearOpMode {
    DcMotor l1, l2, l3, r1, r2, r3;
    ModernRoboticsI2cGyro gyro;

    private void setSpeeds(double l, double r) {
        l1.setPower(l);
        l2.setPower(l);
        l3.setPower(l);
        r1.setPower(r);
        r2.setPower(r);
        r3.setPower(r);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        l3 = hardwareMap.dcMotor.get("l3");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");
        r3 = hardwareMap.dcMotor.get("r3");

        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
        r3.setDirection(DcMotor.Direction.REVERSE);

        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");

        waitForStart();
        gyro.calibrate();
        while(gyro.isCalibrating()) {
            telemetry.addData("Gyro", "Calibrating...");
            sleep(100);
        }
        telemetry.addData("Gyro", "Calibrated!");

        while(opModeIsActive()) {
            int degrees = 15;
            if (gamepad1.x) {
                degrees *= 3;
            }
            if (gamepad1.y) {
                degrees *= 2;
            }

            if (gamepad1.dpad_right) {
                rotate(degrees);
            } else if (gamepad1.dpad_left) {
                rotate(-degrees);
            }

        }
    }

    static final int MAX_HARDWARE_WAIT = 50;
    static final int CLOSE_ENOUGH_TO_ZERO = 2;

    private void rotate(int degrees) throws InterruptedException {
        if (CLOSE_ENOUGH_TO_ZERO >= Math.abs(degrees)) {
            return;
        }

        gyro.resetZAxisIntegrator();
        int count = 0;
        while (count++ < MAX_HARDWARE_WAIT &&
                CLOSE_ENOUGH_TO_ZERO < Math.abs(gyro.getIntegratedZValue())) {
            waitOneFullHardwareCycle();
        }

        telemetry.addData("Hardware Wait", count);

        boolean quit = false;
        while(opModeIsActive() && !quit) {
            waitOneFullHardwareCycle();
            int heading = gyro.getIntegratedZValue();

            int diff = degrees + heading;
            double speed = 0.4;

            telemetry.addData("Heading", heading);
            telemetry.addData("Degrees", degrees);

            if (0 < degrees) { // turning right, so heading should get smaller
                quit = diff <= 0;
                setSpeeds(speed, -speed);
            } else { // turning left, so heading gets bigger.
                quit = diff >= 0;
                setSpeeds(-speed, speed);
            }
        }

        setSpeeds(0.0, 0.0);


        telemetry.addData("Final",String.format("%d %d", degrees, -gyro.getIntegratedZValue()));

    }
}
