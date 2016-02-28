package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by cp on 2/27/16.
 */
public class Derp extends OpMode {
    private DcMotor l1, l2, r1, r2;
    private DcMotor winch, tape;
    private Servo flap, dumper_flipper;
    boolean arm_is_enabled;

    @Override
    public void init()
    {
        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");
        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
        tape = hardwareMap.dcMotor.get("tape");
        winch = hardwareMap.dcMotor.get("winch");
        flap = hardwareMap.servo.get("scoop");
        dumper_flipper = hardwareMap.servo.get("flipper"); // y
        arm_is_enabled = false;
    }

    @Override
    public void stop()
    {
        l1.setPower(0.0);
        l2.setPower(0.0);
        r1.setPower(0.0);
        r2.setPower(0.0);
        tape.setPower(0.0);
        winch.setPower(0.0);
    }

    @Override
    public void start()
    {
        l1.setPower(0.0);
        l2.setPower(0.0);
        r1.setPower(0.0);
        r2.setPower(0.0);
        tape.setPower(0.0);
        winch.setPower(0.0);
    }

    private void setDriveMotorSpeeds(double l, double r)
    {
        l1.setPower(l);
        l2.setPower(l);
        r1.setPower(r);
        r2.setPower(r);
    }

    private void setTapeMotor(double x)
    {
        tape.setPower(x);
    }

    private void setWinchMotor(double x)
    {
        winch.setPower(x);
    }

    @Override
    public void loop()
    {
        double left = - gamepad1.left_stick_y;
        double right = - gamepad1.right_stick_y;

        l1.setPower(left);
        l2.setPower(left);
        r1.setPower(right);
        r2.setPower(right);

        // For hill climbing.
        if (gamepad1.dpad_up) {
            setDriveMotorSpeeds(1.0, 1.0);
        }

        if (gamepad1.dpad_down) {
            if (! arm_is_enabled) {
                arm_is_enabled = true;
                dumper_flipper.setPosition(Servo.MAX_POSITION);
            }
        } else {
            if (arm_is_enabled) {
                arm_is_enabled = false;
                dumper_flipper.setPosition(Servo.MIN_POSITION);
            }
        }

        if (gamepad1.left_bumper) {
            setTapeMotor(1.0);
        } else if(gamepad1.right_bumper) {
            setTapeMotor(-1.0);
        } else {
            setTapeMotor(gamepad1.left_trigger - gamepad1.right_trigger);
        }

        if (gamepad1.a) {
            setWinchMotor(1.0);
        } else if (gamepad1.b) {
            setWinchMotor(-1.0);
        } else {
            setWinchMotor(0.0);
        }


        if (gamepad1.y) {
            flap.setPosition(1.0);
        } else if(gamepad1.x){
            flap.setPosition(0.0);
        } else{
            flap.setPosition(0.5);
        }


        telemetry.addData("Sticks", String.format("%f %f", left, right));
    }
}
