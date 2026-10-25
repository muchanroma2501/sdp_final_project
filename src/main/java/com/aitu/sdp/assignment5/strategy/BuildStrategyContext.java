package com.aitu.sdp.assignment5.strategy;

import com.aitu.sdp.assignment2.ComponentFactory;
import com.aitu.sdp.assignment2.Computer;
import com.aitu.sdp.assignment2.controller.BuildPCRequest;

import java.util.Objects;

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
