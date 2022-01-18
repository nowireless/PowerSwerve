package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class DoubleSolenoidImpl extends DoubleSolenoid implements IDoubleSolenoid {

    public DoubleSolenoidImpl(PneumaticsModuleType moduleType, int forwardChannel, int reverseChannel) {
        super(moduleType, forwardChannel, reverseChannel);
    }

    public DoubleSolenoidImpl(int moduleNumber, PneumaticsModuleType moduleType, int forwardChannel, int reverseChannel) {
        super(moduleNumber, moduleType, forwardChannel, reverseChannel);
    }
}