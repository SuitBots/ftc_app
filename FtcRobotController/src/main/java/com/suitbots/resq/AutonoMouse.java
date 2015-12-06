package com.suitbots.resq;

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

public abstract class AutonoMouse extends LinearOpMode {
    abstract boolean isRedAlliance();

    private ModernRoboticsI2cGyro gyro;
    private ColorSensor lights, lines;
    private OpticalDistanceSensor distance;
    private DcMotor l1, l2, l3, r1, r2, r3;
    private Servo climbers;

    void setup() {
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        lights = hardwareMap.colorSensor.get("color");
        lines = hardwareMap.colorSensor.get("linefollow");
        lights.setI2cAddress(0x70);
        distance = hardwareMap.opticalDistanceSensor.get("distance");
        climbers = hardwareMap.servo.get("flipper");

        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        l3 = hardwareMap.dcMotor.get("l3");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");
        r3 = hardwareMap.dcMotor.get("r3");

        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
        r3.setDirection(DcMotor.Direction.REVERSE);
    }

    void halt() {
        allMotors(0.0);
    }

    public static final double FWD_POWER = .7;
    void allMotors(double power) {
        l1.setPower(power);
        l2.setPower(power);
        l3.setPower(power);
        r1.setPower(power);
        r2.setPower(power);
        r3.setPower(power);
    }

    // In this autonomous, we only need to be able to turn in
    // 45 degree increments.
    public static final double TURN_SPEED = 0.35;
    void turn() {
        Cond heading = new Heading(isRedAlliance() ? -45 : 45);
        double left = isRedAlliance() ? -TURN_SPEED : TURN_SPEED;
        double right = isRedAlliance() ? TURN_SPEED : -TURN_SPEED;

        drive(heading, 0.0, left, 0.0, 0.0, right, 0.0);
    }

    public static final double SQRT_2 = Math.sqrt(2.0);
    public static final int TICKS_PER_METER = 2800;
    public static final double SQUARES_PER_METER = 0.6;

    interface Cond {
        public boolean met();
    }

    class Heading implements Cond {
        int target;

        public Heading(int _target) {
            target = _target;
            gyro.resetZAxisIntegrator();
        }

        public boolean met() {
            int heading = - gyro.getIntegratedZValue();
            if (target > 0) {
                return heading >= target;
            } else {
                return heading <= target;
            }
        }
    }

    class Ticks implements Cond {
        int le, re;
        int target;
        public Ticks(int t) {
            le = l2.getCurrentPosition();
            re = l2.getCurrentPosition();
            target = t;
        }

        public boolean met() {
            int l = Math.abs(l2.getCurrentPosition() - le);
            int r = Math.abs(r2.getCurrentPosition() - re);
            return l >= target || r >= target;
        }
    }

    public static final int COLOR_THRESHOLD = 2;
    class OnLine implements Cond {
        public boolean met() {
            return lines.red() >= COLOR_THRESHOLD &&
                    lines.green() >= COLOR_THRESHOLD &&
                    lines.blue() >= COLOR_THRESHOLD &&
                    lines.alpha() >= COLOR_THRESHOLD;
        }
    }

    class Wait implements Cond {
        double done;
        public Wait(double seconds) {
            done = getRuntime() + seconds;
        }

        public boolean met() {
            return getRuntime() >= done;
        }
    }

    class Or implements Cond {
        Cond a, b;
        public Or(Cond _a, Cond _b) {
            a = _a;
            b = _b;
        }

        public boolean met() {
            return a.met() || b.met();
        }
    }

    private static final int STOP_DISTANCE = 35;
    class SensorClose implements Cond {
        public boolean met() {
            return distance.getLightDetectedRaw() >= STOP_DISTANCE;
        }
    }

    // Set the motors to a particular set of powers until a condition is met.
    private void drive(Cond cond,
                       double pl1, double pl2, double pl3,
                       double pr1, double pr2, double pr3) {
        l1.setPower(pl1);
        l2.setPower(pl2);
        l3.setPower(pl3);
        r1.setPower(pr1);
        r2.setPower(pr2);
        r3.setPower(pr3);

        while (opModeIsActive() && ! cond.met()) {
            // Just keep doing your thing
        }

        halt();
    }

    private void driveStraight(double power, Cond cond) {
        drive(cond, power, power, power, power, power, power);
    }

    void fwd(double squares) {
        driveStraight(FWD_POWER, new Ticks(Math.abs((int) (SQUARES_PER_METER * squares * TICKS_PER_METER))));
    }

    void rev(double squares) {
        driveStraight(-FWD_POWER, new Ticks(Math.abs((int) (SQUARES_PER_METER * squares * TICKS_PER_METER))));
    }

    public static final double SLOW_POWER = .4;
    void toLine(double squares) {
        Cond ticks = new Ticks(Math.abs((int) (SQUARES_PER_METER * squares * TICKS_PER_METER)));
        Cond line = new OnLine();
        driveStraight(SLOW_POWER, new Or(ticks, line));
    }

    void driveToSensor(double squares) {
        Cond ticks = new Ticks(Math.abs((int) (SQUARES_PER_METER * squares * TICKS_PER_METER)));
        Cond close = new SensorClose();
        driveStraight(SLOW_POWER, new Or(ticks, close));
    }

    public static final int LIGHT_COLOR_THRESHOLD = 10;
    boolean lightIsRed() {
        return lights.red() >= LIGHT_COLOR_THRESHOLD;
    }

    boolean lightIsBlue() {
        return lights.blue() >= LIGHT_COLOR_THRESHOLD;
    }

    public static final double BUMP_WAIT_SEC = .5;
    void bump(double left, double right) throws InterruptedException {
        for (int i = 0; i < 3; ++i) {
            drive(new Wait(BUMP_WAIT_SEC), left, left, left, right, right, right);
            drive(new Wait(BUMP_WAIT_SEC / 3.0), -left, -left, -left, -right, -right, -right);
        }
    }

    public static final int CLIMBERS_SLEEP = 2000;
    void dumpClimbers() throws InterruptedException {
        climbers.setPosition(1.0);
        sleep(CLIMBERS_SLEEP);
        climbers.setPosition(0.0);
        sleep(CLIMBERS_SLEEP / 2);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        setup();
        gyro.calibrate();
        while (gyro.isCalibrating()) {
            sleep(50);
        }

        waitForStart();

        fwd(1.0);
        turn();
        fwd(2.0 * SQRT_2);
        toLine(SQRT_2);
        turn();
        driveToSensor(0.5);

        dumpClimbers();

        if (isRedAlliance() == lightIsRed()) {
            bump(1.0, 0.0);
        } else {
            bump(0.0, 1.0);
        }

        rev(0.1);
        turn();
        turn();
        fwd(1.0);
    }
}
