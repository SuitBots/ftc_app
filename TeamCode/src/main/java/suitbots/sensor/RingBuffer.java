package suitbots.sensor;

public class RingBuffer {
    private double[] buf;
    private int count = 0;

    public RingBuffer(final int size) {
        buf = new double[size];
    }

    public void add(final double d) {
        buf[count++ % buf.length] = d;
    }

    public double mean() {
        double tot = 0.0;
        for(int i = 0; i < Math.min(count, buf.length); ++i) {
            tot += buf[i];
        }
        return tot / Math.min(count, buf.length);
    }
}
