package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Vision Test", group = "Test")
public class VisionTest extends OpMode {
    private VisionTargets vision;
    private Controller g1;
    private BeaconFinder finder;

    @Override
    public void init() {
        vision = new VisionTargets();
        g1 = new Controller(gamepad1);
        finder = new BeaconFinder(null, vision, telemetry);
    }

    @Override
    public void start() {
        vision.activate();
    }

    @Override
    public void stop() {
        vision.deactivate();
    }

    @Override
    public void loop() {
        g1.update();
        telemetry.addData("X offset", vision.getXOffset());
        telemetry.addData("Y offset", vision.getYOffset());
        telemetry.addData("Orientation", vision.getOrientation());
        telemetry.addData("Beacon Status", finder.loop(true));
        telemetry.update();
    }
}
