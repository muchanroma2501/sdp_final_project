package com.aitu.sdp.strategy;

import com.aitu.sdp.creational.ComponentFactory;
import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.controller.BuildPCRequest;

/**
 * Encapsulates runtime build-pipeline selection and normalization of optional inputs.
 *
 * <p>Spring-managed implementations are indexed by build type in the facade's
 * {@code Map<String, ComputerBuildStrategy>}, decoupling it from concrete order
 * factories and allowing new pipelines without modifying existing selection logic (OCP).</p>
 */
public interface ComputerBuildStrategy {
    String getBuildType();

    Computer build(ComponentFactory factory, BuildPCRequest request);
}
