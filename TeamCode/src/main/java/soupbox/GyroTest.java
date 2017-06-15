package soupbox;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp(name = "Gyro Test")
public class GyroTest extends OpMode {
    private Robot robot;
    private Controller g1;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, telemetry);
        g1 = new Controller(gamepad1);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Gyro Ready?", robot.isGyroCalibrated() ? "YES" : "no.");
        telemetry.update();
    }

    @Override
    public void loop() {
        robot.loop();
        g1.update();
        telemetry.addData("Gyro Degrees", Math.toDegrees(robot.getHeading()));
        telemetry.addData("Gyro RAD", robot.getHeading());
        if (g1.XOnce()) {
            robot.resetHeading();
        }
    }
}
