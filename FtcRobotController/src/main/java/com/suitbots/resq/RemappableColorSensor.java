package com.suitbots.resq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;

import java.util.concurrent.locks.Lock;

/**
 * Created by cp on 11/9/15.
 */
abstract public class RemappableColorSensor extends LinearOpMode {

    public static final int ADDRESS_SET_NEW_I2C_ADDRESS = 0x70;

    // trigger bytes used to change I2C address on ModernRobotics sensors.
    public static final byte TRIGGER_BYTE_1 = 0x55;
    public static final byte TRIGGER_BYTE_2 = (byte) 0xaa;

    public static final byte COLOR_SENSOR_ORIGINAL_ADDRESS = 0x3c;

    public static final int READ_MODE = 0x80;
    public static final int ADDRESS_MEMORY_START = 0x03;
    public static final int TOTAL_MEMORY_LENGTH = 0x06;

    public static final int BUFFER_CHANGE_ADDRESS_LENGTH = 0x03;

    byte[] readCache;
    Lock readLock;
    byte[] writeCache;
    Lock writeLock;

    private void spewReadCache() {
        String bytes = "Read from cache:";
        for (byte b : readCache) {
            bytes += String.format(" %x", b);
        }
        telemetry.addData("Read Cache", bytes);
    }

    int currentAddress = COLOR_SENSOR_ORIGINAL_ADDRESS;
    // I2c addresses on Modern Robotics devices must be divisible by 2, and between 0x7e and 0x10
    // Different hardware may have different rules.
    // Be sure to read the requirements for the hardware you're using!
    int newAddress = 0x42;

    DeviceInterfaceModule dim;

    public void remapColorSensor(int port) throws InterruptedException {

        // set up the hardware devices we are going to use
        dim = hardwareMap.deviceInterfaceModule.get("dim");

        readCache = dim.getI2cReadCache(port);
        readLock = dim.getI2cReadCacheLock(port);
        writeCache = dim.getI2cWriteCache(port);
        writeLock = dim.getI2cWriteCacheLock(port);

        // wait for the start button to be pressed
        // waitForStart();

        performAction("read", port, currentAddress, ADDRESS_MEMORY_START, TOTAL_MEMORY_LENGTH);
        spewReadCache();

        while(!dim.isI2cPortReady(port)) {
            telemetry.addData("I2cAddressChange", "waiting for the port to be ready...");
            sleep(100);
        }
        telemetry.addData("I2cAddressChange", "Port Ready!");

        // update the local cache
        dim.readI2cCacheFromController(port);

        // Enable writes to the correct segment of the memory map.
        performAction("write", port, currentAddress, ADDRESS_SET_NEW_I2C_ADDRESS, BUFFER_CHANGE_ADDRESS_LENGTH);

        waitOneFullHardwareCycle();



        // Write out the trigger bytes, and the new desired address.
        writeNewAddress();
        dim.setI2cPortActionFlag(port);
        dim.writeI2cCacheToController(port);

        telemetry.addData("I2cAddressChange", "Giving the hardware some time to make the change...");

        // Changing the I2C address takes some time.
        for (int i = 0; i < 500; i++) {
            waitOneFullHardwareCycle();
            if(0 == i % 100) {
                telemetry.addData("Waiting", i);
            }
        }

        // Query the new address and see if we can get the bytes we expect.
        dim.enableI2cReadMode(port, newAddress, ADDRESS_MEMORY_START, TOTAL_MEMORY_LENGTH);

        dim.setI2cPortActionFlag(port);
        dim.writeI2cCacheToController(port);

        telemetry.addData("I2cAddressChange", "Successfully changed the I2C address." + String.format("New address: %02x", newAddress));
    }

    private void performAction(String actionName, int port, int i2cAddress, int memAddress, int memLength) {
        if (actionName.equalsIgnoreCase("read")) dim.enableI2cReadMode(port, i2cAddress, memAddress, memLength);
        if (actionName.equalsIgnoreCase("write")) dim.enableI2cWriteMode(port, i2cAddress, memAddress, memLength);

        dim.setI2cPortActionFlag(port);
        dim.writeI2cCacheToController(port);
        dim.readI2cCacheFromController(port);
    }

    private void writeNewAddress() {
        try {
            writeLock.lock();
            writeCache[4] = (byte) newAddress;
            writeCache[5] = TRIGGER_BYTE_1;
            writeCache[6] = TRIGGER_BYTE_2;
        } finally {
            writeLock.unlock();
        }
    }
}
