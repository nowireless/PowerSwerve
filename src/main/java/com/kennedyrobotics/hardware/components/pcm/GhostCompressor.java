package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.CompressorConfigType;

public class GhostCompressor implements ICompressor {

    private CompressorConfigType compressorConfigType = CompressorConfigType.Disabled;
    private boolean enabled = false;

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public boolean getPressureSwitchValue() {
        return false;
    }

    @Override
    public double getCurrent() {
        return 0;
    }

    @Override
    public double getAnalogVoltage() {
        return 0;
    }

    @Override
    public double getPressure() {
        return 0;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void enableDigital() {
        this.enabled = true;
        compressorConfigType = CompressorConfigType.Digital;
    }

    @Override
    public void enableAnalog(double minPressure, double maxPressure) {
        this.enabled = true;
        compressorConfigType = CompressorConfigType.Analog;
    }

    @Override
    public void enableHybrid(double minPressure, double maxPressure) {
        this.enabled = true;
        compressorConfigType = CompressorConfigType.Hybrid;
    }

    @Override
    public CompressorConfigType getConfigType() {
        return compressorConfigType;
    }


    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Compressor");
        builder.addBooleanProperty(
                "Enabled",
                this::enabled,
                value -> {
                    if (value) {
                        enabled();
                    } else {
                        disable();
                    }
                }
        );
    }

    @Override
    public void close() throws Exception {}
}