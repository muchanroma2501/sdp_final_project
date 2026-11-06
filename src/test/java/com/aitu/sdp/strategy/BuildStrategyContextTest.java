package com.aitu.sdp.strategy;

import com.aitu.sdp.creational.AMDComponentFactory;
import com.aitu.sdp.creational.ComponentFactory;
import com.aitu.sdp.creational.IntelNvidiaComponentFactory;
import com.aitu.sdp.creational.controller.BuildPCRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildStrategyContextTest {
    @Test
    void switchesStrategiesAtRuntime() {
        ComponentFactory intelFactory = new IntelNvidiaComponentFactory();

        BuildPCRequest gamingRequest = new BuildPCRequest();
        gamingRequest.setEcosystem("intel_nvidia");
        gamingRequest.setBuildType("gaming");
        gamingRequest.setRam("32GB DDR5");
        gamingRequest.setStorage("1TB NVMe SSD");
        gamingRequest.setCoolingSystem("280mm AIO Liquid Cooling");
        gamingRequest.setHasRGB(true);

        BuildPCRequest budgetRequest = new BuildPCRequest();
        budgetRequest.setEcosystem("intel_nvidia");
        budgetRequest.setBuildType("budget");
        budgetRequest.setRam("16GB DDR4");
        budgetRequest.setStorage("512GB NVMe SSD");
        budgetRequest.setCoolingSystem("Air Cooler");
        budgetRequest.setHasRGB(false);

        BuildStrategyContext context = new BuildStrategyContext();
        context.setStrategy(new GamingBuildStrategy());
        assertEquals("Intel Core i9-14900K", context.build(intelFactory, gamingRequest).getCpu());

        context.setStrategy(new BudgetBuildStrategy());
        assertEquals("Intel Core i5-13400", context.build(intelFactory, budgetRequest).getCpu());

        BuildPCRequest workstationRequest = new BuildPCRequest();
        workstationRequest.setEcosystem("intel_nvidia");
        workstationRequest.setBuildType("workstation");
        workstationRequest.setRam("64GB DDR5");
        workstationRequest.setStorage("2TB NVMe SSD");
        workstationRequest.setCoolingSystem("360mm AIO Liquid Cooling");
        workstationRequest.setHasRGB(true);

        context.setStrategy(new WorkstationBuildStrategy());
        assertEquals("Intel Core i9-14900K", context.build(intelFactory, workstationRequest).getCpu());
    }

    @Test
    void rejectsNullStrategy() {
        BuildStrategyContext context = new BuildStrategyContext();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> context.build(new AMDComponentFactory(), new BuildPCRequest()));
        assertTrue(ex.getMessage().contains("Strategy must be configured"));
        assertNotNull(ex.getMessage());
    }
}
