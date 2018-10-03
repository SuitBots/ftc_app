package soupbox;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "mecanum", group = "teleop")
public class Mecanum extends OpMode {
    private DcMotor lf, lr, rf, rr;

    @Override
    public void init() {
        lf = hardwareMap.dcMotor.get("lf");
        lr = hardwareMap.dcMotor.get("lr");
        rf = hardwareMap.dcMotor.get("rf");
        rr = hardwareMap.dcMotor.get("rr");
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private static double maxAbs(double... ds) {
        double max = Double.NaN;
        for (final double d : ds) {
            if (Double.isNaN(max)) {
                max = Math.abs(d);
            } else {
                max = Math.max(max, Math.abs(d));
            }
        }
        return max;
    }

    @Override
    public void loop() {
        final double PI4 = Math.PI / 4.0;

        final double x = gamepad1.left_stick_x;
        final double y = - gamepad1.left_stick_y;
        final double r = gamepad1.right_stick_x;

        final double direction = Math.atan2(x, y);
        final double velocity = Math.sqrt(x*x + y*y);

        final double _lf = Math.sin(PI4 + direction) * velocity + r;
        final double _rf = Math.cos(PI4 + direction) * velocity - r;
        final double _lr = Math.cos(PI4 + direction) * velocity + r;
        final double _rr = Math.sin(PI4 + direction) * velocity - r;

        final double max = maxAbs(1.0, _lf, _rf, _lr, _rr);

        lf.setPower(_lf / max);
        lr.setPower(_lr / max);
        rf.setPower(_rf / max);
        rr.setPower(_rr / max);
    }
}
