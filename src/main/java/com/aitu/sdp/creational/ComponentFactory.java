package com.aitu.sdp.creational;

/** Abstract Factory for matching CPU and GPU product families. */
public interface ComponentFactory {
    CPU createCPU(PerformanceTier tier);

    GPU createGPU(PerformanceTier tier);

    CPU createCPU(String model);

    GPU createGPU(String model);

    Motherboard createMotherboard(PerformanceTier tier);

    Motherboard createMotherboard(String model);

    Storage storage(PerformanceTier tier);

    PowerSupply powerSupply(PerformanceTier tier);

    CoolingSystem coolingSystem(PerformanceTier tier);

    PowerSupply createPowerSupply(String model);

    ComponentFamily family();
}
