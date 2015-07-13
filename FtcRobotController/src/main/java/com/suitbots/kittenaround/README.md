# Suit Bots FTC Software

Example code for a ficticious "Kitten Around" game using the fancy
new Android-based control system.

## The Robot

This software assumes a robot [Isaac 5i](Isaac5i.java). It is a very
simple robot. It has four drive wheels. It assumes that the motors are
named "drivelf," "drivelb," "driverf" and "driverb." Of the final two
characters of those names, "l" and "r" refer to "left" and "right"
while "f" and "b" refer to front and back.

## `OpMode`s

### IsaacTheTankTeleop

The first example op mode is a [simple tank drive](IsaacTheTankTeleop.java).
It uses the left joystick on controller 1. (`Start + A` when you first start
the driver station program.) Alls it does is drive.

## Writing a new OpMode

1. Create a new class in the `com.suitbots.kittenaround` package
2. Name the class something descriptive
3. Inherit from the `Isaac5i` class
4. Create a `@Override public void loop() {}` method. This method is executed over and over again during the life of the program.
4. Use the `setDriveMotorPowers()` methods as appropriate.
5. If you need to do initilization or cleanup work in `@Override public void start() {}` or `@Override public void stop {}`, make sure you call the parent class' method via `super.start()` at the beginning of your `start()` method or `super.stop()` at the end of your `stop()` method.
6. Critically, add an entry in the [FtcOpModeRegister](../../qualcomm/ftcrobotcontroller/opmodes/FtcOpModeRegister.java) class to make your new OpMode show up as an option on the driver control station.
7. And you're done.

And that's really all there is to it. As we add motors, servos and sensors
to `Isaac5i`, The `Isaac5i` Java class will gain capabilities that will be
shared between all OpModes.

