package soupbox

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

data class MecanumWheels(val fl:Double, val fr:Double, val bl:Double, val br:Double)

fun mecanum(theta:Double, velocity:Double, rotation:Double):MecanumWheels {
    val _s = Math.sin(theta + Math.PI / 4.0)
    val _c = Math.cos(theta + Math.PI / 4.0)
    val m = Math.max(Math.abs(_s), Math.abs(_c))
    val s = _s / m
    val c = _c / m

    val v1 = velocity * s + rotation
    val v2 = velocity * c - rotation
    val v3 = velocity * c + rotation
    val v4 = velocity * s - rotation

    return MecanumWheels(v1, v2, v3, v4)
}

@TeleOp(name = "Simple Mecanum")
class SimpleMecanum : OpMode() {
    lateinit var fl: DcMotor;
    lateinit var fr: DcMotor;
    lateinit var bl: DcMotor;
    lateinit var br: DcMotor;

    override fun init() {
        fl = hardwareMap.dcMotor.get("fl")
        fr = hardwareMap.dcMotor.get("fr")
        bl = hardwareMap.dcMotor.get("bl")
        br = hardwareMap.dcMotor.get("br")
    }

    override fun loop() {
        val x = - gamepad1.left_stick_x.toDouble()
        val y = gamepad1.left_stick_y.toDouble()

        val theta = Math.atan2(x, y)
        val v_theta = Math.sqrt(x * x + y * y)
        val v_rotation = gamepad1.right_stick_x.toDouble();

        val wheels = mecanum(theta, v_theta, v_rotation)

        fl.power = wheels.fl
        fr.power = wheels.fr
        bl.power = wheels.bl
        br.power = wheels.br
    }
}