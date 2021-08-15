package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.kennedyrobotics.hardware.RobotFactory;
import com.kennedyrobotics.swerve.*;
import com.kennedyrobotics.swerve.SwerveSignal.ControlOrientation;
import com.kennedyrobotics.swerve.drive.DriveSparkMax;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.subsystems.CheesySubsystem;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.Arrays;
import java.util.List;

public class Drive extends SubsystemBase implements CheesySubsystem {

    public static class PeriodicIO {
        // INPUTS
        public Rotation2d gyro_heading = Rotation2d.identity();

        // OUTPUTS
        public SwerveCommand swerveCommand = new SwerveCommand();
    }

    // Constants
    public final double kWheelBaseLength = RobotFactory.getInstance().getConstant("wheelbase_length");
    public final double kTrackWidth = RobotFactory.getInstance().getConstant("track_width");;

    // Helpers
    private final RobotFactory factory_;
    private final SwerveDriveHelper helper_ = new SwerveDriveHelper(kWheelBaseLength, kTrackWidth);
    private final ModuleOffsets moduleOffsets_ = new ModuleOffsets();

    // Hardware
    private final AHRS imu_;
    private final SwerveModule frontLeft_;
    private final SwerveModule frontRight_;
    private final SwerveModule backLeft_;
    private final SwerveModule backRight_;
    private final List<SwerveModule> modules_;

    // Hardware States
    private PeriodicIO periodicIO_;
    private Rotation2d gyroOffset_ = Rotation2d.identity();
    private boolean debugControl_;

    public void setDebugControl(boolean value) { debugControl_ = value; }

    public Drive(RobotFactory factory) {
        factory_ = factory;

        /*
         * Hardware State 
         */
        periodicIO_ = new PeriodicIO();

        /*
         * IMU
         */
        // TODO: Add getAHRS method to RobotFactory and read what port the NavX is from Yaml
        imu_ = new AHRS(Port.kUSB);

        /*
         * PID Configuration 
         */
        double kP = factory_.getConstant("drive", "steer_kp");
        double kI = factory_.getConstant("drive", "steer_ki");
        double kD = factory_.getConstant("drive", "steer_kd");

        /*
         * Modules 
         */
        SwerveModule.Config frontLeft = SwerveModule.getDefaultConfig(kP, kI, kD);
        frontLeft.moduleOffset = moduleOffsets_.getOffset(
                (int)factory.getConstant("drive", "front_left_id")
        );
        frontLeft_ = new SwerveModule(
            "FrontLeft", 
            frontLeft, 
            factory_.getCtreMotor("drive", "front_left_steer"),
            new DriveSparkMax(factory_.getRevMotor("drive", "front_left_drive", CANSparkMaxLowLevel.MotorType.kBrushless))
        );

        SwerveModule.Config frontRight = SwerveModule.getDefaultConfig(kP, kI, kD);
        frontRight.moduleOffset = moduleOffsets_.getOffset(
                (int)factory.getConstant("drive", "front_right_id")
        );
        frontRight_ = new SwerveModule(
            "FrontRight", 
            frontRight, 
            factory_.getCtreMotor("drive", "front_right_steer"),
            new DriveSparkMax(factory_.getRevMotor("drive", "front_right_drive", CANSparkMaxLowLevel.MotorType.kBrushless))
        );

        SwerveModule.Config backLeft = SwerveModule.getDefaultConfig(kP, kI, kD);
        backLeft.moduleOffset = moduleOffsets_.getOffset(
                (int)factory.getConstant("drive", "back_left_id")
        );
        backLeft_ = new SwerveModule(
            "BackLeft",
            backLeft,
            factory_.getCtreMotor("drive", "back_left_steer"),
            new DriveSparkMax(factory_.getRevMotor("drive", "back_left_drive", CANSparkMax.MotorType.kBrushless))
        );

        SwerveModule.Config backRight = SwerveModule.getDefaultConfig(kP, kI, kD);
        backRight.moduleOffset = moduleOffsets_.getOffset(
                (int)factory.getConstant("drive", "back_right_id")
        );
        backRight_ = new SwerveModule(
            "BackRight", 
            backRight, 
            factory_.getCtreMotor("drive", "back_right_steer"),
            new DriveSparkMax(factory_.getRevMotor("drive", "back_right_drive", CANSparkMaxLowLevel.MotorType.kBrushless))
        );

        modules_ = Arrays.asList(
            frontLeft_,
            frontRight_,
            backLeft_,
            backRight_
        );
    }

//    @Override
//    protected void initDefaultCommand() {
//        setDefaultCommand(new TeleopDriveCommand());
//    }

    public void initialize() {
        modules_.forEach((m) -> m.initialize());
    }

    /**
     * Periodic function ran in main robot thread
     */
    @Override
    public void periodic() {
    }

    public void set(SwerveSignal signal) {
        log("Command: " + signal);

        if (signal.orientation() == ControlOrientation.kFieldCentric) {
            signal.fieldOrient(this.getHeading());
        }

        periodicIO_.swerveCommand = helper_.inverseKinmatics(signal);

        // Pass along the swerve singal's brake setting
        periodicIO_.swerveCommand.setBrake(signal.brakeMode());

        log("Move " +
                periodicIO_.swerveCommand.frontLeft.speed + ", " +
                periodicIO_.swerveCommand.frontRight.speed + ", " +
                periodicIO_.swerveCommand.backLeft.speed + ", " +
                periodicIO_.swerveCommand.backRight.speed
        );
    }

    @Override
    public void stop() {
        frontLeft_.stop();
        frontRight_.stop();
        backLeft_.stop();
        backRight_.stop();
    }


    public synchronized Rotation2d getHeading() {
        return  periodicIO_.gyro_heading;
    }

    public synchronized void setHeading(Rotation2d heading) {
        System.out.println("SET HEADING: " + heading.getDegrees());

        gyroOffset_ = heading.rotateBy(Rotation2d.fromDegrees(imu_.getFusedHeading()).inverse());
        System.out.println("Gyro offset: " + gyroOffset_.getDegrees());

       periodicIO_.gyro_heading = heading;
    }


    /*
     * Team 254 CheesySubsystem interface
     */

    @Override
    public synchronized void readPeriodicInputs() {
        periodicIO_.gyro_heading = Rotation2d.fromDegrees(imu_.getFusedHeading()).inverse().rotateBy(gyroOffset_.inverse());
        SmartDashboard.putNumber("Gyro Heading", getHeading().getDegrees());

        modules_.forEach((m) -> m.readPeriodicInputs());


        // Set module signals
        frontLeft_.set(periodicIO_.swerveCommand.frontLeft);
        frontRight_.set(periodicIO_.swerveCommand.frontRight);
        backLeft_.set(periodicIO_.swerveCommand.backLeft);
        backRight_.set(periodicIO_.swerveCommand.backRight);
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        if (!debugControl_) {
            modules_.forEach((m) -> m.writePeriodicOutputs());
        }
    }

    @Override
    public void outputTelemetry() {
        modules_.forEach((m) -> m.outputTelemetry());
    }

    public List<SwerveModule> modules() {
        return modules_;
    }

    private void log(String msg) {
        System.out.println("[Drive] INFO: " + msg);
    }

    private void logWarn(String msg) {
        System.out.println("[Drive] WARN: " + msg);
    }

}