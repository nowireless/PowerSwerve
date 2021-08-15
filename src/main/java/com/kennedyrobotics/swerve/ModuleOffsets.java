package com.kennedyrobotics.swerve;

import com.team254.lib.geometry.Rotation2d;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Swerve Module offsets for absolute encoder.
 * Each swerve module has it own id number, as each individual module
 * has an unique absolute encoder offset value.
 */
public class ModuleOffsets {
    public static class ModuleData {
        /**
         * The key is the module id
         * The value is the offset in degrees
         */
        public Map<Integer, Double> offsets = new HashMap<>();
    }

    private final ModuleData data_;

    public ModuleOffsets() {
        this("module_offsets.yaml");
    }

    public ModuleOffsets(String resourcePath) {
        Yaml yaml = new Yaml(new Constructor(ModuleData.class));
        data_ = yaml.load(
                this.getClass()
                        .getClassLoader()
                        .getResourceAsStream(resourcePath)
        );
    }

    /**
     * Get offset
     * @param id Swerve module id
     * @return
     */
    public Rotation2d getOffset(int id) {
        Double offset = data_.offsets.get(id);
        if (offset == null) {
            throw new IllegalArgumentException("Invalid module id");
        }

        return Rotation2d.fromDegrees(offset);
    }

}
