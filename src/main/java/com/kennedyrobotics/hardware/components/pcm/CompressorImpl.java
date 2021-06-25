package com.kennedyrobotics.hardware.components.pcm;

import edu.wpi.first.wpilibj.Compressor;

public class CompressorImpl extends Compressor implements ICompressor {

    public CompressorImpl(int module) {
        super(module);
    }

    public CompressorImpl() {
        super();
    }
}