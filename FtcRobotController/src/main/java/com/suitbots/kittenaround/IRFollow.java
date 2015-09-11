package com.suitbots.kittenaround;

/**
 * Mothbot.
 *
 * Chase the IR beacon.
 */
public class IRFollow extends Isaac5i {
    // If the power is above this limit, stop, as the beacon is close
    public static final double POWER_THRESHOLD = 0.5;

    // if the angle, positive or negative, is below this, consider the sensor to be
    // "in front" of the robot.
    public static final double CENTER_ANGLE_THRESHOLD = 70.0;

    public static final double DRIVE_MOTOR_POWER = 0.5;

    @Override public void loop() {
        if (! hasIRSignal()) {
            telemetry.addData("IR", "No Signal");
            setDriveMotorPowers(0,0);
        } else {
            telemetry.addData("IR", "Signal Found!");
            sensorTelemetry();

            if(irSensorPower() <= POWER_THRESHOLD) {
                if(Math.abs(irSensorAngle()) < CENTER_ANGLE_THRESHOLD) {
                    setDriveMotorPowers(DRIVE_MOTOR_POWER, DRIVE_MOTOR_POWER);
                }
                else {
                    if(irSensorAngle() > 0) {
                        setDriveMotorPowers(DRIVE_MOTOR_POWER, -DRIVE_MOTOR_POWER);
                    }
                    else {
                        setDriveMotorPowers(-DRIVE_MOTOR_POWER, DRIVE_MOTOR_POWER);
                    }
                }
            }
            else {
                allStop();
            }
        }
    }
}
