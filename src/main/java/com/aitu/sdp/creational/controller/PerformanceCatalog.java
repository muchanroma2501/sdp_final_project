package com.aitu.sdp.creational.controller;

import com.aitu.sdp.creational.ComponentCatalog;
import com.aitu.sdp.creational.ComponentFamily;

import java.util.LinkedHashMap;
import java.util.Map;

/** Catalog projection created by the selected Abstract Factory family. */
final class PerformanceCatalog {
    private final ComponentFamily family;

    PerformanceCatalog(ComponentFamily family) {
        this.family = family;
    }

    Map<String, Object> values() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cpus", ComponentCatalog.cpus(family).stream()
                .map(spec -> Map.of("model", spec.model(),
                        "tdpWatts", spec.tdpWatts(), "socket", spec.socket())).toList());
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
}
