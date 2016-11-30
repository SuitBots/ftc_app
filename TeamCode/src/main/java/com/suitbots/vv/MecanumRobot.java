package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/**
 * Created by Suit Bots on 11/11/2016.
 */

public class MecanumRobot {
    private DcMotor lf, lr, rf, rr, harvester, flipper;
    private ToggleableServo bf, br, dispenser;
    private ModernRoboticsI2cGyro gyro;
    private ModernRoboticsI2cRangeSensor range;
    private OpticalDistanceSensor line;
    private ColorSensor color;
    private Telemetry telemetry;

    public MecanumRobot(HardwareMap hardwareMap, Telemetry _telemetry) {
        telemetry = _telemetry;
        lf = hardwareMap.dcMotor.get("lf");
        lr = hardwareMap.dcMotor.get("lr");
        rf = hardwareMap.dcMotor.get("rf");
        rr = hardwareMap.dcMotor.get("rr");
        flipper = hardwareMap.dcMotor.get("flipper");
        bf = new ToggleableServo(hardwareMap .servo.get("pf"), 0.0, 1.0);
        br = new ToggleableServo(hardwareMap.servo.get("pr"), 1.0, 0.0);
        dispenser = new ToggleableServo(hardwareMap.servo.get("dispenser"), 0.0, 0.5);
        harvester = hardwareMap.dcMotor.get("harvester");
        gyro = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("gyro");
        range = (ModernRoboticsI2cRangeSensor)
                hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "range");
        line = hardwareMap.opticalDistanceSensor.get("line");
        color = hardwareMap.colorSensor.get("color");
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        gyro.calibrate();

        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flipper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void onStart() {
        bf.set(0.0);
        br.set(0.0);
        dispenser.set(0.0);
        bf.onStart();
        br.onStart();
        dispenser.onStart();
    }

    public void onStop() {
        stopDriveMotors();
        flipper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flipper.setPower(0.0);
        harvester.setPower(0.0);
    }


    // Things that need to happen in the teleop loop to accommodate long-running
    // tasks like running the flipper one at a time.
    public void loop() {
        if (isDoneFlipping()) {
            setFlipperPower(0.0);
        }
        // TODO: Use Vuforia to do a target lock system for the shooter

    }

    public void updateSensorTelemetry() {
        telemetry.addData("Flipping", flipping ? "Yes" : "No");
        telemetry.addData("Gyro",  gyro.getIntegratedZValue());
        telemetry.addData("Range", String.format(Locale.US, "%.2f", range.getDistance(DistanceUnit.CM)));
        telemetry.addData("Line", String.format(Locale.US, "%.2f", line.getRawLightDetected()));
        telemetry.addData("Color", String.format(Locale.US, "r: %d\td: %d", color.red(), color.blue()));
        telemetry.addData("Flipper", flipper.getCurrentPosition());
        telemetry.addData("FM", flipper.getMode().toString());
    }

    // The Flipper

    public static final int ONE_FILPPER_REVOLUTION = 1540; // 1120 * 22 / 16
    private boolean flipping = false;
    public void fire() {
        if (flipping) {
            return;
        }
        flipper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flipper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        flipper.setTargetPosition(ONE_FILPPER_REVOLUTION);
        flipper.setPower(1.0);
        flipping = true;
    }

    public static final int FLIPPER_CLOSE_ENOUGH = 10;
    public boolean isDoneFlipping() {
        return flipping
            && ! flipper.isBusy()
            || FLIPPER_CLOSE_ENOUGH >= Math.abs(ONE_FILPPER_REVOLUTION - flipper.getCurrentPosition());
    }

    public void stopFlipperIfItIsNotFlipping() {
        if (! flipping) {
            flipper.setPower(0.0);
        }
    }

    public void setFlipperPower(double p) {
        if (flipping) {
            flipper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flipping = false;
        }
        flipper.setPower(p);
    }

    // Harvesting

    public void setHarvesterPower(double p) {
        harvester.setPower(p);
    }

    // Servo Control

    public void toggleFrontServo() {
        bf.toggle();
    }

    public void toggleBackServo() {
        br.toggle();
    }

    public void toggleDispenser() {
        dispenser.toggle();
    }

    public void setDispenser(boolean x) {
        dispenser.setFirst(x);
    }

    // Sensors

    public double distanceToWallCM() {
        return range.getDistance(DistanceUnit.CM);
    }

