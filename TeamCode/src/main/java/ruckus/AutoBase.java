package ruckus;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class AutoBase extends LinearOpMode {
    protected Robot robot = null;

    protected void initialize(final HardwareMap hardwareMap) {
        robot = new Robot(hardwareMap);
    }

    protected void driveTime(final double seconds,
                             final double leftPower,
                             final double rightPower) {
        final double timeAtStart = getRuntime();
        robot.drive(leftPower, rightPower);
        while(opModeIsActive() && seconds > (getRuntime() - timeAtStart)) {
            // NOTHING!!!!1111one

        }
        robot.drive(0, 0);
    }



    protected void driveDistance(final double inches, double speed) {
        final double WHEEL_RADIUS_IN = 4.0 * Math.PI;
        final double TICKS_PER_REVOLUTION = 560.0; // REV HD HEX 20:1
        // public final double TICKS_PER_REVOLUTION = 1120.0; // REV HD HEX 40:1
        // public final double TICKS_PER_REVOLUTION = 1120.0; // AndyMark NeveRest 40:1
        // public final double TICKS_PRE_REVOLUTION = 1140.0; // Tetrix MAX TorqueNADO 60:1
        final double GEAR_RATIO = 1.0 / 1.0; // drive / driven
        final double TICKS_PER_INCH = (TICKS_PER_REVOLUTION / GEAR_RATIO)
                / WHEEL_RADIUS_IN;

        robot.setMotorRunmode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.setMotorRunmode(DcMotor.RunMode.RUN_TO_POSITION);
        final int ticks = (int)(inches * TICKS_PER_INCH);
        robot.setEncoderTargets(ticks, ticks);
        robot.drive(speed, speed);
        while (opModeIsActive() && robot.motorsAreBusy()) {
            sleep(1);
        }
        robot.drive(0, 0);
        robot.setMotorRunmode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    protected void turn(double degrees) {
        /*
        final double CLOSE_ENOUGH = 10.0;
        final double TURN_SPEED = .1;
        robot.resetGyro();
        robot.setMotorRunmode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (degrees > 0.0) {
            robot.drive(TURN_SPEED, -TURN_SPEED);
        } else {
            robot.drive(-TURN_SPEED,TURN_SPEED);
        }
        while (opModeIsActive() && CLOSE_ENOUGH < (Math.abs(degrees) - Math.abs(robot.getRotationZ()))) {
            sleep(1);
        }
        robot.stop();
        */
    }
}
