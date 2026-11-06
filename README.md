# SDP_FINAL_PROJECT — PC Build Studio

SDP_FINAL_PROJECT is a Java 17 and Spring Boot defense project demonstrating seven Software Design Patterns through a PC Build Studio web application. It supports PC hardware selection, server-side compatibility validation, build assembly, diagnostics, report export, and virtual build-package inspection. The responsive Russian-language browser interface uses plain HTML, CSS, and JavaScript. Hardware metadata comes from an in-memory catalog; no database or frontend build pipeline is required.

Repository: <https://github.com/muchanroma2501/SDPasik2>

## Requirements and commands

- JDK 17 or newer
- Maven 3.6+ or the included Maven Wrapper

Run from the repository root:

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

Open <http://localhost:8080>. Package and run the executable JAR with:

```powershell
.\mvnw.cmd clean package
java -jar target/sdp-final-project-1.0.0.jar
```

Run the clean test suite with `.\mvnw.cmd clean test` (Windows) or `./mvnw clean test` (Unix).

## API

The supported platform values are exactly `intel_nvidia` and `amd`. Preset build types are `gaming`, `budget`, and `workstation`. Report types are `summary` and `full`; formats are `html` and `json`.

| Method | Endpoint | Behavior |
|---|---|---|
| `GET` | `/api/pc/presets` | Returns the gaming, office/budget, and workstation configurations with component specifications. |
| `GET` | `/api/pc/catalog?ecosystem=intel_nvidia` | Returns server-supported CPUs, GPUs, motherboards, RAM, storage, PSUs, and cooling models for the selected platform. The default ecosystem is `intel_nvidia`. |
| `POST` | `/api/pc/diagnose` | Returns structured `READY`, `REVIEW`, or `BLOCKED` diagnostics without constructing a PC. Compatibility problems are returned as JSON checks. |
| `POST` | `/api/pc/custom` | Validates and builds the submitted custom selection. Required fields: `ecosystem`, `cpu`, `gpu`, `motherboard`, `ram`, `storage`, and `powerSupply`; `coolingSystem` is optional and `hasRGB` is a boolean. |
| `POST` | `/api/pc/build` | Builds an existing named pipeline. Requires `ecosystem`, `buildType`, `ram`, and `storage`; `coolingSystem` is optional and `hasRGB` is a boolean. |
| `GET` | `/api/reports/export?reportType=summary&format=html` | Backward-compatible Bridge export using the legacy default gaming configuration. |
| `POST` | `/api/reports/export` | Exports the actual submitted selection with Bridge report and exporter choices. |
| `POST` | `/api/diagnostics/run` | Runs the separate simulated CPU/GPU/thermal stress-test facade for the submitted configuration and returns HTML. |
| `GET` | `/api/visitor/pc-bundle?ecosystem=intel_nvidia&buildType=gaming&query=log` | Builds a configured PC, runs diagnostics, and uses Visitors to size and search its virtual artifact bundle. |

Example diagnostics/build request:

```json
{
  "ecosystem": "intel_nvidia",
  "cpu": "Intel Core i7-14700K",
  "gpu": "NVIDIA GeForce RTX 4070 Ti 12GB",
  "motherboard": "Z790 ATX",
  "ram": "32GB DDR5",
  "storage": "1TB NVMe SSD",
  "powerSupply": "850W 80+ Gold",
  "coolingSystem": "280mm AIO Liquid Cooling",
  "hasRGB": true
}
```

Example report request (the selection is nested under `configuration`):

```json
{
  "reportType": "full",
  "format": "json",
  "configuration": {
    "ecosystem": "amd",
    "cpu": "AMD Ryzen 5 7600",
    "gpu": "AMD Radeon RX 6600 8GB",
    "motherboard": "B650 ATX",
    "ram": "32GB DDR5",
    "storage": "1TB NVMe SSD",
    "powerSupply": "650W 80+ Gold",
    "coolingSystem": "Stock AMD Wraith Stealth",
    "hasRGB": false
  }
}
```

Malformed JSON and invalid selections return readable JSON errors. Invalid or unknown input is rejected with HTTP 400; a well-formed but incompatible build is rejected with HTTP 422. The diagnose endpoint is intentionally different: it returns HTTP 200 with a `BLOCKED` result so clients can display each failed check.

## Pattern-driven architecture

The application entry point is `com.aitu.sdp.Main`, which component-scans `com.aitu.sdp`. Pattern implementations are grouped by domain and pattern rather than by assignment number:

- **Abstract Factory:** `com.aitu.sdp.creational` defines `ComponentFactory` and platform-specific factories for compatible hardware families.
- **Factory Method:** `com.aitu.sdp.creational` includes `ComputerOrderFactory` and gaming, budget, and workstation order factories.
- **Builder:** `com.aitu.sdp.creational` contains computer builders that assemble the immutable `Computer`.
- **Bridge:** `com.aitu.sdp.bridge` separates report detail from HTML/JSON exporters; the HTTP adapter is in `com.aitu.sdp.bridge.controller`.
- **Facade:** `com.aitu.sdp.facade` coordinates PC configuration, reporting, and diagnostics; supporting diagnostics and HTTP adapters live in `com.aitu.sdp.facade.diagnostics` and `com.aitu.sdp.facade.controller`.
- **Strategy:** `com.aitu.sdp.strategy` provides runtime selection among gaming, budget, and workstation pipelines.
- **Visitor:** `com.aitu.sdp.visitor` provides the virtual artifact tree and size/search visitors used by `/api/visitor/pc-bundle`.

The configurator facade coordinates the component provider, catalog, diagnostics, Factory Method order factories, Builder, and Strategy registry. The report facade remains a separate coordinator for report creation. The stress-test facade simulates CPU, GPU, and thermal interactions; its metrics are not hardware certification.

