package com.kennedyrobotics.swerve;

public class SwerveCommand {
    public SwerveCommand() {
        frontRight = new SwerveModule.ModuleSignal();
        frontLeft = new SwerveModule.ModuleSignal();
        backLeft = new SwerveModule.ModuleSignal();
        backRight = new SwerveModule.ModuleSignal();
    }

    public SwerveModule.ModuleSignal frontRight;
    public SwerveModule.ModuleSignal frontLeft;
    public SwerveModule.ModuleSignal backRight;
    public SwerveModule.ModuleSignal backLeft;

    public void setBrake(boolean brake) {
        frontLeft.brake = brake;
        frontRight.brake = brake;
        backLeft.brake = brake;
        backRight.brake = brake;
    }
}
