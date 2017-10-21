package suitbots.sensor;


import com.qualcomm.robotcore.hardware.AnalogInput;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import suitbots.math.Stats;

public class FlexSensor {
    private static final int BASELINE_BUFFER_SIZE = 128;
    private static final int CURRENT_BUFFER_SIZE = 32;

    private final AnalogInput inputDevice;
    private double[] baseline;
    private double baselineMemo = Double.NaN;
    private double baselineStddev;
    private double[] current;
    private int bi, ci;
    private double changeThreshold;

    private int triggeredCount = 0;


    public FlexSensor(AnalogInput _input, final double _changeThreshold) {
        inputDevice = _input;
        changeThreshold = _changeThreshold;
        baseline = new double[BASELINE_BUFFER_SIZE];
        current = new double[CURRENT_BUFFER_SIZE];
    }

    public boolean hasBaseline() {
        return bi > baseline.length;
    }

    private boolean hasCurrent() {
        return ci > current.length;
    }

    private void setupAfterInit() {
        if (Double.isNaN(baselineMemo)) {
            baselineMemo = Stats.mean(baseline);
        }
    }

    public double voltage() {
        return inputDevice.getVoltage();
    }

    public void init_loop() {
        baseline[bi++ % baseline.length] = voltage();
    }

    public void loop() {
        setupAfterInit();
        current[ci++ % current.length] = voltage();
        if (hasCurrent()) {
            final double c = Stats.mean(current);
            if (changeThreshold < Math.abs(1.0 - c / baselineMemo)) {
                ++triggeredCount;
            } else {
                triggeredCount = 0;
            }
        }
    }

    public boolean triggered() {
        return 0 < triggeredCount;
    }

    public boolean triggeredOnce() {
        return 1 == triggeredCount;
    }

    public void dump(Telemetry t) {
        t.addData("Baseline", baselineMemo);
        t.addData("Current Ratio", Stats.mean(current) / baselineMemo);
        t.addData("Current Noise", Stats.stddev(current));
    }
}
