package com.aitu.sdp.facade;

import com.aitu.sdp.creational.ComponentCatalog;
import com.aitu.sdp.creational.ComponentFactory;
import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.ComputerBuilder;
import com.aitu.sdp.creational.GamingComputerBuilder;
import com.aitu.sdp.creational.PowerRequirementCalculator;
import com.aitu.sdp.creational.controller.BuildPCRequest;
import com.aitu.sdp.creational.controller.CustomPCRequest;
import com.aitu.sdp.creational.controller.PCResponse;
import com.aitu.sdp.facade.diagnostics.BuildDiagnostics;
import com.aitu.sdp.facade.diagnostics.BuildDiagnosticsService;
import com.aitu.sdp.facade.diagnostics.DiagnosticStatus;
import com.aitu.sdp.facade.support.ComponentFactoryProvider;
import com.aitu.sdp.strategy.BuildStrategyContext;
import com.aitu.sdp.strategy.ComputerBuildStrategy;
import com.aitu.sdp.strategy.BudgetBuildStrategy;
import com.aitu.sdp.strategy.GamingBuildStrategy;
import com.aitu.sdp.strategy.WorkstationBuildStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PCConfiguratorFacade {
    private final ComponentFactoryProvider factoryProvider;
    private final BuildDiagnosticsService diagnosticsService;
    private final Map<String, ComputerBuildStrategy> strategyRegistry;

    @Autowired
    public PCConfiguratorFacade(ComponentFactoryProvider factoryProvider,
                               BuildDiagnosticsService diagnosticsService,
                               List<ComputerBuildStrategy> strategies) {
        this.factoryProvider = factoryProvider;
        this.diagnosticsService = diagnosticsService;
        this.strategyRegistry = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> normalizeBuildType(strategy.getBuildType()),
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new));
    }

    public PCConfiguratorFacade(ComponentFactoryProvider factoryProvider,
                               BuildDiagnosticsService diagnosticsService) {
        this(factoryProvider, diagnosticsService, List.of(
                new GamingBuildStrategy(),
                new BudgetBuildStrategy(),
                new WorkstationBuildStrategy()));
    }

    public List<PCResponse> getPresets() {
        ComponentFactory factory = factoryProvider.factoryFor("intel_nvidia");
        return List.of(
                response("Ultimate Gaming Beast",
                        new com.aitu.sdp.creational.GamingPCOrderFactory(factory).orderComputer(), "gaming", "intel_nvidia"),
                response("Budget Office PC",
                        new com.aitu.sdp.creational.BudgetPCOrderFactory(factory).orderComputer(), "budget", "intel_nvidia"),
                response("Creator Workstation",
                        new com.aitu.sdp.creational.WorkstationPCOrderFactory(factory).orderComputer(), "workstation", "intel_nvidia"));
    }

    public Map<String, Object> getComponents(String ecosystem) {
        ComponentFactory factory = factoryProvider.factoryFor(ecosystem);
        var family = factory.family();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cpus", ComponentCatalog.cpus(family).stream()
                .map(spec -> Map.of("model", spec.model(), "tdpWatts", spec.tdpWatts(),
                        "socket", spec.socket())).toList());
        result.put("gpus", ComponentCatalog.gpus(family).stream()
                .map(spec -> Map.of("model", spec.model(), "tdpWatts", spec.tdpWatts())).toList());
        result.put("motherboards", ComponentCatalog.motherboards(family).stream()
                .map(spec -> Map.of("model", spec.model(), "socket", spec.socket())).toList());
        result.put("ram", ComponentCatalog.ramModels());
        result.put("storage", ComponentCatalog.storageModels());
        result.put("powerSupplies", ComponentCatalog.powerSupplyModels());
        result.put("coolingSystems", ComponentCatalog.coolingModels());
        return result;
    }

    public BuildDiagnostics diagnose(CustomPCRequest request) {
        return diagnosticsService.diagnose(request);
    }

    public PCResponse buildCustom(CustomPCRequest request) {
        BuildDiagnostics diagnostics = diagnosticsService.diagnose(request);
        requireBuildAllowed(diagnostics);
        Computer computer = buildComputer(request);
        return response("Custom Studio Build", computer, "custom",
                request.getEcosystem(), diagnostics);
    }

    public Computer buildComputerForReport(CustomPCRequest request) {
        BuildDiagnostics diagnostics = diagnosticsService.diagnose(request);
        requireBuildAllowed(diagnostics);
        return buildComputer(request);
    }

    private Computer buildComputer(CustomPCRequest request) {
        ComponentFactory factory = factoryProvider.factoryFor(request.getEcosystem());
        ComputerBuilder builder = new GamingComputerBuilder(factory)
                .cpu(factory.createCPU(request.getCpu().trim()))
                .gpu(factory.createGPU(request.getGpu().trim()))
                .motherboard(factory.createMotherboard(request.getMotherboard().trim()))
                .ram(request.getRam().trim())
                .storage(request.getStorage().trim())
                .powerSupply(factory.createPowerSupply(request.getPowerSupply().trim()))
                .withRGB(request.isHasRGB());
        if (request.getCoolingSystem() != null && !request.getCoolingSystem().isBlank()) {
            builder.coolingSystem(request.getCoolingSystem().trim());
        }
        return builder.build();
    }

    public PCResponse buildNamed(BuildPCRequest request) {
        validateBuildRequest(request);
        ComponentFactory factory = factoryProvider.factoryFor(request.getEcosystem());

        String normalizedKey = normalizeBuildType(request.getBuildType());
        ComputerBuildStrategy strategy = strategyRegistry.get(normalizedKey);
        if (strategy == null) {
            throw new IllegalArgumentException("buildType must be gaming, budget, or workstation");
        }

        BuildStrategyContext context = new BuildStrategyContext();
        context.setStrategy(strategy);
        Computer computer = context.build(factory, request);

        BuildDiagnostics diagnostics = diagnosticsService.diagnose(toRequest(
                request.getEcosystem(), computer));
        requireBuildAllowed(diagnostics);
        return response(request.getBuildType().trim(), computer, strategy.getBuildType(),
                request.getEcosystem(), diagnostics);
    }

    public Computer buildComputerForInspection(BuildPCRequest request) {
        validateBuildRequest(request);
        ComponentFactory factory = factoryProvider.factoryFor(request.getEcosystem());

        String normalizedKey = normalizeBuildType(request.getBuildType());
        ComputerBuildStrategy strategy = strategyRegistry.get(normalizedKey);
        if (strategy == null) {
            throw new IllegalArgumentException("buildType must be gaming, budget, or workstation");
        }

        BuildStrategyContext context = new BuildStrategyContext();
        context.setStrategy(strategy);
        Computer computer = context.build(factory, request);
        BuildDiagnostics diagnostics = diagnosticsService.diagnose(toRequest(
                request.getEcosystem(), computer));
        requireBuildAllowed(diagnostics);
        return computer;
    }

    private static String normalizeBuildType(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private void validateBuildRequest(BuildPCRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        requireText(request.getEcosystem(), "ecosystem");
        requireText(request.getBuildType(), "buildType");
        requireText(request.getRam(), "ram");
        requireText(request.getStorage(), "storage");
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
    }

    private void requireBuildAllowed(BuildDiagnostics diagnostics) {
        if (diagnostics.status() == DiagnosticStatus.BLOCKED || !diagnostics.buildAllowed()) {
            throw new BuildRejectedException(diagnostics);
        }
    }

    private PCResponse response(String name, Computer computer, String pipeline, String ecosystem) {
        BuildDiagnostics diagnostics = diagnosticsService.diagnose(toRequest(ecosystem, computer));
        return response(name, computer, pipeline, ecosystem, diagnostics);
    }

    private PCResponse response(String name, Computer computer, String pipeline,
                                String ecosystem, BuildDiagnostics diagnostics) {
        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("CPU", computer.getCpu());
        specs.put("GPU", computer.getGpu());
        specs.put("RAM", computer.getRam());
        specs.put("Storage", computer.getStorage());
        specs.put("Power Supply", computer.getPowerSupply());
        specs.put("Motherboard", computer.getMotherboard());
        specs.put("Cooling System", computer.getCoolingSystem());
        specs.put("RGB Lighting", computer.isHasRGB() ? "Enabled" : "Disabled");
        int required = PowerRequirementCalculator.requiredWatts(
                computer.getCpuComponent().getTdpWatts(), computer.getGpuComponent().getTdpWatts());
        String highlights = "Abstract Factory selected the " + ecosystem
                + " component family. Strategy executed the " + pipeline
                + " pipeline. Builder created an immutable Computer after validation.";
        String validationStatus = diagnostics.status() == DiagnosticStatus.REVIEW
                ? "Valid - build allowed with recommendations."
                : diagnostics.status() == DiagnosticStatus.BLOCKED
                    ? "Blocked - incompatible configuration."
                    : "Valid - compatible configuration.";
        return new PCResponse(specs,
                validationStatus,
                highlights, name, required, PowerRequirementCalculator.recommendedWatts(required),
                computer.getPowerSupplyComponent().wattage(), diagnostics);
    }

    private CustomPCRequest toRequest(String ecosystem, Computer computer) {
        CustomPCRequest request = new CustomPCRequest();
        request.setEcosystem(ecosystem);
        request.setCpu(computer.getCpu());
        request.setGpu(computer.getGpu());
        request.setMotherboard(computer.getMotherboard());
        request.setRam(computer.getRam());
        request.setStorage(computer.getStorage());
        request.setPowerSupply(computer.getPowerSupply());
        request.setCoolingSystem(computer.getCoolingSystem());
        request.setHasRGB(computer.isHasRGB());
        return request;
    }
}
