package com.aitu.sdp.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Searches for file names containing a query string.
 *
 * <p>The matching rule is case-insensitive by default; a stricter case-sensitive mode is available
 * for explicit tests and demonstration. It traverses all nested directories recursively and returns
 * the matching file names in encounter order.
 */
public final class SearchVisitor implements Visitor {
    private final String query;
    private final boolean caseSensitive;
    private final List<String> matches = new ArrayList<>();

    public SearchVisitor(String query, boolean caseSensitive) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be blank.");
        }
        this.query = caseSensitive ? query : query.toLowerCase();
        this.caseSensitive = caseSensitive;
    }

    @Override
    public void visitFile(FileElement file) {
        Objects.requireNonNull(file, "file cannot be null");
        String fileName = caseSensitive ? file.getName() : file.getName().toLowerCase();
        if (fileName.contains(query)) {
            matches.add(file.getName());
        }
    }

    @Override
    public void visitDirectory(DirectoryElement directory) {
        Objects.requireNonNull(directory, "directory cannot be null");
        for (Element child : directory.getChildren()) {
            child.accept(this);
        }
    }

    public List<String> getMatches() {
        return Collections.unmodifiableList(matches);
    }

    public String getQuery() {
        return query;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}
