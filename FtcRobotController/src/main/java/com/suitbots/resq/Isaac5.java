package com.suitbots.resq;

import android.bluetooth.BluetoothClass;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.robocol.Telemetry;

public  class Isaac5  {
    private DcMotor armmotor, leftmotor, rightmotor;
    private ColorSensor color_fore, color_bottom;
    private GyroSensor gyro;
    private OpticalDistanceSensor distance;
    private Telemetry telemetry;

    Isaac5(HardwareMap hardwareMap, Telemetry _telemetry) {
        telemetry = _telemetry;
        armmotor = hardwareMap.dcMotor.get("armmotor");
        rightmotor = hardwareMap.dcMotor.get("rightmotor");
        leftmotor = hardwareMap.dcMotor.get("leftmotor");
        leftmotor.setDirection(DcMotor.Direction.REVERSE);

        DeviceInterfaceModule cdim = hardwareMap.deviceInterfaceModule.get("dim");
        color_fore =  hardwareMap.colorSensor.get("colorfore");
        color_fore.setI2cAddress(0x42);
        color_bottom = hardwareMap.colorSensor.get("colorbottom");
        distance = hardwareMap.opticalDistanceSensor.get("distance");
        gyro = hardwareMap.gyroSensor.get("gyro");
        gyro.calibrate();

        color_fore.enableLed(false);
        color_bottom.enableLed(true);
    }

    void activateSensors() {
        color_bottom.enableLed(true);
        color_fore.enableLed(true);
    }

    void deactivateSensors() {
        color_bottom.enableLed(false);
        color_fore.enableLed(true);
    }

    void calibrateGyro() {
        while(gyro.isCalibrating()) {
            try {
                Thread.sleep(50);
            } catch(java.lang.InterruptedException ie) {
                // pass
            }
        }
    }

    void sendSensorTelemetry() {
        telemetry.addData("Color Fore", String.format("%x %d %d %d %d",
                color_fore.getI2cAddress(),
                color_fore.red(), color_fore.blue(),
                color_fore.green(), color_fore.alpha()));
        telemetry.addData("Color Down", String.format("%x %d %d %d %d",
                color_bottom.getI2cAddress(),
                color_bottom.red(), color_bottom.blue(),
                color_bottom.green(), color_bottom.alpha()));
        telemetry.addData("Distance", distance.getLightDetected());
        telemetry.addData("Dist Raw", distance.getLightDetectedRaw());
        telemetry.addData("Encoders", String.format("L: %d, R: %d, A: %d",
                leftmotor.getCurrentPosition(),
                rightmotor.getCurrentPosition(),
                armmotor.getCurrentPosition()));

        telemetry.addData("Heading", String.format("%d", gyro.getHeading()));
        telemetry.addData("Calibrating", gyro.isCalibrating());
    }

    protected void setDriveMotorSpeeds(double left, double right) {
        leftmotor.setPower(left);
        rightmotor.setPower(right);
    }

    int getHeading() { return gyro.getHeading(); }

    void turnLeft() { setDriveMotorSpeeds(-1, 1); }
    void turnRight() { setDriveMotorSpeeds(1, -1); }
    void goForward() { setDriveMotorSpeeds(1, 1); }
    void goBackward() { setDriveMotorSpeeds(-1, -1); }
    void stop() { setDriveMotorSpeeds(0, 0); }


    protected void setArmMotorSpeed(double power) {
        armmotor.setPower(power);
    }
    protected void armUp() {
        armmotor.setPower(1);
    }
    protected void armDown() {
        armmotor.setPower(-1);
    }
    protected void armStop(){
        armmotor.setPower(0);
    }
}