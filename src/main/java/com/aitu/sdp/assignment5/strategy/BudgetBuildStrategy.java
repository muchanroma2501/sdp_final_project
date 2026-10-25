package com.aitu.sdp.assignment5.strategy;

import com.aitu.sdp.assignment2.BudgetPCOrderFactory;
import com.aitu.sdp.assignment2.ComponentFactory;
import com.aitu.sdp.assignment2.Computer;
import com.aitu.sdp.assignment2.ComputerOrderFactory;
import com.aitu.sdp.assignment2.controller.BuildPCRequest;
import org.springframework.stereotype.Component;

@Component
public class BudgetBuildStrategy implements ComputerBuildStrategy {
    @Override
    public String getBuildType() {
        return "budget";
    }

    @Override
    public Computer build(ComponentFactory factory, BuildPCRequest request) {
        ComputerOrderFactory orderFactory = new BudgetPCOrderFactory(factory);
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

