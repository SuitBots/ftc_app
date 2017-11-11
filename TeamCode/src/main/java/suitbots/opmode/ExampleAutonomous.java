package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;
@Disabled
@Autonomous(name = "AUTO")
public class ExampleAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("State", "Waiting for start");
        telemetry.update();
        waitForStart();
        final long t0 = System.currentTimeMillis();
        while (opModeIsActive()) {
            final long t1 = System.currentTimeMillis();
            final long dt = t1 - t0;
            telemetry.addData("State", "Running " + dt);
            telemetry.update();
        }

        ((OpModeManagerImpl)internalOpModeServices).initActiveOpMode("TELEOP");
    }
}