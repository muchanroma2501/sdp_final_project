package com.aitu.sdp.creational;

public record CoolingSystem(String model) {
    public CoolingSystem {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Cooling system model cannot be null or blank");
        }
        model = model.trim();
    }
}
