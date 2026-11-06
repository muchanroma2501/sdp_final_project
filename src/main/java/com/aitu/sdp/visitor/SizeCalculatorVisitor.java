package com.aitu.sdp.visitor;

import java.util.Objects;

/**
 * Calculates the total size of a filesystem tree in bytes.
 *
 * <p>The visitor traverses every file and directory and sums the file sizes recursively.
 * It does not depend on traversal order, and it never uses runtime type checks.
 */
public final class SizeCalculatorVisitor implements Visitor {
    private long totalSizeBytes;

    @Override
    public void visitFile(FileElement file) {
        Objects.requireNonNull(file, "file cannot be null");
        totalSizeBytes += file.getSizeBytes();
    }

    @Override
    public void visitDirectory(DirectoryElement directory) {
        Objects.requireNonNull(directory, "directory cannot be null");
        for (Element child : directory.getChildren()) {
            child.accept(this);
        }
    }

    public long getTotalSizeBytes() {
        return totalSizeBytes;
    }
}
