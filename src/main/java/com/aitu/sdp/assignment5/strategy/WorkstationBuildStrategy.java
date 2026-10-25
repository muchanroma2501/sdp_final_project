package com.aitu.sdp.assignment5.strategy;

import com.aitu.sdp.assignment2.ComponentFactory;
import com.aitu.sdp.assignment2.Computer;
import com.aitu.sdp.assignment2.ComputerOrderFactory;
import com.aitu.sdp.assignment2.WorkstationPCOrderFactory;
import com.aitu.sdp.assignment2.controller.BuildPCRequest;
import org.springframework.stereotype.Component;

@Component
public class WorkstationBuildStrategy implements ComputerBuildStrategy {
    @Override
    public String getBuildType() {
        return "workstation";
    }

    @Override
    public Computer build(ComponentFactory factory, BuildPCRequest request) {
        ComputerOrderFactory orderFactory = new WorkstationPCOrderFactory(factory);
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

