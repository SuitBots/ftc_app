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

/**
 * Created by Samantha on 9/2/2017.
 */

public class Robot {
    //Yo necesito dormir.
    private double lastG;
    private BNO055IMU gyro;
    private DcMotor lf, lr, rf, rr;
    private ColorSensor lineDetector;
    public Robot(HardwareMap h) {
        gyro = h.get(BNO055IMU.class, "gyro");
        initilizeGyro();
        lineDetector = h.colorSensor.get("lineDetector");

    }

    public void resetGyro() {
        lastG = getGyroRaw();
    }
    public double getGyroRaw() {
        Orientation angles   = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        return angles.firstAngle;
    }
    public double getGyro(){
        return (getGyroRaw()-lastG)%2*Math.PI;
    }

    private void initilizeGyro() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = false;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        gyro.initialize(parameters);
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
}
