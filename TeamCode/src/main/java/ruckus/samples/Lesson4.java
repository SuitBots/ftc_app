package ruckus.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import ruckus.AutoBase;

@Autonomous(name= "Drive Square")
public class Lesson4 extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap);
        waitForStart();

        for(int i = 0; i < 4; ++i) {
            driveDistance(24, .25);
            turn(90.0);
        }
    }
}
