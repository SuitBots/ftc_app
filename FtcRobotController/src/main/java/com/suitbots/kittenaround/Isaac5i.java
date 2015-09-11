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
    private IrSeekerSensor ir_sensor = null;
    private TouchSensor touch_sensor = null;
    private OpticalDistanceSensor distance_sensor = null;

    @Override
    public void init() {
        drive_left_front = hardwareMap.dcMotor.get("drivelf");
        drive_left_back = hardwareMap.dcMotor.get("drivelr");
        drive_right_front = hardwareMap.dcMotor.get("driverf");
        drive_right_back = hardwareMap.dcMotor.get("driverr");
        ir_sensor = hardwareMap.irSeekerSensor.get("ir");
        touch_sensor = hardwareMap.touchSensor.get("touch");
        distance_sensor = hardwareMap.opticalDistanceSensor.get("distance");

        drive_left_front.setDirection(DcMotor.Direction.REVERSE);
        drive_left_back.setDirection(DcMotor.Direction.REVERSE);

        allStop();
    }

    @Override
    public void stop() {
        allStop();
    }

    // Send telemetry information about all available sensors.
    protected void sensorTelemetry() {
        if (ir_sensor.signalDetected()) {
            telemetry.addData("IR", String.format("IR A: %.2f, S: %.2f",
                    ir_sensor.getAngle(), ir_sensor.getStrength()));
        } else {
            telemetry.addData("IR", "IR: No data");
        }
        telemetry.addData("Touch", touch_sensor.isPressed() ?
                "Touch: Pressed" : "Touch: Not Pressed");
        telemetry.addData("Dist", String.format("Distance %.2f",
                distance_sensor.getLightDetected()));
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

    protected boolean hasIRSignal() {
        return null != ir_sensor && ir_sensor.signalDetected();
    }

    protected double irSensorAngle() {
        if( null == ir_sensor) { return 0.0; }
        return ir_sensor.getAngle();
    }

    protected double irSensorPower() {
        if (null == ir_sensor) { return 0.0; }
        return ir_sensor.getStrength();
    }
}
