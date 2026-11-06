package com.aitu.sdp.creational;

/** Builder with budget-oriented hardware defaults. */
public final class BudgetComputerBuilder extends AbstractComputerBuilder {
    public BudgetComputerBuilder() {
        this(new AMDComponentFactory());
    }

    public BudgetComputerBuilder(ComponentFactory componentFactory) {
        super(componentFactory, PerformanceTier.LOW);
    }
}
