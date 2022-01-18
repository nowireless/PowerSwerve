package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class SolenoidImpl extends Solenoid implements ISolenoid {

    public SolenoidImpl(PneumaticsModuleType moduleType, int channel) {
        super(moduleType, channel);
    }

    public SolenoidImpl(int moduleNumber, PneumaticsModuleType moduleType, int channel) {
        super(moduleNumber, moduleType, channel);
    }
}