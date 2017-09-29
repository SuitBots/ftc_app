package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


/**
 * Created by Suit Bots on 5/23/2017.
 */
@TeleOp (name = "DemoBot" , group = "Graveyard")
public class GraveyardBorrowSorrynotSorryThanksTeleOp extends OpMode {
    private DcMotor l, r;
}
    @Override
    public void init(){
        l = hardwareMap.dcMotor.get("l");
        r = hardwareMap.dcMotor.get("r");
        r.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    public void loop(){
        l.setPower(-gamepad1.left_stick_y);
        r.setPower(-gamepad1.right_stick_y);
    }
    public void stop(){
        l.setPower(0);
        r.setPower(0);
}
}

