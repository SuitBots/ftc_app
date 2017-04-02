package com.suitbots.vv;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

public abstract class AutonomiaRapida extends SteppedAutonomous {
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
        return AllianceColor.RED == allianceColor() ? 2.0 * Math.PI : Math.PI;
    }

    private Step[] blind_man = new Step[] {
            // startHarvester(),
            startGettingLightData(),
            drive("Initial Diagonal", leftForwardDir(), 4.5),
            trueUp(),
            stopGettingLightData(),
            toWhiteLine("To White Line", forwardDir()),
            toWhiteLineSlow("To White Line", Math.PI + forwardDir()),
            trueUp(),
            sneak(),
            press(),
            awayFromWall(.3),
            trueUp(),
            drive("Second Forward", forwardDir(), 1.75),
            trueUp(),
            toWhiteLine("To White Line", forwardDir()),
            toWhiteLineSlow("To White Line", Math.PI + forwardDir()),
            trueUp(),
            sneak(),
            press(),
            awayFromWall(.5),
            rot("Tun to Shoot",-45,-135),
            drive("Towards Vortex", Math.PI, 1.0),
            shoot(),
    };

    private Step[] park_in_center = new Step[] {
            drive("Back to center", Math.PI, 2.0),
    };

    private Step[] park_on_corner = new Step[] {
            rot("Turn to corner", 45, -45),
            drive("Park on corner", Math.PI, 2.0),
    };

    private Step[] basic_steps = blind_man;

    private enum Parking { Center, Corner };

    // TODO: Figure out the ordering re: shoot first + diagonal start v. shoot last
    @Override
    public void runOpMode() throws InterruptedException {
        try {
            Parking parking = Parking.Center;
            initRobot(false);
            Controller c = new Controller(gamepad1);
            boolean debug_mode = false;
            while (!isStarted()) {
                c.update();
                if (c.BOnce()) {
                    shoot_no = (1 + shoot_no) % 3;
                }
                if (c.YOnce()) {
                    debug_mode = !debug_mode;
                }
                if (c.XOnce()) {
                    parking = parking == Parking.Center ? Parking.Corner : Parking.Center;
                }

                telemetry.addData("Ready", robot.isCalibrating() ? "no" : ">>> YES <<<");
                telemetry.addData("(b) Shooting", shoot_no);
                telemetry.addData("(x) Parking", parking.toString());
                telemetry.addData("(y) Debug Mode", debug_mode ? "*** ON ***" : "Off");
                telemetry.addData("Time", getRuntime());
                telemetry.update();
            }

            setSteps(basic_steps);

            if (Parking.Center == parking) {
                appendSteps(park_in_center);
            } else {
                appendSteps(park_on_corner);
            }

            onStart();
            robot.resetGyro();

            if (debug_mode) {
                runDebugMode();
            } else {
                runAutoMode();
            }
        } catch(UnsupportedOperationException uoe) {
            telemetry.addData("Exception", uoe.toString());
            int stackCount = 0;
            StackTraceElement[] stack = uoe.getStackTrace();
            for (StackTraceElement te : stack) {
                telemetry.addData("Stack " + stackCount++, te.getFileName() + ":" + te.getLineNumber());
            }
            telemetry.update();
            while (opModeIsActive()) {
                idle();
            }
        }

        onStop();
    }


    public Step startGettingLightData() {
        return new Step("Start Getting Light Data") {
            @Override
            public void act() throws InterruptedException {
                robot.startCollectingLightMeter();
            }
        };
    }

    public Step stopGettingLightData() {
        return new Step("Stop Getting Light Data") {
            @Override
            public void act() throws InterruptedException {
                robot.startCollectingLightMeter();
                telemetry.addData("Light Average", robot.getAverageLightMeter());
                telemetry.update();
            }
        };
    }

    public Step trueUp() {
        return new Step("True Up") {
            @Override
            public void act() throws InterruptedException {
                turnToAngle(0);
            }
        };
    }

    public Step toWhiteLine(String name, final double direction) {
        return new Step(name) {
            @Override
            public void act() throws InterruptedException {
                driveToWhiteLine(direction);
            }
        };
    }

    public Step toWhiteLineSlow(String name, final double direction) {
        return new Step(name) {
            @Override
            public void act() throws InterruptedException {
                driveToWhiteLineSlow(direction);
            }
        };
    }

    public Step drive(String name, final double direction, final double tiles) {
        return new Step(name) {
            @Override
            public void act() throws InterruptedException {
                driveDirectionTilesFast(direction, tiles);
            }
        };
    }

    public Step sneak() {
        return new Step("Sneak to beacon") {
            @Override
            public void act() throws InterruptedException {
                sneakToBeacons();
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
                robot.setHarvesterPower(-1.0);
                shoot(shoot_no);
                robot.setHarvesterPower(0.0);
            }
        };
    }

    private Step startHarvester() {
        return new Step("Start Harvester") {
            @Override
            public void act() throws InterruptedException {
                new Thread(new Runnable() {
                    @Override public void run() {
                        try {
                            Thread.sleep(250);
                        } catch(InterruptedException ie) {

                        }
                        robot.setHarvesterPower(-1.0);
                    }
                }).run();
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

    private Step waitToSeeIfItCanSeeTarget () {
        return new Step("waitToSeeIfItCanSeeTarget"){
            @Override
            public void act() throws InterruptedException{
                for (int i = 0; i < 40; ++i) {
                    if (vision.canSeeWall()) {
                        break;
                    }
                    sleep(200);
                }
            }
        };
    }

    private Step goLeftIfYouCanNotSeeTheTarget() {
        return new Step("Left if no target") {
            @Override
            public void act() throws InterruptedException {
                if (! vision.canSeeWall()) {
                    driveDirectionTiles(3.0 * Math.PI / 2.0, .25, .2);
                }
            }
        };
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
