package com.kennedyrobotics.hardware;

import edu.wpi.first.wpilibj.geometry.Rotation2d;

public interface HeadingProvider {

    /**
     * Get the current Heading/Yaw angle of the sensor
     * @return current heading of the sensor
     */
    Rotation2d getYaw();

    /**
     * Set the real word heading of the sensor. After this call all calls to getYaw will be adjusted to reflect
     * this new offset
     * @param realWorldHeading current real world heading of the robot.
     */
    void setAngle(Rotation2d realWorldHeading);
}
