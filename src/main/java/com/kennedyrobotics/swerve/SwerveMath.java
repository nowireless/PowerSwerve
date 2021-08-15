package com.kennedyrobotics.swerve;

public class SwerveMath {

    /**
     * 
     * @param currentAngle Current angle in radians
     * @param goalAngle Desired angle in radians (+/- PI)
     * @return Goal angle relative to current angle
     */
    public static double findClosestAngle(double currentAngle, double goalAngle) {
        // TODO: Does the following perserive sign? and bound the following to -/+ PI?
        double heading  = currentAngle % Math.PI;
        double offset = currentAngle - heading;

        return offset + goalAngle;
    }
}