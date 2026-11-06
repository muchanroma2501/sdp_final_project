package com.aitu.sdp.creational;

public record Storage(String model) {
    public Storage {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Storage model cannot be null or blank");
        }
        model = model.trim();
    }
}
