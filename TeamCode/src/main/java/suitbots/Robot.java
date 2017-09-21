package suitbots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

/**
 * Created by Samantha on 9/2/2017.
 */

public class Robot {
    //Yo necesito dormir.
    private Telemetry telemetry;
    private static double pracSpeed = 0.5;
    private double lastG;
    private BNO055IMU imu;
    private DcMotor lf, lr, rf, rr;
    private ColorSensor lineDetector;
    public Robot(HardwareMap h) {
        imu = h.get(BNO055IMU.class, "imu");
        initilizeGyro();
        lineDetector = h.colorSensor.get("lineDetector");

        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void resetGyro() {
        lastG = getGyroRaw();
    }
    public double getGyroRaw() {
        Orientation angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        return angles.firstAngle;
    }
    public double getGyro(){
        return (getGyroRaw()-lastG)* (2.0 * Math.PI);
    }

    private void initilizeGyro() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = false;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);
    }

    public int getLight() {
        return lineDetector.alpha();
    }

    public boolean isAboveWhiteLine() {
        if(getLight() >= 200) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isGyroCalibrated() {
        return imu.isSystemCalibrated();
    }
    public double getHeadingRadians() { return getGyro(); }

    public void resetHeading() {
        lastG = getGyroRaw();
    }

    public void setMotorSpeeds(double lfs, double lrs, double rfs, double rrs){
        lf.setPower(lfs);
        lr.setPower(lrs);
        rf.setPower(rfs);
        rr.setPower(rrs);
    }
    //wait...why do we need this?
    //OOHHHHHHH!!! Found out
    public void setMotorMode(DcMotor.RunMode mode, DcMotor... ms) {
        for (DcMotor m : ms) {
            m.setMode(mode);
        }
    }

    private static final double ENCODER_DRIVE_POWER = .3; // .35;
    public void setEncoderTargets(int lfs, int lrs, int rfs, int rrs) {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rr, rf);
        lf.setTargetPosition(lfs);
        lr.setTargetPosition(lrs);
        rf.setTargetPosition(rfs);
        rr.setTargetPosition(rrs);
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION, lf, lr, rr, rf);
    }
    public void resetDriveMotorModes() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rf, rr);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lr, rf, rr);
    }
//    private void setTargetPosition(int pos, DcMotor... ms) { //we probably don't need this
//        for (DcMotor m : ms) {
//            m.setTargetPosition(pos);
//        }
//    }
    private void setPower(double p, DcMotor... ms) {
        for (DcMotor m : ms) {
            m.setPower(p);
        }
    }

    public static final int ENCODERS_CLOSE_ENOUGH = 10;
    private boolean busy(DcMotor... ms) {
        int total = 0;
        for (DcMotor m : ms) {
            if (m.isBusy()) {
                total += Math.abs(m.getCurrentPosition() - m.getTargetPosition());
            }
        }
        return total > ENCODERS_CLOSE_ENOUGH;
    }
    public boolean driveMotorsBusy() {
        return busy(lf, lr, rf, rr);

    }
    public void onStart() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rr, rf);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lr, rr, rf);
    }

    public void onStop() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rr, rf);
        stopDriveMotors();
    }
    public void stopDriveMotors() {
        lf.setPower(0.0);
        lr.setPower(0.0);

        rf.setPower(0.0);
        rr.setPower(0.0);
    }
//    public void setFrontPower(double p) { pf.setPower(p); }
//    public void setBackPower(double p) { pr.setPower(p); }
public void updateSensorTelemetry() {
    telemetry.addData("Imu", getGyro());
    //telemetry.addData("Color", String.format(Locale.US, "R: %d\tB: %d", color.red(), color.blue()));
    telemetry.addData("Light", getLight());
    telemetry.addData("EncodersC", String.format(Locale.US, "%d\t%d\t%d\t%d\t%d",
            lf.getCurrentPosition(),
            lr.getCurrentPosition(),
            rf.getCurrentPosition(),
            rr.getCurrentPosition()));
    telemetry.addData("EncodersT", String.format(Locale.US, "%d\t%d\t%d\t%d\t%d",
            lf.getTargetPosition(),
            lr.getTargetPosition(),
            rf.getTargetPosition(),
            rr.getTargetPosition()));
    }
//    public double getHeading() {
//        double angle = getGyro();
//        return angle;
//    }
}
