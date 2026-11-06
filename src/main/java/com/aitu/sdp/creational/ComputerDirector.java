package com.aitu.sdp.creational;

/** Director demonstrating reusable Builder configurations. */
public final class ComputerDirector {
    private final ComponentFactory componentFactory;

    public ComputerDirector() {
        this(new IntelNvidiaComponentFactory());
    }

    public ComputerDirector(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new IllegalArgumentException("Component factory cannot be null");
        }
        this.componentFactory = componentFactory;
    }

    public Computer buildEntryLevelOfficePC() {
        return new BudgetComputerBuilder(componentFactory)
                .ram("16GB DDR4")
                .build();
    }

    public Computer buildUltraGamingPC() {
        return new GamingComputerBuilder(componentFactory)
                .ram("64GB DDR5")
                .storage("2TB NVMe SSD")
                .powerSupply("1000W 80+ Gold")
                .motherboard("Z790 ATX")
                .coolingSystem("360mm AIO Liquid Cooling")
                .build();
    }
}
