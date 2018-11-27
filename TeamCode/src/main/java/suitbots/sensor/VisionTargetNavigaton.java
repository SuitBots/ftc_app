package suitbots.sensor;

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

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class VisionTargetNavigaton {
    final private VuforiaLocalizer vuforia;

    private static final float mmPerInch        = 25.4f;
    private static final float mmFTCFieldWidth  = (12*6) * mmPerInch;       // the width of the FTC field (from the center point to the outer panels)
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private VuforiaTrackables targetsRoverRuckus;
    List<VuforiaTrackable> allTrackables;

    public VisionTargetNavigaton(final VuforiaLocalizer _vuforia) {
        vuforia = _vuforia;

        targetsRoverRuckus = this.vuforia.loadTrackablesFromAsset("RoverRuckus");
        VuforiaTrackable blueRover = targetsRoverRuckus.get(0);
        blueRover.setName("Blue-Rover");
        VuforiaTrackable redFootprint = targetsRoverRuckus.get(1);
        redFootprint.setName("Red-Footprint");
        VuforiaTrackable frontCraters = targetsRoverRuckus.get(2);
        frontCraters.setName("Front-Craters");
        VuforiaTrackable backSpace = targetsRoverRuckus.get(3);
        backSpace.setName("Back-Space");

        allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsRoverRuckus);

        OpenGLMatrix blueRoverLocationOnField = OpenGLMatrix
                .translation(0, mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));
        blueRover.setLocation(blueRoverLocationOnField);


        OpenGLMatrix redFootprintLocationOnField = OpenGLMatrix
                .translation(0, -mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));
        redFootprint.setLocation(redFootprintLocationOnField);

        OpenGLMatrix frontCratersLocationOnField = OpenGLMatrix
                .translation(-mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90));
        frontCraters.setLocation(frontCratersLocationOnField);

        OpenGLMatrix backSpaceLocationOnField = OpenGLMatrix
                .translation(mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
        backSpace.setLocation(backSpaceLocationOnField);

        final int CAMERA_FORWARD_DISPLACEMENT  = 110;   // eg: Camera is 110 mm in front of robot center
        final int CAMERA_VERTICAL_DISPLACEMENT = 200;   // eg: Camera is 200 mm above ground
        final int CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        90, 0, 0));

        for (VuforiaTrackable trackable : allTrackables)
        {
            ((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, FRONT);
        }

        targetsRoverRuckus.activate();
    }


    private float distanceToTargetAlongComponentInches(VuforiaTrackable t, int component) {
        VuforiaTrackableDefaultListener l = (VuforiaTrackableDefaultListener) t.getListener();
        if (! l.isVisible()) {
            return mmFTCFieldWidth / 25.4f;
        }
        VectorF robot = l.getRobotLocation().getTranslation();
        VectorF beacon = t.getLocation().getTranslation();
        VectorF diff = robot.subtracted(beacon);

        return diff.get(component) / 25.4f;
    }

    private boolean visible(VuforiaTrackable... ts) {
        for (VuforiaTrackable t : ts) {
            if (((VuforiaTrackableDefaultListener)t.getListener()).isVisible()) {
                return true;
            }
        }
        return false;
    }

    protected Orientation orientation(VuforiaTrackable t) {
        if (visible(t)) {
            VuforiaTrackableDefaultListener l = (VuforiaTrackableDefaultListener) t.getListener();
            OpenGLMatrix matrix = l.getRobotLocation();

            Orientation orientation = Orientation.getOrientation(matrix,
                    AxesReference.EXTRINSIC,
                    AxesOrder.XYZ,
                    AngleUnit.DEGREES);

            return orientation;
        }

        return null;
    }

    private float distanceXInches(VuforiaTrackable t) {
        return distanceToTargetAlongComponentInches(t, 0);
    }
    private float distanceYInches(VuforiaTrackable t) {
        return distanceToTargetAlongComponentInches(t, 1);
    }

    public float distanceFromTargetInches() {
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                return max(abs(distanceXInches(trackable)), abs(distanceYInches(trackable)));
            }
        }
        return Float.NaN;
    }

    public Orientation orientationToTarget() {
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                return orientation(trackable);
            }
        }
        return new Orientation();
    }

    public float angleToTarget() {
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                return orientation(trackable).thirdAngle;
            }
        }
        return Float.NaN;
    }

    public boolean canSeeTarget() {
        for (VuforiaTrackable trackable : allTrackables) {
            if (visible(trackable)) {
                return true;
            }
        }
        return false;
    }
}
