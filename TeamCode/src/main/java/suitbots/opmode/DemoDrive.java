package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.suitbots.util.Controller;

/**
 * Created by Jonah on 2/20/18.
 */

@Disabled
@TeleOp(name = "DemoDrive")
public class DemoDrive extends OpMode {

    private Controller g1, g2;
    private DcMotor leftMotor, rightMotor;

    @Override
    public void init() {
        leftMotor = hardwareMap.dcMotor.get("leftMotor");
        rightMotor = hardwareMap.dcMotor.get("rightMotor");

        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);
    }


    @Override
    public void loop() {
        double runtime = System.currentTimeMillis();

        g1.update();
        g2.update();
        double left = g1.left_stick_y;
        double right = g1.right_stick_y;

        leftMotor.setPower(left);
        rightMotor.setPower(-right);

        telemetry.addData("runtime:", runtime);
        telemetry.addData("left power:", left);
        telemetry.addData("right power:", right);
        telemetry.update();

    }
}
