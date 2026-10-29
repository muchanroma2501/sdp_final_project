# Assignment #6 — Defense Notes

## 1. 3–5 minute demo plan

1. Open `src/main/java/com/aitu/sdp/assignment6/visitor` in IntelliJ.
2. Show `Element` and `Visitor` interfaces.
3. Show `FileElement.accept()` and `DirectoryElement.accept()`.
4. Show `SizeCalculatorVisitor` and `SearchVisitor` implementations.
5. Show the sample tree built in `FileSystemDemoService`.
6. Call `/api/visitor/demo` or run the unit tests.
7. Explain the result: total size = 1160 bytes, matches for `log` = `app.log` and `system.log`.

## 2. Files to open in IntelliJ

- `src/main/java/com/aitu/sdp/assignment6/visitor/Element.java`
- `src/main/java/com/aitu/sdp/assignment6/visitor/Visitor.java`
- `src/main/java/com/aitu/sdp/assignment6/visitor/FileElement.java`
- `src/main/java/com/aitu/sdp/assignment6/visitor/DirectoryElement.java`
- `src/main/java/com/aitu/sdp/assignment6/visitor/SizeCalculatorVisitor.java`
- `src/main/java/com/aitu/sdp/assignment6/visitor/SearchVisitor.java`
- `src/test/java/com/aitu/sdp/assignment6/visitor/FileSystemVisitorTest.java`

## 3. Methods to explain

- `accept(Visitor visitor)`
- `visitFile(FileElement file)`
- `visitDirectory(DirectoryElement directory)`
- `getTotalSizeBytes()`
- `getMatches()`

## 4. Commands to run

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Optional runtime demo:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open:

- http://localhost:8080/api/visitor/demo

## 5. What to demonstrate

Show this idea:

```java
file.accept(visitor); // calls visitor.visitFile(this)
directory.accept(visitor); // calls visitor.visitDirectory(this)
```

Then explain that each visitor has a different responsibility, but the traversal is the same.

## 6. Short explanation of double dispatch

The design uses two runtime dispatch steps:

1. `accept()` is called on the actual object instance.
2. The overloaded visitor method (`visitFile` or `visitDirectory`) is chosen by the Java compiler and runtime type.

This separates the element choice from the operation choice and avoids `instanceof` checks.

## 7. Likely questions and short answers

### 1. What is the Visitor Pattern?
It is a way to add new operations to a fixed object structure without changing the element classes.

### 2. What problem does Visitor solve?
It avoids mixing many unrelated algorithms into files and directories, and it keeps behavior separate from data.

### 3. What are `Element` and `Visitor`?
`Element` declares `accept()`. `Visitor` declares an operation for each concrete element type.

### 4. What is double dispatch?
It is a dispatch mechanism in which the element decides which `accept()` method runs, and then that method calls the correct overloaded `visitX()` method on the visitor.

### 5. Why does `accept()` call `visitor.visitFile(this)`?
Because the exact element instance must be passed to the matching operation. The `this` object is the concrete file or directory being traversed.

### 6. How does adding a new Visitor work?
Create a new class implementing `Visitor` and define `visitFile` and `visitDirectory`. Existing elements remain unchanged.

### 7. Why do we not use `instanceof`?
Because it violates the Visitor design and makes the code more fragile, harder to extend, and less type-safe.

### 8. What is the Single Responsibility Principle in this implementation?
`SizeCalculatorVisitor` handles total size only, and `SearchVisitor` handles search only.

### 9. How does this implementation demonstrate OCP?
A third visitor, for example `FileReportVisitor`, can be added without changing `FileElement` or `DirectoryElement`.

### 10. What are the disadvantages of Visitor?
If a new element type is added, all visitor implementations require updates.

### 11. What happens if a new element type is introduced?
All `Visitor` implementations must add a new `visitX()` method; the design is intentionally more rigid for the element hierarchy.

### 12. How does the directory traversal work?
Each directory loops through its children and calls `child.accept(this)`. This recursively visits the whole tree.

### 13. How did you test the implementation?
By JUnit tests covering valid/invalid input, total size, recursive search, case sensitivity, immutability, and extension through a new visitor.

### 14. What is the difference between Visitor and Strategy?
Strategy changes an algorithm at runtime, while Visitor adds operations over a fixed object structure. Here the object structure is the file system.

### 15. What is the difference between Visitor and Facade?
Facade simplifies a subsystem interface, while Visitor separates operations from the element structure itself.

### 16. What are the encapsulation trade-offs?
The child list is protected by unmodifiable storage, which prevents uncontrolled mutation. The trade-off is that the visitor receives data only through the element interface, not by directly editing internal state.

## 8. Short defense script (English + Russian notes)

### English

"This project uses the Visitor pattern for a virtual file system. `Element` is the abstraction with `accept()`, while `Visitor` declares `visitFile()` and `visitDirectory()`. The `FileElement` calls `visitor.visitFile(this)` and the `DirectoryElement` calls `visitor.visitDirectory(this)`. This is double dispatch: the object selects the `accept()` implementation, and then the visitor overload is chosen for the actual element type. `SizeCalculatorVisitor` computes the total size in bytes, and `SearchVisitor` finds matching file names recursively. I also validate input and protect the child collection to preserve encapsulation. The implementation is covered by JUnit tests and accessible through `/api/visitor/demo`."

### Russian notes

"В этом проекте используется паттерн Visitor для виртуальной файловой системы. `Element` — это абстракция с `accept()`, а `Visitor` объявляет `visitFile()` и `visitDirectory()`. `FileElement` вызывает `visitor.visitFile(this)`, а `DirectoryElement` вызывает `visitor.visitDirectory(this)`. Это double dispatch: объект выбирает реализацию `accept()`, а затем вызывается подходящий перегруженный метод `visit*()`. `SizeCalculatorVisitor` считает общий размер в байтах, а `SearchVisitor` рекурсивно ищет файлы по ��мени. Я также валидирую входные данные и защищаю внутренний список детей, чтобы сохранить инкапсуляцию. Реализация покрыта JUnit-тестами и доступна через `/api/visitor/demo`."

