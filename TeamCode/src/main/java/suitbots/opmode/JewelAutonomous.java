package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.vuforia.EyewearUserCalibrator;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import suitbots.AutoBase;
import suitbots.VisionTargets;

/**
 * Created by Suit Bots on 11/11/2017.
 */

@Autonomous(name = "AUTONOMOUS", group = "Tournament")
public class JewelAutonomous extends AutoBase {

    private enum Column {
        LEFT, CENTER, RIGHT, WHATEVER
    }

    private boolean redAlliance = true;
    private boolean nearPlatform = true;
    private int doubleMajorMode = 0;
    private Column targetColumn = Column.CENTER;
    private RelicRecoveryVuMark target;

    private static final int DOUBLE_MAJOR_MODE_THRESHOLD = 5;

    // This is the number of tiles that we drive after the jewel
    // to line up with the center column. Change this if the center column
    // is way off from the rest of them.
    public static final double NEAR_PLATFORM_BASE_DISTANCE = 1.5;

    // Make sure you take alliance in to account! If you're blue, "left"
    // is the close column. If you're red it's the other way around.
    // change this if you're wide on both left and right (decrease it)
    // or if you always hit center (increase)
    public static final double NEAR_PLATFORM_COLUMN_ADJUST = .35;
    protected double nearPlatformAdjustDriveDistance(final RelicRecoveryVuMark v) {
        if (RelicRecoveryVuMark.LEFT == v) {
            if(redAlliance){
                return NEAR_PLATFORM_COLUMN_ADJUST;
            }
            return -NEAR_PLATFORM_COLUMN_ADJUST;

        } else if (RelicRecoveryVuMark.RIGHT == v) {
            if (redAlliance) {
                return -NEAR_PLATFORM_COLUMN_ADJUST;
            }
            return NEAR_PLATFORM_COLUMN_ADJUST;
        }
        return 0.0;
    }

    public static final double FAR_PLATFORM_BASE_DISTANCE = 0.8;
    public static final double FAR_PLATFORM_COLUMN_ADJUST = .5;
    protected double farPlatformAdjustDriveDistance(final RelicRecoveryVuMark v) {
        if(RelicRecoveryVuMark.LEFT == v){
            if(redAlliance){
                return FAR_PLATFORM_COLUMN_ADJUST;
            }
            return -FAR_PLATFORM_COLUMN_ADJUST;
        }else if(RelicRecoveryVuMark.RIGHT == v){
            if(redAlliance){
                return -FAR_PLATFORM_COLUMN_ADJUST;
            }
            return FAR_PLATFORM_COLUMN_ADJUST;
        }
        return 0.0;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap, telemetry);
        final VisionTargets vt = new VisionTargets();
        vt.initFrontCamera(this);

