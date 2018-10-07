package ruckus.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import ruckus.AutoBase;

@Autonomous(name= "Drive Three Seconds")
public class Lesson1 extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap);
        waitForStart();

        driveTime(3.0, .25, .25);
    }
}
