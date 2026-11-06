package com.aitu.sdp.creational;

/** Builder configuration for creator-focused workstation systems. */
public final class WorkstationComputerBuilder extends AbstractComputerBuilder {
    public WorkstationComputerBuilder(ComponentFactory componentFactory) {
        super(componentFactory, PerformanceTier.HIGH);
        ram("64GB DDR5")
                .withRGB(false);
    }
}
