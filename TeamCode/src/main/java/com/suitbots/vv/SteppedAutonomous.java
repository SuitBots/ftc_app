package com.suitbots.vv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class SteppedAutonomous extends AutonomousBase {
    private List<Step> steps = new ArrayList<>();

    protected void setSteps(Step[] ss) {
        steps.clear();
        appendSteps(ss);
    }

    protected void appendSteps(Step[] ss) {
        for (Step s : ss) {
            appendStep(s);
        }
    }

    protected void appendStep(Step s) {
        steps.add(s);
    }

    abstract class Step {
        private String name;
        public Step(String n) { name = n; }
        public String getName() { return name; }

        abstract public void act() throws InterruptedException;
    }


    protected void runDebugMode() throws InterruptedException {
        int i = 0;
        double t0 = getRuntime(), t1 = getRuntime();
        String prev = "None";
        Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            c.update();
            if (c.dpadUpOnce()) {
                i++;
            } else if (c.dpadDownOnce()) {
                i--;
            } else if (c.leftBumperOnce()) {
                robot.stopDriveMotors();
                t0 = getRuntime();
                steps.get(i++).act();
                t1 = getRuntime();
            } else {
                // We don't need dpad mode here, and we don't want to throw off the
                // autonomous by moving a little bit after a mode switch
                if (! (c.dpadDown() || c.dpadUp() || c.dpadLeft() || c.dpadRight())) {
                    DriveHelper.drive(c, robot);
                }
            }
            if (i < 0) {
                i = steps.size() - 1;
            } else if (i >= steps.size()) {
                i = 0;
            }

            telemetry.addData("Current", steps.get(i).getName());
            telemetry.addData("Previous", String.format(Locale.US, "%s: %.2f", prev, t1 - t0));
            telemetry.update();

        }
    }

    protected void runAutoMode() throws InterruptedException {
        double t0 = getRuntime();
        double begin = 50;
        String prev = "None";
        for(Step s : steps) {
            if (! opModeIsActive()) {
                break;
            }
            telemetry.addData("Last", String.format(Locale.US, "%s: %.2f", prev, getRuntime() - t0));
            prev = s.getName();
            t0 = getRuntime();
            telemetry.addData("Phase", prev);
            telemetry.update();
            s.act();
        }
        telemetry.addData("Last", String.format(Locale.US, "%s: %.2f", prev, getRuntime() - t0));
        telemetry.addData("Phase", "Done");
        telemetry.addData("Total", getRuntime() - begin);
        telemetry.update();
    }
}
