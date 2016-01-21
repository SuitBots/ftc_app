package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * A teleop for Isaac 5'
 */
public class IsaacPrime extends OpMode {
    private DcMotor l1, l2, r1, r2;

    private void setMotorSpeeds(double l, double r) {
        l1.setPower(l);
        l2.setPower(l);
        r1.setPower(r);
        r2.setPower(r);
    }

    private void stopMotors() {
        setMotorSpeeds(0.0, 0.0);
    }

    @Override
    public void stop() {
        stopMotors();
    }

    @Override
    public void start() {

    }

    @Override
    public void init() {
        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");

        l1.setDirection(DcMotor.Direction.REVERSE);
        l2.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void loop() {
        setMotorSpeeds(gamepad1.left_stick_y, gamepad1.right_stick_y);
    }
}
