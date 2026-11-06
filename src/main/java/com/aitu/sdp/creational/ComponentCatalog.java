package com.aitu.sdp.creational;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Central source for component models and their compatibility metadata. */
public final class ComponentCatalog {
    private static final Pattern LEADING_WATTAGE = Pattern.compile("^\\s*(\\d+).*$");
    private static final List<CpuSpec> CPUS = List.of(
            new CpuSpec("Intel Core i5-13400", ComponentFamily.INTEL_NVIDIA, 65, "LGA1700"),
            new CpuSpec("Intel Core i7-14700K", ComponentFamily.INTEL_NVIDIA, 205, "LGA1700"),
            new CpuSpec("Intel Core i9-14900K", ComponentFamily.INTEL_NVIDIA, 253, "LGA1700"),
            new CpuSpec("AMD Ryzen 5 7600", ComponentFamily.AMD, 65, "AM5"),
            new CpuSpec("AMD Ryzen 7 7800X3D", ComponentFamily.AMD, 120, "AM5"),
            new CpuSpec("AMD Ryzen 9 7950X", ComponentFamily.AMD, 170, "AM5"));

    private static final List<GpuSpec> GPUS = List.of(
            new GpuSpec("NVIDIA GeForce RTX 3050 8GB", ComponentFamily.INTEL_NVIDIA, 130),
            new GpuSpec("NVIDIA GeForce RTX 4070 Ti 12GB", ComponentFamily.INTEL_NVIDIA, 285),
            new GpuSpec("NVIDIA GeForce RTX 4080 Super 16GB", ComponentFamily.INTEL_NVIDIA, 320),
            new GpuSpec("NVIDIA GeForce RTX 4090 24GB", ComponentFamily.INTEL_NVIDIA, 450),
            new GpuSpec("Radeon Vega Integrated Graphics", ComponentFamily.AMD, 35),
            new GpuSpec("AMD Radeon RX 6600 8GB", ComponentFamily.AMD, 132),
            new GpuSpec("AMD Radeon RX 7700 XT 12GB", ComponentFamily.AMD, 245),
            new GpuSpec("AMD Radeon RX 7900 XT 20GB", ComponentFamily.AMD, 315),
            new GpuSpec("AMD Radeon RX 7900 XTX 24GB", ComponentFamily.AMD, 355));

    private static final List<MotherboardSpec> MOTHERBOARDS = List.of(
            new MotherboardSpec("B760 Micro-ATX", ComponentFamily.INTEL_NVIDIA, "LGA1700"),
            new MotherboardSpec("Z790 ATX", ComponentFamily.INTEL_NVIDIA, "LGA1700"),
            new MotherboardSpec("B650 ATX", ComponentFamily.AMD, "AM5"),
            new MotherboardSpec("X670E ATX", ComponentFamily.AMD, "AM5"));

    private static final List<PowerSupplySpec> POWER_SUPPLIES = List.of(
            new PowerSupplySpec("500W 80+ Bronze", 500),
            new PowerSupplySpec("650W 80+ Gold", 650),
            new PowerSupplySpec("850W 80+ Gold", 850),
            new PowerSupplySpec("1000W 80+ Gold", 1000));
    private static final List<String> STORAGE_MODELS = List.of(
            "512GB NVMe SSD", "1TB NVMe SSD", "2TB NVMe SSD", "2TB NVMe Gen4 SSD");
    private static final List<String> COOLING_MODELS = List.of(
            "Stock Air Cooler", "Stock AMD Wraith Stealth",
            "280mm AIO Liquid Cooling", "360mm AIO Liquid Cooling");
    private static final List<String> RAM_MODELS = List.of("16GB DDR4", "32GB DDR5", "64GB DDR5");

    private ComponentCatalog() {
    }

    public static List<CpuSpec> cpus(ComponentFamily family) {
        return CPUS.stream().filter(spec -> spec.family() == family).toList();
    }

    public static List<GpuSpec> gpus(ComponentFamily family) {
        return GPUS.stream().filter(spec -> spec.family() == family).toList();
    }

    public static List<MotherboardSpec> motherboards(ComponentFamily family) {
        return MOTHERBOARDS.stream().filter(spec -> spec.family() == family).toList();
    }

