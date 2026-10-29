package com.aitu.sdp.assignment6.visitor;

public interface Visitor {
    void visitFile(FileElement file);
    void visitDirectory(DirectoryElement directory);
}