### Educational power rules

These are deterministic educational estimates, not guaranteed manufacturer recommendations:

- `requiredPowerWatts = CPU TDP + GPU TDP + 150`
- `recommendedPowerWatts = ceil(requiredPowerWatts * 1.10)`
- PSU below required power: blocking `ERROR`
- PSU at or above required power but below recommended power: allowed `WARNING` (`REVIEW`)
- PSU at or above recommended power: `PASS`
- CPU/motherboard socket mismatch, unknown required models, and platform/family mismatch block construction.

The Builder and diagnostics use the shared `PowerRequirementCalculator`. The catalog contains no RAM-generation compatibility metadata, so the application does not claim to validate RAM generation, thermals, benchmarks, FPS, or performance in the compatibility diagnostic.

`BridgePatternDemo` remains runnable as a standalone Java demonstration and is not invoked during Spring Boot startup. The Visitor endpoint composes configuration, Strategy, diagnostics, and artifact inspection without altering existing REST contracts.

## Frontend and browser storage

The static UI is organized as `src/main/resources/static/index.html`, `css/app.css`, and `js/app.js`. It retrieves both platform catalogs and the presets from the API, debounces diagnostics, discards stale requests, and leaves final compatibility decisions to the server. HTML report previews use a sandboxed iframe; data values are rendered with text nodes rather than interpolated into HTML. Recent actions and configurations are saved in browser `localStorage`, with load/clear controls.

## Tests

`.\mvnw.cmd clean test` (Windows) or `./mvnw clean test` (Unix) runs:

- Unit tests for model resolution, sockets, minimum and recommended PSU thresholds, unknown models, invalid platform values, and facade build/rejection behavior.
- Spring MockMvc tests for presets, both catalogs, diagnostics, custom and named builds, report export for the requested components, malformed JSON, and unsupported content types.
- Visitor tests for artifact tree sizing/search and MockMvc coverage for the integrated PC bundle endpoint.

## Project structure

```text
com.aitu.sdp
├── Main.java            # Spring Boot entry point and component-scan root
├── creational           # Hardware model, catalog, factories, Builder, HTTP DTOs
├── bridge               # Bridge reports, exporters, and report controller
├── facade               # Configuration/report facades, diagnostics, support, HTTP adapters
├── strategy             # Runtime build pipeline strategies and context
└── visitor              # Virtual artifact tree, Visitors, and visitor controller/service
src/main/resources/static
├── css/app.css
├── js/app.js
└── index.html
docs                    # Audit, UML, assignment report draft, defense notes
```

## Strategy Pattern — Runtime Build Pipeline

The application has three named build algorithms: gaming, budget, and workstation. The build algorithm is represented as a strategy object and executed through a small context.

### Strategy structure

- `com.aitu.sdp.strategy.ComputerBuildStrategy` — common strategy interface
- `com.aitu.sdp.strategy.GamingBuildStrategy` — gaming build algorithm
- `com.aitu.sdp.strategy.BudgetBuildStrategy` — budget build algorithm
- `com.aitu.sdp.strategy.WorkstationBuildStrategy` — workstation build algorithm
- `com.aitu.sdp.strategy.BuildStrategyContext` — holds the current strategy and delegates execution
- `com.aitu.sdp.facade.PCConfiguratorFacade` — client that selects the strategy and validates the request

The Strategy pattern complements the existing Factory Method and Builder patterns instead of replacing them. The strategy chooses the correct order factory and Builder pipeline for the selected build type, while the Factory Method and Builder remain responsible for the actual assembly flow.

### Runtime strategy switching

The same context can change strategies at runtime:

```java
BuildStrategyContext context = new BuildStrategyContext();
context.setStrategy(new GamingBuildStrategy());
Computer gaming = context.build(factory, gamingRequest);

context.setStrategy(new BudgetBuildStrategy());
Computer budget = context.build(factory, budgetRequest);

context.setStrategy(new WorkstationBuildStrategy());
Computer workstation = context.build(factory, workstationRequest);
```

### Example named-build requests

Gaming:

```json
{
  "ecosystem": "intel_nvidia",
  "buildType": "gaming",
  "ram": "32GB DDR5",
  "storage": "1TB NVMe SSD",
  "coolingSystem": "280mm AIO Liquid Cooling",
  "hasRGB": true
}
```

Budget:

```json
{
  "ecosystem": "intel_nvidia",
  "buildType": "budget",
  "ram": "16GB DDR4",
  "storage": "512GB NVMe SSD",
  "coolingSystem": "Air Cooler",
  "hasRGB": false
}
```

Workstation:

```json
{
  "ecosystem": "intel_nvidia",
  "buildType": "workstation",
  "ram": "64GB DDR5",
  "storage": "2TB NVMe SSD",
  "coolingSystem": "360mm AIO Liquid Cooling",
  "hasRGB": true
}
```

### Running the application

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

Then open <http://localhost:8080>.

### Running the tests

```powershell
.\mvnw.cmd clean test
```

```bash
./mvnw clean test
```

### How the Strategy pattern fits the project

The application still uses:

- Abstract Factory and Builder in `com.aitu.sdp.creational` create a platform-compatible immutable computer.
- Factory Method in `com.aitu.sdp.creational` selects the named order/build pipeline.
- Strategy in `com.aitu.sdp.strategy` selects gaming, budget, or workstation behavior at runtime.
- Facade in `com.aitu.sdp.facade` coordinates configuration, diagnostics, and report operations.
- Bridge in `com.aitu.sdp.bridge` separates report representation from HTML/JSON export.
- Visitor in `com.aitu.sdp.visitor` calculates and searches the artifact bundle returned by `/api/visitor/pc-bundle`.

This makes the code more flexible, easier to extend, and better suited for defense explanations.
