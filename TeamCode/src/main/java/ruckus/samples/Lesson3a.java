package ruckus.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import ruckus.AutoBase;

@Autonomous(name= "Turn 90 Left")
public class Lesson3a extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap);
        waitForStart();

        turn(-90.0);
    }
}
