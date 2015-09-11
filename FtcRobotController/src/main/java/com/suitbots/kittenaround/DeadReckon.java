package com.suitbots.kittenaround;

/**
 * Dead Reckoning Autonomous.
 *
 * Drive forward for 5 seconds, turn left for 5 seconds, drive forward for 5 seconds.
 */
public class DeadReckon extends Isaac5i {
    @Override public void start() { time = 0.0; } // probably superfluous
    @Override public void loop() {
        if(time < 5.0)       { driveForward(); }
        else if(time < 10.0) { turnLeft(); }
        else if(time < 15.0) { driveForward(); }
        else                 { allStop(); }
    }
}
