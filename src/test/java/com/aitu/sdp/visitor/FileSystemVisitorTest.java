package com.aitu.sdp.visitor;

import com.aitu.sdp.creational.Computer;
import com.aitu.sdp.creational.GamingPCOrderFactory;
import com.aitu.sdp.creational.IntelNvidiaComponentFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSystemVisitorTest {
    @Test
    void createsValidFile() {
        FileElement file = new FileElement("notes.txt", 128L);
        assertEquals("notes.txt", file.getName());
        assertEquals(128L, file.getSizeBytes());
    }

    @Test
    void rejectsInvalidFileInput() {
        assertThrows(IllegalArgumentException.class, () -> new FileElement("", 10L));
        assertThrows(IllegalArgumentException.class, () -> new FileElement("notes.txt", -1L));
    }

    @Test
    void createsEmptyDirectoryAndProtectsInternalState() {
        DirectoryElement empty = new DirectoryElement("empty", List.of());
        assertTrue(empty.getChildren().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> empty.getChildren().add(new FileElement("bad.txt", 1L)));
    }

    @Test
    void rejectsInvalidDirectoryInput() {
        assertThrows(IllegalArgumentException.class, () -> new DirectoryElement("", new ArrayList<>()));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryElement("root", null));
        List<Element> invalidChildren = new ArrayList<>();
        invalidChildren.add(null);
        assertThrows(IllegalArgumentException.class, () -> new DirectoryElement("root", invalidChildren));
    }

    @Test
    void sizeCalculatorCountsNestedFiles() {
        DirectoryElement root = sampleTree();
        SizeCalculatorVisitor visitor = new SizeCalculatorVisitor();
        root.accept(visitor);
        assertEquals(1160L, visitor.getTotalSizeBytes());
    }

    @Test
    void searchVisitorFindsMatchesRecursivelyAndHonorsCaseRule() {
        SearchVisitor caseInsensitive = new SearchVisitor("log", false);
        sampleTree().accept(caseInsensitive);
        assertEquals(List.of("app.log", "system.log"), caseInsensitive.getMatches());

        SearchVisitor caseSensitive = new SearchVisitor("LOG", true);
        sampleTree().accept(caseSensitive);
        assertTrue(caseSensitive.getMatches().isEmpty());

        SearchVisitor missing = new SearchVisitor("missing", false);
        sampleTree().accept(missing);
        assertTrue(missing.getMatches().isEmpty());
    }

    @Test
    void canAddNewVisitorWithoutChangingElements() {
        DirectoryElement root = sampleTree();
        NameCollectorVisitor visitor = new NameCollectorVisitor();
        root.accept(visitor);
        assertEquals(List.of("readme.md", "archive.zip", "notes.txt", "budget.xlsx", "app.log", "system.log"), visitor.names());
    }

    @Test
    void buildsComputerArtifactTreeWithSizeMatchingGeneratedFiles() {
        Computer computer = new GamingPCOrderFactory(new IntelNvidiaComponentFactory()).orderComputer();
        FileSystemDemoService service = new FileSystemDemoService();
        DirectoryElement root = service.buildComputerArtifactTree(computer, "<html>diagnostics</html>");

        SizeCalculatorVisitor sizeVisitor = new SizeCalculatorVisitor();
        root.accept(sizeVisitor);
        assertTrue(sizeVisitor.getTotalSizeBytes() > 0);
        assertEquals(sumFileSizes(root), sizeVisitor.getTotalSizeBytes());
        assertEquals("pc-build-bundle", root.getName());
        assertEquals(List.of(
                "build-manifest.json",
                "diagnostic-report.html",
                "drivers",
                "logs"
        ), root.getChildren().stream().map(child -> child instanceof DirectoryElement directory
                ? directory.getName() : ((FileElement) child).getName()).toList());
    }

    @Test
    void inspectsComputerBundleWithSizeAndLogSearchVisitors() {
        Computer computer = new GamingPCOrderFactory(new IntelNvidiaComponentFactory()).orderComputer();
        FileSystemDemoService service = new FileSystemDemoService();

        Map<String, Object> result = service.inspectComputerBundle(computer, "<html>diagnostics</html>", "log");

        assertEquals("pc-build-bundle", result.get("rootName"));
        assertEquals(computer.getCpu(), result.get("cpu"));
        assertEquals(computer.getGpu(), result.get("gpu"));
        assertTrue((Long) result.get("totalSizeBytes") > 0);
        assertEquals(List.of("thermal-stress.log", "power-rail-diagnostics.log"), result.get("matches"));
        assertEquals("log", result.get("searchQuery"));
    }

    private static long sumFileSizes(DirectoryElement directory) {
        long total = 0;
        for (Element child : directory.getChildren()) {
            total += child instanceof FileElement file
                    ? file.getSizeBytes() : sumFileSizes((DirectoryElement) child);
        }
        return total;
    }

    private static DirectoryElement sampleTree() {
        DirectoryElement empty = new DirectoryElement("empty-folder", List.of());
        DirectoryElement nested = new DirectoryElement("nested", List.of(
                new FileElement("notes.txt", 128L),
                new FileElement("budget.xlsx", 256L),
                empty
        ));
        DirectoryElement logs = new DirectoryElement("logs", List.of(
                new FileElement("app.log", 96L),
                new FileElement("system.log", 104L)
        ));
        return new DirectoryElement("root", List.of(
                new FileElement("readme.md", 64L),
                new FileElement("archive.zip", 512L),
                nested,
                logs
        ));
    }

    private static final class NameCollectorVisitor implements Visitor {
        private final List<String> names = new ArrayList<>();

        @Override
        public void visitFile(FileElement file) {
            names.add(file.getName());
        }

        @Override
        public void visitDirectory(DirectoryElement directory) {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        }

        private List<String> names() {
            return List.copyOf(names);
        }
    }
}
