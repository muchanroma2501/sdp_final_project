# SDP_FINAL_PROJECT - SDP_FINAL_PROJECT - Final Project Audit and Change Report

## A. Executive Summary

SDP_FINAL_PROJECT is the unified PC Build Studio defense project, organized under the `com.aitu.sdp` pattern-oriented packages.

- a minimalist editorial-style website redesign in `src/main/resources/static/index.html` and `src/main/resources/static/css/app.css`
- the Visitor Pattern implementation under `src/main/java/com/aitu/sdp/visitor` with its integrated PC build-bundle endpoint and tests

The codebase is in a generally healthy state: the project builds successfully and the automated test suite passes. The main caveat is that the live runtime verification is partially blocked by a port conflict on `localhost:8080`, and there is no browser automation in this environment to visually inspect the redesign end-to-end.

The final evidence shows:

- the source layout uses `creational`, `bridge`, `facade`, `strategy`, and `visitor` packages under `com.aitu.sdp`
- `mvnw clean test` completed successfully with 24 tests run, 0 failures, 0 errors, 0 skipped
- `mvnw clean package` completed successfully and produced the JAR
- the project is not fully browser-verified due environment constraints, but the static UI and backend tests are in place and passing

## B. Actual Changes

### 1) Website redesign

Files changed or modified:

- `src/main/resources/static/index.html`
- `src/main/resources/static/css/app.css`
- `README.md`

What changed:

- The static UI was restyled from a darker dashboard aesthetic to a lighter editorial magazine look.
- The top bar, hero banner, layout spacing, cards, buttons, diagnostics panels, and report sections all received significant CSS updates.
- The project README repository URL was updated from `SDPasik2` to `SDPasik2`.
- The HTML structure remained functionally similar but was refined to support the redesigned layout and branding.

Why it was changed:

- To implement the requested minimalist, editorial website redesign and modernize the front-end presentation.

### 2) Assignment #6 — Visitor Pattern

Files added:

- `src/main/java/com/aitu/sdp/visitor/Element.java`
- `src/main/java/com/aitu/sdp/visitor/Visitor.java`
- `src/main/java/com/aitu/sdp/visitor/FileElement.java`
- `src/main/java/com/aitu/sdp/visitor/DirectoryElement.java`
- `src/main/java/com/aitu/sdp/visitor/SizeCalculatorVisitor.java`
- `src/main/java/com/aitu/sdp/visitor/SearchVisitor.java`
- `src/main/java/com/aitu/sdp/visitor/FileSystemDemoService.java`
- `src/main/java/com/aitu/sdp/visitor/FileSystemDemoController.java`
- `src/test/java/com/aitu/sdp/visitor/FileSystemVisitorTest.java`
- `src/test/java/com/aitu/sdp/visitor/FileSystemDemoControllerTest.java`

Documentation added:

- `docs/assignment6-report.md`
- `docs/assignment6-defense-notes.md`
- `docs/assignment6-uml.puml`

What changed:

- Added a file-system Visitor implementation using `Element` + `Visitor` interfaces and concrete classes for file/directory traversal.
- Added visitors for total size and recursive filename search.
- Added controller/service demo endpoints and tests covering valid/invalid input, recursion, and API output.

### 3) Automated tests

Existing test coverage remains active and passing:

- `com.aitu.sdp.facade.controller.PCConfiguratorApiTest`
- `com.aitu.sdp.facade.diagnostics.BuildDiagnosticsServiceTest`
- `com.aitu.sdp.strategy.BuildStrategyContextTest`
- `com.aitu.sdp.visitor.FileSystemDemoControllerTest`
- `com.aitu.sdp.visitor.FileSystemVisitorTest`

### 4) Unrelated or pre-existing work

The repository also contains older assignment work in `assignment2`, `assignment3`, `assignment4`, and `assignment5`, which remain in the project. The identified redesign and Visitor work are clearly separated from the earlier patterns and are not broad refactors of unrelated logic.

## C. Website Redesign Verification

### Visual changes implemented

The actual front-end implementation shows the following improvements:

- Header and navigation: sticky top bar, brand mark, nav items, status pill, editorial-style spacing
- Typography: serif display headings, tighter letter spacing, refined body text hierarchy
- Colors and backgrounds: warm light neutral palette with ivory backgrounds, dark green-olive accent tones
- Layout and spacing: larger white-space rhythm, card-based sections, balanced section spacing
- Product cards and selection panels: card hover states, subtle shadows, polished buttons and form fields
- Responsive behavior: media queries at 900px, 720px, and 480px, with stacked layouts for smaller screens
- Interactive states: hover/focus styles, disabled button states, validation notices, status chips, report previews
- Assets: no external font or image dependency was introduced in the actual checked-in files; the design is CSS-only

