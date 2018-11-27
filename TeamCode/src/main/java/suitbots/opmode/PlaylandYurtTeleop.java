package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.suitbots.util.Controller;

import static java.lang.Math.abs;
import static java.lang.Math.max;

@TeleOp(name = "Playland Yurt", group = "Tourney")
public class PlaylandYurtTeleop extends OpMode {
    private DcMotor lf, lb, rf, rb;
    private DcMotor harvester;
    private DcMotor lift;
    private DcMotor dumper;

    private DcMotorSimple.Direction driveDirection = DcMotorSimple.Direction.FORWARD;

    private Controller g1, g2;

    @Override
    public void init() {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        lift = hardwareMap.dcMotor.get("lift");
        dumper = hardwareMap.dcMotor.get("dumper");
        harvester = hardwareMap.dcMotor.get("harvester");

        rb.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);

        // dumper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        dumper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);

        dumper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        dumper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        dumper.setPower(1.0);
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
            dumper.setTargetPosition(DUMPER_UP);
            dumper.setPower(1.0);
        } else if (g1.dpadUpOnce()) {
            dumper.setTargetPosition(DUMPER_DUMP);
            dumper.setPower(1.0);
        } else if (g1.dpadDownOnce()) {
            dumper.setTargetPosition(DUMPER_DOWN);
            dumper.setPower(1.0);
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

        telemetry.addData("Dumper Position", dumper.getCurrentPosition());
        telemetry.addData("Dumper Target", dumper.getTargetPosition());
        telemetry.addData("Dumper Mode", dumper.getMode());
        telemetry.addData("Dumper Power", dumper.getPower());
        telemetry.update();
    }
}
