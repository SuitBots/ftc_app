package suitbots;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

public class VisionTargets {
    private static final String LICENSE_KEY = "AXax5///////AAAAGbN9kxkbgkIGtHCm6mYR5yldmTbfnZz3" +
            "bO9SI/KzKjU+rf9FoudDFqj6CaXe2ZRR/FrfJbufcb0PjwE5Fv/7XmV7t7nTpUUmNMj/AM85QGN8ammim" +
            "64AOTpRbemUwBkyVkZ9yROtZDykH/hECBvAciuXCBdLF2XFHOMzgtnbbKlp1+pvgTs5sPYAAvR40cR5Pz" +
            "tLdjKDZOTEhuhryqguwbhX6xr3H7ylNRaT+CEM38h1tik1SxFaKi4TkyefAKlx3xLG1zRWG95jEn8Ltmn" +
            "YqLoThJy/Wu8LeTFg5cedj0GiIQBSxkV9kvyCvMjyZZNc0yr1XJc7nez7dZRC6kDJg9W4RgtaGrE1DYTRU7/xW55Y";

    private VuforiaLocalizer vuforia;
    private VuforiaTrackables relicTrackables;
    private VuforiaTrackable relicTemplate;
    private RelicRecoveryVuMark vuMark;
    private OpenGLMatrix pose;
    private VectorF trans;
    private Orientation rot;

    public void initFrontCamera(final OpMode opMode) { init(opMode, true); }
    public void initBackCamera(final OpMode opMode) { init(opMode, false); }

    private void init(final OpMode opMode, final boolean frontCamera) {
        int cameraMonitorViewId = opMode.hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", opMode.hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = LICENSE_KEY;
        parameters.cameraDirection = frontCamera ?
                VuforiaLocalizer.CameraDirection.FRONT : VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();
    }

    public void loop() {
        vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
            pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
            if (null != pose) {
                trans = pose.getTranslation();
                rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
            } else {
                trans = null;
                rot = null;
            }
        } else {
            pose = null;
            trans = null;
            rot = null;
        }
    }

    public boolean canSeeVisionTarget() {
        return null != vuMark && RelicRecoveryVuMark.UNKNOWN != vuMark;
    }
    public RelicRecoveryVuMark getCurrentVuMark() { return vuMark; }

    public double getTranslationX() { return null == trans ? 0.0 : trans.get(0); }
    public double getTranslationY() { return null == trans ? 0.0 : trans.get(1); }
    public double getTranslationZ() { return null == trans ? 0.0 : trans.get(2); }
    public double getRotationX() { return null == rot ? 0.0 : rot.firstAngle; }
    public double getRotationY() { return null == rot ? 0.0 : rot.secondAngle; }
    public double getRotationZ() { return null == rot ? 0.0 : rot.thirdAngle; }


}
