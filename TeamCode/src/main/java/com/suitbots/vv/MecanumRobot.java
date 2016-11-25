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


    // Things that need to happen in the teleop loop to accommodate long-running
    // tasks like running the flipper one at a time.
    public void loop() {
        if (isDoneFlipping()) {
            setFlipperPower(0.0);
        }
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

    public void gyro1(){
        double gyro1 = gyro.getIntegratedZValue();
    }

    private static double ma(double a, double b) {
        return Math.max(Math.abs(a), Math.abs(b));
    }

    private static double clip(double x) {
        return Math.min(Math.max(-1.0, x), 1.0);
    }

    public static double GYRO_HEADING_REDUCE = 30.0;
    public void drivePreservingDirection(double translationRadians, double velocity) {
        drive(translationRadians, velocity, ((double) gyro.getIntegratedZValue()) / GYRO_HEADING_REDUCE);
    }

    public void drive(double translationRadians, double translationVelocity, double rotationRight) {
        final double vd = translationVelocity;
        final double td = translationRadians;
        final double vt = rotationRight;

        double s =  Math.sin(td + Math.PI / 4.0);
        double c = Math.cos(td + Math.PI / 4.0);
        double m = Math.max(Math.abs(s), Math.abs(c));
        s /= m;
        c /= m;

        final double v1 = vd * s + vt;
        final double v2 = vd * c - vt;
        final double v3 = vd * c + vt;
        final double v4 = vd * s - vt;


        lf.setPower(clip(v1));
        rf.setPower(clip(v2));
        lr.setPower(clip(v3));
        rr.setPower(clip(v4));
    }

    public static final int COLOR_THRESHOLD = 2;
    public boolean colorSensorIsRed() {
        return color.red() > COLOR_THRESHOLD && color.red() > color.blue();
    }

    public boolean colorSensorIsBlue() {
        return color.blue() > COLOR_THRESHOLD && color.blue() > color.red();
    }

    public void setHarvesterPower(double p) {
        harvester.setPower(p);
    }
    public void setFlipperPower(double p) {
        if (flipping) {
            flipper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flipping = false;
        }
        flipper.setPower(p);
    }

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

    public void stop() {
        harvester.setPower(0.0);
        lf.setPower(0.0);
        lr.setPower(0.0);
        rr.setPower(0.0);
        rf.setPower(0.0);
    }
}
