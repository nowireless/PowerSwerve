package com.kennedyrobotics.util;

import edu.wpi.first.wpilibj.DriverStation;

import java.lang.reflect.Field;

public class JoystickWarningHelper {
    private JoystickWarningHelper() {}

    public static void disableWarning() {
        // Need to set m_nextMessageTime in the DriverStation class to the max value for double
        try {
            Field f = DriverStation.class.getDeclaredField("m_nextMessageTime");
            f.setAccessible(true);
            f.set(DriverStation.getInstance(), Double.MAX_VALUE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
