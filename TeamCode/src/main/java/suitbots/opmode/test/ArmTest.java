package suitbots.opmode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.suitbots.util.Controller;

import suitbots.ConfigVars;
import suitbots.opmode.AutoBase;

@TeleOp(name = "ArmTest")
public class ArmTest extends AutoBase {

    private Controller g1, g2;


    private void adjustDumper() {
        final double pos = arm.getCurrentPosition();

        if (pos < ConfigVars.TELEOP_ARM_UP) {
            dumper.setPosition(ConfigVars.TELEOP_DUMP_SERVO_LIMIT * pos / ConfigVars.TELEOP_ARM_UP);
        } else {
            dumper.setPosition(ConfigVars.TELEOP_DUMP_SERVO_LIMIT);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setTargetPosition(0);
        arm.setPower(0);

        g1 = new Controller(gamepad1);
        g2 = new Controller(gamepad2);

        while (opModeIsActive()) {
            g1.update();
            g2.update();

            if (g1.XOnce()) {
                arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else if (g1.YOnce()) {
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            if (g1.dpadLeftOnce()) {
                arm.setTargetPosition(ConfigVars.TELEOP_ARM_UP);
                arm.setPower(0.5);
            } else if (g1.dpadDownOnce()) {
                arm.setTargetPosition(ConfigVars.TELEOP_ARM_DOWN);
                arm.setPower(0.5);
            } else if (! arm.isBusy()) {
                arm.setPower(g1.left_stick_y);
            }
            if (g1.XOnce()) {
                dumper.setPosition(ConfigVars.TELEOP_DUMP_SERVO_POSITION);
            } else {
                adjustDumper();
            }

            telemetry.addData("dpad left", " arm up");
            telemetry.addData("dpad down", " arm down");
            telemetry.addData("x", " servo dump");
            telemetry.addData("", "");
            telemetry.addData("Arm Position", arm.getCurrentPosition());
            telemetry.addData("Arm Power", arm.getPower());
            telemetry.addData("Dump Position", dumper.getPosition());
            telemetry.update();
        }
    }
}
