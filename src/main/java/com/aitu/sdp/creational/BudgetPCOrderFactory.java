package com.aitu.sdp.creational;

/** Factory Method creator for budget computers. */
public final class BudgetPCOrderFactory extends ComputerOrderFactory {
    private final ComponentFactory componentFactory;

    public BudgetPCOrderFactory() {
        this(new AMDComponentFactory());
    }

    public BudgetPCOrderFactory(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new IllegalArgumentException("Component factory cannot be null");
        }
        this.componentFactory = componentFactory;
    }

    @Override
    protected ComputerBuilder createBuilder() {
        return new BudgetComputerBuilder(componentFactory);
    }
}
