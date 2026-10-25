package com.aitu.sdp.assignment5.strategy;

import com.aitu.sdp.assignment2.ComponentFactory;
import com.aitu.sdp.assignment2.Computer;
import com.aitu.sdp.assignment2.controller.BuildPCRequest;

public interface ComputerBuildStrategy {
    String getBuildType();

    Computer build(ComponentFactory factory, BuildPCRequest request);
}