    public static String socketForCpuFamily(ComponentFamily family) {
        return CPUS.stream()
                .filter(spec -> spec.family() == family)
                .findFirst()
                .orElseThrow(() -> unknownComponent("CPU family", family.name(), family))
                .socket();
    }

    public static String socketForMotherboard(String model) {
        String normalizedModel = normalizeModel(model, "Motherboard");
        return MOTHERBOARDS.stream()
                .filter(spec -> spec.model().equals(normalizedModel))
                .findFirst()
                .orElseThrow(() -> unknownComponent("Motherboard", normalizedModel, null))
                .socket();
    }

    public static List<String> powerSupplyModels() {
        return POWER_SUPPLIES.stream().map(PowerSupplySpec::model).toList();
    }

    public static List<String> storageModels() {
        return STORAGE_MODELS;
    }

    public static List<String> coolingModels() {
        return COOLING_MODELS;
    }

    public static List<String> ramModels() {
        return RAM_MODELS;
    }

    public static CPU cpu(ComponentFamily family, PerformanceTier tier) {
        String model = switch (family) {
            case INTEL_NVIDIA -> tier == PerformanceTier.HIGH
                    ? "Intel Core i9-14900K" : "Intel Core i5-13400";
            case AMD -> tier == PerformanceTier.HIGH
                    ? "AMD Ryzen 7 7800X3D" : "AMD Ryzen 5 7600";
        };
        return cpu(family, model);
    }

    public static GPU gpu(ComponentFamily family, PerformanceTier tier) {
        String model = switch (family) {
            case INTEL_NVIDIA -> tier == PerformanceTier.HIGH
                    ? "NVIDIA GeForce RTX 4090 24GB" : "NVIDIA GeForce RTX 3050 8GB";
            case AMD -> tier == PerformanceTier.HIGH
                    ? "AMD Radeon RX 7900 XT 20GB" : "Radeon Vega Integrated Graphics";
        };
        return gpu(family, model);
    }

    public static Motherboard motherboard(ComponentFamily family, PerformanceTier tier) {
        String model = switch (family) {
            case INTEL_NVIDIA -> tier == PerformanceTier.HIGH ? "Z790 ATX" : "B760 Micro-ATX";
            case AMD -> tier == PerformanceTier.HIGH ? "X670E ATX" : "B650 ATX";
        };
        return motherboard(family, model);
    }

    public static Storage storage(PerformanceTier tier) {
        return new Storage(tier == PerformanceTier.HIGH ? "2TB NVMe Gen4 SSD" : "512GB NVMe SSD");
    }

    public static PowerSupply powerSupply(ComponentFamily family, PerformanceTier tier) {
        String model = tier == PerformanceTier.HIGH
                ? (family == ComponentFamily.AMD ? "850W 80+ Gold" : "1000W 80+ Gold")
                : "500W 80+ Bronze";
        return powerSupply(model);
    }

    public static CoolingSystem coolingSystem(ComponentFamily family, PerformanceTier tier) {
        String model;
        if (tier == PerformanceTier.LOW) {
            model = family == ComponentFamily.AMD
                    ? "Stock AMD Wraith Stealth" : "Stock Air Cooler";
        } else {
            model = family == ComponentFamily.AMD
                    ? "280mm AIO Liquid Cooling" : "360mm AIO Liquid Cooling";
        }
        return new CoolingSystem(model);
    }

    public static int cpuTdpWatts(ComponentFamily family, String model) {
        String normalizedModel = normalizeModel(model, "CPU");
        return CPUS.stream()
                .filter(spec -> spec.model().equals(normalizedModel) && spec.family() == family)
                .findFirst()
                .orElseThrow(() -> unknownComponent("CPU", normalizedModel, family))
                .tdpWatts();
    }

    public static int gpuTdpWatts(ComponentFamily family, String model) {
        String normalizedModel = normalizeModel(model, "GPU");
        return GPUS.stream()
                .filter(spec -> spec.model().equals(normalizedModel) && spec.family() == family)
                .findFirst()
                .orElseThrow(() -> unknownComponent("GPU", normalizedModel, family))
                .tdpWatts();
    }

    public static CPU cpu(ComponentFamily family, String model) {
        String normalizedModel = normalizeModel(model, "CPU");
        CpuSpec spec = CPUS.stream()
                .filter(candidate -> candidate.model().equals(normalizedModel) && candidate.family() == family)
                .findFirst()
                .orElseThrow(() -> unknownComponent("CPU", normalizedModel, family));
        return family == ComponentFamily.AMD
                ? new AMDCPU(spec.model(), spec.tdpWatts())
                : new IntelCPU(spec.model(), spec.tdpWatts());
    }

