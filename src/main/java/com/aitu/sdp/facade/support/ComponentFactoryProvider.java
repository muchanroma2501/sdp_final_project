package com.aitu.sdp.facade.support;

import com.aitu.sdp.creational.AMDComponentFactory;
import com.aitu.sdp.creational.ComponentFactory;
import com.aitu.sdp.creational.ComponentFamily;
import com.aitu.sdp.creational.IntelNvidiaComponentFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class ComponentFactoryProvider {
    private final Map<String, ComponentFactory> factories = Map.of(
            "intel_nvidia", new IntelNvidiaComponentFactory(),
            "amd", new AMDComponentFactory());

    public ComponentFactory factoryFor(String ecosystem) {
        ComponentFactory factory = factories.get(normalize(ecosystem));
        if (factory == null) {
            throw new IllegalArgumentException("ecosystem must be 'intel_nvidia' or 'amd'");
        }
        return factory;
    }

    public ComponentFamily familyFor(String ecosystem) {
        return factoryFor(ecosystem).family();
    }

    private String normalize(String ecosystem) {
        return ecosystem == null ? "" : ecosystem.trim().toLowerCase();
    }
}
