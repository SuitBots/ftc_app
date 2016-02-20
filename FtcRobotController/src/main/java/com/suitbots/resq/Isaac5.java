package com.suitbots.resq;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

public  class Isaac5  {
    private DcMotor l1, l2, r1, r2;
    private DcMotor tape, winch;
    private Servo dumper_flipper, flap;
    private DeviceInterfaceModule dim;

    private ColorSensor color_fore, color_under;
    private ModernRoboticsI2cGyro gyro;
    private OpticalDistanceSensor distance;
    private Telemetry telemetry;

    LinearOpMode opmode;

    Isaac5(HardwareMap hardwareMap, Telemetry _telemetry, LinearOpMode op) throws InterruptedException {
        opmode = op;
        dim = hardwareMap.deviceInterfaceModule.get("dim");
        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");
        tape = hardwareMap.dcMotor.get("tape");
        winch = hardwareMap.dcMotor.get("winch");

        encoder_values = new int[4];

        // Reverse the right motors
        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
        // Also the tape motor
        tape.setDirection(DcMotor.Direction.REVERSE);

        flap = hardwareMap.servo.get("scoop");
        dumper_flipper = hardwareMap.servo.get("flipper"); // y

        telemetry = _telemetry;
        distance = hardwareMap.opticalDistanceSensor.get("distance"); // y
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro"); // y

        color_fore = hardwareMap.colorSensor.get("color"); // y
        color_under = hardwareMap.colorSensor.get("linefollow");  // y
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
        color_under.setI2cAddress(0x70);

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
        color_fore.enableLed(true);
        color_under.enableLed(true);
    }

    /// Turn sensor lights off
    public void deactivateSensors() {
        color_fore.enableLed(false);
        color_under.enableLed(false);
    }

    /// The threshold that we're willing to consider a color "detected."
    /// Note that this applies for active-mode only.
    public static final int COLOR_THRESHOLD = 3;
    /// Is Isaac5's color sensor over something white?
    public boolean isOnWhiteLine() {
        return COLOR_THRESHOLD < color_under.red() &&
                COLOR_THRESHOLD < color_under.blue() &&
                COLOR_THRESHOLD < color_under.green() &&
                COLOR_THRESHOLD < color_under.alpha();

    }
    /// Calibrate the gyro sensor
    public void calibrateGyro() throws InterruptedException {
        enableRedLED(true);
        gyro.calibrate();
        while(gyro.isCalibrating()) {
            telemetry.addData("Gyro", "Calibrating");
            opmode.waitOneFullHardwareCycle();
        }
        telemetry.addData("Gyro", "CalibratED");
        enableRedLED(false);
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

        telemetry.addData("Heading", String.format("%d (%d)",
                gyro.getHeading(), gyro.rawZ()));

        telemetry.
                addData("Encoders", String.format("%d/%d %d/%d %d/%d %d/%d",
                        l1.getCurrentPosition(), l1.getTargetPosition(),
                        l2.getCurrentPosition(), l2.getTargetPosition(),
                        r1.getCurrentPosition(), r1.getTargetPosition(),
                        r2.getCurrentPosition(), r2.getTargetPosition()));

        telemetry.addData("Servo Pos", dumper_flipper.getPosition());
    }

    private double clamp(double x) {
        return Math.min(Math.max(-1.0, x), 1.0);
    }

    /// Set drivetrain speeds en masse.
    protected void setDriveMotorSpeeds(double left, double right) {
        l1.setPower(clamp(left));
        l2.setPower(clamp(left));
        r1.setPower(clamp(right));
        r2.setPower(clamp(right));
    }

    /// Get the heading from the gyro sensor
    int getHeading() { return gyro.getHeading(); }
    int getHeadingRaw() { return gyro.getIntegratedZValue(); }

    String getGyroStatus() { return gyro.status(); }

    /// Get the "distance" (really light returned) from the optical distance sensor.
    int getDistance() { return distance.getLightDetectedRaw(); }

    int[] encoder_values;

    public int getEncoderAverage() {
        int sum = l1.getCurrentPosition() +
                l2.getCurrentPosition() +
                r1.getCurrentPosition() +
                r2.getCurrentPosition();
        for (int i = 0; i < encoder_values.length; ++i) {
            sum -= encoder_values[i];
        }
        return sum / encoder_values.length;
    }

    /// Reset both left and right encoders
    public void zeroMotorEncoders() throws InterruptedException {
        encoder_values[0] = l1.getCurrentPosition();
        encoder_values[1] = l2.getCurrentPosition();
        encoder_values[2] = r1.getCurrentPosition();
        encoder_values[3] = r2.getCurrentPosition();
    }

    public void setHeadingToZero() {
        gyro.resetZAxisIntegrator();
    }

    public static final double FULL_SPEED = 1.0;
    public static final double SLOW_SPEED = 0.5;

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

    /// Creep ahead
    public void goForwardSlowly() {
        setDriveMotorSpeeds(SLOW_SPEED, SLOW_SPEED);
    }
    
    /// Full speed backwards
    public void goBackward() {
        setDriveMotorSpeeds(-FULL_SPEED, -FULL_SPEED);
    }
    /// Stop all drive motors
    public void stop() {
        setDriveMotorSpeeds(0.0, 0.0);
        setTapeMotor(0.0);
        setWinchMotor(0.0);
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
        dumper_flipper.setPosition(Servo.MAX_POSITION);
    }
    public void resetDumperArm() { dumper_flipper.setPosition(Servo.MIN_POSITION); }

    public void setTapeMotor (double x) {
        tape.setPower(clamp(x));
    }
    public void setWinchMotor (double x) {
        winch.setPower(clamp(x));
    }

    public void moveFlapUp() {flap.setPosition(1.0);}
    public void moveFlapDown() {flap.setPosition(0.0);}
    public void stopFlap() {flap.setPosition(0.5);}

}
