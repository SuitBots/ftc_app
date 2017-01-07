package com.suitbots.vv;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class AutonomusBaseTwo  extends LinearOpMode{
    private DcMotor lf, lr, rf, rr;
    private ModernRoboticsI2cGyro gyro;

    public void initialize() {
        lf = hardwareMap.dcMotor.get("lf");
        lr = hardwareMap.dcMotor.get("lr");
        rf = hardwareMap.dcMotor.get("rf");
        rr = hardwareMap.dcMotor.get("rr");
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
        gyro.calibrate();
    }

    public boolean isGyroCalibrating(){
        return gyro.isCalibrating();
    }

    public int getHeading(){
        return gyro.getIntegratedZValue();
    }

    public void resetGyro(){
        gyro.resetZAxisIntegrator();
    }

    public void driveForwardSeconds(double seconds) throws InterruptedException {
        long ms = (long) (seconds * 1000.0);
        long t0 = System.currentTimeMillis();
        lf.setPower(1.0);
        lr.setPower(1.0);
        rf.setPower(1.0);
        rr.setPower(1.0);
        while(opModeIsActive() && ms>(System.currentTimeMillis()-t0)){
            idle();
        }
        lf.setPower(0.0);
        lr.setPower(0.0);
        rf.setPower(0.0);
        rr.setPower(0.0);
    }

    private static final double TICKS_PER_REV = 1120.0;
    private static final double WHEEL_DIAMETER = 4.0;
    private static final double TICKS_PER_INCH = TICKS_PER_REV/(WHEEL_DIAMETER*Math.PI);

    public void driveForwardTiles(double tiles) throws InterruptedException {
        lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lf.setTargetPosition((int)(tiles*24*TICKS_PER_INCH));
        lr.setTargetPosition((int)(tiles*24*TICKS_PER_INCH));
        rf.setTargetPosition((int)(tiles*24*TICKS_PER_INCH));
        rr.setTargetPosition((int)(tiles*24*TICKS_PER_INCH));

        lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        lf.setPower(0.5);
        lr.setPower(0.5);
        rf.setPower(0.5);
        rr.setPower(0.5);

        while(opModeIsActive() && (lf.isBusy() || lr.isBusy() || rf.isBusy() || rr.isBusy())){
            idle();
        }
        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lf.setPower(0.0);
        lr.setPower(0.0);
        rf.setPower(0.0);
        rr.setPower(0.0);


    }



    public void turnRobot(int degrees){
        resetGyro();
        if (degrees>0){
            lf.setPower(0.15);
            lr.setPower(0.15);
            rf.setPower(-0.15);
            rr.setPower(-0.15);
        }
        if (degrees<0){
            lf.setPower(-0.15);
            lr.setPower(-0.15);
            rf.setPower(0.15);
            rr.setPower(0.15);
        }
        while (opModeIsActive() && Math.abs(degrees) > Math.abs(gyro.getHeading())){
            idle();
        }
        lf.setPower(0.0);
        lr.setPower(0.0);
        rf.setPower(0.0);
        rr.setPower(0.0);
    }

}
