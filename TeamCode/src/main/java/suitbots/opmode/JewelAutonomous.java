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

    // Make sure you take alliance in to account! If you're blue, "left"
    // is the close column. If you're red it's the other way around.
    public static final double COLUMN_ADJUST = .3;
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

        // Here's the VuMark itself
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
        // This is the drive that you want to move based on the VuMark.
        // There's a method up above where you can do that.
        driveDirectionTiles(forwardDir(), 1.65 + adjustDriveDistance(target), 0.5);
        turnRad(redAlliance  ? (Math.PI / 2.0) : (Math.PI / 2.2));
        driveDirectionTiles(0, .35, .5);
        throwGlyph(1000, .6, -.8);
        robot.release();

        driveDirectionTiles(0, .1, .5);
        driveDirectionTiles(Math.PI, .3, .5);
        driveDirectionTiles(0, .3, .5);
        driveDirectionTiles(Math.PI, .25, .5);
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
