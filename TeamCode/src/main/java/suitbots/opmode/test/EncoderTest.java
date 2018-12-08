package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.suitbots.util.Controller;

import static java.lang.Math.abs;
import static java.lang.Math.max;

@TeleOp(name = "EncoderTest")
public class EncoderTest extends OpMode {
    private DcMotor lf, lb, rf, rb;
    private DcMotor harvester;
    private DcMotor lift;
    private Servo dumper;

    private DcMotorSimple.Direction driveDirection = DcMotorSimple.Direction.FORWARD;

    private Controller g1, g2;

    @Override
    public void init() {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        lift = hardwareMap.dcMotor.get("lift");
        dumper = hardwareMap.servo.get("dumper");
        harvester = hardwareMap.dcMotor.get("harvester");

        rb.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);

        // dumper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



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

    private final int DUMPER_UP = 80;
    private final int DUMPER_DUMP = 125;
    private final int DUMPER_DOWN = 0;

    @Override
    public void loop() {
        g1.update();
        g2.update();

        harvester.setPower(g1.right_trigger - g1.left_trigger);
        lift.setPower(g2.right_trigger - g2.left_trigger);

        if (g1.dpadLeftOnce()) {
            dumper.setPosition(DUMPER_UP);
        } else if (g1.dpadUpOnce()) {
            dumper.setPosition(DUMPER_DUMP);
        } else if (g1.dpadDownOnce()) {
            dumper.setPosition(DUMPER_DOWN);
        }

        if (g1.AOnce()) {
            flopDirection();
        }

        final double fwd  = gamepad1.left_stick_y;
        final double turn = - gamepad1.right_stick_x;
        final double max = max(1.0, abs(fwd) + abs(turn));
        final double speed = gamepad1.left_stick_button ? 1.0 : 0.35;

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

        telemetry.addData("Dumper Position", dumper.getPosition());
        telemetry.addData("lf position", lf.getCurrentPosition());
        telemetry.addData("lb position", lb.getCurrentPosition());
        telemetry.addData("rf position", rf.getCurrentPosition());
        telemetry.addData("rb position", rb.getCurrentPosition());
        telemetry.addData("lift position", lift.getCurrentPosition());
        telemetry.update();
    }
}