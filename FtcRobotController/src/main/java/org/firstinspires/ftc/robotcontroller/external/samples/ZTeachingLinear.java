package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Pickles on 1/18/18.
 */

public class HighTime extends LinearOpMode {
    //setup stuff :3
    DcMotor leftDrive;
    DcMotor rightDrive;



    @Override
    public void runOpMode() {
        //running stuff
        //hardware naming
        leftDrive = hardwareMap.dcMotor.get("leftDrive");
        rightDrive = hardwareMap.dcMotor.get("rightDrive");

        leftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();//---------------Wait for start button to be pushed----------------

        //basic drive block
        leftDrive.setPower(1);
        rightDrive.setPower(1);
        sleep(1000);



        //sets left drive motor to go forward at full power
        leftDrive.setPower(1);
        //sets right drive motor to go forward at full power
        rightDrive.setPower(1);
        //makes it go for 1000 milliseconds (1 second)
        sleep(1000);


        //forward full 1sec
        leftDrive.setPower(1);
        rightDrive.setPower(1);
        sleep(1000);
        //stop 0.75sec
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        sleep(750);
        //left turn half 20 sec
        leftDrive.setPower(-1);
        rightDrive.setPower(1);
        sleep(20000);
        //backwards 0.75 power 5 sec
        leftDrive.setPower(-0.75);
        rightDrive.setPower(-0.75);
        sleep(5000);
        //right turn 0.86 power 2.758 sec
        leftDrive.setPower(0.86);
        rightDrive.setPower(-0.86);
        sleep(2758);

    }
}
