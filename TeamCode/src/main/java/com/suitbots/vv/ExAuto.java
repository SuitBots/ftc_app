package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

//@Autonomous(name = "thing1(for driving seconds)")

public abstract class ExAuto extends AutonomusBaseTwo {

    protected abstract boolean IsRED();

    @Autonomous (name = "ExAuto RED")
    public static class Red extends ExAuto {
        protected boolean IsRED() { return true; }
    }

    @Autonomous (name = "ExAuto BLUE")
    public static class Blue extends ExAuto {
        protected boolean IsRED() { return false; }
    }


    public void runOpMode() throws InterruptedException {
        initialize();
        while(! isStarted()) {
            telemetry.addData("Ready", isGyroCalibrating() ? "no" : "*** YES ***");
            telemetry.update();
        }
        if (IsRED()){
            turnRobot(90);
        }
        if (IsRED()){
            turnRobot(-90);
        }
    }
}
