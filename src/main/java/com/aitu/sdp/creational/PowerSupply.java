package com.aitu.sdp.creational;

/** Validated power supply value object. */
public record PowerSupply(String model, int wattage) {
    public PowerSupply {
        if (model == null || model.isBlank() || wattage <= 0) {
            throw new IllegalArgumentException("Power supply model and positive wattage are required");
        }
    }

    public int getWattage() {
        return wattage;
    }
}
