package com.kennedyrobotics.swerve.drive;

public interface DriveMotor {

    enum ControlMode {
        kPercentOutput,
        kVelocity,
        kVoltage,
        kPosition,
        kMotionProfile
    }

    void initialize();
    void set(ControlMode mode, double value);
    void setInverted(boolean inverted);
    void setBrake(boolean brake);
    void stopMotor();
}
