package suitbots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

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
    private DcMotor armr, arml, lights;
    private ColorSensor jewelColorDetector;//sensor looking backwards!!!! <------------------
    private AnalogInput glyph;
    private Servo soas, swing;

    public Robot(HardwareMap h, Telemetry _telemetry) {
        telemetry = _telemetry;
        imu = h.get(BNO055IMU.class, "imu");
        initilizeGyro();
        jewelColorDetector = h.colorSensor.get("jewelColorDetector");
        glyph = h.analogInput.get("glyph");

        lf = h.dcMotor.get("lf");
        lr = h.dcMotor.get("lr");
        rf = h.dcMotor.get("rf");
        rr = h.dcMotor.get("rr");
        lift = h.dcMotor.get("lift");
        lights = h.dcMotor.get("lights");

        armr = h.dcMotor.get("armr");
        arml = h.dcMotor.get("arml");

        soas = h.servo.get("soas");
        swing = h.servo.get("swing");

        lights = h.dcMotor.get("lights");

        arml.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        lf.setDirection(DcMotorSimple.Direction.REVERSE);


        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lift);
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION, lift);
        insureIndexMode(true);
    }

    public void disableDriveEncoders() {
        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void resetGyro() {
        lastG = getGyroRaw();
    }

    private Orientation orientation;
    public void loop() {
        orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
    }

    public double getGyroRaw() {
        orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
        return orientation.firstAngle;
    }

    public Velocity getVelocity() {
        return imu.getVelocity();
    }

    public double absoluteVelocity() {
        final Velocity velocity = getVelocity();
        return Math.sqrt(velocity.xVeloc * velocity.xVeloc + velocity.yVeloc * velocity.yVeloc + velocity.zVeloc * velocity.zVeloc);

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

    public void setRelicarmPower(final double x) {
        // relicarm.setPower(x);
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

    public void resetDriveMotorModes() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, lr, rf, rr);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lr, rf, rr);
    }

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
    private static final double DRIVE_MOTOR_TICKS_PER_REV = 537.6;
    private static final double TICKS_PER_INCH = DRIVE_MOTOR_TICKS_PER_REV / (Math.PI * 4.0);

    void setEncoderDrivePower(double p) {
        encoder_drive_power = p;
    }

    private double encoder_drive_power = ENCODER_DRIVE_POWER;

    void clearEncoderDrivePower() {
        encoder_drive_power = ENCODER_DRIVE_POWER;
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
    }

    private boolean indexMode = true;
    private static final int ENCODER_TICKS_PER_MOTOR_REV = 1680;
    private static final double WHEEL_CIRCUMFERENCE = 1.9 * Math.PI;
    private static final int LIFT_TICKS_PER_INCH = (int)(ENCODER_TICKS_PER_MOTOR_REV / WHEEL_CIRCUMFERENCE);
    private static final int[] LIFT_INDEX = new int[] {
            0,
            2 * LIFT_TICKS_PER_INCH,
            8 * LIFT_TICKS_PER_INCH,
            14 * LIFT_TICKS_PER_INCH,
            19 * LIFT_TICKS_PER_INCH
    };
    private int indexPosition = 0;
    public int getLiftIndex() { return indexPosition; }
    public int DEBUGgetLiftTarget() { return lift.getTargetPosition(); }
    public int DEBUGgetLiftCurrent() { return lift.getCurrentPosition(); }
    public DcMotor.RunMode DEBUGgetLiftMode() { return lift.getMode(); }

    private void insureIndexMode(final boolean mode) {
        if (mode != indexMode) {
            indexMode = mode;
            if (indexMode) {
                setMotorMode(DcMotor.RunMode.RUN_TO_POSITION, lift);
            } else {
                setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER, lift);
            }
        }
    }

    public void indexLiftUp() {
        insureIndexMode(true);
        indexPosition = Range.clip(indexPosition + 1, 0, LIFT_INDEX.length - 1);
        lift.setTargetPosition(LIFT_INDEX[indexPosition]);
        lift.setPower(1.0);
    }
    public void indexLiftDown() {
        insureIndexMode(true);
        indexPosition = Range.clip(indexPosition - 1, 0, LIFT_INDEX.length - 1);
        lift.setTargetPosition(LIFT_INDEX[indexPosition]);
        lift.setPower(1.0);
    }

    public void setLiftIndex(int index) {
        insureIndexMode(true);
        lift.setTargetPosition(LIFT_INDEX[indexPosition = index]);
        lift.setPower(1.0);
    }

    public void resetLiftEncoder() {
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private static final double MIN_LIFT_POWER = 0.1;
    public void moveLift(double x) {
        // break out of index mode when user input occurs.
        if (indexMode && MIN_LIFT_POWER < Math.abs(x)) {
            insureIndexMode(false);
        }
        if (! indexMode){
            lift.setPower(x);
        }
    }

    public static final double DOWN_SOAS = .68;
    public static final double UP_SOAS = 0.0;
    public void putDownSoas() {
        soas.setPosition(DOWN_SOAS);
    }
    public void putUpSoas() {
        soas.setPosition(UP_SOAS);
    }

    public static final double SWING_FORWARD = 0.3;
    public static final double SWING_BACK = .5;
    public static final double SET_SWING = 0.4;

    public void swingForward() {
        swing.setPosition(SWING_FORWARD);
    }
    public void swingBack() {
        swing.setPosition(SWING_BACK);
    }
    public void setSwing() {
        swing.setPosition(SET_SWING);
    }

    public void DEBUG_setSwing(double x, double y) {
        swing.setPosition(x);
        soas.setPosition(y);
    }

    public void encoderDriveTiles(double direction, double tiles) {
        encoderDriveInches(direction, 24.0 * tiles);
    }

    public void encoderDriveInches(double direction, double inches) {
        final Wheels w = getWheels(direction, 1.0, 0.0);
        final int ticks = (int) (inches * TICKS_PER_INCH);
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
    }

    private static Brain brain = BrainBuilder.makeBrain();

    public int detectJewelColour() { //0 = blue, 1 = red
        final double[] important = new double[]{
                jewelColorDetector.red(),
                jewelColorDetector.blue(),
                jewelColorDetector.green(),
                jewelColorDetector.alpha()
        };
        if (null == brain) {
            throw new RuntimeException("NULL BRAIN");
        }
        try {
            return brain.predict(important);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }
    }

    public void collect() {
        arml.setPower(-0.75);
        armr.setPower(-0.75);
    }

    public void release() {
        arml.setPower(0.75);
        armr.setPower(0.75);
    }

    public void releaseSlow() {
        arml.setPower(.5);
        armr.setPower(.5);
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

    private static double GLYPH_VOLTAGE_THRESHOLD = 1.8;
    public boolean hasGlyph() {
        return glyph.getVoltage() < GLYPH_VOLTAGE_THRESHOLD;
    }
    public void setLights(final double x) {
        lights.setPower(x);
    }

    public void DEBUG_announceLiftStuff() {
        telemetry.addData("Lift Encoder", lift.getCurrentPosition());
        telemetry.addData("Lift Target", lift.getTargetPosition());
    }

    public void caughtGlyph(double distance) {
        if(distance <= 1){
            lights.setPower(100);
        }else{
            lights.setPower(0);
        }
    }
}
