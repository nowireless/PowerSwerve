package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.CompressorConfigType;

/**
 * Interface for {@link edu.wpi.first.wpilibj.Compressor}
 */
public interface ICompressor extends Sendable, AutoCloseable {
    /**
     * Get the status of the compressor.
     *
     * @return true if the compressor is on
     */
    boolean enabled();

    /**
     * Get the pressure switch value.
     *
     * @return true if the pressure is low
     */
    boolean getPressureSwitchValue();

    /**
     * Get the current being used by the compressor.
     *
     * @return current consumed by the compressor in amps
     */
    double getCurrent();

    /**
     * Query the analog input voltage (on channel 0) (if supported).
     *
     * @return The analog input voltage, in volts
     */
    double getAnalogVoltage();

    /**
     * Query the analog sensor pressure (on channel 0) (if supported). Note this is only for use with
     * the REV Analog Pressure Sensor.
     *
     * @return The analog sensor pressure, in PSI
     */
    double getPressure();

    /** Disable the compressor. */
    void disable();

    /** Enable compressor closed loop control using digital input. */
    void enableDigital();

    /**
     * Enable compressor closed loop control using analog input. Note this is only for use with the
     * REV Analog Pressure Sensor.
     *
     * <p>On CTRE PCM, this will enable digital control.
     *
     * @param minPressure The minimum pressure in PSI to enable compressor
     * @param maxPressure The maximum pressure in PSI to disable compressor
     */
    void enableAnalog(double minPressure, double maxPressure);

    /**
     * Enable compressor closed loop control using hybrid input. Note this is only for use with the
     * REV Analog Pressure Sensor.
     *
     * <p>On CTRE PCM, this will enable digital control.
     *
     * @param minPressure The minimum pressure in PSI to enable compressor
     * @param maxPressure The maximum pressure in PSI to disable compressor
     */
    void enableHybrid(double minPressure, double maxPressure);

    /**
     * Gets the current operating mode of the Compressor.
     *
     * @return true if compressor is operating on closed-loop mode
     */
    CompressorConfigType getConfigType();
}