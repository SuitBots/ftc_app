package ruckus.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Func;

import java.util.Locale;

import ruckus.AutoBase;

@Autonomous(name = "Drive Until Crater")
public class Lesson5 extends AutoBase {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize(hardwareMap);
        waitForStart();

        final double TILT_THRESHOLD = 5.0;
        /*

        telemetry.addData("gyro", new Func<Object>() {
            @Override
            public Object value() {
                return String.format(Locale.US, "%.2f %.2f %.2f",
                        robot.getRotationX(), robot.getRotationY(), robot.getRotationZ());
            }
        });

        robot.resetGyro();
        robot.drive(.25, .25);
        while (opModeIsActive()) {
            if (Math.abs(robot.getRotationX()) > TILT_THRESHOLD ||
                    Math.abs(robot.getRotationY()) > TILT_THRESHOLD) {
                break;
            }
            telemetry.update();
        }
        robot.drive(0.0, 0.0);
        */
    }
}
