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

    @Override
    public void runOpMode() throws InterruptedException {
        Controller c = new Controller(gamepad1);
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

        final RelicRecoveryVuMark target = vt.getCurrentVuMark();
        vt.close();

        robot.putDownSoas();
        //robot.grabBlock();
        sleep(1000);

        int identifier = robot.detectJewelColour();
        final boolean jewelIsRed = 1 == identifier;

        if (jewelIsRed == redAlliance) {
            knockForward();
        }else{
            knockBackward();
        }

        robot.putUpSoas();
        // This is the drive that you want to move based on the VuMark
        driveDirectionTiles(forwardDir(), 1.75, 0.5);
        turnRad(Math.PI / 2.0);
        throwGlyph(300, .4, -.6);
        driveDirectionTiles(0, .25, .5);
        robot.release();
        driveDirectionTiles(0, .25, .5);
        driveDirectionTiles(Math.PI, .3, .5);
        robot.stopDriveMotors();

        jumpToTeleop();
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
