package frc.robot.subsystems;

import com.kennedyrobotics.hardware.RobotFactory;
import com.kennedyrobotics.hardware.components.ICanifier;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

    private static final String NAME = "intake";

    private final ICanifier canifier;
    public ICanifier getCanifier() { return canifier; }

    public Intake(RobotFactory factory) {
        setName(NAME);

        canifier = factory.getCanifier("intake");
    }
}
