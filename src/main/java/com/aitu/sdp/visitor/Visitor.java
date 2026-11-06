package com.aitu.sdp.visitor;

public interface Visitor {
    void visitFile(FileElement file);
    void visitDirectory(DirectoryElement directory);
}

