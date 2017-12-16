package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import suitbots.AutoBase;
import suitbots.VisionTargets;

/**
 * Created by Suit Bots on 11/11/2017.
 */

@Autonomous(name = "AUTONOMOUS", group = "Tournament")
public class JewelAutonomous extends AutoBase {
    private boolean redAlliance = true;
    private boolean nearPlatform = true;
    private int doubleMajorMode = 0;

    private static final int DOUBLE_MAJOR_MODE_THRESHOLD = 5;

    // This is the number of tiles that we drive after the jewel
    // to line up with the center column. Change this if the center column
    // is way off from the rest of them.
    public static final double BASE_DISTANCE = .75;

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
            // ¢if (c.BOnce()) nearPlatform = ! nearPlatform;
            if (c.dpadUpOnce()) doubleMajorMode++;
            if (c.dpadDownOnce()) doubleMajorMode--;

            telemetry.addData("Alliance (a)", redAlliance ? "RED" : "BLUE");
            // telemetry.addData("POSITION (b)", nearPlatform ? "CLOSE" : "FAR");
            telemetry.addData("Time", getRuntime());
            telemetry.addData("Vision", vt.getCurrentVuMark());
            telemetry.addData("Double Major (u/d)", DOUBLE_MAJOR_MODE_THRESHOLD > doubleMajorMode
                    ? String.format("%d", doubleMajorMode) : "HOLY CRAP");
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


        if (jewelIsRed == redAlliance) {
            robot.swingForward();
        } else {
            robot.swingBack();
        }

        sleep(500);

        robot.putUpSoas();
        robot.setSwing();

        driveDirectionTiles(forwardDir(), 1.0, .35);

        // make sure we're still aligned coming off the balancing stone
        turnToAngleRad(0);

        if (nearPlatform) {
            // This is the drive that you want to move based on the VuMark.
            // There's a method up above where you can do that.
            driveDirectionTiles(forwardDir(), BASE_DISTANCE + adjustDriveDistance(target), 0.5);
            turnRad((Math.PI / 2.0));
        } else {
            // This is the drive that you want to move based on the VuMark.
            // There's a method up above where you can do that.
            if (redAlliance) {
                turnRad(Math.PI);
                driveDirectionTiles(Math.PI / 2, BASE_DISTANCE + adjustDriveDistance(target), 0.5);
            } else {
                turnRad(0);
                driveDirectionTiles((Math.PI * 3) / 2, BASE_DISTANCE + adjustDriveDistance(target), 0.5);
            }
        }


        robot.resetGyro();
        throwGlyph();
        robot.release();
        driveDirectionTiles(0, .5, 1.0, 1.5);
        driveDirectionTiles(Math.PI, .5, 1.0);
        turnToAngleRad(Math.PI);

        if (DOUBLE_MAJOR_MODE_THRESHOLD <= doubleMajorMode) {
            robot.collect();
            driveDirectionTiles(0.0, 1.0, 1.0, 2.5);
            sleep(1000);
            driveDirectionTiles(Math.PI, 1.0, 1.0, 1.5);
            extraGlyphStrafe(target);
            turnToAngleRad(0.0);
            driveDirectionTiles(0, .75, 1., 1.0);
            throwGlyph();
            robot.release();
            driveDirectionTiles(0, .25, 1.0, 1.5);
            driveDirectionTiles(Math.PI, .5, .5);
            turnRad(Math.PI);
        }
        robot.stopDriveMotors();

    }



    public static final double EXTRA_STRAFE = 1.0;
    protected void extraGlyphStrafe(final RelicRecoveryVuMark v) throws InterruptedException {
        if (RelicRecoveryVuMark.LEFT == v) {
            driveDirectionTiles(3.0 * Math.PI/2.0, EXTRA_STRAFE, 1.0, 1.0);
        } else {
            driveDirectionTiles(Math.PI/2.0, EXTRA_STRAFE, 1.0, 1.0);
        }
    }

    private void throwGlyph() {
        throwGlyph(500, .5, -.5);
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
