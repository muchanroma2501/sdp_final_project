package com.aitu.sdp.visitor;

import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.controller.BuildPCRequest;
import com.aitu.sdp.facade.PCConfiguratorFacade;
import com.aitu.sdp.facade.SubsystemOverheatException;
import com.aitu.sdp.facade.SystemStressTestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/visitor")
public class FileSystemDemoController {
    private final FileSystemDemoService demoService;
    private final PCConfiguratorFacade configuratorFacade;
    private final SystemStressTestFacade stressTestFacade;

    public FileSystemDemoController(FileSystemDemoService demoService) {
        this(demoService, null, null);
    }

    @Autowired
    public FileSystemDemoController(FileSystemDemoService demoService,
                                    PCConfiguratorFacade configuratorFacade,
                                    SystemStressTestFacade stressTestFacade) {
        this.demoService = demoService;
        this.configuratorFacade = configuratorFacade;
        this.stressTestFacade = stressTestFacade;
    }

    @GetMapping("/demo")
    public Map<String, Object> demo() {
        return demoService.demoResults();
    }

    @GetMapping("/pc-bundle")
    public Map<String, Object> inspectPcBundle(
            @RequestParam(name = "ecosystem", defaultValue = "intel_nvidia") String ecosystem,
            @RequestParam(name = "buildType", defaultValue = "gaming") String buildType,
            @RequestParam(name = "query", defaultValue = "log") String query) {
        if (configuratorFacade == null || stressTestFacade == null) {
            throw new IllegalStateException("PC bundle inspection dependencies are not configured");
        }
        BuildPCRequest request = new BuildPCRequest();
        request.setEcosystem(ecosystem);
        request.setBuildType(buildType);
        request.setRam("32GB DDR5-6000");
        request.setStorage("2TB NVMe PCIe 4.0 SSD");

        Computer computer = configuratorFacade.buildComputerForInspection(request);
        String htmlReport;
        try {
            htmlReport = stressTestFacade.executeSystemDiagnostics(computer);
        } catch (SubsystemOverheatException exception) {
            htmlReport = "Thermal diagnostics stopped safely: " + exception.getMessage()
                    + ". Build summary: " + computer;
        }
        return demoService.inspectComputerBundle(computer, htmlReport, query);
    }
}
