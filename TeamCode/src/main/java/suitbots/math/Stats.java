package suitbots.math;

public class Stats {
    public static double mean(final double[] xs) {
        double sum = 0.0;
        for (final double x : xs) {
            sum += x;
        }
        return sum / xs.length;
    }

    public static double stddev(final double[] xs) {
        final double mean = mean(xs);
        double sum = 0.0;
        for (final double x : xs) {
            sum += Math.pow(x - mean, 2.0);
        }
        return Math.sqrt(sum / xs.length);
    }
}
