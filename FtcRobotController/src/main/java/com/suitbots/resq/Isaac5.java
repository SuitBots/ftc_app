package com.suitbots.resq;

import android.bluetooth.BluetoothClass;
import android.graphics.Color;

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
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
    private DeviceInterfaceModule dim;

    private ColorSensor color_fore, color_under;
    private ModernRoboticsI2cGyro gyro;
    private OpticalDistanceSensor distance;
    private Telemetry telemetry;

    // Encoders for the center motors, which should always be on the ground
    private int left_motor_encoder;
    private int right_motor_encoder;

    Isaac5(HardwareMap hardwareMap, Telemetry _telemetry) throws InterruptedException {
        dim = hardwareMap.deviceInterfaceModule.get("dim");
        l1 = hardwareMap.dcMotor.get("l1"); // y
        l2 = hardwareMap.dcMotor.get("l2"); // y
        l3 = hardwareMap.dcMotor.get("l3"); // y
        r1 = hardwareMap.dcMotor.get("r1"); // y
        r2 = hardwareMap.dcMotor.get("r2"); // y
        r3 = hardwareMap.dcMotor.get("r3"); // y
        tape = hardwareMap.dcMotor.get("tape"); // y

        // Reverse the left motors
        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
        r3.setDirection(DcMotor.Direction.REVERSE);

        Dumper_flipper = hardwareMap.servo.get("flipper"); // y

        telemetry = _telemetry;
        distance = hardwareMap.opticalDistanceSensor.get("distance"); // y
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro"); // y

        color_fore = hardwareMap.colorSensor.get("color"); // y
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


        color_under = hardwareMap.colorSensor.get("linefollow");  // y

        zeroMotorEncoders();
    }

    public void enableBlueLED(boolean enabled) {
        dim.setLED(0, enabled);
    }

    public void enableRedLED(boolean enabled) {
        dim.setLED(1, enabled);
    }


    public void resetHeading() {
        gyro.resetZAxisIntegrator();
    }

    /// Turn sensor lights on
    public void activateSensors() {
        color_under.enableLed(true);
    }

    /// Turn sensor lights off
    public void deactivateSensors() {
        color_under.enableLed(false);
    }

    /// The threshold that we're willing to consider a color "detected."
    /// Note that this applies for active-mode only.
    public static final int COLOR_THRESHOLD = 1;
    /// Is Isaac5's color sensor over something white?
    public boolean isOnWhiteLine() {
        return 0 < color_under.red() + color_under.blue() +
                color_under.green() + color_under.alpha();
    }

    /// Calibrate the gyro sensor
    public void calibrateGyro() throws InterruptedException {
        enableRedLED(true);
        gyro.calibrate();
        while(gyro.isCalibrating()) {
            telemetry.addData("Gyro", "Calibrating");
            Thread.sleep(100);
        }
        telemetry.addData("Gyro", "CalibratED");
        enableBlueLED(false);
    }

    /// Sens some sensor-specific telemetry
    public void sendSensorTelemetry() {
        telemetry.addData("Color Fore", String.format("%02x %02x %02x %02x",
                color_fore.red(), color_fore.blue(),
                color_fore.green(), color_fore.alpha()));

        telemetry.addData("Color Bottom", String.format("%02x %02x %02x %02x",
                color_under.red(), color_under.blue(),
                color_under.green(), color_under.alpha()));

        telemetry.addData("Dist Raw", distance.getLightDetectedRaw());
        telemetry.addData("Encoders", String.format("L: %d, R: %d",
                getLeftEncoder(),
                getRightEncoder()));

        telemetry.addData("Heading", String.format("%d (%d)",
                gyro.getHeading(), gyro.getIntegratedZValue()));
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
    int getHeadingRaw() { return gyro.getIntegratedZValue(); }

    String getGyroStatus() { return gyro.status(); }

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

    public void setHeadingToZero() {
        gyro.resetZAxisIntegrator();
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
        setDriveMotorSpeeds(0.0, 0.0);
        setTapeMotor(0.0);
    }

    /// The red value from the front color sensor
    public int getRedFore() { return color_fore.red(); }
    /// The blue value from the front color sensor
    public int getBlueFore() { return color_fore.blue(); }
    /// The green value from the front color sensor
    public int getGreenFore() { return color_fore.green(); }

    public int getRedUnder() { return color_under.red(); }
    public int getGreenUnder() { return color_under.green(); }
    public int getBlueUnder() { return color_under.blue(); }
    public int getAlphaUnder() { return color_under.alpha(); }

    public void moveDumperArmToThrowPosition() {
        Dumper_flipper.setPosition(1.0);
    }

    public void resetDumperArm() {
        Dumper_flipper.setPosition(0.0);
    }

    public void setTapeMotor (double x) {tape.setPower(clamp(x));}

}