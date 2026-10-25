# Assignment #5 Defense Notes

## 1. Short introduction

This feature adds the Strategy design pattern to the PC Build Studio app. The application already has three named build types: gaming, budget, and workstation. Instead of choosing the build algorithm with a `switch`, we now keep each algorithm behind a common interface and let a context execute the selected one.

## 2. What is Strategy?

The Strategy pattern lets an object change its behavior by replacing an algorithm at runtime. The client chooses the strategy, but the context only executes the current one. This is useful when several algorithms do similar work but produce different results.

## 3. New classes

- `ComputerBuildStrategy`: common interface for all build algorithms.
- `GamingBuildStrategy`: creates the gaming build pipeline.
- `BudgetBuildStrategy`: creates the budget build pipeline.
- `WorkstationBuildStrategy`: creates the workstation build pipeline.
- `BuildStrategyContext`: holds the current strategy and delegates execution.
- `PCConfiguratorFacade`: selects the strategy, configures the context, and validates the result.

## 4. Walkthrough of a named build request

1. The user sends `POST /api/pc/build` with JSON like:

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

2. `PCConfiguratorFacade.buildNamed(...)` validates the request.
3. It resolves the correct `ComponentFactory` using `ComponentFactoryProvider`.
4. It looks up the matching strategy from the registry.
5. It creates a `BuildStrategyContext` and calls `setStrategy(...)`.
6. The context calls `strategy.build(factory, request)`.
7. The concrete strategy creates the correct `ComputerOrderFactory` and `Builder`.
8. The facade runs diagnostics and returns `PCResponse`.

## 5. Original problem with conditional selection

Before Strategy, the code looked like this:

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

This is a valid solution, but it mixes algorithm selection and orchestration in one place. If the project adds a new build variant, it must change this switch. The Strategy pattern removes that issue by moving each algorithm into a separate class.

## 6. Runtime switching demo

Use the same `BuildStrategyContext` object:

```java
BuildStrategyContext context = new BuildStrategyContext();
context.setStrategy(new GamingBuildStrategy());
Computer gaming = context.build(factory, gamingRequest);

context.setStrategy(new BudgetBuildStrategy());
Computer budget = context.build(factory, budgetRequest);

context.setStrategy(new WorkstationBuildStrategy());
Computer workstation = context.build(factory, workstationRequest);
```

The same API is used for all three builds. The context does not need to know which concrete class is currently selected.

## 7. Five Clean Code principles

1. Polymorphism instead of conditionals
2. Meaningful names
3. Single Responsibility Principle
4. Context independent of concrete implementations
5. Open for extension

Each is shown in the implementation and the code examples in the report.

## 8. Strategy vs Factory Method vs Builder vs Facade

- Factory Method: chooses the Builder pipeline for a build family.
- Builder: assembles the immutable `Computer` object step by step.
- Facade: coordinates the high-level configuration workflow.
- Strategy: determines which algorithm to use for the named build type.

The Strategy layer works with the existing components instead of replacing them.

## 9. Likely instructor questions

1. Why is this pattern called Strategy?
   - Because each build algorithm is a strategy that can be exchanged at runtime.

2. Where is the Client?
   - In `PCConfiguratorFacade`.

3. Where is the Context?
   - In `BuildStrategyContext`.

4. What is the Strategy interface?
   - `ComputerBuildStrategy`.

5. Which classes implement it?
   - `GamingBuildStrategy`, `BudgetBuildStrategy`, `WorkstationBuildStrategy`.

6. How does the context switch strategy?
   - `setStrategy(...)` changes the current strategy reference.

7. Why is there no `switch` in the context?
   - Because the context only delegates; it does not choose algorithms by type.

8. What is the role of Factory Method here?
   - It creates the correct Builder pipeline for the selected strategy.

9. Why not put everything into the facade?
   - Because the algorithm selection would become harder to extend and test.

10. Can we add a new build type?
    - Yes, by creating a new strategy and registering it as a Spring component.

11. What does the strategy return?
    - A configured `Computer` object.

12. Does the endpoint remain the same?
    - Yes, `POST /api/pc/build` remains the same endpoint.

## 10. Difficult follow-up questions

1. If the context only holds an interface, how do we ensure it never reaches a null strategy?
   - `Objects.requireNonNull` is used in `setStrategy(...)` and `build(...)`.

2. Why is the client responsible for strategy selection, not the context?
   - The context should not know about specific build types; it should only execute the current strategy. The client decides which strategy matches the request.

3. Why is this extension better than just a map or a switch?
   - Because a map or switch still centralizes selection logic and creates a harder-to-extend design. Strategy distributes responsibilities and allows runtime changes without changing the context logic.

## 11. Practical IntelliJ IDEA walkthrough

Open these files in order:

1. `src/main/java/com/aitu/sdp/assignment5/strategy/ComputerBuildStrategy.java`
2. `src/main/java/com/aitu/sdp/assignment5/strategy/GamingBuildStrategy.java`
3. `src/main/java/com/aitu/sdp/assignment5/strategy/BudgetBuildStrategy.java`
4. `src/main/java/com/aitu/sdp/assignment5/strategy/WorkstationBuildStrategy.java`
5. `src/main/java/com/aitu/sdp/assignment5/strategy/BuildStrategyContext.java`
6. `src/main/java/com/aitu/sdp/assignment4/facade/PCConfiguratorFacade.java`
7. `src/main/java/com/aitu/sdp/assignment2/ComputerOrderFactory.java`
8. `src/main/java/com/aitu/sdp/assignment2/GamingPCOrderFactory.java`

This is enough to explain the whole design during the in-class defense.

