package com.aitu.sdp.strategy;

import com.aitu.sdp.creational.ComponentFactory;
import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.ComputerOrderFactory;
import com.aitu.sdp.creational.GamingPCOrderFactory;
import com.aitu.sdp.creational.controller.BuildPCRequest;
import org.springframework.stereotype.Component;

/**
 * Selects the gaming build pipeline and normalizes optional request inputs.
 *
 * <p>Spring registers this strategy for runtime selection by build type, allowing
 * the facade to depend on the strategy abstraction rather than concrete order
 * factories and to add pipelines without changing its selection logic (OCP).</p>
 */
@Component
public class GamingBuildStrategy implements ComputerBuildStrategy {
    @Override
    public String getBuildType() {
        return "gaming";
    }

    @Override
    public Computer build(ComponentFactory factory, BuildPCRequest request) {
        ComputerOrderFactory orderFactory = new GamingPCOrderFactory(factory);
        return orderFactory.orderComputer(
                normalize(request.getRam()),
                normalize(request.getStorage()),
                normalize(request.getCoolingSystem()),
                request.isHasRGB());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
