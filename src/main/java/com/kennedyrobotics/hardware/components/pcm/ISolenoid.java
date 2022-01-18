package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.util.sendable.Sendable;

public interface ISolenoid extends Sendable, AutoCloseable {
    boolean get();
    void set(boolean on);
    void toggle();
}