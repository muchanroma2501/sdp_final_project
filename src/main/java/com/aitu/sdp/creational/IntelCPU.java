package com.aitu.sdp.creational;

/** Intel product in the Intel/NVIDIA component family. */
public final class IntelCPU implements CPU {
    private final String model;
    private final int tdpWatts;

    public IntelCPU() {
        this("Intel Core i9-14900K");
    }

    public IntelCPU(String model) {
        this(model, ComponentCatalog.cpuTdpWatts(ComponentFamily.INTEL_NVIDIA, model));
    }

    public IntelCPU(String model, int tdpWatts) {
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
        return ComponentCatalog.socketForCpuFamily(ComponentFamily.INTEL_NVIDIA);
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
