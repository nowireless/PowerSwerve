package com.kennedyrobotics.math;

import edu.wpi.first.math.geometry.Rotation2d;

public class Rotation2dUtil {

    protected static final Rotation2d kIdentity = new Rotation2d();

    public static final Rotation2d identity() {
        return kIdentity;
    }

    public static Rotation2d fromRadians(double radians) { return new Rotation2d(radians); }

}
