package com.kennedyrobotics.swerve;

import com.team254.lib.geometry.Rotation2d;

/**
 * A swerve drive command consisting of the forward and strafing commands and whether the brake mode is enabled.
 */
public class SwerveSignal {

    public enum ControlOrientation {
        kRobotCentric,
        kFieldCentric
    }

    protected double xSpeed_;
    protected double ySpeed_;
    protected double rotation_;
    protected boolean brakeMode_;
    protected ControlOrientation controlOrientation_;

    public SwerveSignal(double xSpeed, double ySpeed, double rotation) {
        this(xSpeed, ySpeed, rotation, false, ControlOrientation.kRobotCentric);
    }

    public SwerveSignal(double xSpeed, double ySpeed, double rotation, ControlOrientation controlOrientation) {
        this(xSpeed, ySpeed, rotation, false, controlOrientation);
    }

    public SwerveSignal(double xSpeed, double ySpeed, double rotation, boolean brakeMode) {
        this(xSpeed, ySpeed, rotation, brakeMode, ControlOrientation.kRobotCentric);
    }

    public SwerveSignal(double xSpeed, double ySpeed, double rotation, boolean brakeMode, ControlOrientation controlOrientation) {
        xSpeed_ = xSpeed;
        ySpeed_ = ySpeed;
        rotation_ = rotation;
        brakeMode_ = brakeMode;
        controlOrientation_ = controlOrientation;
    }

    public static SwerveSignal NEUTRAL = new SwerveSignal(0, 0, 0);
    public static SwerveSignal BRAKE = new SwerveSignal(0, 0, 0, true);

    /**
     * @return Forward command between -1 and 1
     */
    public double xSpeed() { return xSpeed_; }

    /**
     * @return Strafe command between -1 and 1
     */
    public double ySpeed() { return ySpeed_; }

    /**
     * @return Rotation command between -1 and 1
     */
    public double rotation() { return rotation_; }

    /**
     * @return Is brake mode enabled
     */
    public boolean brakeMode() { return brakeMode_; }

    /**
     * 
     * @return Field or robot centric driving orientation
     */
    public ControlOrientation orientation() { return controlOrientation_; }

    /**
     * 
     * @param heading Current robot in radians
     */
    public SwerveSignal fieldOrient(Rotation2d heading) {
        double angle = Math.IEEEremainder(-heading.getDegrees(), 360);
        angle = Math.toRadians(angle);



        double temp  =  xSpeed_ * Math.cos(angle) + ySpeed_ * Math.sin(angle);
        ySpeed_  =  ySpeed_ * Math.cos(angle) - xSpeed_ * Math.sin(angle);
        xSpeed_ = temp;

        return this;
    }

    @Override
    public String toString() {
        return "X: " + xSpeed_ + ", Y: " + ySpeed_ + ", R: " + rotation_ + (brakeMode_ ? ", BRAKE" : "");
    }

}