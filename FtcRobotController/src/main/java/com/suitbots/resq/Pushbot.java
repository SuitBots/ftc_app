package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Pushbot extends OpMode {
    private DcMotor left, right;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        left.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        left.setPower(0.0);
        right.setPower(0.0);
    }

    private double clamp(double x) {
        return Math.min(1.0, Math.max(-1.0, x));
    }

    @Override
    public void loop() {
        left.setPower(clamp(- gamepad1.left_stick_y));
        right.setPower(clamp(-gamepad1.right_stick_y));
    }
}