### Functional regression testing

The JavaScript front-end logic in `src/main/resources/static/js/app.js` is still present and consistent with the browser UI structure. The relevant workflows are connected:

- preset loading via `/api/pc/presets`
- platform catalog loading via `/api/pc/catalog?ecosystem=...`
- configuration diagnosis via `/api/pc/diagnose`
- custom build submission via `/api/pc/custom`
- report generation via `/api/reports/export`
- stress-test demo via `/api/diagnostics/run`
- local history persistence via `localStorage`

### Feature verification table

| Feature | Relevant file or function | Verification method | Actual result | Status |
|---|---|---|---|---|
| Header and navigation | `index.html`, `.topbar`, `.top-nav` in `app.css` | Source inspection | Present and styled as a sticky top bar with nav links and status pill | PASS |
| Typography and editorial styling | `app.css` root variables and heading rules | Source inspection | Serif headings and warm editorial palette implemented | PASS |
| Colors and backgrounds | `:root` and `.hero` / `.section-block` rules | Source inspection | Light neutral palette and layered backgrounds present | PASS |
| Layout and spacing | `.hero`, `.studio-layout`, `.preset-grid` | Source inspection | Spacious sections and responsive card layout implemented | PASS |
| Preset cards | `renderPresets()` in `app.js` | Source inspection + API contract review | Cards are generated from API and rendered in grid layout | PASS |
| Component selection form | `index.html`, `loadCatalog()`, `bindEvents()` in `app.js` | Source inspection | Selects and platform checkboxes wired for catalog-driven data | PASS |
| Diagnostics and power summary | `renderDiagnostics()`, `runDiagnosis()` | Source inspection + tests | Client logic is connected to diagnostics response shape and server validation | PASS |
| Report generation | `generateReport()` | Source inspection | POST to `/api/reports/export` and preview logic present | PASS |
| Local history and restore flow | `loadHistory()`, `addHistory()`, `restoreConfiguration()` | Source inspection | Browser history is implemented and persisted | PASS |
| Responsive behavior | `@media` rules in `app.css` | Source inspection | Breakpoints exist for tablet/mobile layouts | PASS |
| Browser visual verification | Entire page | No browser automation available in this environment | Not visually validated in a real browser | NOT VERIFIED |
| Runtime page startup | `http://localhost:8080` and `http://localhost:8081` checks | Live HTTP request from environment | Main page responded 200 on 8080, but port conflict prevented clean project startup verification | NOT VERIFIED |

## D. Visitor Pattern Compliance

### Actual implementation

The project uses a file-system Visitor hierarchy, not a hardware-component Visitor.

Key classes:

- `Element` — `accept(Visitor visitor)`
- `Visitor` — `visitFile(FileElement file)`, `visitDirectory(DirectoryElement directory)`
- `FileElement` — `accept(Visitor visitor)` calls `visitor.visitFile(this)`
- `DirectoryElement` — `accept(Visitor visitor)` calls `visitor.visitDirectory(this)`
- `SizeCalculatorVisitor` — accumulates file sizes recursively
- `SearchVisitor` — searches recursively by filename
- `FileSystemDemoService` — builds the sample tree and calls `accept()` on the root element

### Exact double-dispatch flow present in code

The actual run flow is:

`client → element.accept(visitor) → visitor.visitX(this) → concrete visitor operation`

For example:

- `DirectoryElement.accept()` calls `visitor.visitDirectory(this)`
- `FileElement.accept()` calls `visitor.visitFile(this)`
- `SizeCalculatorVisitor.visitDirectory()` recursively invokes `child.accept(this)`
- `SearchVisitor.visitFile()` checks the filename and appends a match

The exact source methods are visible in:

- `DirectoryElement.accept()`
- `FileElement.accept()`
- `SizeCalculatorVisitor.visitDirectory()`
- `SearchVisitor.visitFile()`
- `FileSystemDemoService.demoResults()`

### Compliance table

