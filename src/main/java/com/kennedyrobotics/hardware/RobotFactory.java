package com.kennedyrobotics.hardware;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.kennedyrobotics.hardware.components.CanifierImpl;
import com.kennedyrobotics.hardware.components.GhostCanifier;
import com.kennedyrobotics.hardware.components.ICanifier;
import com.kennedyrobotics.hardware.components.pcm.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.team254.RoboRIOConstants;
import edu.wpi.first.wpilibj.DriverStation;
import javax.annotation.Nonnull;

public class RobotFactory {

    public final String robotName;
    private YamlConfig config;
    private static boolean verbose;
    private static RobotFactory factory;
    private static RoboRIOConstants controllerConstants;

    public static RobotFactory getInstance() {
        if (factory == null) {
            controllerConstants = new RoboRIOConstants();

            String macAddress = RoboRIOConstants.getMACAddress();
            System.out.println("Robot MAC Address:" + macAddress);
            System.out.println("Known Robot Controllers");
            for (var controller : controllerConstants.getKnownControllers()) {
                System.out.println("  " + controller.toString());
            }

            var robotName = controllerConstants.getName();
            if (robotName == null) {
                robotName = "default";
                DriverStation.reportWarning(
                        "Unable to determine robot name, falling back to default.config.yaml!",
                        false
                );
            }
            factory = new RobotFactory(robotName);
        }

        return factory;
    }

    public RobotFactory(String robotName) {
        this.robotName = robotName;
        System.out.println("Loading Config for " + robotName);
        try {
            config =
                    YamlConfig.loadFrom(
                            this.getClass()
                                    .getClassLoader()
                                    .getResourceAsStream(robotName + ".config.yml")
                    );
        } catch (ConfigIsAbstractException e) {
            DriverStation.reportError("Yaml Config was abstract!", e.getStackTrace());
        }
        verbose = getConstant("verbose") >= 1;
    }

    public IMotorControllerEnhanced getCtreMotor(String subsystemName, String name) {
        IMotorControllerEnhanced motor = null;
        var subsystem = getSubsystem(subsystemName);

        // Motor creation
        if (subsystem.isImplemented()) {
            if (isHardwareValid(subsystem.talons.get(name))) {
                motor =
                        CtreMotorFactory.createDefaultTalon(
                                subsystem.talons.get(name),
                                false
                        );
            } else if (isHardwareValid(subsystem.falcons.get(name))) {
                motor =
                        CtreMotorFactory.createDefaultTalon(
                                subsystem.falcons.get(name),
                                true
                        );
            } // Never make the victor a leader
        }
        if (motor == null) {
            if (subsystem.isImplemented()) reportGhostWarning(
                    "Motor",
                    subsystemName,
                    name
            );
            motor = CtreMotorFactory.createGhostTalon();
        }

        // Motor configuration
        // TODO Make this togglable...
//        if (subsystem.isImplemented() && subsystem.invertMotor.contains(name)) {
//            System.out.println("Inverting " + name + " with ID " + motor.getDeviceID());
//            motor.setInverted(true);
//        }
//        motor.config_kP(
//                0,
//                getConstant(subsystemName, "kP", 0),
//                CANConstants.kLongTimeoutMs
//        );
//        motor.config_kI(
//                0,
//                getConstant(subsystemName, "kI", 0),
//                CANConstants.kLongTimeoutMs
//        );
//        motor.config_kD(
//                0,
//                getConstant(subsystemName, "kD", 0),
//                CANConstants.kLongTimeoutMs
//        );
//        motor.config_kF(
//                0,
//                getConstant(subsystemName, "kF", 0),
//                CANConstants.kLongTimeoutMs
//        );

        return motor;
    }

    public IMotorController getCtreMotor(
            String subsystemName,
            String name,
            IMotorController leader
    ) { // TODO: optimize this method
        IMotorController motor = null;
        var subsystem = getSubsystem(subsystemName);
        if (subsystem.isImplemented() && leader != null) {
            if (isHardwareValid(subsystem.talons.get(name))) {
                // Talons must be following another Talon, cannot follow a Victor.
                motor =
                        CtreMotorFactory.createPermanentFollowerTalon(
                                subsystem.talons.get(name),
                                false,
                                leader
                        );
            } else if (isHardwareValid(subsystem.falcons.get(name))) {
                motor =
                        CtreMotorFactory.createPermanentFollowerTalon(
                                subsystem.falcons.get(name),
                                true,
                                leader
                        );
            } else if (isHardwareValid(subsystem.victors.get(name))) {
                // Victors can follow Talons or another Victor.
                motor =
                        CtreMotorFactory.createPermanentFollowerVictor(
                                subsystem.victors.get(name),
                                leader
                        );
            }
        }
        if (motor == null) {
            if (subsystem.isImplemented()) reportGhostWarning(
                    "Motor",
                    subsystemName,
                    name
            );
            motor = CtreMotorFactory.createGhostTalon();
        }
        if (leader != null) {
            motor.setInverted(leader.getInverted());
        }
        return motor;
    }

