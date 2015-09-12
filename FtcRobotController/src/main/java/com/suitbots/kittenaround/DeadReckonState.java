package com.suitbots.kittenaround;

/**
 * Created by cp on 9/11/15.
 */
public class DeadReckonState extends Isaac5i {
    enum State {
        START,
        DRIVING_FORWARD_ORIG,
        TURNING_LEFT,
        DRIVING_FORWARD_FINAL,
        STOP
    }
    State state = START;

    @Override public void loop() {
        switch(state) {
            case START:
                time = .0;
                state = State.DRIVING_FORWARD_ORIG;
                break;
            case DRIVING_FORWARD_ORIG:
                driveForward();
                if (5.0 < time) {
                    state = State.TURNING_LEFT;
                    time = .0;
                }
                break;
            case TURNING_LEFT:
                turnLeft();
                if (5.0 < time) {
                    state = State.DRIVING_FORWARD_FINAL;
                    time = .0;
                }
                break;
            case DRIVING_FORWARD_FINAL:
                driveForward();
                if (5.0 < time) {
                    state = State.STOP;
                }
                break;
            case STOP:
                allStop();
                break;
        }
    }
}
