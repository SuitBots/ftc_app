package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.suitbots.util.Blinken;
import com.suitbots.util.Controller;

import org.firstinspires.ftc.robotcore.external.Func;

import static java.lang.Math.max;
import static java.lang.Math.abs;

@TeleOp(name = "SCREAMAGE")
public class Screamage extends OpMode {
    private DcMotor lf, lb, rf, rb, lift;

    private boolean servoActivated = false;

    private Controller g1;

    private DcMotorSimple.Direction driveDirection = DcMotorSimple.Direction.FORWARD;

    @Override
    public void init() {

        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        lift = hardwareMap.dcMotor.get("lift");

        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        rb.setDirection(DcMotorSimple.Direction.REVERSE);

        g1 = new Controller(gamepad1);

        telemetry.addData("Direction", new Func<DcMotorSimple.Direction>() {
            @Override
            public DcMotorSimple.Direction value() {
                return driveDirection;
            }
        });

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


    @Override
    public void loop() {
        g1.update();

        lift.setPower(gamepad1.right_trigger - gamepad1.left_trigger);

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

        telemetry.update();
    }
}