    public CANSparkMax getRevMotor(String subsystemName, String name, CANSparkMaxLowLevel.MotorType type) {
        var subsystem = getSubsystem(subsystemName);

        if (!subsystem.isImplemented()) return null;

        if (subsystem.sparks.get(name) != null && subsystem.sparks.get(name) > -1) {
            return new CANSparkMax(subsystem.sparks.get(name), type);
        }

        return null;
    }

    private boolean isHardwareValid(Integer hardwareId) {
        return hardwareId != null && hardwareId > -1;
    }

    @Nonnull
    public ISolenoid getSolenoid(String subsystemName, String name) {
        var subsystem = getSubsystem(subsystemName);
        Integer solenoidId = subsystem.solenoids.get(name);
        if (subsystem.isImplemented() && isHardwareValid(solenoidId) && isPcmEnabled()) {
            return new SolenoidImpl(config.pcm, solenoidId);
        }
        if (subsystem.isImplemented()) {
            reportGhostWarning("Solenoid", subsystemName, name);
        }
        return new GhostSolenoid();
    }

    @Nonnull
    public IDoubleSolenoid getDoubleSolenoid(String subsystemName, String name) {
        var subsystem = getSubsystem(subsystemName);
        YamlConfig.DoubleSolenoidConfig solenoidConfig = getSubsystem(subsystemName)
                .doublesolenoids.get(name);
        if (
                subsystem.isImplemented() &&
                        solenoidConfig != null &&
                        isHardwareValid(solenoidConfig.forward) &&
                        isHardwareValid(solenoidConfig.reverse) &&
                        isPcmEnabled()
        ) {
            return new DoubleSolenoidImpl(
                    config.pcm,
                    solenoidConfig.forward,
                    solenoidConfig.reverse
            );
        }
        reportGhostWarning("DoubleSolenoid", subsystemName, name);
        return new GhostDoubleSolenoid();
    }

    @Nonnull
    public ICanifier getCanifier(String subsystemName) {
        var subsystem = getSubsystem(subsystemName);
        if (subsystem.isImplemented() && isHardwareValid(subsystem.canifier)) {
            return new CanifierImpl(subsystem.canifier);
        }
        reportGhostWarning("CANifier", subsystemName, "canifier");
        return new GhostCanifier();
    }

    public ICompressor getCompressor() {
        if (isPcmEnabled()) {
            return new CompressorImpl(getPcmId());
        }
        reportGhostWarning("Compressor", "ROOT", "on PCM ID " + getPcmId());
        return new GhostCompressor();
    }

    public Double getConstant(String name) {
        return getConstant(name, 0.0);
    }

    public Double getConstant(String name, double defaultVal) {
        if (!config.constants.containsKey(name)) {
            DriverStation.reportError("Yaml constants:" + name + " missing", false);
            return defaultVal;
        }
        return config.constants.get(name);
    }

    public double getConstant(String subsystemName, String name) {
        return getConstant(subsystemName, name, 0.0);
    }

    public double getConstant(String subsystemName, String name, double defaultVal) {
        if (!getSubsystem(subsystemName).isImplemented()) {
            return defaultVal;
        }
        if (!getSubsystem(subsystemName).constants.containsKey(name)) {
            DriverStation.reportError(
                    "Yaml " + subsystemName + " constants:" + name + " missing",
                    false
            );
            return defaultVal;
        }
        return getSubsystem(subsystemName).constants.get(name);
    }

    public int getPcmId() {
        if (config.pcm == null) return -1;
        return config.pcm;
    }

    public boolean isPcmEnabled() {
        return getPcmId() > -1;
    }

    public YamlConfig.SubsystemConfig getSubsystem(String subsystemName) {
        var subsystem = config.subsystems.get(subsystemName);
        if (subsystem == null) {
            subsystem = new YamlConfig.SubsystemConfig(false);
        }
        return subsystem;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    private void reportGhostWarning(
            String type,
            String subsystemName,
            String componentName
    ) {
        DriverStation.reportWarning(
                type +
                        "  " +
                        componentName +
                        " not defined or invalid in config for subsystem " +
                        subsystemName +
                        ", using ghost!",
                false
        );
    }
}