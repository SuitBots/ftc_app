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
        // drive away from the lander
        driveInches(10);
        // Drop the lift *just a little* so it's ready to go in Teleop.
        runLiftMotorAsync(-60.0);
        sleep(1000 * delay);

        if (isCraterSide) {
            // turn to face the gold mineral
            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 25 : -30);
                sleep(100);
                driveInches(10);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {

            }

            // SAMPLE!!!!11111one
            sleep(200);
            driveAndIntake((goldMineralPosition == MineralPosition.RIGHT) ? 45 : 41);
            sleep(100);
            setPower(-1.0, harvester);

            // back up
            switch (goldMineralPosition) {
                case LEFT:
                    driveInches(-20);
                    break;
                case RIGHT:
                    driveInches(-22);
                    break;
                case CENTER:
                case UNKNOWN:
                    driveInches(-16);
                    break;
            }
            sleep(100);

            // turn to face the wall
            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 55 : 115);
            }
            else if(goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN ){
                turnDegrees(85);
            }
            sleep(100);

            setPower(0.0, harvester);

            // drive to the wall
            if (goldMineralPosition == MineralPosition.RIGHT) {
                driveInches(105);
                sleep(10);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {
                driveInches(93);
            } else if (goldMineralPosition == MineralPosition.LEFT) {
                driveInches(72);
                sleep(100);
            }


            // turn to face the depot
            turnDegrees(30);

            // ENTER THE DEPOT
            driveInches(71);

            // exactly what it says on the tin
            flingTheTeamMarker();
            sleep(200);

            //drives backwards to park
            driveInches(-125);
            sleep(2000);

            //lower lift
            runLiftMotor(-95);




        } else /* depot side */ {

            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 25 : -25);
                sleep(100);
                driveInches(15);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {

            }

            sleep(200);
            driveAndIntake((goldMineralPosition == MineralPosition.RIGHT) ? 45 : 41);
            sleep(100);
            setPower(-1.0, harvester);

            if (goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? -50 : 55);
            }

            driveInches(45);
            setPower(0.0, harvester);
            flingTheTeamMarker();
            sleep(100);
            driveInches(-5);
            sleep( 10);
        }
    }
}
