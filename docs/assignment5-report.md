# Assignment #5 — Strategy Pattern Report

## 1. Introduction

PC Build Studio is a Java 17 and Spring Boot project for selecting hardware, validating compatibility, and constructing a custom or named PC configuration. The existing project already contains three named build pipelines: gaming, budget, and workstation. These are good candidates for the Strategy pattern because they all follow the same algorithm contract, but each build type creates a different hardware configuration and different Builder flow.

The original implementation in `PCConfiguratorFacade` selected the right order factory with a `switch` inside the client. This worked, but the algorithm-selection logic was concentrated in one place and was harder to extend. The Strategy pattern solves this by separating the algorithm behind a common interface and letting the client choose the strategy at runtime.

## 2. UML Class Diagram

The new Strategy UML diagram is available in [assignment5-uml.md](assignment5-uml.md).

`PCConfiguratorFacade` acts as the Client. It validates the request, resolves the platform factory, picks the correct `ComputerBuildStrategy`, configures a `BuildStrategyContext`, and calls the context. The context itself only holds a `ComputerBuildStrategy` reference and delegates execution to it. Each concrete strategy then reuses the appropriate Factory Method order factory and Builder pipeline.

## 3. Clean Code

### Principle 1 — Polymorphism instead of conditional algorithm selection

Before:

```java
private ComputerOrderFactory orderFactory(String type, ComponentFactory factory) {
    return switch (type.trim().toLowerCase()) {
        case "gaming" -> new GamingPCOrderFactory(factory);
        case "budget" -> new BudgetPCOrderFactory(factory);
        case "workstation" -> new WorkstationPCOrderFactory(factory);
        default -> throw new IllegalArgumentException("buildType must be gaming, budget, or workstation");
    };
}
```

After:

```java
ComputerBuildStrategy strategy = strategyRegistry.get(normalizedKey);
BuildStrategyContext context = new BuildStrategyContext();
context.setStrategy(strategy);
Computer computer = context.build(factory, request);
```

This is better because the algorithm is shared behind one interface instead of being hidden in a conditional block. The response remains the same, but the design is easier to extend.

### Principle 2 — Meaningful, intention-revealing names

Names such as `GamingBuildStrategy`, `BudgetBuildStrategy`, `WorkstationBuildStrategy`, `setStrategy()`, and `build()` describe the purpose of the code directly. A second-year student can see that the strategy class is responsible for a specific build algorithm and that the context changes the selected strategy at runtime.

### Principle 3 — Single Responsibility Principle

Each concrete strategy handles only one named build algorithm. `GamingBuildStrategy` creates a gaming build, `BudgetBuildStrategy` creates a budget build, and `WorkstationBuildStrategy` creates a workstation build. The `BuildStrategyContext` only delegates to the selected strategy and does not start validating requests or building diagnostics. This keeps responsibilities separated and makes the classes easier to reason about.

### Principle 4 — Context is independent of concrete implementations

The context depends only on the interface:

```java
public class BuildStrategyContext {
    private ComputerBuildStrategy strategy;

    public void setStrategy(ComputerBuildStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
    }

    public Computer build(ComponentFactory factory, BuildPCRequest request) {
        return Objects.requireNonNull(strategy, "Strategy must be configured before execution")
                .build(factory, request);
    }
}
```

The context does not check for `GamingBuildStrategy`, `BudgetBuildStrategy`, or `WorkstationBuildStrategy` directly. This makes the design flexible and obeys the Dependency Inversion principle.

### Principle 5 — Open for extension

A new strategy can be added by implementing `ComputerBuildStrategy` and registering it as a Spring component. Example:

```java
@Component
public class QuietBuildStrategy implements ComputerBuildStrategy {
    @Override
    public String getBuildType() {
        return "quiet";
    }

    @Override
    public Computer build(ComponentFactory factory, BuildPCRequest request) {
        // new algorithm
        return null;
    }
}
```

The `PCConfiguratorFacade` uses the injected list of strategies to build a registry. The context and previously created strategies do not need to change, which demonstrates the open-closed idea in a practical way.

## 4. Conclusion

The Strategy pattern adds flexibility to the existing PC build workflow. It keeps the named build algorithms controlled, testable, and easily extensible without rewriting the normal request-validation and diagnostics flow. The added abstractions are justified because the project has multiple interchangeable build algorithms with shared behavior and different final assembly details.

## 5. GitHub Repository Link

The project repository is configured as:

<https://github.com/muchanroma2501/SDPasik2>

This URL is taken from the current project metadata and should be verified before submission if the instructor expects a different canonical repository address.

