package com.aitu.sdp.assignment6.visitor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/visitor")
public class FileSystemDemoController {
    private final FileSystemDemoService demoService;

    public FileSystemDemoController(FileSystemDemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/demo")
    public Map<String, Object> demo() {
        return demoService.demoResults();
    }
}