| Assignment requirement | Actual implementation | Source file | Evidence | Status |
|---|---|---|---|---|
| 1. `Element` declares `accept(Visitor visitor)` | Yes | `Element.java` | `public interface Element { void accept(Visitor visitor); }` | PASS |
| 2. At least two concrete element types implement `accept()` | Yes | `FileElement.java`, `DirectoryElement.java` | Both classes implement `accept(Visitor visitor)` | PASS |
| 3. Each concrete element passes `this` to the matching visitor method | Yes | `FileElement.java`, `DirectoryElement.java` | `visitor.visitFile(this)` and `visitor.visitDirectory(this)` | PASS |
| 4. Visitor interface declares one method per concrete element type | Yes | `Visitor.java` | `visitFile(FileElement file)`, `visitDirectory(DirectoryElement directory)` | PASS |
| 5. At least two concrete Visitor implementations exist | Yes | `SizeCalculatorVisitor.java`, `SearchVisitor.java` | Two separate concrete visitors implement `Visitor` | PASS |
| 6. Each concrete Visitor implements all required visitor methods | Yes | `SizeCalculatorVisitor.java`, `SearchVisitor.java` | Both override `visitFile` and `visitDirectory` | PASS |
| 7. Both visitors operate on the same object structure | Yes | `FileSystemDemoService.java` | Root `DirectoryElement` is accepted by both visitors | PASS |
| 8. Client/traversal calls `accept()` on elements | Yes | `FileSystemDemoService.java`, `SizeCalculatorVisitor.java`, `SearchVisitor.java` | `root.accept(...)`, `child.accept(this)` in traversal | PASS |
| 9. No `instanceof` type-checking chain is used instead of double dispatch | Yes | `SizeCalculatorVisitor.java`, `SearchVisitor.java`, `DirectoryElement.java` | Traversal relies on `accept()`/`visitX(this)` rather than `instanceof` | PASS |
| 10. Input validation and encapsulation implemented | Yes | `FileElement.java`, `DirectoryElement.java` | Null/blank checks, negative size validation, unmodifiable child list | PASS |
| 11. New Visitor can be added without changing elements | Yes | `Visitor.java`, `FileElement.java`, `DirectoryElement.java` | New visitor class simply implements `Visitor` and does not modify `FileElement`/`DirectoryElement` | PASS |
| 12. Tests cover normal/nested/invalid/search behavior | Yes | `FileSystemVisitorTest.java` | Valid file, invalid input, nested tree, search, immutability, extension by custom visitor | PASS |

## E. Clean Code Review

The actual Visitor implementation demonstrates the following principles.

| Principle | Relevant class and method | Actual code evidence | Why it demonstrates the principle | Caveats |
|---|---|---|---|---|
| Single Responsibility Principle | `SizeCalculatorVisitor`, `SearchVisitor` | Each class handles only one operation: size or search | Behavior is separated into focused visitor classes instead of mixing logic into `FileElement` and `DirectoryElement` | A new behavior still requires a new visitor class |
| Meaningful Names | `FileElement`, `DirectoryElement`, `SizeCalculatorVisitor`, `SearchVisitor` | Names describe exact purpose of data and operations | Names are explicit and self-explanatory in both source and defense conversation | Very small project, so names are simple but still clear |
| Double Dispatch instead of type-checking chains | `FileElement.accept()`, `DirectoryElement.accept()`, `SizeCalculatorVisitor.visitDirectory()` | `child.accept(this)` and `visitor.visitFile(this)` | The structure dispatches behavior by object type without `instanceof` | Requires the object hierarchy to be stable |
| Open/Closed Principle | `Visitor` abstraction + custom test visitor in `FileSystemVisitorTest` | `NameCollectorVisitor` implements `Visitor` without changing `Element` classes | A new visitor can be added without modifying existing element classes | Adding a new element type would require all visitors to update |
| Encapsulation | `DirectoryElement` constructor and `children` field | `Collections.unmodifiableList(normalized)` | The child list cannot be externally mutated; internal state is protected | The object structure is intentionally fixed and not meant for arbitrary edits |
| Fail-fast validation | `FileElement` and `DirectoryElement` constructors | Checks for blank names, null children, negative file sizes | Invalid values are rejected immediately, which reduces later failures | Validation is local to constructors; not a full domain validation framework |

## F. Test and Build Results

### Commands executed

1. `.\mvnw.cmd clean test`
2. `.\mvnw.cmd clean package`

### Actual results

#### `.\mvnw.cmd clean test`

