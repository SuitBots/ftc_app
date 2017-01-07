package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.robocol.PeerApp;

import java.util.Locale;

/**
 * Created by Suit Bots on 11/22/2016.
 */
public abstract class ShalfDogronAutonomousBeta extends AutonomousBase {
    private int particles_to_shoot = 2;
    private Controller c1;
    private Controller c2;

    protected abstract AllianceColor getAlliance();


    // Correct for any heading drift during a previous stage
    protected void trueUp() throws InterruptedException {
        turnToAngle(AllianceColor.BLUE == getAlliance() ? 0 : 180);
    }


    @Autonomous(name = "Shalf RED", group = "Tournament")
    public static class Red extends ShalfDogronAutonomousBeta {
        protected AllianceColor getAlliance() { return AllianceColor.RED; }
    }

    @Autonomous(name = "Shalf BLUE", group = "Tournament")
    public static class Blue extends ShalfDogronAutonomousBeta {
        protected AllianceColor getAlliance() { return AllianceColor.BLUE; }
    }

    @Override
    protected double forwardDir() {
        if(AllianceColor.RED == getAlliance()) {
            return Math.PI * 2.0;
        } else {
            return Math.PI;
        }
    }

    // Shalf Dogron Autonomous Building Blocks

    private void backUp() throws InterruptedException {
        driveDirectionInches(forwardDir() + Math.PI, 6.0);
    }

    private void initialForward() throws InterruptedException {
        // This is always going to be backwards, as we shoot first and hit buttons later
        driveDirectionTiles(Math.PI, .6);
    }

    private void leftToWall() throws InterruptedException {
        driveDirectionTiles(leftDir(), 3.0);
    }

    private void safetyForward() throws InterruptedException {
        driveDirectionTiles(forwardDir(), 1.8);
    }

    private void shooterInitialBackup() throws InterruptedException {
        driveDirectionTiles(forwardDir() + Math.PI, 2.0);
    }

    private void shooterToGoalBackup() throws InterruptedException {
        driveDirectionTiles(forwardDir() + Math.PI, .5);
    }

    private void reverse() throws InterruptedException {
        turn(180);
        robot.resetGyro();
    }

    private void fullAutonomous() throws InterruptedException {
        final AllianceColor a = getAlliance();
        initialForward();
        turnToAngle(0);

        shoot(particles_to_shoot);

        driveDiagonalToTheWall();
        trueUp();

        driveForwardToWhiteLine();
        trueUp();

        achieveWallDistance();
        trueUp();

        pressButton();
        trueUp();

        safetyForward();
        trueUp();

        driveForwardToWhiteLine();
        trueUp();

        achieveWallDistance();
        trueUp();

        pressButton();
    }

    private void debugLoop() throws InterruptedException {
        while(opModeIsActive()) {
            c1.update();
            c2.update();

            if (c1.rightBumperOnce() || c1.leftBumperOnce()) {
                int task = 0;

                if (c1.A()) task |= 1;
                if (c1.B()) task |= 2;
                if (c1.X()) task |= 4;
                if (c1.Y()) task |= 8;
                if (c1.dpadUp()) task |= 16;
                if (c1.dpadDown()) task |= 32;
                if (c1.dpadLeft()) task |= 64;
                if (c1.dpadRight()) task |= 128;

                switch(task) {
                    case 1: // A
                        leftToWall();
                        break;
                    case 2: // B
                        driveDiagonalToTheWall();
                        break;
                    case 3: // A and B
                        achieveWallDistance();
                        break;
                    case 4: // X
                        initialForward();
                        break;
                    case 5: // A and X
                        pressButton();
                        break;
                    case 8: // Y
                        shoot(2);
                        break;
                    case 12: // X and Y
                        trueUp();
                        break;
                    case 6: // B and X
                        robot.toggleBackServo();
                        break;
                    case 10: // B and Y
                        robot.toggleFrontServo();
                        break;
                    case 16: // dpad up
                        driveForwardToWhiteLine();
                        break;
                    case 32: // dpad down
                        backUp();
                        break;
                    case 64: // dpad left
                        reverse();
                        break;
                    case 128:
                        safetyForward();
                        break;
                    default:
                        robot.updateSensorTelemetry();
                        telemetry.addData("Wall",
                                String.format(Locale.US, "%s %.2f %.2f",
                                        vision.canSeeWall() ? "Y" : "N",
                                        vision.getXOffset(),
                                        vision.getOrientation()));
                        break;

                }
            }

            DriveHelper.drive(c1, robot);
            telemetry.update();
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        c1 = new Controller(gamepad1);
        c2 = new Controller(gamepad2);

        initRobot();
        // TODO: default this to off for the real deal
        boolean debug_loop = true;

        while (! isStarted()) {
            c1.update();
            if (c1.AOnce()) {
                particles_to_shoot = (1 + particles_to_shoot) % 3;
            }
            if (c1.BOnce()) {
                debug_loop = ! debug_loop;
            }
            telemetry.addData("Gyro", robot.isCalibrating() ? "Calibrating" : "CALIBRATED");
            telemetry.addData("Particles to shoot", particles_to_shoot);
            telemetry.addData("Debug Mode", debug_loop ? "*** ON ***" : "Off");
            telemetry.update();
            idle();
        }

        robot.resetGyro();
        robot.onStart();

        if (debug_loop) {
            debugLoop();
        } else {
            fullAutonomous();
        }

        robot.onStop();
    }

    protected void driveDiagonalToTheWall() throws InterruptedException {
        turnToAngle(getAlliance() == AllianceColor.RED ? -120 : -60);
        driveDirectionTiles(forwardDir(), 2.5);
        trueUp();
    }

    protected static double DISTANCE_TO_WALL_CM = 18.0;
    private void achieveWallDistance() throws InterruptedException {
        achieveWallDistance(DISTANCE_TO_WALL_CM, getAlliance());
    }

    private static final int BEACON_PRESSING_MOVE_CM = 15;
    private void pressButton() throws InterruptedException {
        final boolean back_button = robot.getColor() == getAlliance();

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }

        sleep(250);

        driveDirectionCM(3.0 * Math.PI / 2.0, BEACON_PRESSING_MOVE_CM);
        driveDirectionCM(Math.PI / 2.0, BEACON_PRESSING_MOVE_CM);

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }
    }


    private static final int DESIRED_RELATIVE_HEADING = 90;
    private void rotate() throws InterruptedException {
        turn(DESIRED_RELATIVE_HEADING);
    }

    private void setPhase(String phase) {
        telemetry.addData("Phase", phase);
        telemetry.update();
    }
}
