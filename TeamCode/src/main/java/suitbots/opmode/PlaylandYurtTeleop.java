package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.suitbots.util.Controller;

import suitbots.ConfigVars;
import suitbots.StatefulServo;

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
        final double percent = Math.abs((double) pos / (double) ConfigVars.TELEOP_ARM_UP);

        dumperServo.setPosition(1.0 - Math.min(1.0, Math.pow(percent, 2.0)));
    }

    @Override
    public void loop() {
        g1.update();
        g2.update();

        harvester.setPower(g1.right_trigger - g1.left_trigger);
        lift.setPower(g2.right_trigger - g2.left_trigger);

        if (g1.dpadUp()) {
            arm.setTargetPosition(ConfigVars.TELEOP_ARM_UP);
            arm.setPower(.3);
        } else if (g1.dpadDownOnce()) {
            arm.setTargetPosition(ConfigVars.TELEOP_ARM_DOWN);
            arm.setPower(.3);
        } else {
            if (ConfigVars.TELEOP_ARM_UP == arm.getTargetPosition() &&
                    .7 < Math.abs((float)arm.getCurrentPosition() / (float)ConfigVars.TELEOP_ARM_UP)) {
                arm.setPower(.1);
            }
        }

        if (g1.AOnce()) {
            flopDirection();
        }

        if (g2.A()) {
            dumperServo.setPosition(1.0);
        } else if (g2.B()) {
            dumperServo.setPosition(0.0);
        } else
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
        telemetry.update();
    }
}
