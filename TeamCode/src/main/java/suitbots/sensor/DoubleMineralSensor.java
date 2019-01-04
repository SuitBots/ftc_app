package suitbots.sensor;

import com.qualcomm.robotcore.hardware.AnalogInput;

public class DoubleMineralSensor {
    private RingBuffer startSizeBuf;
    private double startSize = 0.0;
    private AnalogInput fsr;

    public DoubleMineralSensor(final AnalogInput _fsr) {
        fsr = _fsr;
        startSizeBuf = new RingBuffer(100);
    }

    public void init_loop() {
        startSizeBuf.add(fsr.getVoltage());
    }

    public void start() {
        startSize = startSizeBuf.mean();
    }

    public boolean triggered() {
        return Math.abs(fsr.getVoltage()) > Math.abs(startSize * 1.01);
    }
}
