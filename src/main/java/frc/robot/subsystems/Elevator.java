package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.kennedyrobotics.hardware.CANConstants;
import com.kennedyrobotics.hardware.CTREMotorUtil;
import com.kennedyrobotics.hardware.RobotFactory;
import com.kennedyrobotics.hardware.components.pcm.ISolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.function.Supplier;

public class Elevator extends SubsystemBase {
    private static final String NAME = "elevator";

    public enum ControlState {
        OpenLoop,
        PositionPID,
    }

    private class PeriodicIO {
        // INPUT
        double elevatorExtensionRaw;
        double elevatorExtensionInches;
        boolean isAtLowLimit;
        double feedForward; // Can be used to offset the cube when it is present in the elevator

        double outputPercent;
        double leaderMotorCurrent;
        double followerMotorCurrent;

        // OUTPUT
        double demand;
    }


    // Hardware
    private final IMotorControllerEnhanced liftMotorLeader;
    private final IMotorController liftMotorFollower;
    private final ISolenoid liftBrake;

    // State
    ControlState controlState = ControlState.OpenLoop;
    PeriodicIO periodicIO = new PeriodicIO();
    boolean hasBeenZeroed;

    private final double intakeHeightOffsetInches; // Height offset from the intake to the floor

    // Dependencies
    private final Supplier<Boolean> intakeHasCubeSupplier;

    public Elevator(RobotFactory factory, Supplier<Boolean> intakeHasCubeSupplier) {
        setName(NAME);
        this.intakeHasCubeSupplier = intakeHasCubeSupplier;

        // State
        intakeHeightOffsetInches = factory.getConstant(NAME, "intakeHeightOffset");

        // Hardware
        // There are 2 lift motors, one will be the leader, and other other will be the follower.
        // The leader will be configured as follows:
        // - Use the internal encoder to determine the height of the elevator
        // - A normally open limit switch will be used to determine when the elevator is at the flower limit
        // - When the elevator is at the lower limit the encoder postion will be reset.
        // - Positive Encoder values mean up
        // - Positive Motor command values mean up
        //
        liftMotorLeader = factory.getMotor(NAME, "liftLeader");
        liftMotorLeader.setInverted(false);
        liftMotorLeader.setNeutralMode(NeutralMode.Brake);
        liftMotorLeader.configForwardLimitSwitchSource(
                LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CANConstants.kLongTimeoutMs);
        liftMotorLeader.configSelectedFeedbackSensor(
                FeedbackDevice.IntegratedSensor, 0, CANConstants.kLongTimeoutMs);
        CTREMotorUtil.configClearPositionOnLimitR(liftMotorLeader, true, CANConstants.kLongTimeoutMs);

        // Follower configuraiton
        liftMotorFollower = factory.getMotor(NAME, "liftFollower", liftMotorLeader);

        // The lift break is a single solenoid, that by default does not break the elevator. It has to be commanded to
        // break.
        liftBrake = factory.getSolenoid(NAME, "liftBrake");
    }

    //
    // Control loop and Periodic IO
    //

    @Override
    public void periodic() {
        if (RobotState.isDisabled()) {
            // When the robot is disabled lets zero the elevator when its at the low limit
            resetIfAtLimit();
        }

        // Read inputs
        readPeriodicInputs();

        // Write output
        writePeriodicOutputs();
    }

    public void readPeriodicInputs() {
        periodicIO.isAtLowLimit = CTREMotorUtil.isRevLimitSwitchClosed(liftMotorLeader);


        // Encoder counts to elevator extension in inches. TODO Need to add the scale value to the hight calculate
        // 1 Revolution   ? Inches
        // ------------ * ------------
        // 2048 CPR       1 Revolution
        periodicIO.elevatorExtensionRaw = liftMotorLeader.getSelectedSensorPosition(0);
        periodicIO.elevatorExtensionInches = periodicIO.elevatorExtensionRaw * 1.0;

        // Query the state of the take subsystem to determine if the intake has a cube, if so apply a feedforward value
        // to compensate for the weight of the cube.
        if (intakeHasCubeSupplier.get()) {
            periodicIO.feedForward = 0.0; // TODO
        } else {
            periodicIO.feedForward = 0.0;
        }


        // Motor stats
        periodicIO.leaderMotorCurrent = CTREMotorUtil.getStatorCurrent(liftMotorLeader);
        periodicIO.followerMotorCurrent = CTREMotorUtil.getStatorCurrent(liftMotorFollower);
    }

