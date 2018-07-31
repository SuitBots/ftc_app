package soupbox

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

fun smooth(x : Double) : Double {
    return Math.pow(x, 3.0)
}

@TeleOp(name = "Simple Tank", group = "Preseason")
class SimpleTank : OpMode() {
    lateinit var robot: Robot;

    override fun init() {
        robot = Robot(hardwareMap, telemetry)
    }

    override fun loop() {
        var f = smooth(gamepad1.left_stick_y.toDouble())
        var t = smooth(- gamepad1.right_stick_x.toDouble())

        val d = Math.max(1.0, Math.abs(f) + Math.abs(t))

        robot.drive((f + t) / d, (f - t) / d)
    }

}