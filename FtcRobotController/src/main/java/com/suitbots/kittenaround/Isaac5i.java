package com.suitbots.kittenaround;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Isaac 5i's hardware definition.
 *
 * Override this class to add a new OpMode on to Isaac 5. You'll have access to all of
 * Isaac's hardware and convienence methods. If you write a custom start() or stop()
 * method, be sure to call Isaac5Basic's start() or stop() via super.
 */
public abstract class Isaac5i extends OpMode {
    private DcMotor drive_left_front = null;
    private DcMotor drive_left_back = null;
    private DcMotor drive_right_front = null;
    private DcMotor drive_right_back = null;
    // private CompassSensor compass = null;
    private IrSeekerSensor ir_sensor = null;
    private TouchSensor touch_sensor = null;
    private OpticalDistanceSensor distance_sensor = null;

    // stop everything!
    protected void allStop() {
        setDriveMotorPowers(0.0, 0.0);
    }

    @Override
    public void init() {
        drive_left_front = hardwareMap.dcMotor.get("drivelf");
        drive_left_back = hardwareMap.dcMotor.get("drivelr");
        drive_right_front = hardwareMap.dcMotor.get("driverf");
        drive_right_back = hardwareMap.dcMotor.get("driverr");
        // compass = hardwareMap.compassSensor.get("compass");
        ir_sensor = hardwareMap.irSeekerSensor.get("ir");
        try {
            touch_sensor = hardwareMap.touchSensor.get("touch");
        } catch(Exception e) {
            touch_sensor = null;
        }
        try {
            distance_sensor = hardwareMap.opticalDistanceSensor.get("distance");
        } catch(Exception e) {
            distance_sensor = null;
        }

        drive_left_front.setDirection(DcMotor.Direction.REVERSE);
        drive_left_back.setDirection(DcMotor.Direction.REVERSE);

        allStop();
    }

    protected void sensorTelemetry() {
        if (ir_sensor != null) {
            if (ir_sensor.signalDetected()) {
                telemetry.addData("IR", String.format("IR A: %.2f, S: %.2f",
                        ir_sensor.getAngle(), ir_sensor.getStrength()));
            } else {
                telemetry.addData("IR", "IR: No data");
            }

        } else {
            telemetry.addData("IR", "IR Unavailable");
        }
        if (touch_sensor != null) {
            telemetry.addData("Touch", touch_sensor.isPressed() ?
                    "Touch: Pressed" : "Touch: Not Pressed");
        } else {
            telemetry.addData("Touch", "Touch Unavailable");
        }
        if (distance_sensor != null) {
            telemetry.addData("Dist", String.format("Distance %.2f",
                    distance_sensor.getLightDetected()));
        } else {
            telemetry.addData("Dist" ,"Distance Unavailable");
        }
    }

    @Override
    public void stop() {
        allStop();
    }

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

    protected void driveForward() {
        setDriveMotorPowers(1.0, 1.0);
    }

    protected void driveBackward() {
        setDriveMotorPowers(-1.0, -1.0);
    }

    protected void turnLeft() {
        setDriveMotorPowers(-1.0, 1.0);
    }

    protected void turnRight() {
        setDriveMotorPowers(1.0, -1.0);
    }
}
