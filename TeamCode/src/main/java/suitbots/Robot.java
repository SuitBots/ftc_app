package suitbots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

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

    private double lastG;
    private BNO055IMU imu;
    private DcMotor lf, lr, rf, rr;
    private ColorSensor lineDetector;
    public Robot(HardwareMap h) {
        imu = h.get(BNO055IMU.class, "gyro");
        initilizeGyro();
        lineDetector = h.colorSensor.get("lineDetector");

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
//    public double getHeadingRadians() {
    //This one I'm having trouble with
////    degrees -> radians
////    1 = MATH.PI/180
//        double angles = imu.getAngularOrientation().angleUnit.fromRadians();
//        return angles;
//    }

    //I forgot how to this one T.T
//    public void resetHeading() {
//        imu.getAngularOrientation(0,0,0);
//    }

    public void setMotorSpeeds(double lfs, double lrs, double rfs, double rrs){
        lf.setPower(lfs);
        lr.setPower(lrs);
        rf.setPower(rfs);
        rr.setPower(rrs);
    }
    //wait...why do we need this?
//    public void setMotorMode(DcMotor.RunMode runMode) {
//        return DcMotor.RunMode.;
//    }
    public void setEncoderTargets(int lfs, int lrs, int rfs, int rrs) {
        lf.setTargetPosition(lfs);
        lr.setTargetPosition(lrs);
        rf.setTargetPosition(rfs);
        rr.setTargetPosition(rrs);
    }
    public boolean driveMotorsAreBusy() {
        if (lf.isBusy() || rf.isBusy() || lr.isBusy()|| rr.isBusy()){
            return true;
        }else {
            return false;
        }
    }
}
