package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by cp on 11/11/15.
 */
public class Stopppppp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        while(opModeIsActive()){
            int distance = isaac5.getDistance();
                    if(distance > 45){
                        isaac5.stop();
                    }
                    else if(distance > 35){
                        isaac5.slow();
                    }
                    else{
                        isaac5.goForward();
                    }

        }
    }
}
