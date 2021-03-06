package suitbots.opmode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.suitbots.util.Blinken;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import suitbots.sensor.TensorFlowDetector;
import suitbots.sensor.VisionTargetNavigaton;

public abstract class AutoBase extends LinearOpMode {
    protected DcMotor lift;
    protected DcMotor harvester;
    private DcMotor lf, lb, rf, rb;
    protected Blinken blinken;
    protected BNO055IMU imu;
    private Servo dumper;
    private TensorFlowDetector tensorFlowDetector;
    private VisionTargetNavigaton visionTargetNavigaton;
    private double lastZ, lastX, lastY;

    public enum MineralPosition {
        LEFT, CENTER, RIGHT, UNKNOWN
    }

    // @todo
    protected MineralPosition getMineralPosition() {
        telemetry.addData("TensorFlow", null == tensorFlowDetector ? "No" : "Yes");
        return null == tensorFlowDetector ? MineralPosition.UNKNOWN : tensorFlowDetector.detect();
    }

    private void initilizeGyro() {
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = false;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);
    }

    protected void initialize() {
        initialize(true);
    }

    protected void initialize(boolean withTensorFlow) {
        lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(DcMotorSimple.Direction.REVERSE);

        if (hardwareMap.servo.contains("blinken")) {
            blinken = new Blinken(hardwareMap.servo.get("blinken"));
        } else {
            blinken = null;
        }

        tensorFlowDetector = TensorFlowDetector.make(hardwareMap, telemetry);
        if (null != tensorFlowDetector) {
            visionTargetNavigaton = new VisionTargetNavigaton(tensorFlowDetector.getLocalizer());
        }

        initilizeGyro();

        lf = hardwareMap.dcMotor.get("lf");
        rf = hardwareMap.dcMotor.get("rf");
        lb = hardwareMap.dcMotor.get("lb");
        rb = hardwareMap.dcMotor.get("rb");

        harvester = hardwareMap.dcMotor.get("harvester");

        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        lf.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        updateOrientation();

        dumper = hardwareMap.servo.get("dumper");

    }

    public boolean targetIsVisible() {
        return null != visionTargetNavigaton && visionTargetNavigaton.canSeeTarget();
    }

    public float distanceToTarget() {
        return distanceToTarget(Float.NaN);
    }

    public float distanceToTarget(float defaultValue) {
        return null == visionTargetNavigaton ? defaultValue : visionTargetNavigaton.distanceFromTargetInches();
    }

    public float angleToTarget() {
        return null == visionTargetNavigaton ? Float.NaN : visionTargetNavigaton.angleToTarget();
    }



    protected void announceMinearalPositions() {
        final MineralPosition pos = getMineralPosition();
        if (null != blinken) {
            switch (pos) {
                case LEFT:
                    blinken.enactSolidRed();
                    break;
                case RIGHT:
                    blinken.enactSolidGreen();
                    break;
                case CENTER:
                    blinken.enactSolidBlue();
                    break;
            }
        }
        telemetry.addData("Position", pos);
    }



    protected void setMode(final DcMotor.RunMode mode, final DcMotor... motors) {
        for (final DcMotor motor : motors) {
            motor.setMode(mode);
        }
    }

    protected void setEncoderTargets(final int ticks, final DcMotor... motors) {
        for (final DcMotor motor : motors) {
            motor.setTargetPosition(ticks);
        }
    }

    protected void setPower(final double power, final DcMotor... motors) {
        for (final DcMotor motor : motors) {
            motor.setPower(power);
        }
    }

    private static final double DRIVE_POWER = .25;
    private static final double TICKS_PER_DRIVE_MOTOR_REV = 560.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0;
    private static final double TICKS_PER_INCH = TICKS_PER_DRIVE_MOTOR_REV / (Math.PI * (2.0 * WHEEL_DIAMETER_INCHES));
    protected void driveInches(final double inches) {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, rf, lb, rb);
        setEncoderTargets((int) Math.floor(TICKS_PER_INCH * inches), lf, lb, rf, rb);
        setMode(DcMotor.RunMode.RUN_TO_POSITION, lf, rf, lb, rb);
        setPower(DRIVE_POWER, lf, rf, lb, rb);
        while(opModeIsActive() && (lf.isBusy() && lb.isBusy() && rf.isBusy() && rb.isBusy())) {
            sleep(0);
        }
        setPower(0.0, lf, rb, lb, rb);
        setMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lb, rf, rb);
    }


    private double previousTurnSpeed = Double.NaN;
    private void setTurnSpeeds(double angleDegrees, double speed) {
        if (speed != previousTurnSpeed) {
            final double leftSpeed = angleDegrees < 0.0 ? SLOW_TURN_SPEED : - SLOW_TURN_SPEED;
            setPower(leftSpeed, lf, lb);
            setPower(-leftSpeed, rf, rb);
        }
    }

    private static final double TURN_SPEED = .25;
    private static final double SLOW_TURN_SPEED = .1;
    private static final double SLOW_TURN_ANGLE = 20.0;
    private static final double FUDGE_FACTOR = 5.0;
    protected void turnDegrees(final double angleDegrees) {

        if (180.0 < Math.abs(angleDegrees)) {
            turnDegrees(angleDegrees % 180.0);
        } else {
            double speed = 0.0;
            resetGyro();
            setMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lb, rf, rb);
            final double leftSpeed = angleDegrees < 0.0 ? TURN_SPEED : -TURN_SPEED;
            double rot = Math.abs(getRotationZ());
            double diff = Math.abs(angleDegrees) - Math.abs(getRotationZ());
            do {
                if (rot < SLOW_TURN_ANGLE || diff < SLOW_TURN_ANGLE) {
                    setTurnSpeeds(angleDegrees, SLOW_TURN_SPEED);
                } else {
                    setTurnSpeeds(angleDegrees, TURN_SPEED);
                }
                updateOrientation();
                rot = Math.abs(getRotationZ());
                diff = Math.abs(angleDegrees) - Math.abs(getRotationZ());
            } while (FUDGE_FACTOR < Math.abs(Math.abs(angleDegrees) - Math.abs(rot)));

            setPower(0.0, lf, lb, rf, rb);
            previousTurnSpeed = Double.NaN;
        }
    }

    private static final double TICKS_PER_REV = 145.6; // gobilda 5.2:!
        private static final double MM_PER_REV = 8.0;
    protected void runLiftMotor(final double mm) {
        final int ticksToLand = (int) Math.floor(TICKS_PER_REV * (mm / MM_PER_REV));
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setTargetPosition(ticksToLand);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(1.0);
        while (opModeIsActive() && lift.isBusy()) {
            sleep(0);
        }
        lift.setPower(0.0);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    protected void driveWithPowerUntilTilt(final double left, final double right, final double minTilt) {
        resetGyro();
        setMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lb, rf, rb);
        setPower(left, lf, lb);
        setPower(right, rf, rb);

        while (minTilt < (Math.abs(getRotationX()) + Math.abs(getRotationY()))) {
            updateOrientation();
            sleep(0);
        }
        setPower(0, lf, lb, rf, rb);
    }

    private Orientation getOrientation() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    }
    public void resetGyro() {
        final Orientation orientation = getOrientation();
        lastZ = orientation.firstAngle;
        lastY = orientation.secondAngle;
        lastX = orientation.thirdAngle;
    }

    private static boolean negative(final double x) {
        return 0.0 > x;
    }

    public static double angleDiff(double a, double b) {
        return 180.0 - Math.abs(Math.abs(a - b) - 180.0);
    }

    private Orientation currentOrientation;

    public void updateOrientation() {
        currentOrientation = getOrientation();
    }

    public double getRotationZ() {
        return angleDiff(currentOrientation.firstAngle, lastZ);
    }
    public double getRotationZRaw() { return currentOrientation.firstAngle; }
    public double getRotationZOffset() { return lastZ; }
    public double getRotationX() {
        return angleDiff(currentOrientation.thirdAngle, lastX);
    }
    public double getRotationXRaw() { return currentOrientation.thirdAngle; }
    public double getRotationY() { return angleDiff(currentOrientation.secondAngle, lastY); }
    public double getRotationYRaw() { return currentOrientation.secondAngle; }

    protected void debugDrive(final Controller c) {
        setPower(c.left_stick_y, lf, lb);
        setPower(c.right_stick_y, rf, rb);
    }

    public void flingTheTeamMarker() {
        dumper.setPosition(0.5);
        sleep(1000);
        dumper.setPosition(1.0);
    }


}
