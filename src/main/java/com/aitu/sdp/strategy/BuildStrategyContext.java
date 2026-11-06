package com.aitu.sdp.strategy;

import com.aitu.sdp.creational.ComponentFactory;
import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.controller.BuildPCRequest;

import java.util.Objects;

/**
 * Executes the build pipeline selected at runtime through {@link ComputerBuildStrategy}.
 *
 * <p>The facade resolves Spring-registered strategies by build type and configures
 * this context, keeping concrete order factories out of the facade and supporting
 * extension through new strategies in accordance with the Open/Closed Principle.</p>
 */
public class BuildStrategyContext {
    private ComputerBuildStrategy strategy;

    public void setStrategy(ComputerBuildStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
    }

    public Computer build(ComponentFactory factory, BuildPCRequest request) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy must be configured before execution");
        }
        return strategy.build(factory, request);
    }
}
