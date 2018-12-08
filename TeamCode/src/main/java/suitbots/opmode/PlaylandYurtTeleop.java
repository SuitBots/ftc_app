package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.suitbots.util.Controller;

import suitbots.StatefulServo;

import static java.lang.Math.abs;
import static java.lang.Math.max;

@TeleOp(name = "TELEOP", group = "Tourney")
public class PlaylandYurtTeleop extends OpMode {
    private DcMotor lf, lb, rf, rb;
    private DcMotor harvester;
    private DcMotor lift;
    private StatefulServo dumper;

    private DcMotorSimple.Direction driveDirection = DcMotorSimple.Direction.FORWARD;

    private Controller g1, g2;

    @Override
    public void init() {
        lf = hardwareMap.dcMotor.get("lf");
        lb = hardwareMap.dcMotor.get("lb");
        rf = hardwareMap.dcMotor.get("rf");
        rb = hardwareMap.dcMotor.get("rb");
        lift = hardwareMap.dcMotor.get("lift");
        dumper = new StatefulServo(hardwareMap.servo.get("dumper"));
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



    @Override
    public void loop() {
        g1.update();
        g2.update();

        harvester.setPower(g1.right_trigger - g1.left_trigger);
        lift.setPower(g2.right_trigger - g2.left_trigger);

        if (g1.rightBumper()) {
            dumper.setPosition(.5);
        } else {
            dumper.setPosition(1.0);
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
        telemetry.update();
    }
}
