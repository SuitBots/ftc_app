package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Simple Shooter")
public class SimpleShooter extends AutonomousBase {
    @Override
    public void runOpMode() throws InterruptedException {
        int shoot_particles = 2;
        Controller c = new Controller(gamepad1);
        while (! isStarted()) {
            c.update();
            if (c.AOnce()) {
                shoot_particles = (1 + shoot_particles) % 3;
                telemetry.addData("Shooting", shoot_particles);
                telemetry.addData("Gyro", robot.isCalibrating() ? "Calibrating" : "CalibratED");
                telemetry.update();
            }
        }

        robot.onStart();
        driveDirectionCM(0.0, 60);
        shoot(shoot_particles);
        driveDirectionCM(0.0, 60);
        robot.onStop();
    }
}