    public void writePeriodicOutputs() {
        if (controlState == ControlState.OpenLoop) {
            liftMotorLeader.set(ControlMode.PercentOutput, periodicIO.demand, DemandType.ArbitraryFeedForward, periodicIO.feedForward);

            if (periodicIO.demand == 0.0) {
                // When the elevator is not being command to move set the break
                if (!liftBrake.get()) {
                    System.out.println("Turning brake on");
                }
                liftBrake.set(true);
            } else {
                // WHen the elevator is being commanded to move release the break
                if (liftBrake.get()) {
                    System.out.println("Turning brake off");
                }
                liftBrake.set(false);
            }

        } else if (controlState == ControlState.PositionPID) {
            // TODO Need to add PID of some sort
            System.out.println("PositionPID does not work yet");
            liftMotorLeader.neutralOutput();
        } else {
            DriverStation.reportError("Elevator in unknown state: "+controlState.toString(), false);
            liftMotorLeader.neutralOutput();
            liftBrake.set(false);
        }
    }

    public void outputTelemetry() {
        SmartDashboard.putString("Elevator Control State", controlState.toString());
        SmartDashboard.putBoolean("Elevator has been zeroed", hasBeenZeroed());
        SmartDashboard.putNumber("Elevator Output %", periodicIO.outputPercent);
        SmartDashboard.putNumber("Elevator Current (Leader)", periodicIO.leaderMotorCurrent);
        SmartDashboard.putNumber("Elevator Current (Follower)", periodicIO.followerMotorCurrent);
        SmartDashboard.putNumber("Elevator Height", getInchesOffGround());
        SmartDashboard.putNumber("Elevator Height Raw", periodicIO.elevatorExtensionRaw);
        SmartDashboard.putBoolean("Elevator Low Limit", periodicIO.isAtLowLimit);
    }

    //
    // Getters for Elevator State
    //

    /**
     * Height of intake from the ground
     * @return height in inches
     */
    public double getInchesOffGround() {
        return periodicIO.elevatorExtensionInches + intakeHeightOffsetInches;
    }

    /**
     * Height of how far the elevator has extended from its home position
     * @return extension in inches.
     */
    public double getExtension() {
        return periodicIO.elevatorExtensionInches;
    }

    /**
     * Has the elevator reach the low limit
     * @return if true then the elevator is at the low limit
     */
    public boolean isAtLowLimit() {
        return periodicIO.isAtLowLimit;
    }


    /**
     * Set point in inches for closed loop contol states (PositionPID)
     * @return if in closed loop control mode the height in inches, otherwise NaN
     */
    public double getSetPoint() {
        if (controlState == ControlState.PositionPID) {
            return periodicIO.demand;
        }

        return Double.NaN;
    }

    public boolean hasBeenZeroed() {
        return hasBeenZeroed;
    }


    //
    // Sensor helpers
    //

    /**
     * Reset Sensors if the elevator is at the low limit
     */
    public void resetIfAtLimit() {
        if (this.isAtLowLimit()) {
            zeroSensors();
        }
    }


    /**
     * Current control state of the elevator
     * @return the state
     */
    public ControlState getControlState() {
        return controlState;
    }

    //
    // Modify/Control Elevator State
    //
    public void zeroSensors() {
        liftMotorLeader.setSelectedSensorPosition(0, 0, CANConstants.kTimeoutMs);
        hasBeenZeroed = true;
    }

    public void setOpenLoop(double percentage) {
        controlState = ControlState.OpenLoop;
        periodicIO.demand = percentage;
    }
}
