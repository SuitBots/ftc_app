package suitbots.opmode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.suitbots.util.Controller;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

public class DepotAuto extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();

        double delay = 0.0;
        final Controller c = new Controller(gamepad1);

        while (! isStarted()) {
            c.update();

            announceMinearalPositions();
            telemetry.addData("Delay (dpad u/d)", delay);
            telemetry.update();

            if (c.dpadDownOnce()) {
                delay = max(0.0, delay - 1.0);
            } else if (c.dpadUpOnce()) {
                delay = min(15.0, delay + 1.0);
            }
        }

        final ElapsedTime et = new ElapsedTime();
        et.reset();
        while (et.seconds() < delay) {
            announceMinearalPositions();
            telemetry.update();
        }

        final MineralPosition pos = getMineralPosition();


        runLiftMotor(170.0);
        // move away from the lander
        driveInches(6);

        final double SAMPLE_TURN_ANGLE = 20;
        final double FINAL_TURN_ANGLE = -90;

        // turn to face the gold mineral
        switch (pos) {
            case LEFT:
                turnDegrees(-SAMPLE_TURN_ANGLE);
                break;
            case RIGHT:
                turnDegrees(SAMPLE_TURN_ANGLE);
                break;
        }

        // sample
        driveInches(12);
        driveInches(-12);

        // turn to face the far wall
        switch (pos) {
            case LEFT:
                turnDegrees(FINAL_TURN_ANGLE + SAMPLE_TURN_ANGLE);
                break;
            case RIGHT:
                turnDegrees(FINAL_TURN_ANGLE - SAMPLE_TURN_ANGLE);
                break;
            case UNKNOWN:
            case CENTER:
                turnDegrees(FINAL_TURN_ANGLE);
        }

        // drive towards the wall
        driveInches(24.0 * sqrt(2));

        // square up to the wall
        // todo: do this based on the vuforia target
        turnDegrees(45);

        // drive up to the wall
        driveInches(distanceToTarget(12.0f) - 2.0);
        turnDegrees(90.0);
        driveInches(48.0);
        dumpTeamMarker();

        // todo: this, if we're feeling brave
        // driveInches(-72.0);
        // even better: just drive back 48 inches and then reach with an arm to break the plane
    }

}