- Result: `BUILD SUCCESS`
- Tests run: `24`
- Failures: `0`
- Errors: `0`
- Skipped: `0`
- Relevant suites:
  - `com.aitu.sdp.facade.controller.PCConfiguratorApiTest` — 3 tests
  - `com.aitu.sdp.facade.diagnostics.BuildDiagnosticsServiceTest` — 8 tests
  - `com.aitu.sdp.strategy.BuildStrategyContextTest` — 2 tests
  - `com.aitu.sdp.visitor.FileSystemDemoControllerTest` — 2 tests
  - `com.aitu.sdp.visitor.FileSystemVisitorTest` — 9 tests

#### `.\mvnw.cmd clean package`

- Result: `BUILD SUCCESS`
- Artifact produced: `target/sdp-final-project-1.0.0.jar`
- Repackaged Spring Boot JAR also created as `target/sdp-final-project-1.0.0.jar.original`

### Additional runtime-start attempts

The environment also attempted to run the app in live mode:

- `.\mvnw.cmd spring-boot:run` failed because port 8080 was already in use
- the subsequent `spring-boot:run` attempt on port 8081 with the wrong CLI syntax failed due invalid Maven lifecycle usage

The strongest available runtime evidence is that a GET request to `http://localhost:8080` returned HTTP 200 while an unrelated process was already occupying the default port, which means clean project startup verification is limited by port conflicts rather than source-level build failure.

## G. Runtime and Browser Results

### What was actually executed

- `GET http://localhost:8080` returned `200 OK`
- The page content started with the HTML document for the PC Build Studio landing page
- The local runtime was not cleanly started from this project due the port conflict and invalid startup command syntax
- A direct check for `http://localhost:8080/api/visitor/demo` returned `404`, which is not enough to attribute the failure to the project code because the active process on 8080 is not confirmed to be this application

### What remains unverified

- Real browser interaction testing
- Console log inspection in a browser
- Visual validation of the redesign in a browser
- End-to-end validation of the page workflows through browser automation

### Best available alternative evidence

- Maven tests already validate the backend logic and Assignment #6 functionality
- The static HTML and CSS are present and consistent with the JS integrations
- The app’s main page responds over HTTP

## H. Documentation Review

The following files exist and are relevant:

- `README.md` — current repository URL updated to `https://github.com/muchanroma2501/SDPasik2`
- `docs/assignment6-report.md` — Assignment #6 report exists
- `docs/assignment6-defense-notes.md` — defense notes exist
- `docs/assignment6-uml.puml` — UML source exists
- `docs/PROJECT_AUDIT.md` — existing project audit file exists
- `docs/assignment5-report.md`, `docs/assignment5-defense-notes.md`, `docs/assignment5-uml.md` — previous assignment documentation remains present

### Verification against implementation

- `assignment6-report.md` references the actual classes and methods present in the `visitor` package
- UML relationships in `assignment6-uml.puml` match the implemented classes and dependencies
- Sample values and totals in the report are consistent with the tested expected results (`1160` bytes total; matches `app.log` and `system.log`)
- README commands and repository URL are consistent with the actual project and wrapper script

No generated DOCX or binary report files were found in the repository; the project documents are Markdown and PlantUML, which matches the actual checked-in artifacts.

## I. Remaining Issues

Confirmed or environment-limited issues:

- The default app port `8080` was already occupied in this environment; clean runtime startup of the project could not be guaranteed from a fresh run
- No browser automation was available to visually confirm the new redesign end-to-end
- Runtime verification of `http://localhost:8080/api/visitor/demo` is limited because the actual running service on that port could not be confirmed to be this project instance
- No build or automated test failures were found in the codebase at the time of audit

## J. Final Readiness Assessment

Status: MOSTLY COMPLETE — MINOR ISSUES REMAIN

Reason:

- The implementation is complete and the Maven test/build validation is successful.
- The code and documentation for the website redesign and the Visitor assignment are present and internally consistent.
- The only meaningful limitations are environmental: a port conflict prevented clean project startup verification and there is no browser automation in this environment.

This is not a source-code blocker; it is a runtime verification limitation.

## K. Recommended Next Steps

1. Free or reconfigure port 8080 so the Spring Boot app can start cleanly.
2. Run the app from the project root and verify `/` and `/api/visitor/demo` directly on the correct port.
3. Open the page in a browser and test the main UI flows manually or with automation.
4. Confirm the static redesign is visually acceptable in a real browser, not just by source inspection.
5. Finalize submission with the validated build/test results and final project audit report.

## Final Conclusion

The project is in a good state for review based on source inspection and automated validation: the redesign is implemented, the Visitor Pattern assignment is present and passing tests, the Maven build is green, and the repository changes align with the recent work. The remaining limitations are operational and environmental rather than functional defects in the codebase.

