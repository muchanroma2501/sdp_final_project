package com.aitu.sdp.creational;

/** Factory Method creator for creator workstation builds. */
public final class WorkstationPCOrderFactory extends ComputerOrderFactory {
    private final ComponentFactory componentFactory;

    public WorkstationPCOrderFactory(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new IllegalArgumentException("Component factory cannot be null");
        }
        this.componentFactory = componentFactory;
    }

    @Override
    protected ComputerBuilder createBuilder() {
        return new WorkstationComputerBuilder(componentFactory);
    }
}
