package suitbots;

import com.qualcomm.robotcore.hardware.Servo;

public class StatefulServo {
    private final Servo servo;
    private double pos = Double.NaN;

    public StatefulServo(final Servo s) {
        servo = s;
    }

    public void setPosition(final double x) {
        if ((!Double.isNaN(x)) && x != pos) {
            servo.setPosition(pos = x);
        }
    }

    public double getPosition() {
        return servo.getPosition();
    }
}
