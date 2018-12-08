package suitbots;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PID {
    public interface RuntimeProvider {
        public double getCurrentTime();
    }

    private final String name;
    private final RuntimeProvider runtimeProvider;
    private final FtcDashboard dashboard = FtcDashboard.getInstance();

    private double kp, ki, kd;
    private double setpoint;
    private double lastTime = 0.0;
    private double sampleTimeInSeconds = 0.1;
    private double output = 0.0;
    private double lastInput = Double.NaN;
    private double ITerm = 0.0;
    private double outMin = -1.0, outMax = 1.0;

    public void setSetpoint(final double x) {
        setpoint = x;
    }

    public void setTunings(final double kp, final double ki, final double kd) {
        this.kp = kp;
        this.ki = ki * sampleTimeInSeconds;
        this.kd = kd / sampleTimeInSeconds;
    }

    public void setSampleTimeInSeconds(final double x) {
        if (0 < x) {
            final double ratio = x / sampleTimeInSeconds;
            ki *= ratio;
            kd /= ratio;
            sampleTimeInSeconds = x;
        }
    }

    public void setMinMax(final double _min, final double _max) {
        outMax = _max;
        outMin = _min;
        output = clamp(output);
        ITerm = clamp(ITerm);
    }

    public void begin() {
        lastTime = runtimeProvider.getCurrentTime();
    }

    public PID(final String name, final RuntimeProvider rp) {
        this.name = name;
        runtimeProvider = rp;
    }

    private double clamp(final double x) {
        return min(max(outMin, x), outMax);
    }

    public double compute(final double input) {
        final double t = runtimeProvider.getCurrentTime();
        final double dt = t - lastTime;
        if (Double.isNaN(lastInput)) {
            lastInput = input;
        }
        if (dt > sampleTimeInSeconds) {
            final TelemetryPacket packet = new TelemetryPacket();

            final double error = setpoint - input;
            ITerm += ki * error;

            ITerm = clamp(ITerm);

            final double dInput = input - lastInput;

            lastInput = input;
            lastTime = t;

            output = clamp(kp * error + ki * ITerm + kd * dInput);

            packet.put(name + ".error", error);
            packet.put(name + ".ITerm", ITerm);
            packet.put(name + ".output", output);
            packet.put(name + ".dInput", dInput);
            packet.put(name + ".time", t);
            dashboard.sendTelemetryPacket(packet);
        }

        return output;
    }
}
