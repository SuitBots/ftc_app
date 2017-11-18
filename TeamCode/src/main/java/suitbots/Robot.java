package suitbots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


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
    private DcMotor lf, lr, rf, rr, lift;
    private DcMotor armr, arml;
    private ColorSensor jewelColorDetector;//sensor looking backwards!!!! <------------------
    private Servo soas;

    public Robot(HardwareMap h, Telemetry _telemetry) {
        telemetry = _telemetry;
        imu = h.get(BNO055IMU.class, "imu");
        initilizeGyro();
        jewelColorDetector = h.colorSensor.get("jewelColorDetector");

        //pf = new LazyCR(hardwareMap.crservo.get("pf"));
        //pr = new LazyCR(hardwareMap.crservo.get("pr"));


        lf = h.dcMotor.get("lf");
        lr = h.dcMotor.get("lr");
        rf = h.dcMotor.get("rf");
        rr = h.dcMotor.get("rr");
        lift = h.dcMotor.get("lift");

        armr = h.dcMotor.get("armr");
        arml = h.dcMotor.get("arml");

        //rightGripper = h.servo.get("rightGripper");
        //leftGripper = h.servo.get("leftGripper");
        soas = h.servo.get("soas");

        arml.setDirection(DcMotorSimple.Direction.REVERSE);

        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        lf.setDirection(DcMotorSimple.Direction.REVERSE);


        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetGyro() {
        lastG = getGyroRaw();
    }

    public double getGyroRaw() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        return angles.firstAngle;
    }

    public double getGyro() {
        return (getGyroRaw() - lastG) % (2.0 * Math.PI);
    }

    public double getGyroDeg() {
        return Math.toDegrees(getGyro());
    }

    private void initilizeGyro() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = false;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);
    }

    public boolean isGyroCalibrated() {
        return imu.isSystemCalibrated();
    }

    public double getHeadingRadians() {
        return getGyro();
    }

    public double getHeadingDeg() {
        return (double) getGyroDeg();
    }

    public void resetHeading() {
        lastG = getGyroRaw();
    }

    public void setMotorSpeeds(double lfs, double lrs, double rfs, double rrs) {
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

    private static final double ENCODER_DRIVE_POWER = .3; // .35;
    // Assuming 4" wheels
    private static final double TICKS_PER_INCH = 1120 * (16. / 24.) / (Math.PI * 4.0);
    private static final double TICKS_PER_CM = TICKS_PER_INCH / 2.54;

    void setEncoderDrivePower(double p) {
        encoder_drive_power = p;
    }

    private double encoder_drive_power = ENCODER_DRIVE_POWER;

    void clearEncoderDrivePower() {
        encoder_drive_power = ENCODER_DRIVE_POWER;
    }

    private int averageRemainingTicks(DcMotor... ms) {
        int total = 0;
        int count = 0;
        for (DcMotor m : ms) {
            if (m.getMode() == DcMotor.RunMode.RUN_TO_POSITION && 100 < Math.abs(m.getTargetPosition())) {
                total += Math.abs(m.getTargetPosition() - m.getCurrentPosition());
                count += 1;
            }
        }
        return 0 == count ? 0 : total / count;
    }

    private static int SLOW_DOWN_HERE = 1120;
    private static double ARBITRARY_SLOW_SPEED = .3;
    private boolean slowedDown = false;

    private void encoderDriveSlowdown() {
        if (!slowedDown) {
            if (lf.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
                int remaining = averageRemainingTicks(lf, lr, rf, rr);
                if (remaining < SLOW_DOWN_HERE) {
                    slowedDown = true;
                    setPower(ARBITRARY_SLOW_SPEED, lf, lr, rf, rr);
                }
            }
        }
    }


    private static class Wheels {
        public double lf, lr, rf, rr;

        public Wheels(double lf, double rf, double lr, double rr) {
            this.lf = lf;
            this.rf = rf;
            this.lr = lr;
            this.rr = rr;
        }
    }

    private Wheels getWheels(double direction, double velocity, double rotationVelocity) {
        final double vd = velocity;
        final double td = direction;
        final double vt = rotationVelocity;

        double s = Math.sin(td + Math.PI / 4.0);
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

        return new Wheels(v1 / scale, v2 / scale, v3 / scale, v4 / scale);
    }

    private static double ma(double... xs) {
        double ret = 0.0;
        for (double x : xs) {
            ret = Math.max(ret, Math.abs(x));
        }
        return ret;
    }

    public void drive(double direction, double velocity, double rotationVelocity) {
        Wheels w = getWheels(direction, velocity, rotationVelocity);
        lf.setPower(w.lf);
        rf.setPower(w.rf);
        lr.setPower(w.lr);
        rr.setPower(w.rr);

        telemetry.addData("Powers", String.format(Locale.US, "%.2f %.2f %.2f %.2f", w.lf, w.rf, w.lr, w.rr));
    }

    public static final double OPEN_RIGHT = 0.90;
    public static final double OPEN_LEFT = 0.00;
    public static final double OPEN_LITTLE_RIGHT = 0.55;
    public static final double OPEN_LITTLE_LEFT = 0.35;
    public static final double CLOSED_RIGHT = 0.70;
    public static final double CLOSED_LEFT = 0.20;


    public void moveLift(double x) {
        lift.setPower(x);
    }

    public static final double DOWN_SOAS = 0.7;
    public static final double UP_SOAS = 0.20;
    public void putDownSoas() {
        soas.setPosition(DOWN_SOAS);
    }
    public void putUpSoas() {
        soas.setPosition(UP_SOAS);
    }

    public boolean jewelIsRed() {
        return jewelColorDetector.red() > jewelColorDetector.blue();
    }

    public void encoderDriveTiles(double direction, double tiles) {
        encoderDriveInches(direction, 24.0 * tiles);
    }

    public void encoderDriveInches(double direction, double inches) {
        final Wheels w = getWheels(direction, 1.0, 0.0);
        final int ticks = (int) (inches * TICKS_PER_INCH);
        encoderDrive(ticks * w.lf, ticks * w.rf, ticks * w.lr, ticks * w.rr);
    }

    public void encoderDriveCM(double direction, double cm) {
        direction %= Math.PI * 2.0;
        final Wheels w = getWheels(direction, 1.0, 0.0);
        final int ticks = (int) (cm * TICKS_PER_CM);
        encoderDrive(ticks * w.lf, ticks * w.rf, ticks * w.lr, ticks * w.rr);
    }

    private void encoderDrive(double lft, double rft, double lrt, double rrt) {
        encoderDrive((int) lft, (int) rft, (int) lrt, (int) rrt);
    }

    private void encoderDrive(int lft, int rft, int lrt, int rrt) {
        setPower(0.0, lf, lr, rf, rr);
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rf, rr);
        setTargetPosition(lft, lf);
        setTargetPosition(rft, rf);
        setTargetPosition(lrt, lr);
        setTargetPosition(rrt, rr);
        setMode(DcMotor.RunMode.RUN_TO_POSITION, lf, rf, lr, rr);
        setPower(encoder_drive_power, lf, lr, rf, rr);
        slowedDown = false;
    }

    public int detectJewelColour() { //0 = blue, 1 = red
        final float[] important = new float[]{
                jewelColorDetector.red(),
                jewelColorDetector.blue(),
                jewelColorDetector.green(),
                jewelColorDetector.alpha()
        };
        return Brain.predict(important);
    }

    public void collect() {
        arml.setPower(-0.5);
        armr.setPower(-0.5);
    }

    public void release() {
        arml.setPower(0.25);
        armr.setPower(0.25);
    }

    public void diagonalRelease() {
        arml.setPower(.5);
        armr.setPower(-.5);
    }

    public void stoparms() {
        arml.setPower(0.0);
        armr.setPower(0.0);
    }

    public void setArmMotors(final double l, final double r) {
        arml.setPower(l);
        armr.setPower(r);
    }

    public void announceEncoders() {
        telemetry.addData("LF", lf.getCurrentPosition());
        telemetry.addData("RF", rf.getCurrentPosition());
        telemetry.addData("LR", lr.getCurrentPosition());
        telemetry.addData("RR", rr.getCurrentPosition());
    }

    public void resetEncoders() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rf, rr);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lr, rf, rr);
    }

}
