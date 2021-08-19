package com.kennedyrobotics.telemetry;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import java.util.*;

public class CANBusMonitor {

    private final List<TalonSRX> talonSRXs;
    private final List<TalonFX> talonFXs;
//    private final List<VictorSPX> victors;
    private final List<CANSparkMax> sparks;
    private final List<CANifier> canifiers;
    private final List<CANCoder> cancoders;
    private final List<PigeonIMU> pigtions;

    private final List<StatusFrameEnhanced> enhancedStatus = Arrays.asList(
            StatusFrameEnhanced.Status_1_General,
            StatusFrameEnhanced.Status_2_Feedback0,
            StatusFrameEnhanced.Status_3_Quadrature,
            StatusFrameEnhanced.Status_4_AinTempVbat,
            StatusFrameEnhanced.Status_6_Misc,
            StatusFrameEnhanced.Status_7_CommStatus,
            StatusFrameEnhanced.Status_8_PulseWidth,
            StatusFrameEnhanced.Status_9_MotProfBuffer,
            StatusFrameEnhanced.Status_10_Targets,
            StatusFrameEnhanced.Status_11_UartGadgeteer,
            StatusFrameEnhanced.Status_12_Feedback1,
            StatusFrameEnhanced.Status_13_Base_PIDF0,
            StatusFrameEnhanced.Status_14_Turn_PIDF1,
            StatusFrameEnhanced.Status_15_FirmwareApiStatus
    );

    public CANBusMonitor() {
        talonSRXs = new ArrayList<>();
        talonFXs = new ArrayList<>();
//        victors = new ArrayList<>();
        sparks = new ArrayList<>();
        canifiers = new ArrayList<>();
        cancoders = new ArrayList<>();
        pigtions = new ArrayList<>();
    }

    public void scan() {
        var data = new HashMap<String, Map<Integer, Map<String, Integer>>>();

        {
            var talonSRXData = new HashMap<Integer, Map<String, Integer>>();
            for (var talon : talonSRXs) {
                var statusData = new HashMap<String, Integer>();
                for (var status : enhancedStatus) {
                    var period = talon.getStatusFramePeriod(status);
                    statusData.put(status.name(), period);
                }
                talonSRXData.put(talon.getDeviceID(), statusData);
            }
            data.put("TalonSRX", talonSRXData);
        }

        {
            var talonFXData = new HashMap<Integer, Map<String, Integer>>();
            for (var talon : talonFXs) {
                var statusData = new HashMap<String, Integer>();
                for (var status : enhancedStatus) {
                    var period = talon.getStatusFramePeriod(status);
                    statusData.put(status.name(), period);
                }
                talonFXData.put(talon.getDeviceID(), statusData);
            }
            data.put("TalonFX", talonFXData);
        }

        {
            var sparkMaxData = new HashMap<Integer, Map<String, Integer>>();
            for (var spark : sparks) {
                var statusData = new HashMap<String, Integer>();

                var allStatus = Arrays.asList(
                    CANSparkMaxLowLevel.PeriodicFrame.kStatus0,
                    CANSparkMaxLowLevel.PeriodicFrame.kStatus1,
                    CANSparkMaxLowLevel.PeriodicFrame.kStatus2
                );

                for (var status: allStatus) {
//                    spark.getStat
                }

                sparkMaxData.put(spark.getDeviceId(), statusData);
            }
            data.put("SparkMAX", sparkMaxData);
        }
    }
}
