package suitbots.opmode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ControlSystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.suitbots.util.Controller;

import java.util.Locale;

@Autonomous(name = "AutoRoverR")
public class AutoRoverR extends AutoBase {

    public DcMotor lf, lb, rf, rb;
    public DcMotor lift;
    private boolean isCraterSide = true;

    protected void driveAndIntake(int inches) {
        setPower(1.0, harvester);
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
            telemetry.addData("Side (a)", isCraterSide ? "Crater" : "Depot");
            telemetry.addData("Delay (up/down)", String.format(Locale.US, "%d sec", delay));
            telemetry.update();

            if (c.AOnce()) isCraterSide = !isCraterSide;
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

        if (isCraterSide) {

            // drive away from the lander
            driveInches(10);
            sleep(1000 * delay);

            // turn to face the gold mineral
            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 35 : -35);
                sleep(100);
                driveInches(10);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {

            }

            // SAMPLE!!!!11111one
            sleep(200);
            driveAndIntake((goldMineralPosition == MineralPosition.RIGHT) ? 45 : 41);
            sleep(100);

            // back up
            switch (goldMineralPosition) {
                case LEFT:
                    driveInches(-20);
                    break;
                case RIGHT:
                    driveInches(-25);
                    break;
                case CENTER:
                case UNKNOWN:
                    driveInches(-20);
                    break;
            }
            sleep(100);

            // turn to face the wall
            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 55 : 125);
            }
            else if(goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN ){
                turnDegrees(90);
            }
            sleep(100);

            // drive to the wall
            if (goldMineralPosition == MineralPosition.RIGHT) {
                driveInches(102);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {
                driveInches(89);
            } else if (goldMineralPosition == MineralPosition.LEFT) {
                driveInches(70);
            }

            // turn to face the depot
            turnDegrees(25);

            // ENTER THE DEPOT
            driveInches(71);

            // exactly what it says on the tin
            flingTheTeamMarker();

            sleep(100);

            // drives backwards to park
            driveInches(-115);


        } else /* depot side */ {

            driveInches(10);
            sleep(1000 * delay);

            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 35 : -35);
                sleep(100);
                driveInches(10);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {

            }

            sleep(200);
            driveAndIntake((goldMineralPosition == MineralPosition.RIGHT) ? 45 : 41);
            sleep(100);

            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? -50 : 55);
            }

            driveInches(45);
            flingTheTeamMarker();
            sleep(100);
            driveInches(-5);
            sleep( 10);
        }
    }
}
