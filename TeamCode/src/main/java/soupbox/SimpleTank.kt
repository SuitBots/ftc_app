package soupbox

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp(name = "Simple Tank")
class SimpleTank : OpMode() {
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
        val l = gamepad1.left_stick_y.toDouble()
        val r = gamepad1.right_stick_y.toDouble()

        fl.power = l
        bl.power = l
        fr.power = r
        br.power = r
    }
}