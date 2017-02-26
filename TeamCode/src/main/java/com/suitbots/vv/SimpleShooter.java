package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Simple Shooter", group = "Tournament")
public class SimpleShooter extends AutonomousBase {
    @Override
    public double forwardDir() {
        return Math.PI;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initRobot(false);
        int shoot_particles = 2;
        int wait = 0;
        Controller c = new Controller(gamepad1);
        while (! isStarted()) {
            c.update();
            if (c.AOnce()) {
                shoot_particles = (1 + shoot_particles) % 3;
            }
            if (c.BOnce()) {
                wait = (wait + 1000) % 10000;
            }
            telemetry.addData("Ready", robot.isCalibrating() ? "no" : ">>> YES <<<");
            telemetry.addData("(a) Shooting", shoot_particles);
            telemetry.addData("(b) Wait seconds", wait / 1000);
            telemetry.addData("Remember", "Diagonal, along the center tape");
            telemetry.update();
        }
        robot.onStart();
        sleep(wait);
        driveDirectionTiles(forwardDir(), 1.2 * Math.sqrt(2.0), .7);
        shoot(shoot_particles);
        driveDirectionTiles(forwardDir(),  Math.sqrt(2.0), .7);
        robot.onStop();
    }
}
