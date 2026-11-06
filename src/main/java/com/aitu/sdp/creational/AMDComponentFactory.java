package com.aitu.sdp.creational;

/** Creates compatible AMD components. */
public final class AMDComponentFactory implements ComponentFactory {
    @Override
    public CPU createCPU(PerformanceTier tier) {
        return ComponentCatalog.cpu(ComponentFamily.AMD, tier);
    }

    @Override
    public GPU createGPU(PerformanceTier tier) {
        return ComponentCatalog.gpu(ComponentFamily.AMD, tier);
    }

    @Override
    public CPU createCPU(String model) {
        return ComponentCatalog.cpu(ComponentFamily.AMD, model);
    }

    @Override
    public GPU createGPU(String model) {
        return ComponentCatalog.gpu(ComponentFamily.AMD, model);
    }

    @Override
    public Motherboard createMotherboard(PerformanceTier tier) {
        return ComponentCatalog.motherboard(ComponentFamily.AMD, tier);
    }

    @Override
    public Motherboard createMotherboard(String model) {
        return ComponentCatalog.motherboard(ComponentFamily.AMD, model);
    }

    @Override
    public Storage storage(PerformanceTier tier) {
        return ComponentCatalog.storage(tier);
    }

    @Override
    public PowerSupply powerSupply(PerformanceTier tier) {
        return ComponentCatalog.powerSupply(ComponentFamily.AMD, tier);
    }

    @Override
    public CoolingSystem coolingSystem(PerformanceTier tier) {
        return ComponentCatalog.coolingSystem(ComponentFamily.AMD, tier);
    }

    @Override
    public PowerSupply createPowerSupply(String model) {
        return ComponentCatalog.powerSupply(model);
    }

    @Override
    public ComponentFamily family() {
        return ComponentFamily.AMD;
    }
}
