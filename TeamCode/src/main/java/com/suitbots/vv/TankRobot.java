package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TankRobot {
    private DcMotor lm, rm, spinner;
    private ModernRoboticsI2cGyro gyro;
    private ModernRoboticsI2cRangeSensor range;
    private OpticalDistanceSensor line;

    private DcMotor.RunMode mode_stack[] = new DcMotor.RunMode[10];
    private int mode_stack_pos = 0;

    private DcMotor.RunMode currentRunMode() {
        return mode_stack[mode_stack_pos];
    }

    private void setCurrentRunMode() {
        lm.setMode(currentRunMode());
        rm.setMode(currentRunMode());
    }

    public void initHardware(HardwareMap hardwareMap) {
        lm = hardwareMap.dcMotor.get("l1");
        rm = hardwareMap.dcMotor.get("r1");
        rm.setDirection(DcMotorSimple.Direction.REVERSE);
        spinner = hardwareMap.dcMotor.get("spinner");
        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        if (null == gyro) {
            throw new RuntimeException("Could not load gyro sensor");
        }
        range = (ModernRoboticsI2cRangeSensor)
                hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "range");
        line = hardwareMap.opticalDistanceSensor.get("line");
        mode_stack[mode_stack_pos] = DcMotor.RunMode.RUN_USING_ENCODER;
        setCurrentRunMode();
    }

    public void calibrateGyro() { gyro.calibrate(); }
    public boolean isGyroCalibrating() {
        return gyro.isCalibrating();
    }

    public void resetDriveEncoders() {
        pushRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        popRunMode();
    }

    public void pushRunMode(DcMotor.RunMode m) {
        mode_stack[++mode_stack_pos] = m;
        setCurrentRunMode();
    }

    public void popRunMode() {
        --mode_stack_pos;
        setCurrentRunMode();
    }

    public void setDriveMotors(double l, double r) {
        lm.setPower(l);
        rm.setPower(r);
    }

    public void setDriveMotorsEncoderTarget(int ticks) {
        lm.setTargetPosition(ticks);
        rm.setTargetPosition(ticks);
    }

    public boolean motorsAreBusy() {
        return lm.isBusy() || rm.isBusy();
    }
    public void setSpinner(double p) {
        spinner.setPower(p);
    }

    public static final double WHEEL_RADIUS_IN = 4.0 * Math.PI;

    public void resetGyro() {
        gyro.resetZAxisIntegrator();
    }
    public int getHeading() {
        return gyro.getIntegratedZValue();
    }
    public double getLineReading() {
        return line.getLightDetected();
    }
    public double getAcousticRangeInches() {
        return range.getDistance(DistanceUnit.INCH);
    }
    public double getAcousticRangeCM() {
        return range.getDistance(DistanceUnit.CM);
    }

    public double getRangeLightDetected() {
        return range.getRawLightDetected();
    }
}
