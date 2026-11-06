package com.aitu.sdp.visitor;

import java.util.Objects;

public final class FileElement implements Element {
    private final String name;
    private final long sizeBytes;

    public FileElement(String name, long sizeBytes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name cannot be blank.");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("File size cannot be negative.");
        }
        this.name = name.trim();
        this.sizeBytes = sizeBytes;
    }

    public String getName() {
        return name;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");
        visitor.visitFile(this);
    }
}

