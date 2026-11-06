package com.aitu.sdp.creational;

/** Factory Method creator for gaming computers. */
public final class GamingPCOrderFactory extends ComputerOrderFactory {
    private final ComponentFactory componentFactory;

    public GamingPCOrderFactory() {
        this(new IntelNvidiaComponentFactory());
    }

    public GamingPCOrderFactory(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new IllegalArgumentException("Component factory cannot be null");
        }
        this.componentFactory = componentFactory;
    }

    @Override
    protected ComputerBuilder createBuilder() {
        return new GamingComputerBuilder(componentFactory);
    }
}