    public static CPU cpu(String model) {
        String normalizedModel = normalizeModel(model, "CPU");
        CpuSpec spec = CPUS.stream().filter(candidate -> candidate.model().equals(normalizedModel))
                .findFirst().orElseThrow(() -> unknownComponent("CPU", normalizedModel, null));
        return cpu(spec.family(), spec.model());
    }

    public static GPU gpu(ComponentFamily family, String model) {
        String normalizedModel = normalizeModel(model, "GPU");
        GpuSpec spec = GPUS.stream()
                .filter(candidate -> candidate.model().equals(normalizedModel) && candidate.family() == family)
                .findFirst()
                .orElseThrow(() -> unknownComponent("GPU", normalizedModel, family));
        return family == ComponentFamily.AMD
                ? new AMDGPU(spec.model(), spec.tdpWatts())
                : new NvidiaGPU(spec.model(), spec.tdpWatts());
    }

    public static GPU gpu(String model) {
        String normalizedModel = normalizeModel(model, "GPU");
        GpuSpec spec = GPUS.stream().filter(candidate -> candidate.model().equals(normalizedModel))
                .findFirst().orElseThrow(() -> unknownComponent("GPU", normalizedModel, null));
        return gpu(spec.family(), spec.model());
    }

    public static Motherboard motherboard(ComponentFamily family, String model) {
        String normalizedModel = normalizeModel(model, "Motherboard");
        MotherboardSpec spec = MOTHERBOARDS.stream()
                .filter(candidate -> candidate.model().equals(normalizedModel) && candidate.family() == family)
                .findFirst()
                .orElseThrow(() -> unknownComponent("Motherboard", normalizedModel, family));
        return switch (spec.model()) {
            case "B760 Micro-ATX" -> new B760_MicroATX_Motherboard();
            case "Z790 ATX" -> new Z790_ATX_Motherboard();
            case "B650 ATX" -> new B650_ATX_Motherboard();
            case "X670E ATX" -> new X670E_ATX_Motherboard();
            default -> throw new IllegalStateException("Unmapped catalog motherboard: " + spec.model());
        };
    }

    public static Motherboard motherboard(String model) {
        String normalizedModel = normalizeModel(model, "Motherboard");
        MotherboardSpec spec = MOTHERBOARDS.stream()
                .filter(candidate -> candidate.model().equals(normalizedModel))
                .findFirst().orElseThrow(() -> unknownComponent("Motherboard", normalizedModel, null));
        return motherboard(spec.family(), spec.model());
    }

    public static PowerSupply powerSupply(String model) {
        String normalizedModel = normalizeModel(model, "Power supply");
        PowerSupplySpec spec = POWER_SUPPLIES.stream()
                .filter(candidate -> candidate.model().equals(normalizedModel))
                .findFirst().orElse(null);
        if (spec != null) {
            return new PowerSupply(spec.model(), spec.wattage());
        }
        Matcher wattageMatch = LEADING_WATTAGE.matcher(normalizedModel);
        if (!wattageMatch.matches()) {
            throw new IllegalArgumentException(
                    "Power supply model must begin with its wattage, for example 850W 80+ Gold");
        }
        try {
            return new PowerSupply(normalizedModel, Integer.parseInt(wattageMatch.group(1)));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Power supply wattage is outside the supported range", exception);
        }
    }

    public record CpuSpec(String model, ComponentFamily family, int tdpWatts, String socket) {
    }

    public record GpuSpec(String model, ComponentFamily family, int tdpWatts) {
    }

    public record MotherboardSpec(String model, ComponentFamily family, String socket) {
    }

    private record PowerSupplySpec(String model, int wattage) {
    }

    private static IllegalArgumentException unknownComponent(
            String kind, String model, ComponentFamily family) {
        String familyText = family == null ? "" : " for " + family;
        return new IllegalArgumentException("Unknown " + kind + familyText + ": " + model);
    }

    private static String normalizeModel(String model, String kind) {
        if (model == null || model.isBlank()) {
            throw unknownComponent(kind, model, null);
        }
        return model.trim();
    }
}
