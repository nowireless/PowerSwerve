package com.kennedyrobotics.swerve;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.kennedyrobotics.hardware.CANConstants;
import com.kennedyrobotics.swerve.drive.DriveMotor;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.subsystems.CheesySubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule implements CheesySubsystem {
    private static final double kSteerNativeUnitsPerRotationQuad = 4 * 7 * 71 * 40.0/48.0;
    public static final double kSteerNativeUnitsPerRotationAbs = 1023;
    public static final int kSteerPIDId = 0;

    /**
     * Operating mode of the steering motor.
     */
    public enum EncoderType {
        kQuadrature,
        kAbsolute,
    }

    public static class ModuleSignal {
        /**
         * Speed of drive wheel from -1.0, 1.0
         */
        public double speed;

        /**
         * Angle of drive wheel in radians
         */
        public Rotation2d angle = Rotation2d.identity();

        /**
         * Brake Mode
         */
        public boolean brake;
    }

    public static class Config {
        public double kP;
        public double kI;
        public double kD;

        public double nativeUnitsPerRotationAbs;
        public Rotation2d moduleOffset = Rotation2d.identity();
    }

    public static Config getDefaultConfig() {
        return getDefaultConfig(0, 0, 0);
    }

    public static Config getDefaultConfig(double p, double i, double d) {
        Config config = new Config();
        config.kP = p;
        config.kI = i;
        config.kD = d;

        config.nativeUnitsPerRotationAbs = kSteerNativeUnitsPerRotationAbs;
        config.moduleOffset = Rotation2d.identity();

        return config;
    }

    private class PeriodicIO {
        boolean ready = false;

        // INPUTS
        double absoluteAngleRaw;
        Rotation2d absoluteAngle;
        double steerClosedLoopError;


        // OUTPUTS
        boolean brakeMode;
        double driveValue;
        Rotation2d goalAngle = Rotation2d.identity();
    }


    private final double kSteerVoltageRamp = 0.1;

    private final Config config_;
    private final String name_;
    private final DriveMotor drive_;
    private final IMotorControllerEnhanced steer_;

    private boolean inverted_;

    private PeriodicIO periodicIO_ = new PeriodicIO();

    public SwerveModule(String name, Config config, IMotorControllerEnhanced steer, DriveMotor drive) {
        name_ = name;
        config_ = config;

        /*
         * Configure Drive motor
         */
        drive_ = drive;
        drive_.setInverted(false);

        /*
         * Configure Steer motor 
         */
        steer_ = steer;
        steer_.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, 100);
        steer_.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20, 100);
        steer_.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1000, 100);
        steer_.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 1000, 100);
        steer_.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 1000, 100);
        steer_.setStatusFramePeriod(StatusFrameEnhanced.Status_12_Feedback1, 1000, 100);

        final ErrorCode sensorPresent = steer_.configSelectedFeedbackSensor(FeedbackDevice
            .Analog, 0, 100); //primary closed-loop, 100 ms timeout
        if (sensorPresent != ErrorCode.OK) {
            DriverStation.reportError("Could not detect " + name_ + " encoder: " + sensorPresent, false);
        }

        steer_.setInverted(true);
        steer_.setSensorPhase(false);
        steer_.enableVoltageCompensation(true);
        steer_.configVoltageCompSaturation(12.0, CANConstants.kLongTimeoutMs); // TODO Should this be change to something like 10v
        steer_.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, CANConstants.kLongTimeoutMs);
        steer_.configVelocityMeasurementWindow(1, CANConstants.kLongTimeoutMs);
        steer_.configClosedloopRamp(kSteerVoltageRamp, CANConstants.kLongTimeoutMs);
        steer_.configNeutralDeadband(0.04, 0);
        steer_.setNeutralMode(NeutralMode.Brake);

        steer_.config_kP(kSteerPIDId, config.kP, CANConstants.kLongTimeoutMs);
        steer_.config_kI(kSteerPIDId, config.kI, CANConstants.kLongTimeoutMs);
        steer_.config_kD(kSteerPIDId, config.kD, CANConstants.kLongTimeoutMs);
    }

    public IMotorControllerEnhanced getSteer() { return steer_; }
    public DriveMotor getDrive() { return drive_; }

    /**
     * (re)Initialize the module for use
     */
    public synchronized void initialize() {
        System.out.println("Initializing module " + name_);
        if (!periodicIO_.ready) {
            // Force a IO read
            this.readPeriodicInputs();
        }

//        // This will reset the accumulator used to track encoder position when the
//        // ADC value transitions from 1023 to 0
//        // Example:
//        //  Before: ADC: 512, Position: 1000
//        //  After:  ADC: 512, Position: 512
//        int adcValue = steer_.getSensorCollection().getAnalogInRaw();
//        System.out.println("Module: " + name_ + " ADC value " + adcValue);
//        steer_.setSelectedSensorPosition(adcValue, kSteerPIDId, CANConstants.kTimeoutMs);
//        steer_.getSensorCollection()
        drive_.initialize();
    }

    public synchronized void set(ModuleSignal state) {
        periodicIO_.goalAngle = state.angle;
        periodicIO_.driveValue = state.speed;
        periodicIO_.brakeMode = state.brake;
    }

    /**
     * Convert the given angle in degrees to talon native units
     * @param angle degrees 
     * @return angle in native units
     */
    private double angleToNative(double angle, EncoderType type) {
        switch (type) {
        // case kQuadrature:
        //     return config_.nativeUnitsPerRotationQuad * (angle / (2.0*Math.PI));
        case kAbsolute:
            return config_.nativeUnitsPerRotationAbs * (angle / (2.0*Math.PI));
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * Convert the given angle in talon native units to radians
     * @param nativeValue angle in native units
     * @return angle in radians 
     */
    private double nativeToAngle(double nativeValue, EncoderType type) {
        switch (type) {
        // case kQuadrature:
        //    return nativeValue * (2.0 * Math.PI) / config_.nativeUnitsPerRotationQuad;
        case kAbsolute:
            return nativeValue * (2.0 * Math.PI) / config_.nativeUnitsPerRotationAbs;
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * Read in sensor data
     */
    @Override
    public synchronized void readPeriodicInputs() {
        // periodicIO_.quadratureAngle = Rotation2d.fromRadians(nativeToAngle(
        //        steer_.getSelectedSensorPosition(kSteerPIDId), EncoderType.kQuadrature
        //));

        periodicIO_.steerClosedLoopError = steer_.getClosedLoopError(kSteerPIDId)/config_.nativeUnitsPerRotationAbs;

        periodicIO_.absoluteAngleRaw = steer_.getSelectedSensorPosition(0);
        periodicIO_.absoluteAngle = Rotation2d.fromRadians(nativeToAngle(
                periodicIO_.absoluteAngleRaw, EncoderType.kAbsolute
        )).rotateBy(config_.moduleOffset.inverse());

        periodicIO_.ready = true;
    }

    /**
     * Handle setting drive, and drive commands
     */
    @Override
    public synchronized void writePeriodicOutputs() {
        double driveValue = periodicIO_.driveValue;

        double currentAngle = periodicIO_.absoluteAngle.getRadians();
        double goalAngle = periodicIO_.goalAngle.getRadians();
        double angleError = Math.IEEEremainder(goalAngle - currentAngle, 2.0*Math.PI);

        SmartDashboard.putNumber(name_ + " Angle Error", Math.toDegrees(angleError));
        // Minimize azimuth rotation, reversing drive if necessary
        inverted_ = Math.abs(angleError) > (Math.PI/4.0);
        // System.out.println("Inverted: " + inverted_);

        if (inverted_) {
            angleError -= Math.copySign(Math.PI, angleError);
            driveValue *= -1;
        }
        steer_.set(ControlMode.Position, periodicIO_.absoluteAngleRaw + angleToNative(angleError, EncoderType.kAbsolute));

        drive_.setBrake(periodicIO_.brakeMode);
        drive_.set(DriveMotor.ControlMode.kPercentOutput, driveValue);
    }

    @Override
    public boolean checkSystem() {
        return false;
    }

    /**
     * Push telemetry will publish debug related data to the smartdashboard, or badlog
     */
    @Override
    public synchronized void outputTelemetry() {
        SmartDashboard.putNumber(name_ + " Steer Closed Loop Error", periodicIO_.steerClosedLoopError);
        SmartDashboard.putNumber(name_ + " Drive", periodicIO_.driveValue);
        SmartDashboard.putNumber(name_ + " Abs Raw", periodicIO_.absoluteAngleRaw);
        SmartDashboard.putNumber(name_ + " Abs Angle", periodicIO_.absoluteAngle.getDegrees());
    }

    @Override
    public synchronized void stop() {
        drive_.stopMotor();
        steer_.neutralOutput();
    }

    /**
     * Current angle of swerve module. This is not reflect if the module is currently inverted
     * @return angle
     */
    public Rotation2d angle() {
        return periodicIO_.absoluteAngle;
    } 

} 
