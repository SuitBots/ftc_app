package soupbox

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

class Robot constructor(hardwareMap: HardwareMap, telemetry: Telemetry, debug: Boolean = false) {
    val fl : DcMotor;
    val bl : DcMotor;
    val fr : DcMotor;
    val br : DcMotor;
    val telemetry: Telemetry
    val debug: Boolean

    init {
        fl = hardwareMap.dcMotor.get("fl")
        fr = hardwareMap.dcMotor.get("fr")
        bl = hardwareMap.dcMotor.get("bl")
        br = hardwareMap.dcMotor.get("br")

        fl.direction = DcMotorSimple.Direction.REVERSE
        bl.direction = DcMotorSimple.Direction.REVERSE
        this.telemetry = telemetry
        this.debug = debug
    }

    fun drive(l: Double, r: Double) {
        drive(l, r, l, r)
    }

    fun drive(fl: Double, fr: Double, bl: Double, br: Double) {
        this.fl.power = fl
        this.fr.power = fr
        this.bl.power = bl
        this.br.power = br

        if (debug) {
            telemetry.addData("Drivetrain", "${fl} ${fr} ${bl} ${br}")
        }
    }
}