import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MrRobot {
    DcMotor lf, rf, lr, rr;

    MrRobot(final HardwareMap hardwareMap) {
        lf = hardwareMap.dcMotor.get("lf");
        lr = hardwareMap.dcMotor.get("lr");
        rf = hardwareMap.dcMotor.get("rf");
        rr = hardwareMap.dcMotor.get("rr");

        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void drive(double left, double right) {
        lf.setPower(left);
        lr.setPower(left);
        rf.setPower(right);
        rr.setPower(right);
    }
}
