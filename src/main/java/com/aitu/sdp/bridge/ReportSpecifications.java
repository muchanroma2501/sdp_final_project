package com.aitu.sdp.bridge;

import com.aitu.sdp.creational.Computer;

import java.util.LinkedHashMap;
import java.util.Map;

final class ReportSpecifications {
    private ReportSpecifications() {
    }

    static Map<String, String> forComputer(Computer computer, ReportDetail detail) {
        Map<String, String> specifications = new LinkedHashMap<>();
        specifications.put("CPU", computer.getCpu());
        specifications.put("GPU", computer.getGpu());
        specifications.put("RAM", computer.getRam());
        specifications.put("Storage", computer.getStorage());
        if (detail == ReportDetail.FULL_DIAGNOSTIC) {
            specifications.put("Power Supply", computer.getPowerSupply());
            specifications.put("Motherboard", computer.getMotherboard());
            specifications.put("Cooling System", computer.getCoolingSystem());
            specifications.put("RGB Lighting", computer.isHasRGB() ? "Enabled" : "Disabled");
        }
        return specifications;
    }
}
