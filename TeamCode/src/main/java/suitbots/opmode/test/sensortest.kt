package suitbots.opmode.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.*
import com.suitbots.util.Blinken
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit


@TeleOp(name = "Cold", group = "test")
class ParallaxPing : OpMode() {
    lateinit var colDist : DistanceSensor;

    override fun init() {
        colDist =  hardwareMap.get(DistanceSensor::class.java, "dist")
    }

    fun dist() : Double {
        val d = colDist.getDistance(DistanceUnit.CM)
        if (null == d || d.isNaN()) {
            return Double.MAX_VALUE
        } else{
            return d;
        }
    }

    override fun loop() {
        telemetry.addData("distance", dist())
        telemetry.update()
    }
}

class Ringbuf (val size : Int){
    val buffer = ArrayList<Double>(size)
    var count = 0

    init {
        buffer.ensureCapacity(size)
        for (i in 0..size) {
            buffer.add(0.0)
        }
    }

    fun add(x : Double) {
        buffer.set(count++ % size, x)
    }

    fun mean() : Double {
        return buffer.sum() / Math.min(count, size).toDouble()
    }

    fun count() : Int {
        return Math.min(count, size)
    }
}

class MaxBotixEZ1(analog : AnalogInput) {
    val orig : AnalogInput
    val scale : Double;

    init {
        orig = analog
        scale = if (analog.maxVoltage < 5.0) {
            1024.0 / analog.maxVoltage
        } else {
            512.0 / analog.maxVoltage
        }
    }

    private fun raw() : Double {
        return scale * orig.voltage
    }

    fun dist(unit : DistanceUnit) : Double {
        return Math.max(unit.fromInches(raw()), minDist(unit))
    }

    fun minDist(unit : DistanceUnit) : Double {
        return unit.fromInches(7.0)
    }
}

@TeleOp(name = "Dist", group = "test")
class Dist : OpMode() {
    lateinit var dist : MaxBotixEZ1;


    override fun init() {
        dist = MaxBotixEZ1(hardwareMap.analogInput.get("fsr"))
    }

    override fun init_loop() {
        super.init_loop()
    }

    override fun loop() {
        telemetry.addData("min Inch", dist.minDist(DistanceUnit.INCH))
        telemetry.addData("cur Inch", dist.dist(DistanceUnit.INCH))
        telemetry.update()
    }
}

@TeleOp(name = "FSR", group = "test")
class FSR : OpMode() {
    lateinit var blink : Blinken
    lateinit var fsr : AnalogInput
    var buf = Ringbuf(30)
    var zerovalue = 0.0

    override fun init_loop() {
        val v = fsr.voltage
        buf.add(v)
        telemetry.addData("InitFSR", v)
        telemetry.addData("InitMean", buf.mean())
        telemetry.addData("Init Count", buf.count)
        telemetry.update()
    }

    override fun start() {
        zerovalue = buf.mean()
    }

    override fun init() {
        blink = Blinken(hardwareMap.servo.get("blink"))
        fsr = hardwareMap.analogInput.get("fsr")
    }

    fun triggered() : Boolean {
        val v = fsr.voltage
        return Math.abs(v) > Math.abs(zerovalue * 1.01)
    }

    override fun loop() {
        val v = fsr.voltage
        telemetry.addData("Zero Value", zerovalue)
        telemetry.addData("FSR", v)
        telemetry.addData("Triggered?", if (triggered()) "yes" else "no")
        telemetry.update()

        if (triggered()) {
            blink.enactFixedPalettePatternConfetti()
        } else {
            blink.off()
        }
    }
}