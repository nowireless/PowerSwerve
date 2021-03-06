package com.kennedyrobotics.hardware;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kennedyrobotics.hardware.components.motor.GhostMotorControllerEnhanced;
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

    public static double getStatorCurrent(IMotorController motor) {
        // If only CTRE had these methods in the interface...
        if (motor instanceof TalonFX) {
            return ((TalonFX) motor).getStatorCurrent();
        } else if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getStatorCurrent();
        }
        return 0;
    }

    /**
     * Enables clearing the position of the feedback sensor when the forward
     * limit switch is triggered.
     *
     * @param clearPositionOnLimitF     Whether clearing is enabled, defaults false
     * @param timeoutMs
     *            Timeout value in ms. If nonzero, function will wait for
     *            config success and report an error if it times out.
     *            If zero, no blocking or checking is performed.
     * @return Error Code generated by function. 0 indicates no error.
     */
    public static ErrorCode configClearPositionOnLimitF(IMotorControllerEnhanced motor, boolean clearPositionOnLimitF, int timeoutMs) {
        if (motor instanceof BaseMotorController) {
            return ((BaseMotorController)motor).configClearPositionOnLimitF(clearPositionOnLimitF, timeoutMs);
        } else if (motor instanceof GhostMotorControllerEnhanced) {
            return ErrorCode.OK;
        }

        return ErrorCode.NotImplemented;
    }

    /**
     * Enables clearing the position of the feedback sensor when the reverse
     * limit switch is triggered
     *
     * @param clearPositionOnLimitR     Whether clearing is enabled, defaults false
     * @param timeoutMs
     *            Timeout value in ms. If nonzero, function will wait for
     *            config success and report an error if it times out.
     *            If zero, no blocking or checking is performed.
     * @return Error Code generated by function. 0 indicates no error.
     */
    public static ErrorCode configClearPositionOnLimitR(IMotorControllerEnhanced motor, boolean clearPositionOnLimitR, int timeoutMs) {
        if (motor instanceof BaseMotorController) {
            return ((BaseMotorController)motor).configClearPositionOnLimitR(clearPositionOnLimitR, timeoutMs);
        } else if (motor instanceof GhostMotorControllerEnhanced) {
            return ErrorCode.OK;
        }

        return ErrorCode.NotImplemented;
    }

    /**
     * Is forward limit switch closed.
     * <p>
     * This method relies on the Status 1 message, which has a default period of 10ms. For more
     * information, see: https://phoenix-documentation.readthedocs.io/en/latest/ch18_CommonAPI.html
     *
     * @return  '1' iff forward limit switch is closed, 0 iff switch is open. This function works
     *          regardless if limit switch feature is enabled.  Remote limit features do not impact this routine.
     */
    public static boolean isFwdLimitSwitchClosed(IMotorControllerEnhanced motor) {
        if (motor instanceof TalonFX) {
            return ((TalonFX) motor).getSensorCollection().isFwdLimitSwitchClosed() == 1; // Why is this an init?
        } else if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSensorCollection().isFwdLimitSwitchClosed();
        }
        return false;
    }

    /**
     * Is reverse limit switch closed.
     * <p>
     * This method relies on the Status 1 message, which has a default period of 10ms. For more
     * information, see: https://phoenix-documentation.readthedocs.io/en/latest/ch18_CommonAPI.html
     *
     * @return  '1' iff reverse limit switch is closed, 0 iff switch is open. This function works
     *          regardless if limit switch feature is enabled.  Remote limit features do not impact this routine.
     */
    public static boolean isRevLimitSwitchClosed(IMotorControllerEnhanced motor) {
        if (motor instanceof TalonFX) {
            return ((TalonFX) motor).getSensorCollection().isRevLimitSwitchClosed() == 1; // Why is this an init?
        } else if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSensorCollection().isRevLimitSwitchClosed();
        }
        return false;
    }

}