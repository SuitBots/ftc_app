package com.suitbots.kittenaround;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Isaac 5's hardware definition.
 *
 * Override this class to add a new OpMode on to Isaac 5. You'll have access to all of
 * Isaac's hardware and convienence methods. If you write a custom start() or stop()
 * method, be sure to call Isaac5Basic's start() or stop() via super.
 */
public abstract class Isaac5 extends OpMode {
    private DcMotor drive_left_front = null;
    private DcMotor drive_left_back = null;
    private DcMotor drive_right_front = null;
    private DcMotor drive_right_back = null;

    // stop everything!
    protected void allStop() {
        setDriveMotorPowers(0.0, 0.0);
    }

    @Override
    public void start() {
        drive_left_front = hardwareMap.dcMotor.get("drivelf");
        drive_left_back = hardwareMap.dcMotor.get("drivelb");
        drive_right_front = hardwareMap.dcMotor.get("driverf");
        drive_right_back = hardwareMap.dcMotor.get("driverb");

        // philosophy note: fail hard and fast if your hardware universe isn't what you expect.
        assert null != drive_left_front;
        assert null != drive_left_back;
        assert null != drive_right_front;
        assert null != drive_right_back;

        drive_right_front.setDirection(DcMotor.Direction.REVERSE);
        drive_right_back.setDirection(DcMotor.Direction.REVERSE);

        allStop();
    }

    @Override
    public void stop() {
        allStop();
    }

    // true fact: DcMotor objects will throw an exception if you try to set
    // their power to something outside of [-1.0, 1.0]. Avoid that!
    private float clampMotorSpeed(double x) {
        return (float) Math.max(-.99, Math.min(.99, x));
    }

    protected void setDriveMotorPowers(double left_front, double left_back,
                                       double right_front, double right_back) {
        drive_left_front.setPower(clampMotorSpeed(left_front));
        drive_left_back.setPower(clampMotorSpeed(left_back));
        drive_right_front.setPower(clampMotorSpeed(right_front));
        drive_right_back.setPower(clampMotorSpeed(right_back));
    }

    protected void setDriveMotorPowers(double left, double right) {
        setDriveMotorPowers(left, left, right, right);
    }
}
