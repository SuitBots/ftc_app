import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "TELEOP")
public class BasicTeleop extends OpMode {
    MrRobot robot;

    @Override
    public void init() {
        robot = new MrRobot(hardwareMap);
    }

    @Override
    public void loop() {
        double left = gamepad1.left_stick_y;
        double right = gamepad1.right_stick_y;

        robot.drive(left, right);
    }
}
