package com.kennedyrobotics.hardware;

/**
 * Based on https://github.com/TheGreenMachine/Zodiac/blob/master/src/main/java/com/team1816/lib/hardware/ConfigIsAbstractException.java
 */
public class ConfigIsAbstractException extends Exception {

    public ConfigIsAbstractException() {
        super("Cannot instantiate config marked as abstract!");
    }
}