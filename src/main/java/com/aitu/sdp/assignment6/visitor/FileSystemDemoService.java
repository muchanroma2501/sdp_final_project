package com.aitu.sdp.assignment6.visitor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileSystemDemoService {
    public DirectoryElement buildSampleTree() {
        DirectoryElement emptyDirectory = new DirectoryElement("empty-folder", List.of());

        DirectoryElement nested = new DirectoryElement("nested", List.of(
                new FileElement("notes.txt", 128L),
                new FileElement("budget.xlsx", 256L),
                emptyDirectory
        ));

        DirectoryElement root = new DirectoryElement("root", List.of(
                new FileElement("readme.md", 64L),
                new FileElement("archive.zip", 512L),
                nested,
                new DirectoryElement("logs", List.of(
                        new FileElement("app.log", 96L),
                        new FileElement("system.log", 104L)
                ))
        ));

        return root;
    }

    public Map<String, Object> demoResults() {
        DirectoryElement root = buildSampleTree();

        SizeCalculatorVisitor sizeVisitor = new SizeCalculatorVisitor();
        root.accept(sizeVisitor);

        SearchVisitor searchVisitor = new SearchVisitor("log", false);
        root.accept(searchVisitor);

        SearchVisitor missVisitor = new SearchVisitor("missing", false);
        root.accept(missVisitor);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("rootName", root.getName());
        response.put("totalSizeBytes", sizeVisitor.getTotalSizeBytes());
        response.put("searchQuery", "log");
        response.put("matches", searchVisitor.getMatches());
        response.put("misses", missVisitor.getMatches());
        response.put("structure", describeTree(root));
        return response;
    }

    private List<String> describeTree(DirectoryElement directory) {
        TreeDescriptionVisitor treeVisitor = new TreeDescriptionVisitor();
        directory.accept(treeVisitor);
        return treeVisitor.getEntries();
    }

    private static final class TreeDescriptionVisitor implements Visitor {
        private final List<String> entries = new ArrayList<>();

        @Override
        public void visitFile(FileElement file) {
            entries.add("- " + file.getName() + " (" + file.getSizeBytes() + " bytes)");
        }

        @Override
        public void visitDirectory(DirectoryElement directory) {
            entries.add("- " + directory.getName() + "/");
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        }

        public List<String> getEntries() {
            return List.copyOf(entries);
        }
    }
}
