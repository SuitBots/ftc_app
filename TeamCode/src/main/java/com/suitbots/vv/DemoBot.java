package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by Suit Bots on 2/23/2017.
 */

@Disabled
@TeleOp (name = "DemoBot")
public class DemoBot extends OpMode {
    private DcMotor l, r;

    @Override
    public void init(){
        l = hardwareMap.dcMotor.get("l");
        r = hardwareMap.dcMotor.get("r");
        r.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop(){
        l.setPower(-gamepad1.left_stick_y);
        r.setPower(-gamepad1.right_stick_y);
    }
}
