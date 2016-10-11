package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Fwd One Sec.")
@Disabled
public class ForwardOneSecond extends LinearOpMode {
    public void runOpMode() throws InterruptedException {
        DcMotor left = hardwareMap.dcMotor.get("left");
        DcMotor right = hardwareMap.dcMotor.get("right");
        right.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        long t0 = System.currentTimeMillis();
        left.setPower(1.0);
        right.setPower(1.0);
        while (opModeIsActive()) {
            long t1 = System.currentTimeMillis();
            if (1000 < (t1 - t0)) {
                break;
            }
            idle();
        }
        left.setPower(0.0);
        right.setPower(0.0);
    }
}
