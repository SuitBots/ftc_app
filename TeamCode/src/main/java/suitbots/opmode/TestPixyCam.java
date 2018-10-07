package suitbots.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import suitbots.sensor.PixyCam;

// From this forum posting: https://ftcforum.usfirst.org/forum/ftc-technology/android-studio/7211-i2c/page2
// Be sure to read the entire post and get the PixyCam in the correct mode before you try it out.
// Thank you, team 7330!

@TeleOp(name="TestPixyCam")
public class TestPixyCam extends OpMode
{

    PixyCam pixyCam;
    ElapsedTime elapsedTime = new ElapsedTime();

    /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init()
    {
        pixyCam = hardwareMap.get(PixyCam.class, "pixycam");
    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    @Override
    public void loop()
    {
        if (elapsedTime.milliseconds() > 100) // Update every tenth of a second.
        {
            elapsedTime.reset();
            final PixyCam.Block block1 = pixyCam.GetBiggestBlock(1);
            telemetry.addData("Block 1:", block1.toString());
            final PixyCam.Block block2 = pixyCam.GetBiggestBlock(2);
            telemetry.addData("Block 2:", block2.toString());
        }
    }
}
