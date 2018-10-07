package ruckus.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import ruckus.AutoBase;

@Autonomous(name= "Drive One Tile")
public class Lesson2 extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap);
        waitForStart();

        driveDistance(24, .25);
    }
}
