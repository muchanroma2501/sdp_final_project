package com.aitu.sdp.creational;

/** AMD product in the AMD component family. */
public final class AMDCPU implements CPU {
    private final String model;
    private final int tdpWatts;

    public AMDCPU() {
        this("AMD Ryzen 5 7600");
    }

    public AMDCPU(String model) {
        this(model, ComponentCatalog.cpuTdpWatts(ComponentFamily.AMD, model));
    }

    public AMDCPU(String model, int tdpWatts) {
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

    @Override
    public String getSocket() {
        return ComponentCatalog.socketForCpuFamily(ComponentFamily.AMD);
    }

    private static String requireModel(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CPU model cannot be null or blank");
        }
        return value.trim();
    }

    private static int requireTdp(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("CPU TDP must be positive");
        }
        return value;
    }
}
