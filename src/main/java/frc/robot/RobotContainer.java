// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.kennedyrobotics.hardware.RobotFactory;
import com.team254.lib.loops.Looper;
import com.team254.lib.subsystems.SubsystemManager;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveWithController;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.Arrays;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem;
  public final Drive m_drive;
  private final Elevator m_elevator;

  public final SubsystemManager m_subsystemManager;
  public final Looper m_enabledLooper = new Looper();
  public final Looper m_disabledLooper = new Looper();

  private final ExampleCommand m_autoCommand;

  private final XboxController m_controller = new XboxController(0);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   * This code is initialized in robotInit in {@link Robot}
   */
  public RobotContainer() {
    var factory = RobotFactory.getInstance();

    //
    // Initialize Subsystems
    //
    m_exampleSubsystem = new ExampleSubsystem();
    m_drive = new Drive(factory);
    m_elevator = new Elevator(factory, () -> false);

    m_subsystemManager = new SubsystemManager(Arrays.asList(
        m_drive
    ));
    m_subsystemManager.registerDisabledLoops(m_disabledLooper);
    m_subsystemManager.registerEnabledLoops(m_enabledLooper);


    m_drive.initialize(); // TODO Move into Drive constructor?

    //
    // Setup commands
    //
    m_drive.setDefaultCommand(new DriveWithController(m_drive, m_controller));
    m_autoCommand = new ExampleCommand(m_exampleSubsystem);

    //
    // Configure the button bindings
    //
    configureButtonBindings();

  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {}

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return m_autoCommand;
  }
}
