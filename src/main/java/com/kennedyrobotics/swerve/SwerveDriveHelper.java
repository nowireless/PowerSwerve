package com.kennedyrobotics.swerve;

import com.kennedyrobotics.math.Rotation2dUtil;

public class SwerveDriveHelper {
    public static final double kEpsilon = 1e-5;


    /**
     * Wheel base length in inches. Distance from modules from front to back.
     */
    private final double wheelBaseLength_;
    /**
     * Track width in inches. Distance from modules from side to side.
     */
    private final double trackWidth_;

    public SwerveDriveHelper(double wheelBaseLength, double trackWidth) {
        wheelBaseLength_ = wheelBaseLength;
        trackWidth_ = trackWidth;
    }

    /**
     * 
     * @todo Create real world version of this function
     * 
     * @param forwardSpeed Forward/reverse command from -1.0 to 1.0
     * @param strafeSpeed Strafe command -1.0 to 1.0. Positive for right movement
     * @param rotateSpeed Rotation command from -1.0 to 1.0. Positive for clockwise 
     * @return Calculated drive state
     */
    public SwerveCommand inverseKinmatics(double forwardSpeed, double strafeSpeed, double rotateSpeed) {
        // The follow equations are from ether's swerve kinematics presentation
        // TODO: Link here
        // The follow are from figure 5.

        // The following are from figure 5
        // double a = strafeSpeed - rotateSpeed * (wheelBaseLength_/2.0);
        // double b = strafeSpeed + rotateSpeed * (wheelBaseLength_/2.0);
        // double c = forwardSpeed - rotateSpeed * (trackWidth_/2.0);
        // double d = forwardSpeed + rotateSpeed * (trackWidth_/2.0);

        // The following are from Eher's spreadsheat
        double r = Math.sqrt(wheelBaseLength_*wheelBaseLength_+trackWidth_*trackWidth_);
        double a = strafeSpeed - rotateSpeed * (wheelBaseLength_/r);
        double b = strafeSpeed + rotateSpeed * (wheelBaseLength_/r);
        double c = forwardSpeed - rotateSpeed * (trackWidth_/r);
        double d = forwardSpeed + rotateSpeed * (trackWidth_/r);

        SwerveCommand state = new SwerveCommand();
        double[] speeds = new double[4];
        speeds[0] = Math.sqrt(b*b+d*d);
        speeds[1] = Math.sqrt(b*b+c*c);
        speeds[2] = Math.sqrt(a*a+d*d);
        speeds[3] = Math.sqrt(a*a+c*c);

        double max = 0;
        for (Double speed : speeds) {
            max = Math.max(max, Math.abs(speed));
        }

        if (1.0 < Math.abs(max)) {
            for (int i = 0; i < speeds.length; i++) {
                speeds[i] = speeds[i] / max; 
            }
        }

        state.frontLeft.speed = speeds[0];
        state.frontRight.speed = speeds[1];
        state.backLeft.speed = speeds[2];
        state.backRight.speed = speeds[3];

        state.frontLeft.angle = Rotation2dUtil.fromRadians(Math.atan2(b, d));
        state.frontRight.angle = Rotation2dUtil.fromRadians(Math.atan2(b, c));
        state.backLeft.angle = Rotation2dUtil.fromRadians(Math.atan2(a, d));
        state.backRight.angle = Rotation2dUtil.fromRadians(Math.atan2(a, c));

        return state;
    }

    public SwerveCommand inverseKinmatics(SwerveSignal signal) {
        return inverseKinmatics(signal.xSpeed(), signal.ySpeed(), signal.rotation());
    }

}