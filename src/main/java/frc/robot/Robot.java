// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.kennedyrobotics.hardware.RobotFactory;
import com.kennedyrobotics.math.Rotation2dUtil;
import com.kennedyrobotics.util.JoystickWarningHelper;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.util.CrashTracker;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  public Robot() {
    CrashTracker.logRobotConstruction();
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    try {
      CrashTracker.logRobotInit();

      // Disable Joystick warning
      JoystickWarningHelper.disableWarning();

      var factory = RobotFactory.getInstance();
      DriverStation.reportError("Robot Name: " + factory, false);

      // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
      // autonomous chooser on the dashboard.
      m_robotContainer = new RobotContainer();
    } catch(Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    m_robotContainer.m_subsystemManager.outputToSmartDashboard();

    SmartDashboard.putNumber("Memory Free", Runtime.getRuntime().freeMemory());
    SmartDashboard.putNumber("Memory Total", Runtime.getRuntime().totalMemory());
    SmartDashboard.putNumber("Memory Max", Runtime.getRuntime().maxMemory());
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    SmartDashboard.putString("Match Cycle", "DISABLED");

    try {
      CrashTracker.logDisabledInit();
      m_robotContainer.m_enabledLooper.stop();
      m_robotContainer.m_disabledLooper.start();

    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  @Override
  public void disabledPeriodic() {
    SmartDashboard.putString("Match Cycle", "DISABLED");

  }

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    SmartDashboard.putString("Match Cycle", "AUTONOMOUS");


    try {
      CrashTracker.logAutoInit();
      m_robotContainer.m_disabledLooper.stop();

      m_robotContainer.m_drive.initialize();

      m_autonomousCommand = m_robotContainer.getAutonomousCommand();

      // schedule the autonomous command (example)
      if (m_autonomousCommand != null) {
        m_autonomousCommand.schedule();
      }

      m_robotContainer.m_enabledLooper.start();
    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putString("Match Cycle", "AUTONOMOUS");
  }

  @Override
  public void teleopInit() {
    SmartDashboard.putString("Match Cycle", "TELEOP");

    try {
      CrashTracker.logTeleopInit();
      m_robotContainer.m_disabledLooper.stop();

      m_robotContainer.m_drive.initialize();
      m_robotContainer.m_drive.setHeading(Rotation2dUtil.identity()); // TODO this is the initial heading of the robot

      // This makes sure that the autonomous stops running when
      // teleop starts running. If you want the autonomous to
      // continue until interrupted by another command, remove
      // this line or comment it out.
      if (m_autonomousCommand != null) {
        m_autonomousCommand.cancel();
      }

      m_robotContainer.m_enabledLooper.start();
    } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putString("Match Cycle", "TELEOP");

  }

  @Override
  public void testInit() {
    SmartDashboard.putString("Match Cycle", "TEST");

    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    SmartDashboard.putString("Match Cycle", "TEST");
    m_robotContainer.m_disabledLooper.stop();
    m_robotContainer.m_enabledLooper.stop();

  }
}
