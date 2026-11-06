package com.aitu.sdp.creational;

/** Creates compatible Intel and NVIDIA components. */
public final class IntelNvidiaComponentFactory implements ComponentFactory {
    @Override
    public CPU createCPU(PerformanceTier tier) {
        return ComponentCatalog.cpu(ComponentFamily.INTEL_NVIDIA, tier);
    }

    @Override
    public GPU createGPU(PerformanceTier tier) {
        return ComponentCatalog.gpu(ComponentFamily.INTEL_NVIDIA, tier);
    }

    @Override
    public CPU createCPU(String model) {
        return ComponentCatalog.cpu(ComponentFamily.INTEL_NVIDIA, model);
    }

    @Override
    public GPU createGPU(String model) {
        return ComponentCatalog.gpu(ComponentFamily.INTEL_NVIDIA, model);
    }

    @Override
    public Motherboard createMotherboard(PerformanceTier tier) {
        return ComponentCatalog.motherboard(ComponentFamily.INTEL_NVIDIA, tier);
    }

    @Override
    public Motherboard createMotherboard(String model) {
        return ComponentCatalog.motherboard(ComponentFamily.INTEL_NVIDIA, model);
    }

    @Override
    public Storage storage(PerformanceTier tier) {
        return ComponentCatalog.storage(tier);
    }

    @Override
    public PowerSupply powerSupply(PerformanceTier tier) {
        return ComponentCatalog.powerSupply(ComponentFamily.INTEL_NVIDIA, tier);
    }

    @Override
    public CoolingSystem coolingSystem(PerformanceTier tier) {
        return ComponentCatalog.coolingSystem(ComponentFamily.INTEL_NVIDIA, tier);
    }

    @Override
    public PowerSupply createPowerSupply(String model) {
        return ComponentCatalog.powerSupply(model);
    }

    @Override
    public ComponentFamily family() {
        return ComponentFamily.INTEL_NVIDIA;
    }
}
