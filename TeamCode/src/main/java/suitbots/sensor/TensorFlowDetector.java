package suitbots.sensor;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;
import java.util.Locale;

import suitbots.VuforiaKey;
import suitbots.opmode.AutoBase;

public class TensorFlowDetector {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    public static final String WEBCAM_NAME = "Webcam 1";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private Telemetry telemetry;

    private TensorFlowDetector(final HardwareMap hardwareMap, final Telemetry _telemetry) {
        telemetry = _telemetry;
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VuforiaKey.VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, WEBCAM_NAME);

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);

        tfod.activate();
    }

    public VuforiaLocalizer getLocalizer() {
        return vuforia;
    }

    private List<Recognition> lastReco;

    private List<Recognition> getRecos() {
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (null != updatedRecognitions) {
            lastReco = updatedRecognitions;
        }
        return lastReco;
    }

    public AutoBase.MineralPosition detect() {
        List<Recognition> updatedRecognitions = getRecos();
        if (updatedRecognitions != null) {
            int goldMineralX = -1;
            int silverMineral1X = -1;
            int silverMineral2X = -1;
            for (Recognition recognition : updatedRecognitions) {
                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    goldMineralX = (int) recognition.getLeft();
                } else if (silverMineral1X == -1) {
                    silverMineral1X = (int) recognition.getLeft();
                } else {
                    silverMineral2X = (int) recognition.getLeft();
                }
            }


            telemetry.addData("G/S/S", String.format(Locale.US, "%d %d %d", goldMineralX, silverMineral1X, silverMineral2X));

            if (-1 == goldMineralX) {
                return AutoBase.MineralPosition.RIGHT;
            } else {
                final int silverMineralX = Math.max(silverMineral1X, silverMineral2X);
                if (goldMineralX > silverMineralX) {
                    return AutoBase.MineralPosition.CENTER;
                } else {
                    return AutoBase.MineralPosition.LEFT;
                }
            }
        } else {
            telemetry.addData("TF Data", "Nope");
        }
        return AutoBase.MineralPosition.UNKNOWN;
    }

    public static TensorFlowDetector make(final HardwareMap hardwareMap, final Telemetry t) {
        if (null == hardwareMap.tryGet(WebcamName.class, WEBCAM_NAME)) {
            return null;
        } else {
            return new TensorFlowDetector(hardwareMap, t);
        }
    }
}
