package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by Suit Bots on 5/16/2017.
 */

public class DemoOpMode extends OpMode {
    DcMotor left, right;
    public void init() {
        left = hardwareMap.dcMotor.get("L");
        right = hardwareMap.dcMotor.get("R");
        right.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    
    public void loop() {
        left.setPower(- gamepad1.left_stick_y);
        right.setPower(- gamepad1.right_stick_y);
    }
}
