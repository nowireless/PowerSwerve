package com.kennedyrobotics.swerve;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SwerveDriveHelperTest {
    public static final double kTestEpsilon = 1e-5;


    @Test
    public void test() {
        SwerveDriveHelper k = new SwerveDriveHelper(30, 30);
     
        SwerveCommand state = k.inverseKinmatics(1.0, 0, 0);
        assertEquals(1, state.frontLeft.speed, kTestEpsilon);
        assertEquals(1, state.frontRight.speed, kTestEpsilon);
        assertEquals(1, state.backLeft.speed, kTestEpsilon);
        assertEquals(1, state.backRight.speed, kTestEpsilon);

        assertEquals(0, state.frontLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(0, state.frontRight.angle.getRadians(), kTestEpsilon);
        assertEquals(0, state.backLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(0, state.backRight.angle.getRadians(), kTestEpsilon);

        state = k.inverseKinmatics(0.0, 0, 0);
        assertEquals(0, state.frontLeft.speed, kTestEpsilon);
        assertEquals(0, state.frontRight.speed, kTestEpsilon);
        assertEquals(0, state.backLeft.speed, kTestEpsilon);
        assertEquals(0, state.backRight.speed, kTestEpsilon);

        assertEquals(0, state.frontLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(0, state.frontRight.angle.getRadians(), kTestEpsilon);
        assertEquals(0, state.backLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(0, state.backRight.angle.getRadians(), kTestEpsilon);

        state = k.inverseKinmatics(0.0, 0, 1);
        assertEquals(1, state.frontLeft.speed, kTestEpsilon);
        assertEquals(1, state.frontRight.speed, kTestEpsilon);
        assertEquals(1, state.backLeft.speed, kTestEpsilon);
        assertEquals(1, state.backRight.speed, kTestEpsilon);

        assertEquals(Math.PI/4.0, state.frontLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(3*Math.PI/4.0, state.frontRight.angle.getRadians(), kTestEpsilon);
        assertEquals(-Math.PI/4.0, state.backLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(-3*Math.PI/4.0, state.backRight.angle.getRadians(), kTestEpsilon);

        state = k.inverseKinmatics(0.0, 0, -1);
        assertEquals(1, state.frontLeft.speed, kTestEpsilon);
        assertEquals(1, state.frontRight.speed, kTestEpsilon);
        assertEquals(1, state.backLeft.speed, kTestEpsilon);
        assertEquals(1, state.backRight.speed, kTestEpsilon);

        assertEquals(-3*Math.PI/4.0, state.frontLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(-Math.PI/4.0, state.frontRight.angle.getRadians(), kTestEpsilon);
        assertEquals(3*Math.PI/4.0, state.backLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(Math.PI/4.0, state.backRight.angle.getRadians(), kTestEpsilon);

        state = k.inverseKinmatics(1, 1, 0);
        assertEquals(1, state.frontLeft.speed, kTestEpsilon);
        assertEquals(1, state.frontRight.speed, kTestEpsilon);
        assertEquals(1, state.backLeft.speed, kTestEpsilon);
        assertEquals(1, state.backRight.speed, kTestEpsilon);

        assertEquals(Math.PI/4.0, state.frontLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(Math.PI/4.0, state.frontRight.angle.getRadians(), kTestEpsilon);
        assertEquals(Math.PI/4.0, state.backLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(Math.PI/4.0, state.backRight.angle.getRadians(), kTestEpsilon);

        state = k.inverseKinmatics(0.5, 0.5, 0.5);
        assertEquals(1, state.frontLeft.speed, kTestEpsilon);
        assertEquals(0.717438935, state.frontRight.speed, kTestEpsilon);
        assertEquals(0.717438935, state.backLeft.speed, kTestEpsilon);
        assertEquals(0.171572875, state.backRight.speed, kTestEpsilon);

        assertEquals(Math.PI/4.0, state.frontLeft.angle.getRadians(), kTestEpsilon);
        assertEquals(80.3*Math.PI/180.0, state.frontRight.angle.getRadians(), 0.01);
        assertEquals(9.7*Math.PI/180.0, state.backLeft.angle.getRadians(), 0.01);
        assertEquals(Math.PI/4.0, state.backRight.angle.getRadians(), kTestEpsilon);

        state = k.inverseKinmatics(6.10E-05, -0.010314941, 0.0);
        assertEquals(0.010315122, state.frontLeft.speed, kTestEpsilon);
        assertEquals(0.010315122, state.frontRight.speed, kTestEpsilon);
        assertEquals(0.010315122, state.backLeft.speed, kTestEpsilon);
        assertEquals(0.010315122, state.backRight.speed, kTestEpsilon);

        assertEquals(-89.7*Math.PI/180.0, state.frontLeft.angle.getRadians(), 0.01);
        assertEquals(-89.7*Math.PI/180.0, state.frontRight.angle.getRadians(), 0.01);
        assertEquals(-89.7*Math.PI/180.0, state.backLeft.angle.getRadians(), 0.01);
        assertEquals(-89.7*Math.PI/180.0, state.backRight.angle.getRadians(), 0.01);

    }
}