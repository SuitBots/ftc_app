package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by cp on 11/11/15.
 */
public class MeterDash extends LinearOpMode {
    @Override
    public void runOpMode()throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        isaac5.zeroMotorEncoders();

        while(opModeIsActive()){
            isaac5.sendSensorTelemetry();
            int LeftEncoder = isaac5.getLeftEncoder();
            int RightEncoder = isaac5.getRightEncoder();
                if(LeftEncoder > 5500 && RightEncoder > 5500){
                    isaac5.stop();
                }
                else{
                    isaac5.goBackward();
                }

        }
    }
}
