package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/**
 * Created by Suit Bots on 11/11/2016.
 */

public class MecanumRobot {
    private DcMotor lf, lr, rf, rr, harvester, flipper;
    private ToggleableServo bf, br;
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
        harvester = hardwareMap.dcMotor.get("harvester");
        gyro = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("gyro");
        range = (ModernRoboticsI2cRangeSensor)
                hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "range");
        line = hardwareMap.opticalDistanceSensor.get("line");
        color = hardwareMap.colorSensor.get("color");
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        gyro.calibrate();
    }

    public double distanceToWallCM() {
        return range.getDistance(DistanceUnit.CM);
    }

    public static final double LINE_LIGHT_THRESHOLD = 3.0;
    public boolean isOnWhiteLine() {
        return line.getRawLightDetected() > LINE_LIGHT_THRESHOLD;
    }

    public boolean isNotOnWhiteLine() {
        return line.getRawLightDetected() < LINE_LIGHT_THRESHOLD;
    }

    public boolean isCalibrating(){
        return gyro.isCalibrating();
    }

    public void resetGyro() {
        gyro.resetZAxisIntegrator();
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


    public static double GYRO_HEADING_REDUCE = 15.0;
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

        telemetry.addData("MecInput", String.format(Locale.US, "%.2f, %.2f, %.2f",
                Math.toDegrees(translationRadians), translationVelocity, rotationRight));
        telemetry.addData("Mecanum", String.format(Locale.US, "%.2f, %.2f, %.2f, %.2f", v1, v2, v3, v4));
        telemetry.addData("Gyro1",  gyro.getIntegratedZValue());
        telemetry.addData("Range1", range.getDistance(DistanceUnit.CM));
        telemetry.addData("Line1", line.getRawLightDetected());
        telemetry.addData("Color1", String.format(Locale.US, "%d\t%d", color.red(), color.blue()));
        telemetry.update();
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
        flipper.setPower(p);
    }

    public void toggleFrontServo() {
        bf.toggle();
    }

    public void toggleBackServo() {
        br.toggle();
    }

    public void stop() {
        harvester.setPower(0.0);
        lf.setPower(0.0);
        lr.setPower(0.0);
        rr.setPower(0.0);
        rf.setPower(0.0);
    }
}
