package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.suitbots.util.Blinken;
import com.suitbots.util.Controller;

import java.util.Locale;

import suitbots.ConfigVars;
import suitbots.StatefulServo;
import suitbots.sensor.DoubleMineralSensor;

import static java.lang.Math.abs;
import static java.lang.Math.max;

@TeleOp(name = "TELEOP", group = "Tourney")
public class PlaylandYurtTeleop extends OpMode {
    private DcMotor lf, lb, rf, rb;
    private DcMotor harvester;
    private DcMotor lift;
    private DcMotor arm;

    private StatefulServo dumperServo;

    private DcMotorSimple.Direction driveDirection = DcMotorSimple.Direction.FORWARD;

    private Controller g1, g2;

    private DoubleMineralSensor doubleMineralSensor;
    private Blinken blinken;
    private ElapsedTime et = new ElapsedTime();

    @Override
    public void init() {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        lift = hardwareMap.dcMotor.get("lift");
        dumperServo = new StatefulServo(hardwareMap.servo.get("dumper"));
        arm = hardwareMap.dcMotor.get("arm");
        harvester = hardwareMap.dcMotor.get("harvester");

        rb.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);

        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // arm.setDirection(DcMotorSimple.Direction.REVERSE);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);

        doubleMineralSensor = new DoubleMineralSensor(hardwareMap.analogInput.get("fsr"));
        blinken = new Blinken(hardwareMap.servo.get("blink"));
        blinken.off();
    }

    private static DcMotorSimple.Direction flop(final DcMotorSimple.Direction x) {
        if (x == DcMotorSimple.Direction.REVERSE) {
            return DcMotorSimple.Direction.FORWARD;
        } else {
            return DcMotorSimple.Direction.REVERSE;
        }
    }

    private void flopDirection() {
        lf.setDirection(flop(lf.getDirection()));
        lb.setDirection(flop(lb.getDirection()));
        rf.setDirection(flop(rf.getDirection()));
        rb.setDirection(flop(rb.getDirection()));
        driveDirection = flop(driveDirection);
    }

    private void adjustDumper() {
        final double pos = arm.getCurrentPosition();
        final double armUpScaled = ConfigVars.TELEOP_ARM_UP / ConfigVars.TELEOP_DUMP_SERVO_LIMIT;

        final double percent = Math.abs((double) pos / armUpScaled);
        if (percent > ConfigVars.ARM_UP_PERCENT_SLOW) {
            arm.setPower(ConfigVars.ARM_UP_SPEED_FINAL);
        }
        dumperServo.setPosition(1.0 - Math.min(1.0, Math.pow(percent, ConfigVars.SERVO_ANGLE_EXPONENTIAL)));
    }

    @Override
    public void init_loop() {
        super.init_loop();
        doubleMineralSensor.init_loop();
    }

    @Override
    public void start() {
        super.start();
        doubleMineralSensor.start();
        et.reset();
        blinken.off();
    }

    @Override
    public void stop() {
        super.stop();
        blinken.off();
    }

    private static final double ENDGAME_BEGINS = 90.0;
    private static final double ENDGAME_URGENT = 105.0;
    private void setBlinkenState() {
        final boolean hasTwoMinerals = doubleMineralSensor.triggered();
        final double elapsedTime = et.seconds();

        telemetry.addData("Two Minerals?", hasTwoMinerals ? "YES" : "no.");

        if (elapsedTime > ENDGAME_URGENT) {
            // Super Endgame. HANG NOW.
            if (hasTwoMinerals) {
                blinken.enactSolidRed();
            } else {
                blinken.enactFixedPalettePatternStrobeRed();
            }
        } else if (elapsedTime > ENDGAME_BEGINS) {
            // Endgame. Think about hanging.
            if (hasTwoMinerals) {
                blinken.enactSolidGold();
            } else {
                blinken.enactFixedPalettePatternStrobeGold();
            }
        } else {
            // Teleop. Go nuts.
            if (hasTwoMinerals) {
                blinken.enactSolidGreen();
            } else {
                blinken.off();
            }
        }
    }

    @Override
    public void loop() {
        g1.update();
        g2.update();
        setBlinkenState();

        harvester.setPower(g1.right_trigger - g1.left_trigger);
        lift.setPower(g2.right_trigger - g2.left_trigger);

        if (g2.dpadUp() || g1.dpadUp()) {
            arm.setTargetPosition(ConfigVars.TELEOP_ARM_UP);
            arm.setPower(ConfigVars.ARM_UP_SPEED);
        } else if (g2.dpadDownOnce() || g1.dpadDownOnce()) {
            arm.setTargetPosition(ConfigVars.TELEOP_ARM_DOWN);
            arm.setPower(ConfigVars.ARM_DOWN_SPEED);
        } else {
            if (ConfigVars.TELEOP_ARM_UP == arm.getTargetPosition() &&
                    .7 < Math.abs((float)arm.getCurrentPosition() / (float)ConfigVars.TELEOP_ARM_UP)) {
                arm.setPower(.1);
            }
        }

        if (g1.AOnce()) {
            flopDirection();
        }

        if (g1.B()) {
            dumperServo.setPosition(0.0);
        }
        if (g1.X()) {
            dumperServo.setPosition(ConfigVars.TELEOP_DUMP_SERVO_POSITION);
        } else {
            adjustDumper();
        }

        final double fwd = gamepad1.left_stick_y;
        final double turn = -gamepad1.right_stick_x;
        final double max = max(1.0, abs(fwd) + abs(turn));
        final double speed = gamepad1.left_stick_button ? 1.0 : ConfigVars.TELEOP_SLOW_SPEED;

        double l = (speed * fwd + turn) / max;
        double r = (speed * fwd - turn) / max;

        if (driveDirection == DcMotorSimple.Direction.REVERSE) {
            double x = r;
            r = l;
            l = x;
        }

        lf.setPower(l);
        lb.setPower(l);
        rf.setPower(r);
        rb.setPower(r);

        telemetry.addData("Dumper Position", dumperServo.getPosition());
        telemetry.addData("Arm position", arm.getCurrentPosition());
        telemetry.addData("Elapsed Time", et.seconds());

        final DcMotorEx armEx = (DcMotorEx) arm;
        if (null != armEx) {
            final PIDCoefficients pid = armEx.getPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
            telemetry.addData("Arm PID", String.format(Locale.US, "%.2f %.2f %.2f", pid.p, pid.i, pid.d));
        }
        telemetry.update();
    }
}
