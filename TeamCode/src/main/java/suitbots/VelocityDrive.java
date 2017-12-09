package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.suitbots.util.Controller;
import com.vuforia.VuMarkTarget;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

import suitbots.AutoBase;

@Autonomous(name = "Velocity ThAng", group = "Tournament")
public class VelocityDrive extends AutoBase {
    @Override
    protected double forwardDir() {
        return 0;
    }

    boolean redAlliance = true;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap, telemetry);
        waitForStart();

        double maxVelocity = 0;
        telemetry.addData("Velocity: ", getVelocity());
        do{
            robot.drive(0, .5, 0);
            maxVelocity = maxVelocity(maxVelocity,getVelocity());
            //wait(500);
            telemetry.addData("Max Velocity", maxVelocity);
            telemetry.addData("Runtime", getRuntime());
            telemetry.update();
        } while(checkVelocity(maxVelocity,getVelocity()));

        robot.stopDriveMotors();
    }

}
