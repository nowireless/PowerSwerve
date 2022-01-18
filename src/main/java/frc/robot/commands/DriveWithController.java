package frc.robot.commands;

import com.kennedyrobotics.swerve.SwerveSignal;
import com.kennedyrobotics.swerve.SwerveSignal.ControlOrientation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drive;

public class DriveWithController extends CommandBase {
    private static final double kDeadband = 0.15;

    private final Drive drive_;
    private final XboxController controller_;
    private boolean fieldOrient_;

    public DriveWithController(Drive drive, XboxController controller) {
        this.drive_ = drive;
        this.controller_ = controller;
        addRequirements(drive_);
    }

    @Override
    public void initialize() {
        fieldOrient_ = false;
    }

    @Override
    public void execute() {
         double xMove = -controller_.getLeftY();
         double yMove = controller_.getLeftX();
         double rotate;

        double left = controller_.getLeftTriggerAxis();
        double right = controller_.getRightTriggerAxis();

        if(left < right) {
            rotate =  right;
        } else {
            rotate =  -left;
        }

        if (Math.abs(xMove) < kDeadband) {
            xMove = 0;
        }

        if (Math.abs(yMove) < kDeadband) {
            yMove = 0;
        }

        if (Math.abs(rotate) < kDeadband) {
            rotate = 0;
        }

        xMove = Math.copySign(xMove * xMove, xMove);
        yMove = Math.copySign(yMove * yMove, yMove);
        rotate = Math.copySign(rotate * rotate, rotate);

        // TODO add an option, to easily nerf in this command
        xMove *= 0.3;
        yMove *= 0.3;
        rotate *= 0.25;
//        xMove *= 0.8;
//        yMove *= 0.8;
//        rotate *= 0.5;

        if(controller_.getAButton()) {
            fieldOrient_ = false;
        } else if (controller_.getBButton()) {
            fieldOrient_ = true;
        }

        ControlOrientation co = ControlOrientation.kRobotCentric;
        if (fieldOrient_) {
            co = ControlOrientation.kFieldCentric;
        }

        SwerveSignal signal = new SwerveSignal(xMove, yMove, rotate, false, co);

        drive_.set(signal);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive_.stop();
    }

}