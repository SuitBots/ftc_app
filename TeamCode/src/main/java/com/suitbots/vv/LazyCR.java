package com.suitbots.vv;

import com.qualcomm.robotcore.hardware.CRServo;

public class LazyCR {
    private CRServo servo;

    public LazyCR(CRServo s) {
        servo = s;
        s.setPower(0.0);
    }

    public void stop() {
        setPower(0.0);
    }

    private static final double epsilon = 0.01;
    public void setPower(double p) {
        if (epsilon < Math.abs(servo.getPower() - p)) {
            servo.setPower(p);
        }
    }
}
