package com.longhorns.resq;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class LHTeleop extends OpMode {
    private DcMotor l1, l2, r1, r2;

    private double clamp(double x) {
        return Math.min(Math.max(-1.0, x), 1.0);
    }

    /// Set drivetrain speeds en masse.
    protected void setDriveMotorSpeeds(double left, double right) {
        l1.setPower(clamp(left));
        l2.setPower(clamp(left));
        r1.setPower(clamp(right));
        r2.setPower(clamp(right));
    }

    @Override public void loop() {
        setDriveMotorSpeeds(-gamepad1.left_stick_y, -gamepad1.right_stick_y);
        if (gamepad1.dpad_down) {
            setDriveMotorSpeeds(-1.0, -1.0);
        } else if (gamepad1.dpad_up) {
            setDriveMotorSpeeds(1.0, 1.0);
        } else if (gamepad1.dpad_right) {
            setDriveMotorSpeeds(1.0, -1.0);
        } else if (gamepad1.dpad_left) {
            setDriveMotorSpeeds(-1.0, 1.0);
        }

    }

    @Override public void init() {
        l1 = hardwareMap.dcMotor.get("l1");
        l2 = hardwareMap.dcMotor.get("l2");
        r1 = hardwareMap.dcMotor.get("r1");
        r2 = hardwareMap.dcMotor.get("r2");

        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override public void start() {

    }

    @Override public void stop() {
        l1.setPower(0.0);
        l2.setPower(0.0);
        r1.setPower(0.0);
        r2.setPower(0.0);

    }
}
