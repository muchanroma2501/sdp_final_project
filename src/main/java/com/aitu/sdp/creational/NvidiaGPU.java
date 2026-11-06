package com.aitu.sdp.creational;

/** NVIDIA product in the Intel/NVIDIA component family. */
public final class NvidiaGPU implements GPU {
    private final String model;
    private final int tdpWatts;

    public NvidiaGPU() {
        this("NVIDIA GeForce RTX 4090 24GB");
    }

    public NvidiaGPU(String model) {
        this(model, ComponentCatalog.gpuTdpWatts(ComponentFamily.INTEL_NVIDIA, model));
    }

    public NvidiaGPU(String model, int tdpWatts) {
        this.model = requireModel(model);
        this.tdpWatts = requireTdp(tdpWatts);
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public int getTdpWatts() {
        return tdpWatts;
    }

    private static String requireModel(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("GPU model cannot be null or blank");
        }
        return value.trim();
    }

    private static int requireTdp(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("GPU TDP must be positive");
        }
        return value;
    }
}
