package soupbox;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "tank", group = "teleop")
public class Tank extends OpMode {
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

    @Override
    public void loop() {
        final double fwd = - gamepad1.left_stick_y;
        final double rot = gamepad1.right_stick_x;

        final double left = fwd + rot;
        final double right = fwd - rot;
        final double max = Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));

        lf.setPower(left / max);
        lr.setPower(left / max);
        rf.setPower(right / max);
        rr.setPower(right / max);
    }
}
