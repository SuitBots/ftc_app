package com.suitbots.vv;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Suit Bots on 11/22/2016.
 */

public class ToggleableServo {
    private Servo servo;
    double a, b;
    boolean at_a = true;

    public ToggleableServo(Servo s, double _a, double _b) {
        servo = s;
        a = _a;
        b = _b;
    }

    public void onStart() {
        servo.setPosition(a);
        at_a = true;
    }

    public void toggle() {
        servo.setPosition(at_a ? b : a);
        at_a = ! at_a;
    }

    public void set(double p) {
        servo.setPosition(p);
    }

    public void setFirst(boolean x) {
        if (x != at_a) {
            toggle();
        }
    }
}
