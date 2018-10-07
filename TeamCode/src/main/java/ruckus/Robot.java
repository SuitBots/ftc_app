package ruckus;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Robot {
    private DcMotor lf, rf, lr, rr;

    public Robot(final HardwareMap hardware) {
        lf = hardware.dcMotor.get("lf");
        rf = hardware.dcMotor.get("rf");
        lr = hardware.dcMotor.get("lr");
        rr = hardware.dcMotor.get("rr");

        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);

        initilizeGyro(hardware);
    }

    public void drive(double leftMotorPower, double rightMotorPower) {
        lf.setPower(leftMotorPower);
        lr.setPower(leftMotorPower);
        rf.setPower(rightMotorPower);
        rr.setPower(rightMotorPower);
    }

    public void stop() { drive(0.0, 0.0); }

    // Things for encoders. Ignore for now!

    public void setMotorRunmode(DcMotor.RunMode mode) {
        lf.setMode(mode);
        lr.setMode(mode);
        rf.setMode(mode);
        rr.setMode(mode);
    }

    public void setEncoderTargets(final int leftTarget, final int rightTarget) {
        lf.setTargetPosition(leftTarget);
        lr.setTargetPosition(leftTarget);
        rf.setTargetPosition(rightTarget);
        rr.setTargetPosition(rightTarget);
    }

    public boolean motorsAreBusy() {
        return lf.isBusy() || lr.isBusy() || rf.isBusy() || rr.isBusy();
    }


    // Things for the IMU. Ignore for now!
    private BNO055IMU imu;
    private double lastZ, lastX, lastY;
    private void initilizeGyro(HardwareMap hardware) {
        imu = hardware.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = false;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);
    }
    private Orientation getOrientation() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
    }
    public void resetGyro() {
        final Orientation orientation = getOrientation();
        lastX = orientation.firstAngle;
        lastY = orientation.secondAngle;
        lastZ = orientation.thirdAngle;
    }

    public double getRotationZ() {
        return getOrientation().thirdAngle - lastZ;
    }



    public double getRotationX() {
        return getOrientation().firstAngle - lastX;
    }



    public double getRotationY() { return getOrientation().secondAngle - lastY; }

}
