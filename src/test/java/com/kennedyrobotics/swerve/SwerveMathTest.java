package com.kennedyrobotics.swerve;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SwerveMathTest {
    public static final double kTestEpsilon = 1e-5;

    @Test
    public void testFindClosestAngle() {
        assertEquals(0, SwerveMath.findClosestAngle(0, 0), kTestEpsilon);

        assertEquals(Math.toRadians(0), 
            SwerveMath.findClosestAngle(Math.toRadians(0), Math.toRadians(0)),
            kTestEpsilon
        );

        assertEquals(Math.toRadians(30), 
            SwerveMath.findClosestAngle(Math.toRadians(0), Math.toRadians(30)),
            kTestEpsilon
        );

        assertEquals(Math.toRadians(-30), 
            SwerveMath.findClosestAngle(Math.toRadians(0), Math.toRadians(-30)),
            kTestEpsilon
        );

        
    }

}