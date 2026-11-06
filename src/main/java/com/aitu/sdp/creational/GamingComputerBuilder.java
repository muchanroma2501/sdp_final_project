package com.aitu.sdp.creational;

/** Builder with gaming-oriented hardware defaults. */
public final class GamingComputerBuilder extends AbstractComputerBuilder {
    public GamingComputerBuilder() {
        this(new IntelNvidiaComponentFactory());
    }

    public GamingComputerBuilder(ComponentFactory componentFactory) {
        super(componentFactory, PerformanceTier.HIGH);
    }
}