        whileNotStarted(vt);
        target = doSensorOnAStickStuff(vt);
        depositGlyph();
        doubleMajorPenaltyMode();
    }

    private void lowerLift() {
        robot.setLiftIndex(0);
    }

    private void depositGlyph() throws InterruptedException {
        driveToCryptoboxColumn();
        dumpGlyph();
        rotateAfterDumpingGlyph();
    }

    private void driveToCryptoboxColumn() throws InterruptedException {
        if (nearPlatform) {
            nearPlatformCryptoboxDrive();
        } else {
            farPlatformCryptoboxDrive();
        }
        robot.resetGyro();
    }

    private void farPlatformCryptoboxDrive() throws InterruptedException {
        driveDirectionTiles(forwardDir(), 1.2, .25, 2.0);
        turnToAngleRad(0.0);
        // This is the drive that you want to move based on the VuMark.
        // There's a method up above where you can do that.
        turnRad(redAlliance ? Math.PI : 0.0);
        driveDirectionTiles(redAlliance ? (3.0 * Math.PI / 2.0) : Math.PI / 2.0,
                FAR_PLATFORM_BASE_DISTANCE + farPlatformAdjustDriveDistance(target), .5, 2.5);
    }

    private void nearPlatformCryptoboxDrive() throws InterruptedException {
        driveDirectionTiles(forwardDir(),
                NEAR_PLATFORM_BASE_DISTANCE + nearPlatformAdjustDriveDistance(target),
                0.4, 2.5);
        turnToAngleRad(Math.PI / 2.0);
    }

    private void rotateAfterDumpingGlyph() throws InterruptedException {
        if (nearPlatform) {
            turnToAngleRad(Math.PI);
        } else if(redAlliance) {
            turnRad((Math.PI)/2);
        } else {
            turnRad((3*Math.PI)/2);
        }
    }

    private void dumpGlyph() throws InterruptedException {
        // @todo is this the same for near and far?
        driveDirectionTiles(0, .25, 0.5, 1.5);
        throwGlyph();
        sleep(500);
        robot.releaseSlow();
        driveDirectionTiles(0, .25, .5, 1.0);
        driveDirectionTiles(Math.PI, .4, 0.7,1.0);
        robot.stoparms();
    }

    private RelicRecoveryVuMark doSensorOnAStickStuff(VisionTargets vt) {
        // Here's the VuMark itself
        final RelicRecoveryVuMark target = vt.getCurrentVuMark();
        vt.close();

        robot.setSwing();
        robot.putDownSoas();

        sleep(2000);

        int identifier = robot.detectJewelColour();
        final boolean jewelIsRed = 1 == identifier;


        if (jewelIsRed == redAlliance) {
            robot.swingForward();
        } else {
            robot.swingBack();
        }


        sleep(750);

        robot.putUpSoas();
        robot.setSwing();
        return target;
    }

    private Column fromVuMark(RelicRecoveryVuMark vm) {
        if (RelicRecoveryVuMark.LEFT.equals(vm)) {
            return Column.LEFT;
        } else if (RelicRecoveryVuMark.RIGHT.equals(vm)) {
            return Column.RIGHT;
        } else {
            return Column.CENTER;
        }
    }

    private int fromColumn(final Column c) {
        switch (c) {
            case LEFT:
                return 2;
            case CENTER:
                return 1;
            case RIGHT:
                return 0;
        }
        return 0;
    }

    private int traverse() {
        int dest = fromColumn(targetColumn);
        int src = fromColumn(fromVuMark(target));
        return dest - src;
    }

    private double happyGlyphMagnitude() {
        final int t = Math.abs(traverse());
        switch (t) {
            case 2:
                return .6;
            case 1:
                return .4;
            default:
                return 0;
        }
    }

    private double happyGlyphDirection() {
        if (traverse() > 0) {
            return Math.PI / 2.0;
        } else {
            return 3.0 * Math.PI / 2.0;
        }
    }


    private void doubleMajorPenaltyMode() throws InterruptedException {
        // @todo What needs to change here for the far platform?
        if (DOUBLE_MAJOR_MODE_THRESHOLD <= doubleMajorMode) {
            if (nearPlatform) {
                robot.collect();
                telemetry.addData("DMM Direction", happyGlyphDirection());
                telemetry.addData("DMM Magnitude", happyGlyphMagnitude());
                telemetry.addData("DMM Traverse", traverse());
                telemetry.update();
                driveDirectionTiles(happyGlyphDirection(),
                        happyGlyphMagnitude(),
                        .25, 2.0);
                driveDirectionTiles(0.0, 1.5, .5, 2);
                sleep(500);
                robot.stoparms();
                robot.setLiftIndex(2);
                driveDirectionTiles(Math.PI, 1.0, .5, 1.5);
                turnToAngleRad(0.0);
                driveDirectionTiles(0, .6, .45, 2.0);
                throwGlyph();
                robot.stoparms();
                robot.releaseSlow();
                driveDirectionTiles(Math.PI, .3, .4, 1.0);
                driveDirectionTiles(0, .3, .4, 1.0);
                driveDirectionTiles(Math.PI, .3, .4, 1.0);
                robot.setLiftIndex(0);
            }
        }
        lowerLift();
    }

    private void adjustGlyphTarget(int direction) {
        if (0 < direction) {
            if (Column.WHATEVER == targetColumn) {
                targetColumn = Column.LEFT;
            } else if (Column.LEFT == targetColumn) {
                targetColumn = Column.CENTER;
            } else if (Column.CENTER == targetColumn) {
                targetColumn = Column.RIGHT;
            } else {
                targetColumn = Column.WHATEVER;
            }
        } else if (0 > direction){
            if (Column.WHATEVER == targetColumn) {
                targetColumn = Column.RIGHT;
            } else if (Column.LEFT == targetColumn) {
                targetColumn = Column.WHATEVER;
            } else if (Column.CENTER == targetColumn) {
                targetColumn = Column.LEFT;
            } else {
                targetColumn = Column.CENTER;
            }
        }
    }

    private void whileNotStarted(VisionTargets vt) {
        while (! isStarted()) {
            vt.loop();
            c.update();
            if (c.AOnce()) redAlliance = ! redAlliance;
            if (c.BOnce()) nearPlatform = ! nearPlatform;
            if (c.dpadUpOnce()) doubleMajorMode++;
            if (c.dpadDownOnce()) doubleMajorMode--;
            if (c.dpadLeftOnce()) adjustGlyphTarget(1);
            if (c.dpadRightOnce()) adjustGlyphTarget(-1);

            telemetry.addData("Alliance (a)", redAlliance ? "RED" : "BLUE");
            telemetry.addData("POSITION (b)", nearPlatform ? "NEAR" : "FAR");
            telemetry.addData("Time", getRuntime());
            telemetry.addData("Vision", vt.getCurrentVuMark());
            telemetry.addData("Alignment", Math.toDegrees(vt.getRotationZ()));
            if (nearPlatform) {
                telemetry.addData("Double Major (u/d)", DOUBLE_MAJOR_MODE_THRESHOLD > doubleMajorMode
                        ? String.format("%d", DOUBLE_MAJOR_MODE_THRESHOLD - doubleMajorMode) : "HOLY CRAP");
                telemetry.addData("Target (l/r)", targetColumn);
            }
            telemetry.update();

        }

        robot.resetEncoders();
        robot.resetGyro();
    }

    private void throwGlyph() {
        throwGlyph(500, .25, -.25);
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
