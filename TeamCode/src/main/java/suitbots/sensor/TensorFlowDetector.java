package suitbots.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

import suitbots.VuforiaKey;
import suitbots.opmode.AutoBase;

public class TensorFlowDetector {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    public static final String WEBCAM_NAME = "Webcam 1";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private TensorFlowDetector(final HardwareMap hardwareMap) {
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
    }

    public VuforiaLocalizer getLocalizer() {
        return vuforia;
    }

    public AutoBase.MineralPosition detect() {
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
            if (updatedRecognitions.size() == 3) {
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
                if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                    if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                        return AutoBase.MineralPosition.LEFT;
                    } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                        return AutoBase.MineralPosition.RIGHT;
                    } else {
                        return AutoBase.MineralPosition.CENTER;
                    }
                }
            }
        }
        return AutoBase.MineralPosition.UNKNOWN;
    }

    public static TensorFlowDetector make(final HardwareMap hardwareMap) {
        if (null == hardwareMap.tryGet(WebcamName.class, WEBCAM_NAME)) {
            return null;
        } else {
            return new TensorFlowDetector(hardwareMap);
        }
    }
}
