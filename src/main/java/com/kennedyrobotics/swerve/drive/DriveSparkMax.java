package com.kennedyrobotics.swerve.drive;

import com.kennedyrobotics.exceptions.TodoException;
import com.revrobotics.CANError;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSparkMax implements DriveMotor {

    private static int id = 0;

    private int id_;

    private final CANSparkMax controller_;

    public DriveSparkMax(CANSparkMax controller) {
        id_ = id++;

        controller_ = controller;

        if (controller_ == null) return;

        // TODO Verify that this bug has been fixed, removing this now
        // // There is a bug with the spark max library that always sets the sensor kNoSensor
        // CANError err = SparkMaxUtil.setSensorType(controller_, SensorType.kHallSensor);
        // if (err != CANError.kOk) {
        //     DriverStation.reportError("Unable to set kHallSensor for spark ID: " + controller_.getDeviceId(), false);
        // }

        this.initialize();
    }

    @Override
    public void initialize() {
        // Configure the Drive Motor for use
        controller_.setOpenLoopRampRate(0.5);

        // Periodic Status 0 - Default Rate: 10ms
        // - Applied Output
        // - Faults
        // - Sticky Faults
        // - Is Follower
        controller_.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100);
        // Periodic Status 1 - Default Rate: 20ms
        // - Motor Velocity
        // - Motor Temperature
        // - Motor Voltage
        // - Motor Current
        controller_.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 500);
        // Periodic Status 2 - Default Rate: 20ms
        // - Motor Position
        // TODO: This frame is currently not in use, as the motor encoder is not in use
        controller_.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 500);

    }

    @Override
    public void set(ControlMode mode, double value) {
        if (controller_ == null) return;

        switch (mode) {
        case kPercentOutput:
            controller_.set(value);
            break;
        case kVelocity:
        case kVoltage:
        case kPosition:
        case kMotionProfile:
            throw new TodoException();
        default:
            throw new IllegalStateException("Unknown control mode: " + mode.toString());
        }

        SmartDashboard.putNumber("" + id_ + ": Spark Max applied output", controller_.getAppliedOutput());
        SmartDashboard.putNumber("" + id_ + ": Spark Max faults", controller_.getFaults());
        SmartDashboard.putNumber("" + id_
                + ": Spark Max Motor Temp", controller_.getMotorTemperature());
    }

    @Override
    public void setInverted(boolean inverted) {
        if (controller_ == null) return;

        controller_.setInverted(inverted);
    }

    @Override
    public void setBrake(boolean brake) {
        if (controller_ == null) return;

        controller_.setIdleMode(brake ? IdleMode.kBrake: IdleMode.kCoast);
    }

    @Override
    public void stopMotor() {
        if (controller_ == null) return;

        controller_.stopMotor();
    }
}
