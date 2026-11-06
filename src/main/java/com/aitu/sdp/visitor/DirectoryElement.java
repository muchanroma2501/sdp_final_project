package com.aitu.sdp.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DirectoryElement implements Element {
    private final String name;
    private final List<Element> children;

    public DirectoryElement(String name, List<Element> children) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Directory name cannot be blank.");
        }
        if (children == null) {
            throw new IllegalArgumentException("Directory children cannot be null.");
        }
        List<Element> normalized = new ArrayList<>();
        for (Element child : children) {
            if (child == null) {
                throw new IllegalArgumentException("Directory children cannot contain null values.");
            }
            normalized.add(child);
        }
        this.name = name.trim();
        this.children = Collections.unmodifiableList(normalized);
    }

    public String getName() {
        return name;
    }

    public List<Element> getChildren() {
        return children;
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");
        visitor.visitDirectory(this);
    }
}

