package suitbots.opmode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ControlSystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.suitbots.util.Controller;

@Autonomous(name = "AutoRoverR")
public class AutoRoverR extends AutoBase {

    public DcMotor lf, lb, rf, rb;
    public DcMotor lift;
    private boolean isCraterSide;


    //todo test and fix values
    private int leftAngleAdjust = 45;
    private int rightAngleAdjust = -45;
    private int leftDistanceOffset = 12;
    private int rightDistanceOffset = 12;

    @Override
    public void runOpMode() {

        initialize();

        final Controller c = new Controller(gamepad1);

        while (! isStarted()) {
            c.update();
            announceMinearalPositions();
            telemetry.addData("Side (a)", isCraterSide ? "Crater" : "Depot");
            telemetry.update();

            if (c.AOnce()) isCraterSide = ! isCraterSide;
        }

        final MineralPosition goldMineralPosition = getMineralPosition();


        getRuntime();


        if(isCraterSide) {
            runLiftMotor(177);
            sleep(300);
            driveInches(10);
            if(goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT){
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? -45 : 45);
            }
            else if(goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN){

            }
            sleep(200);
            driveInches(20);
            if(goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT){
                turnDegrees((goldMineralPosition == MineralPosition.LEFT) ? leftAngleAdjust : rightAngleAdjust);
            }
            else if(goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN){

            }
            turnDegrees(90);

            if(goldMineralPosition == MineralPosition.LEFT || goldMineralPosition == MineralPosition.RIGHT){
            driveInches((goldMineralPosition == MineralPosition.LEFT) ? leftDistanceOffset : rightDistanceOffset);
            }
            else if(goldMineralPosition == MineralPosition.CENTER || goldMineralPosition == MineralPosition.UNKNOWN){

            }
            driveInches(36);

        } else {
            runLiftMotor(177);
            sleep(300);
            driveInches(36*Math.sqrt(2));
            flingTheTeamMarker();
            sleep(700);
            driveInches(-5);

        }
    }
}
