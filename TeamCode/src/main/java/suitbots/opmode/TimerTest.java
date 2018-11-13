package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.suitbots.util.Blinken;
import com.suitbots.util.Controller;

@TeleOp(name = "Teleop Lights")
public class TimerTest extends OpMode {
    private enum Lights {
        RED, BLUE;
    };

    private Blinken blinken;
    private Controller c;
    private Lights lights = Lights.RED;
    private ElapsedTime et;

    @Override
    public void init() {
        blinken = new Blinken(hardwareMap.servo.get("blinken"));
        blinken.enactFixedPalettePatternBeatsPerMinuteRainbowPalette();
        c = new Controller(gamepad1);
        et = new ElapsedTime();
    }

    @Override
    public void start() {
        super.start();
        et.reset();
    }

    private void setColor() {
        if (et.seconds() < 90.0) {
            if (Lights.RED == lights) {
                blinken.enactSolidRed();
            } else {
                blinken.enactSolidBlue();
            }
        } else {
            if (Lights.RED == lights) {
                blinken.enactFixedPalettePatternBreathRed();
            } else {
                blinken.enactFixedPalettePatternBreathBlue();
            }
        }
    }

    @Override
    public void loop() {
        c.update();
        if (c.XOnce()) {
            lights = (Lights.RED == lights) ? Lights.BLUE : Lights.RED;
        }
        setColor();
    }
}
