package com.aitu.sdp.creational;

/**
 * Immutable product assembled by the Builder.
 *
 * <p>Clean Code principle: Immutability and Encapsulation. Every field is
 * private and final, and construction validates all required values.</p>
 */
public final class Computer {
    private final CPU cpu;
    private final GPU gpu;
    private final String ram;
    private final Storage storage;
    private final PowerSupply powerSupply;
    private final Motherboard motherboard;
    private final CoolingSystem coolingSystem;
    private final boolean hasRGB;

    public Computer(CPU cpu, GPU gpu, String ram, Storage storage,
                    PowerSupply powerSupply, Motherboard motherboard,
                    CoolingSystem coolingSystem, boolean hasRGB) {
        this.cpu = requireComponent(cpu, "CPU");
        this.gpu = requireComponent(gpu, "GPU");
        this.ram = requireText(ram, "RAM");
        this.storage = requireComponent(storage, "Storage");
        this.powerSupply = requireComponent(powerSupply, "Power supply");
        this.motherboard = requireComponent(motherboard, "Motherboard");
        this.coolingSystem = requireComponent(coolingSystem, "Cooling system");
        this.hasRGB = hasRGB;
    }

    /** Compatibility constructor; all hardware strings are resolved into typed components. */
    public Computer(String cpu, String gpu, String ram, String storage,
                    String powerSupply, String motherboard,
                    String coolingSystem, boolean hasRGB) {
        this(ComponentCatalog.cpu(requireText(cpu, "CPU")),
                ComponentCatalog.gpu(requireText(gpu, "GPU")),
                ram,
                new Storage(storage),
                ComponentCatalog.powerSupply(requireText(powerSupply, "Power supply")),
                ComponentCatalog.motherboard(requireText(motherboard, "Motherboard")),
                new CoolingSystem(coolingSystem),
                hasRGB);
    }

    private static <T> T requireComponent(T value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " cannot be null");
        }
        return value;
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be null or blank");
        }
        return value.trim();
    }

    public String getCpu() { return cpu.getModel(); }
    public String getGpu() { return gpu.getModel(); }
    public String getRam() { return ram; }
    public String getStorage() { return storage.model(); }
    public String getPowerSupply() { return powerSupply.model(); }
    public String getMotherboard() { return motherboard.getModel(); }
    public String getCoolingSystem() { return coolingSystem.model(); }
    public boolean isHasRGB() { return hasRGB; }

    public CPU getCpuComponent() { return cpu; }
    public GPU getGpuComponent() { return gpu; }
    public Storage getStorageComponent() { return storage; }
    public PowerSupply getPowerSupplyComponent() { return powerSupply; }
    public Motherboard getMotherboardComponent() { return motherboard; }
    public CoolingSystem getCoolingSystemComponent() { return coolingSystem; }

    @Override
    public String toString() {
        return "Computer {" + System.lineSeparator()
                + "  CPU: " + getCpu() + System.lineSeparator()
                + "  GPU: " + getGpu() + System.lineSeparator()
                + "  RAM: " + ram + System.lineSeparator()
                + "  Storage: " + getStorage() + System.lineSeparator()
                + "  Power Supply: " + getPowerSupply() + System.lineSeparator()
                + "  Motherboard: " + getMotherboard() + System.lineSeparator()
                + "  Cooling System: " + getCoolingSystem() + System.lineSeparator()
                + "  RGB Lighting: " + (hasRGB ? "Enabled" : "Disabled") + System.lineSeparator()
                + "}";
    }
}
