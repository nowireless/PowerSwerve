package com.kennedyrobotics.hardware;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Handy helper functions for interacting with CTRE Motor Controllers
 *
 * Based on FRC Team 1816 MotorUtil
 * https://github.com/TheGreenMachine/Zodiac/blob/master/src/main/java/com/team1816/lib/hardware/MotorUtil.java
 */
public class CTREMotorUtil {

    /**
     * checks the specified error code for issues
     *
     * @param errorCode error code
     * @param message   message to print if error happens
     */
    public static void checkError(ErrorCode errorCode, String message) {
        if (errorCode != ErrorCode.OK) {
            DriverStation.reportError(message + errorCode, false);
        }
    }

    /**
     *
     * @param motor
     * @return
     */
    public static double getSupplyCurrent(IMotorControllerEnhanced motor) {
        // If only CTRE had these methods in the interface...
        if (motor instanceof TalonFX) {
            return ((TalonFX) motor).getSupplyCurrent();
        } else if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSupplyCurrent();
        }
        return 0;
    }
}