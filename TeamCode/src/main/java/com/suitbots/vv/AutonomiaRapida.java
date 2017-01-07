package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.SyncdDevice;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.configuration.ConfigurationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AutonomiaRapida extends AutonomousBase {
    private int shoot_no = 2;

    public abstract AllianceColor allianceColor();

    @Autonomous(name = "Rapida RED")
    public static class Red extends AutonomiaRapida {
        @Override
        public AllianceColor allianceColor() { return AllianceColor.RED; }
    }

    @Autonomous(name = "Rapida BLUE")
    public static class Blue extends AutonomiaRapida {
        @Override
        public AllianceColor allianceColor() { return AllianceColor.BLUE; }
    }

    @Override
    public double forwardDir() {
        return AllianceColor.RED == allianceColor() ? 0.0 : Math.PI;
    }

    abstract class Step {
        private String name;
        public Step(String n) { name = n; }
        public String getName() { return name; }

        abstract public void act() throws InterruptedException;
    }

    private Step[] phase_0_steps = new Step[] {
            new Step("Initial Forward") {
                @Override
                public void act() throws InterruptedException {
                    robot.resetGyro();
                    driveDirectionTiles(Math.PI, 0.65);
                    turnToAngle(0);
                }
            }
    };

    private Step[] phase_1_steps = new Step[] {
            new Step("Drive To Beacon") {
                @Override
                public void act() throws InterruptedException {
                    if (AllianceColor.RED == allianceColor()) {
                        turn(120);
                    } else {
                        turn(60);
                    }
                    driveDirectionTiles(forwardDir(), 3.5);
                    if (AllianceColor.RED == allianceColor()) {
                        turn(60);
                    } else {
                        turn(-60);
                    }
                }
            },
            new Step("Approach Beacon 1") {
                @Override
                public void act() throws InterruptedException {
                    driveToBeacon();
                }
            },
            new Step("Press Button 1") {
                @Override
                public void act() throws InterruptedException {
                    pressButton();
                }
            },
            new Step("Next Beacon") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(forwardDir(), 2.0);
                }
            },
            new Step("Approach Beacon 2") {
                @Override
                public void act() throws InterruptedException {
                    driveToBeacon();
                }
            },
            new Step("Press Button 2") {
                @Override
                public void act() throws InterruptedException {
                    pressButton();
                }
            }
    };

    private Step[] shooting = new Step[] {
        new Step("Shoot") {
            @Override
            public void act() throws InterruptedException {
                shoot(shoot_no);
            }
        }
    };

    private Step[] post_shoot = new Step[] {
            new Step("Face Center") {
                @Override
                public void act() throws InterruptedException {
                    turn(AllianceColor.RED == allianceColor() ? -45 : -135);
                }
            },
            new Step("Approach Vortex") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(Math.PI, Math.sqrt(2.0));
                }
            },
            new Step("Fire!") {
                @Override
                public void act() throws InterruptedException {
                    shoot(shoot_no);
                }
            }
    };

    private Step[] ramp_steps = new Step[] {
            new Step("Away from Wall") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(leftDir() + Math.PI, .5);
                }
            },
            new Step("Back to Ramp") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(forwardDir() + Math.PI, 4.5);
                }
            }
    };

    private Step[] center_steps = new Step[] {
            new Step("Turn Towards Vortex") {
                @Override
                public void act() throws InterruptedException {
                    if (AllianceColor.RED == allianceColor()) {
                        turn(-45);
                    } else {
                        turn(45);
                    }
                }
            },
            new Step("Drive To Vortex") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(forwardDir() + Math.PI, 2.5 * Math.sqrt(2.0));
                }
            }
    };

    private ArrayList<Step> steps = new ArrayList<>();

    // TODO: Figure out the ordering re: shoot first + diagonal start v. shoot last
    @Override
    public void runOpMode() throws InterruptedException {
        initRobot();
        Controller c = new Controller(gamepad1);
        boolean ramp = true, debug_mode = true, pre_shoot = true;
        while (! isStarted()) {
            c.update();
            if (c.BOnce()) {
                shoot_no = (1 + shoot_no) % 3;
            }
            if (c.AOnce()) {
                ramp = ! ramp;
            }
            if (c.XOnce()) {
                pre_shoot = ! pre_shoot;
            }
            if (c.YOnce()) {
                debug_mode = ! debug_mode;
            }
            telemetry.addData("Ready", robot.isCalibrating() ? "no" : ">>> YES <<<");
            telemetry.addData("(a) Park on", ramp ? "Ramp" : "Vortex");
            telemetry.addData("(b) Shooting", shoot_no);
            telemetry.addData("(x) Shoot", pre_shoot ? "Beginning" : "End");
            telemetry.addData("(y) Debug Mode", debug_mode ? "*** ON ***" : "Off");
            telemetry.update();
        }

        steps.addAll(Arrays.asList(phase_1_steps));
        steps.addAll(Arrays.asList(ramp ? ramp_steps : center_steps));

        onStart();
        robot.resetGyro();

        if (debug_mode) {
            runDebugMode();
        } else {
            runAutoMode();
        }

        onStop();
    }

    private void runDebugMode() throws InterruptedException {
        int i = 0;
        double t0 = getRuntime(), t1 = getRuntime();
        String prev = "None";
        Controller c = new Controller(gamepad1);
        while (opModeIsActive()) {
            c.update();
            if (c.dpadUpOnce()) {
                ++i;
            } else if (c.dpadDownOnce()) {
                --i;
            }
            if (i < 0) {
                i = steps.size() - 1;
            } else if (i >= steps.size()) {
                i = 0;
            }
            telemetry.addData("Current", steps.get(i).getName());
            telemetry.addData("Previous", String.format(Locale.US, "%s: %.2f", prev, t1 - t0));
            telemetry.update();

            if (c.leftBumperOnce()) {
                robot.stopDriveMotors();
                t0 = getRuntime();
                steps.get(i++).act();
                t1 = getRuntime();
            } else {
                DriveHelper.drive(c, robot);
            }
        }
    }

    private void runAutoMode() throws InterruptedException {
        double t0 = getRuntime();
        String prev = "None";
        for(Step s : steps) {
            telemetry.addData("Last", String.format(Locale.US, "%s: %.2f", prev, getRuntime() - t0));
            prev = s.getName();
            t0 = getRuntime();
            telemetry.addData("Phase", prev);
            telemetry.update();
            s.act();
        }
        telemetry.addData("Last", String.format(Locale.US, "%s: %.2f", prev, getRuntime() - t0));
        telemetry.addData("Phase", "Done");
        telemetry.update();
    }

    private void driveToBeacon() {
        while (opModeIsActive() && BeaconFinder.Status.CONTINUE == beaconLoop()) {
            idle();
        }
    }

    private static final double BEACON_PRESSING_MOVE_CM = 10.0;

    // todo: replace the current button pressers with a rack and pinion system
    //       so driving and aligning is not required for this step
    private void pressButton() throws InterruptedException {
        robot.resetGyro();
        final boolean back_button = robot.getColor() == allianceColor();

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }

        snooze(250);

        driveDirectionCM(3.0 * Math.PI / 2.0, BEACON_PRESSING_MOVE_CM);
        driveDirectionCM(Math.PI / 2.0, BEACON_PRESSING_MOVE_CM);

        if (back_button) {
            robot.toggleBackServo();
        } else {
            robot.toggleFrontServo();
        }
        turnToAngle(0);
    }
}
