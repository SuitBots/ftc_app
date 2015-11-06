package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class Isaac5 extends OpMode {
    private DcMotor armmotor, leftmotor, rightmotor;
    @Override
    public void init() {
        armmotor=hardwareMap.dcMotor.get("armmotor");
        rightmotor=hardwareMap.dcMotor.get("rightmotor");
        leftmotor=hardwareMap.dcMotor.get("leftmotor");
        leftmotor.setDirection(DcMotor.Direction.REVERSE);
    }

    protected void setDriveMotorSpeeds(double left, double right) {
        leftmotor.setPower(left);
        rightmotor.setPower(right);

    }


    protected void armUp() {
        armmotor.setPower(1);
    }
    protected void armDown() {
        armmotor.setPower(-1);
    }
    protected void armStop(){
        armmotor.setPower(0);
    }
}