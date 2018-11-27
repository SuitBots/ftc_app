package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.util.Locale;

import suitbots.VuforiaKey;
import suitbots.opmode.AutoBase;
import suitbots.sensor.VisionTargetNavigaton;

@TeleOp(name = "Distance and Angle", group = "Single")
public class DistanceAndAngle extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VuforiaKey.VUFORIA_KEY;
        parameters.cameraDirection   = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        final VisionTargetNavigaton nav = new VisionTargetNavigaton(vuforia);

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Visible", nav.canSeeTarget());
            telemetry.addData("Distance", String.format(Locale.US, "%.2f inches", nav.distanceFromTargetInches()));
            final Orientation o = nav.orientationToTarget();
            telemetry.addData("Angle", String.format(Locale.US, "%.2f %.2f %.2f",
                    o.firstAngle, o.secondAngle, o.thirdAngle));
            telemetry.update();
        }
    }
}
