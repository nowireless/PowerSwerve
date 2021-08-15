package frc.robot.subsystems;

import com.kennedyrobotics.hardware.RobotFactory;
import com.kennedyrobotics.hardware.components.ICanifier;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDManager extends SubsystemBase {

    private enum CarriageLEDState {
        GlobalFault,
        Off
    }

    private enum ElevatorLEDState {
        GlobalFault,
        ElevatorFault, // The Elevator sensor was not zero-ed
        MovingUp,
        MovingDownward,
        StandBy, // Robot disabled, maybe a breathing color?
        Off,
    }

    private class PeriodicIO {
        // INPUT

        // OUTPUT
    }

    // Hardware
    private final ICanifier carriageCanifer;

    // State

    // Dependencies
    private final Elevator elevator;
    private final Intake intake;

    public LEDManager(RobotFactory factory, Elevator elevator, Intake intake) {
        this.elevator = elevator;
        this.intake = intake;
        carriageCanifer = this.intake.getCanifier();
    }

    @Override
    public void periodic() {

    }
}
