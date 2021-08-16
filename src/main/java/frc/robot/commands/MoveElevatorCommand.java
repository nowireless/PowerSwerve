package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Elevator;

public class MoveElevatorCommand extends CommandBase {

    private final Elevator elevator_;
    private final double demand_;

    public MoveElevatorCommand(Elevator elevator, double demand) {
        elevator_ = elevator;
        demand_ = demand;

        addRequirements(elevator);
    }

    @Override
    public void execute() {
        elevator_.setOpenLoop(demand_);
    }

    @Override
    public void end(boolean interrupted) {
        elevator_.neutral();
    }
}