    public double getLineLightReading() {
        return line.getRawLightDetected();
    }

    public boolean isCalibrating(){
        return gyro.isCalibrating();
    }

    public void resetGyro() {
        gyro.resetZAxisIntegrator();
    }

    public int getHeading() {
        return gyro.getIntegratedZValue();
    }

    public static final int COLOR_THRESHOLD = 2;
    public boolean colorSensorIsRed() {
        return color.red() > COLOR_THRESHOLD && color.red() > color.blue();
    }

    public boolean colorSensorIsBlue() {
        return color.blue() > COLOR_THRESHOLD && color.blue() > color.red();
    }

    // Driving

    public void drivePreservingDirection(double translationRadians, double velocity) {
        final int angle = gyro.getIntegratedZValue();
        if (0 != angle) {
            final double rotSpeed = Math.log((double) Math.abs(angle)) * (angle < 0 ? -1.0 : 1.0);
            drive(translationRadians, velocity, rotSpeed);
        }
    }

    /// Maximum absolute value of some number of arguments
    private static double ma(double... xs) {
        double ret = 0.0;
        for (double x : xs) {
            ret = Math.max(ret, Math.abs(x));
        }
        return ret;
    }

    public void drive(double direction, double velocity, double rotationVelocity) {
        final double vd = velocity;
        final double td = direction;
        final double vt = rotationVelocity;

        double s =  Math.sin(td + Math.PI / 4.0);
        double c = Math.cos(td + Math.PI / 4.0);
        double m = Math.max(Math.abs(s), Math.abs(c));
        s /= m;
        c /= m;

        final double v1 = vd * s + vt;
        final double v2 = vd * c - vt;
        final double v3 = vd * c + vt;
        final double v4 = vd * s - vt;

        // Ensure that none of the values go over 1.0. If none of the provided values are
        // over 1.0, just scale by 1.0 and keep all values.
        double scale = ma(1.0, v1, v2, v3, v4);

        lf.setPower(v1 / scale);
        rf.setPower(v2 / scale);
        lr.setPower(v3 / scale);
        rr.setPower(v4 / scale);
    }

    /// Shut down all motors
    public void stopDriveMotors() {
        lf.setPower(0.0);
        lr.setPower(0.0);
        rr.setPower(0.0);
        rf.setPower(0.0);
    }

    // Encoder Driving

    // Assuming 4" wheels
    private static final double TICKS_PER_CM = 1140 / (Math.PI * 4.0 * 2.54);
    private static final double ENCODER_DRIVE_POWER = .5;

    private void setMode(DcMotor.RunMode mode, DcMotor... ms) {
        for (DcMotor m : ms) {
            m.setMode(mode);
        }
    }

    private void setPower(double p, DcMotor... ms) {
        for (DcMotor m : ms) {
            m.setPower(p);
        }
    }

    private void setTargetPosition(int pos, DcMotor... ms) {
        for (DcMotor m : ms) {
            m.setTargetPosition(pos);
        }
    }

    public boolean driveMotorsBusy() {
        return lf.isBusy() || lr.isBusy() || rf.isBusy() || rr.isBusy();
    }

    public void encoderDriveForward(int cm) {
        final int ticks = (int)(cm * TICKS_PER_CM);
        setPower(0.0, lf, lr, rf, rr);
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rf, rr);
        setMode(DcMotor.RunMode.RUN_TO_POSITION, lf, lr, rf, rr);
        setTargetPosition(ticks, lf, lr, rf, rr);
        setPower(ENCODER_DRIVE_POWER, lf, lr, rf, rr);
    }

    public void encoderDriveBackward(int cm) {
        encoderDriveForward(- cm);
    }

    private static double STRAFE_TICKS_PER_CM = TICKS_PER_CM;
    public void encoderDriveLeft(int cm) {
        final int ticks = (int)(cm * STRAFE_TICKS_PER_CM);
        setPower(0.0, lf, lr, rf, rr);
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rf, rr);
        setMode(DcMotor.RunMode.RUN_TO_POSITION, lf, lr, rf, rr);
        setTargetPosition(- ticks, lf, rr);
        setTargetPosition(ticks, rf, lr);
        setPower(ENCODER_DRIVE_POWER, lf, lr, rf, rr);
    }

    public void encoderDriveRight(int cm) {
        encoderDriveLeft(- cm);
    }
}
