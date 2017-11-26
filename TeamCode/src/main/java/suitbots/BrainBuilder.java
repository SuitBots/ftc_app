package suitbots;

import android.content.res.Resources;

import org.firstinspires.ftc.teamcode.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BrainBuilder {
    private static double[] readDoubleArray(final String s) {
        final String[] ss = readRow(s);
        final double[] ret = new double[ss.length];
        for (int i = 0; i < ss.length; ++i) {
            ret[i] = Double.parseDouble(ss[i]);
        }
        return ret;
    }

    private static double[][] readDoubleArrayArray(final String s) {
        final String[][] raw = readRows(s);
        final double[][] ret = new double[raw.length][];
        for (int i = 0; i < raw.length; ++i) {
            final String[] row = raw[i];
            ret[i] = new double[row.length];
            for(int j = 0; j < row.length; ++j) {
                ret[i][j] = Double.parseDouble(row[j]);
            }
        }
        return ret;
    }

    public static int[] readIntArray(final String s) {
        final String[] ss = readRow(s);
        final int[] ret = new int[ss.length];
        for (int i = 0; i < ss.length; ++i) {
            ret[i] = Integer.parseInt(ss[i]);
        }
        return ret;
    }

    public static String[][] readRows(final String s) {
        final String[] ss = s.split("\\}\\s*,\\s*\\{");
        final String[][] ret = new String[ss.length][];
        for (int i = 0; i < ss.length; ++i) {
            ret[i] = readRow(ss[i]);
        }
        return ret;
    }

    public static String[] readRow(final String s) {
        final String[] ss = s.replaceAll("[\\{\\}\\s]", "").split(",");
        return ss;
    }

    public static Brain makeBrain() {
        try {
            final InputStream is = BrainBuilder.class.getClassLoader().getResourceAsStream("res/raw/brain_config.dat");
            if (null == is) {
                throw new RuntimeException("Could not load brain config");
            }
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            final int nClasses = Integer.parseInt(br.readLine().trim());
            final int nRows = Integer.parseInt(br.readLine().trim());
            final String kernel = br.readLine().trim();
            final double gamma = Double.parseDouble(br.readLine().trim());
            final double coef0 = Double.parseDouble(br.readLine().trim());
            final double degree = Double.parseDouble(br.readLine().trim());
            final double[][] vectors = readDoubleArrayArray(br.readLine().trim());
            final double[][] coeffs = readDoubleArrayArray(br.readLine().trim());
            final double[] intercepts = readDoubleArray(br.readLine().trim());
            final int[] weights = readIntArray(br.readLine().trim());
            return new Brain(nClasses, nRows, vectors, coeffs, intercepts, weights,
                    kernel, gamma, coef0, degree);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
