package com.team254;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RoboRIOConstantsTest {
    
    @Test
    public void testGetMACAddress() {
        var macAddress = RoboRIOConstants.getMACAddress();
        System.out.println(macAddress);

        // Verify that we get back a MAC address like the following: ac:de:48:00:11:22
        assertThat(macAddress, matchesPattern("^([a-z0-9]{2}:){5}[a-z0-9]{2}$"));
    }

    @Test
    public void testLoadingOfYamlFile() {
        RoboRIOConstants constants = new RoboRIOConstants("testing/controller_constants.yaml");
        assertTrue(constants.getKnownControllers().size() == 2, "Wrong number of controlers loaded from yaml");

        assertEquals("competition", constants.getKnownControllers().get(0).name);
        assertEquals("00:80:2f:17:f8:26", constants.getKnownControllers().get(0).macAddress);

        assertEquals("testbench", constants.getKnownControllers().get(1).name);
        assertEquals("00:80:2f:17:c3:4f", constants.getKnownControllers().get(1).macAddress);

    }

    @Test
    public void testGetViaMAC_KnownMAC() {
        RoboRIOConstants constants = new RoboRIOConstants("testing/controller_constants.yaml");
        assertTrue(constants.getKnownControllers().size() == 2, "Wrong number of controlers loaded from yaml");

        System.out.println(constants.getKnownControllers());

        var controller = constants.getViaMAC("00:80:2f:17:f8:26");
        assertNotNull(controller);
        assertEquals("competition", controller.name);
        assertEquals("00:80:2f:17:f8:26", controller.macAddress);

        controller = constants.getViaMAC("00:80:2f:17:c3:4f");
        assertNotNull(controller);
        assertEquals("testbench", controller.name);
        assertEquals("00:80:2f:17:c3:4f", controller.macAddress);
    }

    @Test
    public void testGetViaMAC_UnknownMAC() {
        RoboRIOConstants constants = new RoboRIOConstants("testing/controller_constants.yaml");

        var controller = constants.getViaMAC("de:ad:be:ef:00:00");
        assertNull(controller);
    }

}
