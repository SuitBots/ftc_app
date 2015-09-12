package com.suitbots.kittenaround;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Linear OpMode: a solid "recommend against."
 */
public class DeadLinear extends LinearOpMode {
    private DcMotor drive_left_front = null;
    private DcMotor drive_left_back = null;
    private DcMotor drive_right_front = null;
    private DcMotor drive_right_back = null;

    @Override
    public void init() {
        drive_left_front = hardwareMap.dcMotor.get("drivelf");
        drive_left_back = hardwareMap.dcMotor.get("drivelr");
        drive_right_front = hardwareMap.dcMotor.get("driverf");
        drive_right_back = hardwareMap.dcMotor.get("driverr");
        drive_left_front.setDirection(DcMotor.Direction.REVERSE);
        drive_left_back.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void runOpMode() {
        driveForward();
        sleep(5000);
        turnLeft();
        sleep(5000);
        driveForward();
        sleep(5000);
        allStop();
    }

    // Wow, copying. That's tacky. Thanks, Java.

    // true fact: DcMotor objects will throw an exception if you try to set
    // their power to something outside of [-1.0, 1.0]. Avoid that!
    private float clampMotorSpeed(double x) {
        return (float) Math.max(-.99, Math.min(.99, x));
    }

    protected void setDriveMotorPowers(double left_front, double left_back,
                                       double right_front, double right_back) {
        drive_left_front.setPower(clampMotorSpeed(left_front));
        drive_left_back.setPower(clampMotorSpeed(left_back));
        drive_right_front.setPower(clampMotorSpeed(right_front));
        drive_right_back.setPower(clampMotorSpeed(right_back));
    }

    protected void setDriveMotorPowers(double left, double right) {
        setDriveMotorPowers(left, left, right, right);
    }

    protected void driveForward() { setDriveMotorPowers(1.0, 1.0); }
    protected void driveBackward() {
        setDriveMotorPowers(-1.0, -1.0);
    }
    protected void turnLeft() {
        setDriveMotorPowers(1.0, -1.0);
    }
    protected void turnRight() {
        setDriveMotorPowers(-1.0, 1.0);
    }

    protected void allStop() {
        setDriveMotorPowers(0.0, 0.0);
    }
}
