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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

public  class Isaac5  {
    private DcMotor l1, l2, l3, r1, r2, r3;
    private DcMotor tape;
    private Servo Dumper_flipper;

    private ColorSensor color_fore;
    private GyroSensor gyro;
    private OpticalDistanceSensor distance;
    private Telemetry telemetry;
    private Autonomus autonomus;

    // Encoders for the center motors, which should always be on the ground
    private int left_motor_encoder;
    private int right_motor_encoder;

    Isaac5(HardwareMap hardwareMap, Telemetry _telemetry) throws InterruptedException {
        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        l3 = hardwareMap.dcMotor.get("l3");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");
        r3 = hardwareMap.dcMotor.get("r3");
        tape = hardwareMap.dcMotor.get("tape");

        // Reverse the left motors
        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
        r3.setDirection(DcMotor.Direction.REVERSE);

        Dumper_flipper = hardwareMap.servo.get("flipper");

        telemetry = _telemetry;
        distance = hardwareMap.opticalDistanceSensor.get("distance");
        gyro = hardwareMap.gyroSensor.get("gyro");

        color_fore = hardwareMap.colorSensor.get("color");
        /***************************************
         *  ____    _    _   _  ____ _____ ____
         * |  _ \  / \  | \ | |/ ___| ____|  _ \
         * | | | |/ _ \ |  \| | |  _|  _| | |_) |
         * | |_| / ___ \| |\  | |_| | |___|  _ <
         * |____/_/   \_\_| \_|\____|_____|_| \_\
         *
         * The color sensor is, for whatever reason, on a non-standard
         * I2c address. Magic it up here to make sure it's communicating
         * on the correct address. If we're ever able to revert the
         * sensor to its original state, please get rid of this noise.
         *
         ***************************************/
        color_fore.setI2cAddress(0x70);

        zeroMotorEncoders();
        calibrateGyro();
    }

    /// Turn sensor lights on
    public void activateSensors() {
        color_fore.enableLed(true);
    }

    /// Turn sensor lights off
    public void deactivateSensors() {
        color_fore.enableLed(true);
    }

    /// Calibrate the gyro sensor
    public void calibrateGyro() throws InterruptedException {
        gyro.calibrate();
        while(gyro.isCalibrating()) {
            telemetry.addData("Gyro", "Calibrating");
            Thread.sleep(100);
        }
        telemetry.addData("Gyro", "CalibratED");
    }

    /// Sens some sensor-specific telemetry
    public void sendSensorTelemetry() {
        telemetry.addData("Color Fore", String.format("%x %d %d %d %d",
                color_fore.getI2cAddress(),
                color_fore.red(), color_fore.blue(),
                color_fore.green(), color_fore.alpha()));

        telemetry.addData("Distance", distance.getLightDetected());
        telemetry.addData("Dist Raw", distance.getLightDetectedRaw());
        telemetry.addData("Encoders", String.format("L: %d, R: %d",
                getLeftEncoder(),
                getRightEncoder()));

        telemetry.addData("Heading", gyro.getHeading());

        telemetry.addData("MERaw", String.format("%d %d, %d %d, %d, %d",
                l1.getCurrentPosition(), r1.getCurrentPosition(),
                l2.getCurrentPosition(), r2.getCurrentPosition(),
                l3.getCurrentPosition(), r3.getCurrentPosition()));
    }

    private double clamp(double x) {
        return Math.min(Math.max(-1.0, x), 1.0);
    }

    /// Set drivetrain speeds en masse.
    protected void setDriveMotorSpeeds(double left, double right) {
        l1.setPower(clamp(left));
        l2.setPower(clamp(left));
        l3.setPower(clamp(left));
        r1.setPower(clamp(right));
        r2.setPower(clamp(right));
        r3.setPower(clamp(right));
    }

    protected void diagnosticSetDriveMotorSpeeds(double _l1, double _l2, double _l3,
                                                 double _r1, double _r2, double _r3) {
        l1.setPower(_l1);
        l2.setPower(_l2);
        l3.setPower(_l3);
        r1.setPower(_r1);
        r2.setPower(_r2);
        r3.setPower(_r3);
    }

    /// Get the heading from the gyro sensor
    int getHeading() { return gyro.getHeading(); }

    /// Get the "distance" (really light returned) from the optical distance sensor.
    int getDistance() { return distance.getLightDetectedRaw(); }


    /// Get the change in the left encoder value since last reset
    public int getLeftEncoder() {
        return -(l2.getCurrentPosition() - left_motor_encoder);
    }

    /// Get the change in the right encoder value since last reset
    public int getRightEncoder() {
        return -(r2.getCurrentPosition() - right_motor_encoder);
    }

    /// Reset both left and right encoders
    public void zeroMotorEncoders() {
        left_motor_encoder = l2.getCurrentPosition();
        right_motor_encoder = r2.getCurrentPosition();
    }


    public static final double FULL_SPEED = 1.0;
    public static final double SLOW_SPEED = 0.2;

    /// Full speed left turn
    public void turnLeft() {
        setDriveMotorSpeeds(-FULL_SPEED, FULL_SPEED);
    }
    /// Full speed right turn
    public void turnRight() {
        setDriveMotorSpeeds(FULL_SPEED, -FULL_SPEED);
    }
    /// Slow speed left turn
    public void turnLeftSlowly() {
        setDriveMotorSpeeds(-SLOW_SPEED, SLOW_SPEED);
    }
    /// Slow speed right turn
    public void turnRightSlowly() {
        setDriveMotorSpeeds(SLOW_SPEED, -SLOW_SPEED);
    }
    /// Full speed ahead
    public void goForward() {
        setDriveMotorSpeeds(FULL_SPEED, FULL_SPEED);
    }
    /// Full speed backwards
    public void goBackward() {
        setDriveMotorSpeeds(-FULL_SPEED, -FULL_SPEED);
    }
    /// Stop all drive motors
    public void stop() {
        setDriveMotorSpeeds(0, 0);
    }

    /// The red value from the front color sensor
    public int getRedFore() { return color_fore.red(); }
    /// The blue value from the front color sensor
    public int getBlueFore() { return color_fore.blue(); }
    /// The green value from the front color sensor
    public int getGreenFore() { return color_fore.green(); }

    public void moveDumperArmToThrowPosition() {
        Dumper_flipper.setPosition(1.0);
    }

    public void resetDumperArm() {
        Dumper_flipper.setPosition(0.0);
    }

    public void setTapeMotor (double x) {tape.setPower(clamp(x));}

}