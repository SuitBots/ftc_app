package soupbox

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Simple Tank", group = "Preseason")
class SimpleTank : OpMode() {
    lateinit var robot: Robot;

    override fun init() {
        robot = Robot(hardwareMap, telemetry)
    }

    override fun loop() {
        robot.drive(gamepad1.left_stick_y.toDouble(), gamepad1.right_stick_y.toDouble())
    }
}