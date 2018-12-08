package suitbots;

import com.qualcomm.robotcore.hardware.Servo;

public class StatefulServo {
    private final Servo servo;
    private double state = Double.NaN;

    public StatefulServo(final Servo s) {
        this.servo = s;
    }

    public void setPosition(double x) {
        if (state != x) {
            servo.setPosition(state = x);
        }
    }

    public double getPosition() {
        return servo.getPosition();
    }
}
