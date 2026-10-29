# Assignment #6 — Visitor Pattern Report

## 1. Introduction and Pattern Justification

The Visitor pattern is useful when one object structure needs to support multiple independent operations without mixing those operations into the element classes themselves. In this project, the existing design already contains a natural object structure: a root directory with nested directories and files. The pattern is especially appropriate here because the element types are stable, but the required operations are different: size calculation and search.

The project uses the File System Visitor variant instead of forcing the pattern onto the existing PC configuration model. That keeps the implementation simple, realistic, and aligned with the assignment requirements. The object structure is formed by `Element`, `FileElement`, and `DirectoryElement`, while the operations are encapsulated in `SizeCalculatorVisitor` and `SearchVisitor`.

The key idea is that a client does not call `if/else` or `instanceof` to decide what to do. Instead, it calls `element.accept(visitor)`, and the runtime type of the element chooses the proper overloaded visitor method.

## 2. UML Class Diagram

The UML source is available in [assignment6-uml.puml](assignment6-uml.puml).

The diagram includes:

- `Element` and `Visitor` interfaces
- `FileElement` and `DirectoryElement` concrete elements
- `SizeCalculatorVisitor` and `SearchVisitor` concrete visitors
- a client service and controller that demonstrate the pattern
- the `DirectoryElement -> Element` relationship and the traversal flow

## 3. Visitor Implementation and Double Dispatch

### Core API

```java
public interface Element {
    void accept(Visitor visitor);
}

public interface Visitor {
    void visitFile(FileElement file);
    void visitDirectory(DirectoryElement directory);
}
```

### Concrete elements

```java
public final class FileElement implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitFile(this);
    }
}

public final class DirectoryElement implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitDirectory(this);
    }
}
```

### How double dispatch works

The double dispatch happens through two layers of dynamic dispatch:

1. Client code calls `element.accept(visitor)`.
2. The actual runtime type of the object determines which `accept()` implementation executes.
3. `FileElement.accept()` calls `visitor.visitFile(this)`, and `DirectoryElement.accept()` calls `visitor.visitDirectory(this)`.
4. Java chooses the most specific overloaded `visitFile` or `visitDirectory` method based on the runtime type of the element and the static type of the visitor.

This is not just naming magic. The actual reason is the combination of dynamic dispatch on `accept()` and method overloading on `visitFile()` and `visitDirectory()`. The element tells the visitor, “I am a FileElement”, and the visitor performs the matching action.

Because the element classes do not do `instanceof` checks, the code remains open for extension and avoids brittle type tests.

## 4. Clean Code Principles

### 1. Single Responsibility Principle (SRP)

Each concrete visitor performs only one operation:

- `SizeCalculatorVisitor` calculates total file size.
- `SearchVisitor` finds matching files by name.

This separation keeps each class focused and easy to test. The data model (`FileElement`, `DirectoryElement`) only describes the file system, while operations stay in dedicated visitors.

### 2. Meaningful Names

The names express intent clearly:

- `SizeCalculatorVisitor` clearly states the purpose.
- `SearchVisitor` states the operation.
- `accept()`, `visitFile()`, and `visitDirectory()` are action names that describe the dispatch flow directly.

This improves readability and supports oral defense without additional explanation.

### 3. Double Dispatch Instead of Type Checks

The project avoids `instanceof` and `switch`-style type processing. Instead:

```java
child.accept(this);
```

This is the Visitor mechanism. It lets every concrete element call the correct visitor method without hardcoded type discrimination in the traversal logic.

### 4. Open/Closed Principle (OCP)

The system is open for extension because a new visitor can be added without changing the existing `Element` implementations.

Example:

```java
public final class FileReportVisitor implements Visitor {
    @Override
    public void visitFile(FileElement file) {
        // report file details
    }

    @Override
    public void visitDirectory(DirectoryElement directory) {
        // report directory summary
    }
}
```

This new visitor can be used over the same `DirectoryElement` tree without modifying `FileElement`, `DirectoryElement`, or the existing visitors. That is exactly the OCP benefit: add behavior, not change stable structure.

### 5. Encapsulation

The directory protects its internal child list by storing it as an unmodifiable list. The object does not expose a mutable collection to outside code.

```java
this.children = Collections.unmodifiableList(normalized);
```

This means a `DirectoryElement` can be traversed safely, while external callers cannot break internal structure by inserting or removing children directly. The visitor obtains the data it needs through the public API and controlled traversal, not by mutating the tree from the outside.

### 6. Fail Fast / Input Validation

The constructors reject invalid inputs immediately:

- blank file or directory names are rejected
- negative file size is rejected
- null child elements are rejected
- null query is rejected

This prevents invalid object states from entering the structure and failing later in more confusing ways.

### 7. Immutability and Separation of Concerns

The directory tree is assembled once and then treated as read-only. Visitor logic is separate from the data model, so each class keeps a clear responsibility. This reduces risk of unexpected side effects and makes the structure easier to reason about.

## 5. Sample Output

The sample file system used in the demo includes:

- root directory
- nested directory
- empty directory
- several files with different sizes
- deep nested files

Example tree:

```text
root/
  readme.md (64 bytes)
  archive.zip (512 bytes)
  nested/
    notes.txt (128 bytes)
    budget.xlsx (256 bytes)
    empty-folder/
  logs/
    app.log (96 bytes)
    system.log (104 bytes)
```

### Total size result

The expected total size is:

- readme.md: 64
- archive.zip: 512
- notes.txt: 128
- budget.xlsx: 256
- app.log: 96
- system.log: 104

Total = 1160 bytes.

The `SizeCalculatorVisitor` returns a total of 1160 bytes for the same structure.

### Search result

Search query: `log` (case-insensitive)

Matches:

- `app.log`
- `system.log`

No-results example:

- query: `missing` -> empty match list

## 6. Trade-Offs and Encapsulation

The Visitor pattern is excellent for adding operations to a stable object model, but it also has trade-offs. It adds a new abstraction (`Visitor`) and slightly increases the number of classes. If a new element type is added, all visitors must be updated to handle it. That is the classic cost of a closed element hierarchy.

In this implementation, the trade-off is acceptable because the file system structure is small and the operations are intentionally separated. The element classes remain simple and safe, while operations remain independent and extensible.

## 7. Testing and Live Demonstration

The project includes JUnit tests for the visitor module. They cover:

- valid and invalid file creation
- empty directory handling
- protection of the child list internal state
- recursive size calculation
- recursive search and case-sensitivity behavior
- OCP-style extension via a new visitor

The project test command used in this environment is:

```powershell
.\mvnw.cmd test
```

The build command used here is:

```powershell
.\mvnw.cmd clean package
```

The application also exposes a small demonstration endpoint:

```http
GET /api/visitor/demo
```

This returns a JSON summary with the root name, total size, and matched search results.

## 8. Conclusion

The Visitor pattern is a good fit here because the file system structure is stable and there are multiple independent actions over the same tree. The implementation satisfies the assignment requirements: `Element` declares `accept()`, each concrete element dispatches to the right `visitX()` method, and the count/search operations are implemented as separate visitors.

The design is clean, extensible, and easy to explain in defense: a tree of elements accepts a visitor, and the visitor decides what to do without type checks or mixing responsibilities into the domain classes.

## 9. GitHub Repository

Repository link:

<https://github.com/muchanroma2501/SDPasik5>

