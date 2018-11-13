package suitbots.opmode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.suitbots.util.Blinken;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public abstract class AutoBase extends LinearOpMode {
    protected DcMotor lift;
    private DcMotor lf, lb, rf, rb;
    protected Blinken blinken;
    protected BNO055IMU imu;
    private double lastZ, lastX, lastY;

    public enum MineralPosition {
        LEFT, CENTER, RIGHT
    }

    // @todo
    protected MineralPosition getMineralPosition() {
        return MineralPosition.CENTER;
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
        lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        blinken = new Blinken(hardwareMap.servo.get("blinken"));

        initilizeGyro();

        lf = hardwareMap.dcMotor.get("lf");
        rf = hardwareMap.dcMotor.get("rf");
        lb = hardwareMap.dcMotor.get("lb");
        rb = hardwareMap.dcMotor.get("rb");

        rb.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    protected void announceMinearalPositions() {
        final MineralPosition pos = getMineralPosition();
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

    private void setMode(final DcMotor.RunMode mode, final DcMotor... motors) {
        for (final DcMotor motor : motors) {
            motor.setMode(mode);
        }
    }

    private void setEncoderTargets(final int ticks, final DcMotor... motors) {
        for (final DcMotor motor : motors) {
            motor.setTargetPosition(ticks);
        }
    }

    private void setPower(final double power, final DcMotor... motors) {
        for (final DcMotor motor : motors) {
            motor.setPower(power);
        }
    }

    private static final double DRIVE_POWER = .5;
    private static final double TICKS_PER_DRIVE_MOTOR_REV = 560.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0;
    private static final double TICKS_PER_INCH = TICKS_PER_DRIVE_MOTOR_REV / (Math.PI * (2.0 * WHEEL_DIAMETER_INCHES));
    protected void driveInches(final double inches) {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, lf, rf, lb, rb);
        setEncoderTargets((int) Math.floor(TICKS_PER_INCH * inches), lf, lb, rf, rb);
        setPower(DRIVE_POWER, lf, rf, lb, rb);
        while(opModeIsActive() && (lf.isBusy() && lb.isBusy() && rf.isBusy() && rb.isBusy())) {
            sleep(0);
        }
        setPower(0.0, lf, rb, lb, rb);
        setMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lb, rf, rb);
    }

    private static final double TURN_SPEED = .25;
    protected void turnDegrees(final double angleDegrees) {
        resetGyro();
        setMode(DcMotor.RunMode.RUN_USING_ENCODER, lf, lb, rf, rb);
        final double leftSpeed = angleDegrees < 0.0 ? TURN_SPEED : - TURN_SPEED;
        setPower(leftSpeed, lf, lb);
        setPower(-leftSpeed, rf, rb);
        while (Math.abs(angleDegrees) > Math.abs(getRotationZ()) && opModeIsActive()) {
            sleep(0);
        }
        setPower(0.0, lf, lb, rf, rb);
    }

    private static final double TICKS_PER_REV = 145.6; // gobilda 5.2:!
    private static final double MM_PER_REV = 8.0;
    void runLiftMotor(final double mm) {
        final int ticksToLand = (int) Math.floor(TICKS_PER_REV * (mm / MM_PER_REV));
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setTargetPosition(ticksToLand);
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
            sleep(0);
        }
        setPower(0, lf, lb, rf, rb);
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

    protected void debugDrive(final Controller c) {
        setPower(c.left_stick_y, lf, lb);
        setPower(c.right_stick_y, rf, rb);
    }

}
