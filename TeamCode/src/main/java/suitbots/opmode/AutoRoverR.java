package suitbots.opmode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.suitbots.util.Controller;

import java.util.Locale;

@Autonomous(name = "AutoRoverR")
public class AutoRoverR extends AutoBase {

    public DcMotor lf, lb, rf, rb;
    public DcMotor lift;
    private boolean isCraterSide = true;
    private boolean doDoubleSample = true;

    protected void driveAndIntake(int inches) {
        setPower(-1.0, harvester);
        driveInches(inches);
        setPower(0.0, harvester);
    }

    @Override
    public void runOpMode() {


        initialize();

        final Controller c = new Controller(gamepad1);

        int delay = 0;
        while (!isStarted()) {
            c.update();
            announceMinearalPositions();
            telemetry.addData("Time", getRuntime());
            telemetry.addData("Side (a)", isCraterSide ? "Crater" : "Depot");
            telemetry.addData("Delay (up/down)", String.format(Locale.US, "%d sec", delay));
            telemetry.addData("Double Sample? (b)", doDoubleSample ? "Yes" : "No");
            telemetry.update();

            if (c.AOnce()) isCraterSide = !isCraterSide;
            if (c.BOnce()) doDoubleSample = !doDoubleSample;
            if (c.dpadUpOnce()) {
                delay = Math.min(15, delay + 1);
            } else if (c.dpadDownOnce()) {
                delay = Math.max(0, delay - 1);
            }
        }

        final MineralPosition goldMineralPosition = getMineralPosition();

        // drop
        runLiftMotor(184.1);
        sleep(300);
        // drive away from the lander
        driveInches(5);
        // Drop the lift *just a little* so it's ready to go in Teleop.
        runLiftMotorDontWaitForFinish(-60.0);
        sleep(1000 * delay);

        if (isCraterSide) {
            // turn to face the gold mineral
            craterSide(goldMineralPosition);
        } else /* depot side */ {

            depotSide(goldMineralPosition);
        }
        setPower(0.0, lift);
        setMode(DcMotor.RunMode.RUN_USING_ENCODER, lift);
    }

    private void craterSide(MineralPosition goldMineralPosition) {
        //turn to face
        switch (goldMineralPosition) {
            case LEFT:
                turnDegrees(25);
                sleep(100);
                driveInches(5);
                break;
            case RIGHT:
                turnDegrees(-25);
                sleep(100);
                driveInches(5);
                break;
            case CENTER:
            case UNKNOWN:
                break;
        }

        // SAMPLE!!!!11111one
        sleep(200);
        driveAndIntake((goldMineralPosition == MineralPosition.RIGHT) ? 20 : 21);
        sleep(100);
        setPower(-1.0, harvester);

        // back up
        switch (goldMineralPosition) {
            case LEFT:
                driveInches(-10);
                break;
            case RIGHT:
                driveInches(-8);
                break;
            case CENTER:
            case UNKNOWN:
                driveInches(-8);
                break;
        }
        sleep(100);

        // turn to face the wall
        switch (goldMineralPosition) {
            case LEFT:
                turnDegrees(55);
                break;
            case RIGHT:
                turnDegrees(110);
                break;
            case CENTER:
            case UNKNOWN:
                turnDegrees(77);
                break;
        }
        sleep(100);

        setPower(0.0, harvester);

        // drive to the wall
        //turn to face the depot
        switch (goldMineralPosition) {
            case LEFT:
                driveInches(42);
                sleep(100);
                turnDegrees(30);
                break;
            case RIGHT:
                driveInches(55);
                sleep(100);
                turnDegrees(25);
                break;
            case CENTER:
            case UNKNOWN:
                driveInches(45);
                sleep(200);
                turnDegrees(35);
                break;
        }


        //double sample
        if (doDoubleSample) {
            doubleSampleFromCrater(goldMineralPosition);

        } else {
            // ENTER THE DEPOT
            switch (goldMineralPosition) {
                case LEFT:
                    driveInches(35);
                    break;
                case RIGHT:
                    driveInches(45);
                    break;
                case CENTER:
                case UNKNOWN:
                    driveInches(40);
                    break;
            }
        }
        // exactly what it says on the tin
        flingTheTeamMarker();
        sleep(200);

        //drives backwards to park
        driveInches(-64);
        sleep(800);
        turnDegrees(-8);

        runLiftMotor(-105);
    }

    private void doubleSampleFromCrater(MineralPosition goldMineralPosition) {
        //drive close to depot
        driveInches(22);
        //affected by side?
        turnDegrees(-45);
        //drive up to get inline with particle position
        driveInches(1);//RIGHT
        switch (goldMineralPosition) {
            case LEFT:
                driveInches(1);
                break;
            case RIGHT:
                break;
            case CENTER:
            case UNKNOWN:
                driveInches(1);
                break;
        }
        //turn to face mineral
        turnDegrees(90);

        //sample mineral
        driveInches(25);

        //turn to face and drive to depot
        switch (goldMineralPosition) {
            case LEFT:
                turnDegrees(45);
                driveInches(20);
                break;
            case RIGHT:
                turnDegrees(-45);
                driveInches(20);
                break;
            case CENTER:
            case UNKNOWN:
                break;
        }
        //turn correction
        switch (goldMineralPosition) {
            case LEFT:
                turnDegrees(90);
                break;
            case RIGHT:
                break;
            case CENTER:
            case UNKNOWN:
                turnDegrees(45);
                break;
        }
    }

    private void depotSide(MineralPosition goldMineralPosition) {
        //turn to face minerals and drive close to them
        switch (goldMineralPosition) {
            case LEFT:
                turnDegrees(25);
                sleep(100);
                driveInches(8);
                break;
            case RIGHT:
                turnDegrees(-25);
                sleep(100);
                driveInches(8);
                break;
            case CENTER:
            case UNKNOWN:
                break;
        }

        sleep(200);
        //SAMPLE
        driveAndIntake((goldMineralPosition == MineralPosition.RIGHT) ? 23 : 21);
        sleep(100);
        setPower(-1.0, harvester);

        //turn to face depot
        switch (goldMineralPosition) {
            case LEFT:
                turnDegrees(-50);
                break;
            case RIGHT:
                turnDegrees(55);
                break;
            case CENTER:
            case UNKNOWN:
                break;
        }

        //drive into depot
        driveInches(23);
        setPower(0.0, harvester);
        flingTheTeamMarker();
        sleep(100);
        driveInches(-5);
        sleep(10);
    }
}
