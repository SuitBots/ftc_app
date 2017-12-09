package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.suitbots.util.Controller;
import com.vuforia.VuMarkTarget;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import suitbots.AutoBase;
import suitbots.VisionTargets;

/**
 * Created by Suit Bots on 11/11/2017.
 */

@Autonomous(name = "AUTONOMOUS", group = "Tournament")
public class JewelAutonomous extends AutoBase {
    boolean redAlliance = true;

    // This is the number of tiles that we drive after the jewel
    // to line up with the center column. Change this if the center column
    // is way off from the rest of them.
    public static final double BASE_DISTANCE = 1.75;

    // Make sure you take alliance in to account! If you're blue, "left"
    // is the close column. If you're red it's the other way around.
    // change this if you're wide on both left and right (decrease it)
    // or if you always hit center (increase)
    public static final double COLUMN_ADJUST = .35;
    protected double adjustDriveDistance(final RelicRecoveryVuMark v) {
        if (RelicRecoveryVuMark.LEFT == v) {
            if(redAlliance){
                return COLUMN_ADJUST;
            }
            return -COLUMN_ADJUST;

        } else if (RelicRecoveryVuMark.RIGHT == v) {
            if(redAlliance){
                return -COLUMN_ADJUST;
            }
            return COLUMN_ADJUST;
        }
        return 0.0;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap, telemetry);
        final VisionTargets vt = new VisionTargets();
        vt.initFrontCamera(this);

        while (! isStarted()) {
            vt.loop();
            c.update();
            if (c.AOnce()) redAlliance = ! redAlliance;
            telemetry.addData("Alliance (a)", redAlliance ? "RED" : "BLUE");
            telemetry.addData("Time", getRuntime());
            telemetry.addData("Vision", vt.getCurrentVuMark());
            telemetry.update();
        }

        // Here's the VuMark itself
        final RelicRecoveryVuMark target = vt.getCurrentVuMark();
        vt.close();

        robot.setSwing();
        sleep(500);

        robot.putDownSoas();
        //robot.grabBlock();
        sleep(2000);

        int identifier = robot.detectJewelColour();
        final boolean jewelIsRed = 1 == identifier;


        if (jewelIsRed  == redAlliance) {
            robot.swingForward();
        }else{
            robot.swingBack();
        }

        sleep(500);

        robot.putUpSoas();
        // This is the drive that you want to move based on the VuMark.
        // There's a method up above where you can do that.
//        driveDirectionTiles(forwardDir(), BASE_DISTANCE + adjustDriveDistance(target), 0.5);
//
//        turnRad((Math.PI / 2.0));
//
//        throwGlyph();
//        robot.release();
//        driveDirectionTiles(0, .5, .75);
//        driveDirectionTiles(Math.PI, .5, .5);
//
//        // MORE GLYPHZ?
//        turnRad(Math.PI);
//        robot.collect();
//        driveDirectionTiles(0, 1.2, .75);
//        sleep(1000);
//        driveDirectionTiles(Math.PI, .5, 1);
//        turnRad(Math.PI);
//        robot.stoparms();
//        driveDirectionTiles(0, .5, 1);
//        throwGlyph();
//        robot.release();
//        driveDirectionTiles(0, .5, .75);
//        driveDirectionTiles(Math.PI, .3, .5);

        robot.stopDriveMotors();
    }

    private void throwGlyph() {
        throwGlyph(300, 0, 1);
    }

    private void throwGlyph(final long time, final double leftPower, final double rightPower) {
        robot.setArmMotors(leftPower, rightPower);
        sleep(time);
        robot.stoparms();
    }

    @Override
    protected double forwardDir() {
        return redAlliance ? Math.PI : 0.0;
    }
}
