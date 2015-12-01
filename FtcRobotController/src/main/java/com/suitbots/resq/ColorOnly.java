package com.suitbots.resq;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.LED;

/**
 * Created by cp on 11/15/15.
 */
public class ColorOnly extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Isaac5 isaac5 = new Isaac5(hardwareMap, telemetry);

        waitForStart();

        boolean led_enabled = false, previously_pressed = false;

        while(opModeIsActive()) {
            if (gamepad1.a && !previously_pressed) {
                previously_pressed = true;
                led_enabled = ! led_enabled;
            } else {
                previously_pressed = false;
            }

            if (led_enabled) {
                isaac5.activateSensors();
            } else {
                isaac5.deactivateSensors();
            }


            telemetry.addData("Led Enabled?", led_enabled ? "Yep" : "Nope");
            telemetry.addData("Color Fore", String.format("%03d %03d %03d",
                    isaac5.getRedFore(), isaac5.getGreenFore(), isaac5.getBlueFore()));
            telemetry.addData("Color Under", String.format("%03d %03d %03d %03d",
                    isaac5.getRedUnder(), isaac5.getGreenUnder(),
                    isaac5.getBlueUnder(), isaac5.getAlphaUnder()));
        }
    }
}
