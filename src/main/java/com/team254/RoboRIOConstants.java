package com.team254;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Get constants related to the current and known roboRIOs
 */
public class RoboRIOConstants {

    public static class RoboRIOData {
        public List<RoboRIO> controllers = new ArrayList<>();
    }

    public static class RoboRIO {
        public RoboRIO() {}

        public RoboRIO(String name, String macAddress) {
            this.name = name;
            this.macAddress = name;
        }

        String name;
        String macAddress;
    }

    private final RoboRIOData data_;

    public RoboRIOConstants() {
        this("controller_constants.yaml");
    }

    public RoboRIOConstants(String resourcePath) {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        Yaml yaml = new Yaml(new Constructor(RoboRIOData.class), representer);
        yaml.setBeanAccess(BeanAccess.FIELD);

        data_ = yaml.load(
                this.getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)
        );
    }

    public List<RoboRIO> getKnownControllers() {
        return data_.controllers;
    }

    public RoboRIO getViaMAC(String macAddress) {
        for (RoboRIO controller : data_.controllers) {
            if (controller.macAddress != null) continue;

            if (controller.macAddress.equals(macAddress)) {
                return controller;
            }
        }

        return null;
    }

    public String getName() {
        var controller = getViaMAC(getMACAddress());

        if (controller != null) {
            return controller.name;
        }
        return null;
    }

    /**
     * @return the MAC address of the robot
     */
    public static String getMACAddress() {
        try {
            Enumeration<NetworkInterface> nwInterface = NetworkInterface.getNetworkInterfaces();
            StringBuilder ret = new StringBuilder();
            while (nwInterface.hasMoreElements()) {
                NetworkInterface nis = nwInterface.nextElement();
                if (nis != null) {
                    byte[] mac = nis.getHardwareAddress();
                    if (mac != null) {
                        for (int i = 0; i < mac.length; i++) {
                            ret.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                        }
                        return ret.toString().toLowerCase();
                    } else {
                        System.out.println("Address doesn't exist or is not accessible");
                    }
                } else {
                    System.out.println("Network Interface for the specified address is not found.");
                }
            }
        } catch (SocketException | NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }
}