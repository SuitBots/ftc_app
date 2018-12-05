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
    private boolean isCraterSide;


    @Override
    public void runOpMode() {

        initialize();

        final Controller c = new Controller(gamepad1);

        int delay = 0;
        while (! isStarted()) {
            c.update();
            announceMinearalPositions();
            telemetry.addData("Side (a)", isCraterSide ? "Crater" : "Depot");
            telemetry.addData("Delay (up/down)", String.format(Locale.US, "%d sec", delay));
            telemetry.update();

            if (c.AOnce()) isCraterSide = ! isCraterSide;
            if (c.dpadUpOnce()) {
                delay = Math.max(15, 0 + delay);
            } else if (c.dpadDownOnce()) {
                delay = Math.min(0, delay - 1);
            }
        }
        sleep(1000 * delay);

        final MineralPosition goldMineralPosition = getMineralPosition();


        getRuntime();

        runLiftMotor(184);
        sleep(300);

        if(isCraterSide) {

            driveInches(10);

            if(goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT) {
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? 35 : -35);
                sleep(100);
                driveInches(10);
            }
            else if(goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN){

            }

            sleep(200);
            driveInches((goldMineralPosition == MineralPosition.RIGHT) ? 43 : 39);
            sleep(100);

            if (goldMineralPosition == MineralPosition.RIGHT) {
                driveInches(-23);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {
                driveInches(-18);
            } else if (goldMineralPosition == MineralPosition.LEFT) {
                driveInches(-20);
            }

            if(goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT){
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? -35 : 35);
            }
            sleep(100);
            turnDegrees(90);

            if (goldMineralPosition == MineralPosition.RIGHT) {
                driveInches(101);
            } else if (goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN) {
                driveInches(89);
            } else if (goldMineralPosition == MineralPosition.LEFT) {
                driveInches(73);
            }

           turnDegrees(45);
           driveInches(76);
           flingTheTeamMarker();
           sleep(700);
           //driveInches(-50);


        } else /* depot side */{
            driveInches(90);
            flingTheTeamMarker();
            sleep(700);
            driveInches(-5);

        }
    }
}
