package com.suitbots.vv;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

public class VisionTargets {
    private VuforiaLocalizer vuforia;
    private VuforiaTrackable red1, red2, blue1, blue2;
    private VuforiaTrackables FTC2016;

    private static final float mmPerInch        = 25.4f;
    private static final float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels
    private static float MAX_DISTANCE_CM = mmFTCFieldWidth / 10f;

    private List<VuforiaTrackable> targets = new ArrayList<VuforiaTrackable>();

    public VisionTargets() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(com.qualcomm.ftcrobotcontroller.R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AXax5///////AAAAGbN9kxkbgkIGtHCm6mYR5yldmTbfnZz3bO9SI/KzKjU+rf9FoudDFqj6CaXe2ZRR/FrfJbufcb0PjwE5Fv/7XmV7t7nTpUUmNMj/AM85QGN8ammim64AOTpRbemUwBkyVkZ9yROtZDykH/hECBvAciuXCBdLF2XFHOMzgtnbbKlp1+pvgTs5sPYAAvR40cR5PztLdjKDZOTEhuhryqguwbhX6xr3H7ylNRaT+CEM38h1tik1SxFaKi4TkyefAKlx3xLG1zRWG95jEn8LtmnYqLoThJy/Wu8LeTFg5cedj0GiIQBSxkV9kvyCvMjyZZNc0yr1XJc7nez7dZRC6kDJg9W4RgtaGrE1DYTRU7/xW55Y";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        FTC2016 = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        blue1 = FTC2016.get(0);
        blue1.setName("wheels"); // blue 1

        red2 = FTC2016.get(1);
        red2.setName("tools"); // red 2

        blue2 = FTC2016.get(2);
        blue2.setName("legos"); // blue 2

        red1 = FTC2016.get(3);
        red1.setName("gears"); // red 1

        targets.addAll(FTC2016);


        final OpenGLMatrix red1Loc = OpenGLMatrix
                /* Then we translate the target off to the RED WALL. Our translation here
                is a negative translation in X.*/
                .translation(-mmFTCFieldWidth/2, -12 * mmPerInch, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        red1.setLocation(red1Loc);

        final OpenGLMatrix red2Loc = OpenGLMatrix
                /* Then we translate the target off to the RED WALL. Our translation here
                is a negative translation in X.*/
                .translation(-mmFTCFieldWidth/2, 36 * mmPerInch, 0)
                .multiplied(Orientation.getRotationMatrix(
                        /* First, in the fixed (field) coordinate system, we rotate 90deg in X, then 90 in Z */
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        red2.setLocation(red2Loc);

        // And we'll just put the red ones in the same place as the blue ones, because
        // we only care about location and orientation relative to the targets
        blue1.setLocation(red1Loc);
        blue2.setLocation(red2Loc);

        // We're not going to worry about robot position so much as we are about distance
        // between the phone and the vision target. Assume that the phone is in the middle
        // of the robot and that we don't care about its orientation.
        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix.identityMatrix();

        for(VuforiaTrackable target: targets) {
            ((VuforiaTrackableDefaultListener)target.getListener())
                    .setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        }
    }

    public void activate() {
        FTC2016.activate();
    }

    public void deactivate() {
        FTC2016.deactivate();
    }

    public boolean canSeeWall() {
        return visible(red1, red2, blue1, blue2);
    }


    private boolean visible(VuforiaTrackable... ts) {
        for (VuforiaTrackable t : ts) {
            if (((VuforiaTrackableDefaultListener)t.getListener()).isVisible()) {
                return true;
            }
        }
        return false;
    }

    private float distanceToTargetAlongComponentCM(VuforiaTrackable t, int component) {
        VuforiaTrackableDefaultListener l = (VuforiaTrackableDefaultListener) t.getListener();
        if (! l.isVisible()) {
            return mmFTCFieldWidth / 10.f;
        }
        VectorF robot = l.getRobotLocation().getTranslation();
        VectorF beacon = t.getLocation().getTranslation();
        VectorF diff = robot.subtracted(beacon);

        return diff.get(component) / 10.0f;
    }

    private float distanceXCM(VuforiaTrackable t) {
        return distanceToTargetAlongComponentCM(t, 0);
    }
    private float distanceYCM(VuforiaTrackable t) {
        return distanceToTargetAlongComponentCM(t, 1);
    }


    public float getXOffset() {
        for(VuforiaTrackable t : targets) {
            if (visible(t)) {
                return distanceXCM(t);
            }
        }
        return MAX_DISTANCE_CM;
    }

    public float getYOffset() {
        for (VuforiaTrackable t : targets) {
            if (visible(t)) {
                return distanceYCM(t);
            }
        }
        return MAX_DISTANCE_CM;
    }

    protected float orientation(VuforiaTrackable t) {
        if (visible(t)) {
            VuforiaTrackableDefaultListener l = (VuforiaTrackableDefaultListener) t.getListener();
            OpenGLMatrix matrix = l.getRobotLocation();

            Orientation orientation = Orientation.getOrientation(matrix,
                    AxesReference.EXTRINSIC,
                    AxesOrder.XYZ,
                    AngleUnit.DEGREES);

            return orientation.thirdAngle;
        }

        return -361f;
    }

    public float getOrientation() {
        for (VuforiaTrackable t : targets) {
            if (visible(t)) {
                return (90f + orientation(t)) % 360f;
            }
        }
        return -361f;
    }
}
