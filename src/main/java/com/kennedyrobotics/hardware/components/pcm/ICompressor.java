package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.util.sendable.Sendable;

public interface ICompressor extends Sendable, AutoCloseable {
    void start();

    void stop();

    boolean enabled();

    double getCompressorCurrent();
}