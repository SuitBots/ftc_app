package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.suitbots.util.Controller;

import suitbots.Robot;
import suitbots.VisionTargets;

@TeleOp(name = "Fill your eyes....")
public class SampleVision extends OpMode {
    private VisionTargets vt;

    @Override
    public void init() {
        vt = new VisionTargets();
        vt.initFrontCamera(this);
    }

    @Override
    public void loop() {
        vt.loop();
        if (vt.canSeeVisionTarget()) {
            telemetry.addData("Vision", vt.getCurrentVuMark());
            telemetry.addData("TX", vt.getTranslationX());
            telemetry.addData("TY", vt.getTranslationY());
            telemetry.addData("TZ", vt.getTranslationZ());
            telemetry.addData("RX", Math.toDegrees(vt.getRotationX()));
            telemetry.addData("RY", Math.toDegrees(vt.getRotationY()));
            telemetry.addData("RZ", Math.toDegrees(vt.getRotationZ()));
        } else {
            telemetry.clear();
            telemetry.addData("Vision", "Zero");
        }
        telemetry.update();
    }
}
