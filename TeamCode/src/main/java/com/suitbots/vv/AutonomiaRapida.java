package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public abstract class AutonomiaRapida extends AutonomousBase {
    private int shoot_no = 2;

    public abstract AllianceColor allianceColor();

    @Autonomous(name = "Beacons RED", group = "Tournament")
    public static class Red extends AutonomiaRapida {
        @Override
        public AllianceColor allianceColor() { return AllianceColor.RED; }
    }

    @Autonomous(name = "Beacons BLUE", group = "Tournament")
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

    public Step drive(String name, final double direction, final double tiles) {
        return new Step(name) {
            @Override
            public void act() throws InterruptedException {
                driveDirectionTilesFast(direction, tiles);
            }
        };
    }

    public Step rot(String name, final int if_red, final int if_blue) {
        return new Step(name) {
            @Override
            public void act() throws InterruptedException {
                if(AllianceColor.RED == allianceColor()) {
                    turn(if_red);
                } else {
                    turn(if_blue);
                }
            }
        };
    }

    private Step approach() {
        return new Step("Approach Beacon") {
            @Override
            public void act() throws InterruptedException {
                driveToBeacon();
            }
        };
    }

    private Step press() {
        return new Step("Press Button 1") {
            @Override
            public void act() throws InterruptedException {
                pressButton();
            }
        };
    }

    private Step awayFromWall(final double distance) {
        return new Step("Away from Wall 1") {
            @Override
            public void act() throws InterruptedException {
                driveDirectionTiles(pressersDir() + Math.PI, distance);
            }
        };
    }

    private Step awayFromWall() {
        return awayFromWall(.75);
    }

    private Step trueToWall() {
        return new Step("True to wall") {
            @Override
            public void act() throws InterruptedException {
                alignToVisionTarget();
            }
        };
    }

    private Step shoot() {
        return new Step("Shoot") {
            @Override
            public void act() throws InterruptedException {
                shoot(shoot_no);
                robot.setHarvesterPower(0.0);
            }
        };
    }

    private Step startHarvester() {
        return new Step("Start Harvester") {
            @Override
            public void act() throws InterruptedException {
                robot.setHarvesterPower(- 1.0);
            }
        };
    }

    private Step turnParallel(String name, final int if_red, final int if_blue) {
        return new Step(name) {
            @Override
            public void act() throws InterruptedException {
                if (AllianceColor.RED == allianceColor()) {
                    turnUntilBeaconIsVisible(if_red);
                } else {
                    turnUntilBeaconIsVisible(if_blue);
                }
            }
        };
    }

    private Step[] quick_beacons = new Step[] {
            drive("Initial Forward", forwardDir(), 2.0),
            drive("Initial Left", leftForwardDir(), 1.0),
            approach(),
            press(),
            awayFromWall(),
            trueToWall(),
            drive("Back", forwardDir(), 2.0),
            approach(),
            press(),
            awayFromWall(),
            trueToWall(),
            rot("Turn towards bucket", -45, -135),
            drive("To shoot", Math.PI, Math.sqrt(2.0)),
            shoot(),
            drive("To Center", Math.PI, Math.sqrt(2.0))
    };

    private Step[] han_solo = new Step[] {
            startHarvester(),
            drive("Initial Forward",  Math.PI, .65),
            shoot(),
            rot("Turn towards beacon", 120, 50),
            drive("Drive to Beacon", forwardDir(), 2.3),
            turnParallel("Turn parallel", 60, -60),
            approach(),
            press(),
            awayFromWall(),
            trueToWall(),
            drive("Next beacon", forwardDir(), 2.0),
            approach(),
            press(),
            awayFromWall(.5),
            trueToWall(),
            drive("Back to ramp", forwardDir() + Math.PI, 3.5)
    };

    private Step[] good_old_steps = new Step[] {
            new Step("Initial Forward") {
                @Override
                public void act() throws InterruptedException {
                    robot.resetGyro();
                    driveDirectionTilesFast(Math.PI, 0.65);
                    turnToAngle(0);
                }
            },
            new Step("Shoot") {
                @Override
                public void act() throws InterruptedException {
                    shoot(shoot_no);
                }
            },
            new Step("Turn towards Beacon") {
                @Override
                public void act() throws InterruptedException {
                    if (AllianceColor.RED == allianceColor()) {
                        turn(120);
                    } else {
                        turn(50);
                    }
                }
            },
            new Step("Drive To Beacon") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTilesFast(forwardDir(), 2.3);
                }
            },
            new Step("Turn parallel to wall") {
                @Override
                public void act() throws InterruptedException {
                    if (AllianceColor.RED == allianceColor()) {
                        turnUntilBeaconIsVisible(60);
                    } else {
                        turnUntilBeaconIsVisible(-60);
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
            new Step("Away from Wall 1") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(pressersDir() + Math.PI, .75);
                }
            },
            new Step("True to wall 1") {
                @Override
                public void act() throws InterruptedException {
                    alignToVisionTarget();
                }
            },
            new Step("Next Beacon") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTilesFast(forwardDir(), 2);
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
            },
            new Step("Away from Wall") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTiles(pressersDir() + Math.PI, .5);
                }
            },
            new Step("True to wall 2") {
                @Override
                public void act() throws InterruptedException {
                    alignToVisionTarget();
                }
            },
            new Step("Back to Ramp") {
                @Override
                public void act() throws InterruptedException {
                    driveDirectionTilesFast(forwardDir() + Math.PI, 3.5);
                }
            }
    };

    private Step[] basic_steps = han_solo;

    private ArrayList<Step> steps = new ArrayList<>();

    // TODO: Figure out the ordering re: shoot first + diagonal start v. shoot last
    @Override
    public void runOpMode() throws InterruptedException {
        initRobot();
        Controller c = new Controller(gamepad1);
        boolean debug_mode = false;
        while (! isStarted()) {
            c.update();
            if (c.BOnce()) {
                shoot_no = (1 + shoot_no) % 3;
            }
            if (c.YOnce()) {
                debug_mode = ! debug_mode;
            }
            if (c.XOnce()) {
                if (basic_steps == han_solo) {
                    basic_steps = quick_beacons;
                } else {
                    basic_steps = han_solo;
                }
            }
            telemetry.addData("Ready", robot.isCalibrating() ? "no" : ">>> YES <<<");
            telemetry.addData("(b) Shooting", shoot_no);
            telemetry.addData("(y) Debug Mode", debug_mode ? "*** ON ***" : "Off");
            telemetry.addData("(x) Strategy", han_solo == basic_steps ? "Han Solo" : "Sonic");
            telemetry.addData("Time", getRuntime());
            telemetry.update();
        }

        steps.addAll(Arrays.asList(basic_steps));

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
                i++;
            } else if (c.dpadDownOnce()) {
                i--;
            } else if (c.leftBumperOnce()) {
                robot.stopDriveMotors();
                t0 = getRuntime();
                steps.get(i++).act();
                t1 = getRuntime();
            } else {
                // We don't need dpad mode here, and we don't want to throw off the
                // autonomous by moving a little bit after a mode switch
                if (! (c.dpadDown() || c.dpadUp() || c.dpadLeft() || c.dpadRight())) {
                    DriveHelper.drive(c, robot);
                }
            }
            if (i < 0) {
                i = steps.size() - 1;
            } else if (i >= steps.size()) {
                i = 0;
            }

            telemetry.addData("Current", steps.get(i).getName());
            telemetry.addData("Previous", String.format(Locale.US, "%s: %.2f", prev, t1 - t0));
            telemetry.update();

        }
    }

    private void runAutoMode() throws InterruptedException {
        double t0 = getRuntime();
        double begin = 50;
        String prev = "None";
        for(Step s : steps) {
            if (! opModeIsActive()) {
                break;
            }
            telemetry.addData("Last", String.format(Locale.US, "%s: %.2f", prev, getRuntime() - t0));
            prev = s.getName();
            t0 = getRuntime();
            telemetry.addData("Phase", prev);
            telemetry.update();
            s.act();
        }
        telemetry.addData("Last", String.format(Locale.US, "%s: %.2f", prev, getRuntime() - t0));
        telemetry.addData("Phase", "Done");
        telemetry.addData("Total", getRuntime() - begin);
        telemetry.update();
    }

    // TODO: Handle NO_DATA in a more useful way.
    // Currently we just sit in one spot and wait if there's NO_DATA.
    // Maybe, if it persists for half a second or so, we should drive
    // around a bit to see if we can't get a signal elsewhere.
    private void driveToBeacon() throws InterruptedException {
        int no_data = 0;
        while (opModeIsActive() && no_data < 10) {
            BeaconFinder.Status s = beaconLoop();
            if (s == BeaconFinder.Status.NO_DATA) {
                telemetry.addData("Beacon", "INVISIBLE");
                ++no_data;
                sleep(100);
            } else if (s == BeaconFinder.Status.CONTINUE) {
                no_data = 0;
                telemetry.addData("Beacon", "INVISIBLE");
                idle();
            } else {
                break;
            }
            telemetry.update();
        }
    }

    // todo: replace the current button pressers with a rack and pinion system
    //       so driving and aligning is not required for this step
    private void pressButton() throws InterruptedException {
        robot.resetGyro();
        AllianceColor beacon = robot.getColor();
        if (beacon == allianceColor()) {
            robot.pressBackButton();
        } else if (beacon != AllianceColor.NONE) {
            robot.pressFrontButton();
        }
    }
}
