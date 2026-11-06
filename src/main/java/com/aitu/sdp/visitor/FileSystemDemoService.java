package com.aitu.sdp.visitor;

import com.aitu.sdp.creational.Computer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public DirectoryElement buildComputerArtifactTree(Computer computer, String htmlReport) {
        Objects.requireNonNull(computer, "computer cannot be null");
        String specs = computer.toString();
        long manifestSize = specs.isBlank()
                ? 256L : specs.getBytes(StandardCharsets.UTF_8).length;
        long reportSize = htmlReport == null || htmlReport.isBlank()
                ? 512L : htmlReport.getBytes(StandardCharsets.UTF_8).length;

        DirectoryElement drivers = new DirectoryElement("drivers", List.of(
                new FileElement("cpu-microcode-" + sanitizeFileName(computer.getCpu()) + ".bin", 384L),
                new FileElement("gpu-shader-cache-" + sanitizeFileName(computer.getGpu()) + ".pkg", 640L)
        ));
        DirectoryElement logs = new DirectoryElement("logs", List.of(
                new FileElement("thermal-stress.log", 192L),
                new FileElement("power-rail-diagnostics.log", 160L),
                new FileElement("bios-post-check.txt", 96L)
        ));

        return new DirectoryElement("pc-build-bundle", List.of(
                new FileElement("build-manifest.json", manifestSize),
                new FileElement("diagnostic-report.html", reportSize),
                drivers,
                logs
        ));
    }

    public Map<String, Object> inspectComputerBundle(Computer computer, String htmlReport,
                                                      String searchKeyword) {
        String effectiveKeyword = searchKeyword == null || searchKeyword.isBlank()
                ? "log" : searchKeyword.trim();
        DirectoryElement bundleRoot = buildComputerArtifactTree(computer, htmlReport);

        SizeCalculatorVisitor sizeVisitor = new SizeCalculatorVisitor();
        bundleRoot.accept(sizeVisitor);

        SearchVisitor searchVisitor = new SearchVisitor(effectiveKeyword, false);
        bundleRoot.accept(searchVisitor);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("rootName", bundleRoot.getName());
        response.put("cpu", computer.getCpu());
        response.put("gpu", computer.getGpu());
        response.put("totalSizeBytes", sizeVisitor.getTotalSizeBytes());
        response.put("searchQuery", effectiveKeyword);
        response.put("matches", searchVisitor.getMatches());
        response.put("structure", describeTree(bundleRoot));
        return response;
    }

    private static String sanitizeFileName(String value) {
        String sanitized = value == null ? "" : value.trim().replaceAll("[^A-Za-z0-9._-]+", "-");
        sanitized = sanitized.replaceAll("^[.-]+|[.-]+$", "");
        return sanitized.isBlank() ? "component" : sanitized;
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
