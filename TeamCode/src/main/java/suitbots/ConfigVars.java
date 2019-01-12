package suitbots;

import com.acmerobotics.dashboard.config.Config;

@Config
public class ConfigVars {
    public static double ENCODER_DRIVE_POWER = .25;

    public static double SLOW_TURN_ANGLE = 30.0;
    public static double TURN_SPEED = .2;
    public static double SLOW_TURN_SPEED = .1;
    public static double TURNING_FUDGE_FACTOR = 2.0;

    public static double ARM_UP_SPEED = 1.0;
    public static double ARM_DOWN_SPEED = .85;
    public static double ARM_UP_SPEED_FINAL = .3;
    public static double ARM_UP_PERCENT_SLOW = .7;

    public static double WALL_DRIVE_KP = .01;
    public static double WALL_DRIVE_KI = 0.0;
    public static double WALL_DRIVE_KD = 0.0;

    public static double INCHES_FROM_WALL = 4.0;

    /// Non-fast speed to drive during Teleop
    public static double TELEOP_SLOW_SPEED = .35;

    /// Position of the dump arm before dumping occurs
    public static int TELEOP_ARM_UP = -465;
    /// Position of the dump arm in the resting position
    public static int TELEOP_ARM_DOWN = 0;
    /// Position of the dump servo when dumping
    public static double TELEOP_DUMP_SERVO_POSITION = .6;
    /// Proportion of the dump servo's full range with which to move over time
    public static double TELEOP_DUMP_SERVO_LIMIT = .7;

    public static double SERVO_ANGLE_EXPONENTIAL = 1.0;

    public static double HARVESTER_SCALE_FACTOR = .75;
}
