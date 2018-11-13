package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.suitbots.util.Controller;

@TeleOp(name = "Lift Encoder")
public class EncoderTest extends OpMode {
    private DcMotor lift;
    private Controller c;

    @Override
    public void init() {
        lift = hardwareMap.dcMotor.get("lift");
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        c = new Controller(gamepad1);
    }

    @Override
    public void loop() {
        c.update();
        telemetry.addData("lift encoder", lift.getCurrentPosition());
        telemetry.update();

        if (c.XOnce()) {
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        lift.setPower(c.left_trigger - c.right_trigger);

    }
}
