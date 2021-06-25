package com.kennedyrobotics.hardware.components.motor;

import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;

public interface ILazyMotorControllerEnhanced extends IMotorControllerEnhanced {
    double getLastSet();
}