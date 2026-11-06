package com.aitu.sdp.creational;

/** AMD product in the AMD component family. */
public final class AMDGPU implements GPU {
    private final String model;
    private final int tdpWatts;

    public AMDGPU() {
        this("Radeon Vega Integrated Graphics");
    }

    public AMDGPU(String model) {
        this(model, ComponentCatalog.gpuTdpWatts(ComponentFamily.AMD, model));
    }

    public AMDGPU(String model, int tdpWatts) {
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
