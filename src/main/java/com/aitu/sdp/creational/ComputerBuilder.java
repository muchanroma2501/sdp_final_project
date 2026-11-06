package com.aitu.sdp.creational;

/** Builder interface for fluent Computer construction. */
public interface ComputerBuilder {
    ComputerBuilder cpu(String cpu);
    ComputerBuilder cpu(CPU cpu);
    ComputerBuilder gpu(String gpu);
    ComputerBuilder gpu(GPU gpu);
    ComputerBuilder ram(String ram);
    ComputerBuilder storage(String storage);
    ComputerBuilder storage(Storage storage);
    ComputerBuilder powerSupply(String powerSupply);
    ComputerBuilder powerSupply(PowerSupply powerSupply);
    ComputerBuilder motherboard(String motherboard);
    ComputerBuilder motherboard(Motherboard motherboard);
    ComputerBuilder coolingSystem(String coolingSystem);
    ComputerBuilder coolingSystem(CoolingSystem coolingSystem);
    ComputerBuilder withRGB(boolean hasRGB);
    Computer build();
}
