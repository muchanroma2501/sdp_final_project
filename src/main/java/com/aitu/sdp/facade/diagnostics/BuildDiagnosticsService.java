package com.aitu.sdp.facade.diagnostics;

import com.aitu.sdp.creational.ComponentCatalog;
import com.aitu.sdp.creational.ComponentFamily;
import com.aitu.sdp.creational.CPU;
import com.aitu.sdp.creational.GPU;
import com.aitu.sdp.creational.Motherboard;
import com.aitu.sdp.creational.PowerRequirementCalculator;
import com.aitu.sdp.creational.PowerSupply;
import com.aitu.sdp.creational.controller.CustomPCRequest;
import com.aitu.sdp.facade.support.ComponentFactoryProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BuildDiagnosticsService {
    private static final Map<String, String> CATALOG_ALIASES = Map.of(
            "32GB DDR5-6000", "32GB DDR5",
            "2TB NVMe PCIe 4.0 SSD", "2TB NVMe Gen4 SSD");

    private final ComponentFactoryProvider factoryProvider;
    private final CompatibilityChecker compatibilityChecker;

    public BuildDiagnosticsService(ComponentFactoryProvider factoryProvider,
                                   CompatibilityChecker compatibilityChecker) {
        this.factoryProvider = factoryProvider;
        this.compatibilityChecker = compatibilityChecker;
    }

    public BuildDiagnostics diagnose(CustomPCRequest request) {
        List<DiagnosticCheck> checks = new ArrayList<>();
        if (request == null) {
            checks.add(compatibilityChecker.error("required-fields", "Обязательные поля",
                    "Тело запроса не задано.", "Передайте конфигурацию ПК."));
            return result(checks, null, null, null);
        }

        List<String> missing = missingFields(request);
        checks.add(missing.isEmpty()
                ? compatibilityChecker.pass("required-fields", "Обязательные поля",
                        "Все обязательные поля заполнены.")
                : compatibilityChecker.error("required-fields", "Обязательные поля",
                        "Не заполнены поля: " + String.join(", ", missing) + ".",
                        "Заполните обязательные поля и повторите проверку."));

        ComponentFamily family = null;
        try {
            family = factoryProvider.familyFor(request.getEcosystem());
            checks.add(compatibilityChecker.pass("platform-selection", "Выбор платформы",
                    "Выбрана платформа " + request.getEcosystem().trim() + "."));
        } catch (IllegalArgumentException exception) {
            checks.add(compatibilityChecker.error("platform-selection", "Выбор платформы",
                    exception.getMessage(), "Используйте 'intel_nvidia' или 'amd'."));
        }

        CPU cpu = resolveCpu(request.getCpu(), checks);
        GPU gpu = resolveGpu(request.getGpu(), checks);
        Motherboard motherboard = resolveMotherboard(request.getMotherboard(), checks);
        validateCatalogSelection(request.getRam(), ComponentCatalog.ramModels(), "RAM", "ram", checks);
        validateCatalogSelection(request.getStorage(), ComponentCatalog.storageModels(),
                "накопитель", "storage", checks);
        validateCatalogSelection(request.getPowerSupply(), ComponentCatalog.powerSupplyModels(),
                "блок питания", "powerSupply", checks);
        if (hasText(request.getCoolingSystem())) {
            validateCatalogSelection(request.getCoolingSystem(), ComponentCatalog.coolingModels(),
                    "система охлаждения", "coolingSystem", checks);
        }

        Integer requiredWatts = cpu != null && gpu != null
                ? PowerRequirementCalculator.requiredWatts(cpu.getTdpWatts(), gpu.getTdpWatts())
                : null;
        Integer selectedWatts = resolvePsuWatts(request.getPowerSupply());
        Integer recommendedWatts = requiredWatts == null
                ? null : PowerRequirementCalculator.recommendedWatts(requiredWatts);

        if (family == null) {
            checks.add(compatibilityChecker.error("platform-family", "Семейство компонентов",
                    "Нельзя проверить семейство компонентов без корректной платформы.",
                    "Выберите 'intel_nvidia' или 'amd'."));
        } else if (cpu != null && gpu != null && motherboard != null) {
            checks.add(compatibilityChecker.platformCheck(
                    ComponentCatalog.cpus(family).stream().anyMatch(spec -> spec.model().equals(cpu.getModel()))
                            && ComponentCatalog.gpus(family).stream().anyMatch(spec -> spec.model().equals(gpu.getModel()))
                            && ComponentCatalog.motherboards(family).stream()
                            .anyMatch(spec -> spec.model().equals(motherboard.getModel()))));
        } else {
            checks.add(compatibilityChecker.error("platform-family", "Семейство компонентов",
                    "Нельзя проверить семейство: один или несколько компонентов неизвестны.",
                    "Выберите модели из каталога выбранной платформы."));
        }
        checks.add(compatibilityChecker.socketCheck(cpu, motherboard));
        checks.add(compatibilityChecker.powerCheck(selectedWatts, requiredWatts));
        checks.add(compatibilityChecker.headroomCheck(selectedWatts, recommendedWatts));
        return result(checks, requiredWatts, recommendedWatts, selectedWatts);
    }

    private CPU resolveCpu(String model, List<DiagnosticCheck> checks) {
        try {
            return ComponentCatalog.cpu(model);
        } catch (IllegalArgumentException exception) {
            checks.add(compatibilityChecker.error("cpu-model", "Процессор", exception.getMessage(),
                    "Выберите процессор из каталога."));
            return null;
        }
    }

    private GPU resolveGpu(String model, List<DiagnosticCheck> checks) {
        try {
            return ComponentCatalog.gpu(model);
        } catch (IllegalArgumentException exception) {
            checks.add(compatibilityChecker.error("gpu-model", "Видеокарта", exception.getMessage(),
                    "Выберите видеокарту из каталога."));
            return null;
        }
    }

    private Motherboard resolveMotherboard(String model, List<DiagnosticCheck> checks) {
        try {
            return ComponentCatalog.motherboard(model);
        } catch (IllegalArgumentException exception) {
            checks.add(compatibilityChecker.error("motherboard-model", "Материнская плата",
                    exception.getMessage(), "Выберите материнскую плату из каталога."));
            return null;
        }
    }

    private void validateCatalogSelection(String value, List<String> models, String title,
                                          String id, List<DiagnosticCheck> checks) {
        String selection = hasText(value) ? value.trim() : null;
        String catalogModel = selection == null ? null : CATALOG_ALIASES.get(selection);
        if (selection != null && models.contains(selection)) {
            checks.add(compatibilityChecker.pass(id, title,
                    "Выбранная модель " + selection + " есть в каталоге."));
        } else if (catalogModel != null && models.contains(catalogModel)) {
            checks.add(compatibilityChecker.pass(id, title,
                    "Выбранная модель " + selection + " соответствует каталогу " + catalogModel + "."));
        } else if (hasText(value)) {
            checks.add(compatibilityChecker.error(id, title,
                    "Неизвестная модель: " + selection + ".",
                    "Выберите значение из каталога компонентов."));
        }
    }

    private Integer resolvePsuWatts(String model) {
        if (!hasText(model) || !ComponentCatalog.powerSupplyModels().contains(model.trim())) {
            return null;
        }
        PowerSupply powerSupply = ComponentCatalog.powerSupply(model.trim());
        return powerSupply.wattage();
    }

    private List<String> missingFields(CustomPCRequest request) {
        List<String> missing = new ArrayList<>();
        addMissing(request.getEcosystem(), "ecosystem", missing);
        addMissing(request.getCpu(), "cpu", missing);
        addMissing(request.getGpu(), "gpu", missing);
        addMissing(request.getMotherboard(), "motherboard", missing);
        addMissing(request.getRam(), "ram", missing);
        addMissing(request.getStorage(), "storage", missing);
        addMissing(request.getPowerSupply(), "powerSupply", missing);
        return missing;
    }

    private void addMissing(String value, String field, List<String> missing) {
        if (!hasText(value)) {
            missing.add(field);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private BuildDiagnostics result(List<DiagnosticCheck> checks, Integer required,
                                    Integer recommended, Integer selected) {
        boolean blocked = checks.stream().anyMatch(check -> check.severity() == DiagnosticSeverity.ERROR);
        boolean warning = checks.stream().anyMatch(check -> check.severity() == DiagnosticSeverity.WARNING);
        DiagnosticStatus status = blocked ? DiagnosticStatus.BLOCKED
                : warning ? DiagnosticStatus.REVIEW : DiagnosticStatus.READY;
        return new BuildDiagnostics(status, List.copyOf(checks), required, recommended, selected, !blocked);
    }
}
