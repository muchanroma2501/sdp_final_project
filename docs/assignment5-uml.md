# Assignment #5 — Strategy Pattern UML

```mermaid
classDiagram
    class PCConfiguratorFacade {
        +buildNamed(request)
        +validateBuildRequest(request)
    }

    class BuildStrategyContext {
        -ComputerBuildStrategy strategy
        +setStrategy(strategy)
        +build(factory, request)
    }

    interface ComputerBuildStrategy {
        +getBuildType()
        +build(factory, request)
    }

    class GamingBuildStrategy
    class BudgetBuildStrategy
    class WorkstationBuildStrategy

    class ComponentFactory {
        <<interface>>
    }
    class IntelNvidiaComponentFactory
    class AMDComponentFactory

    class ComputerOrderFactory {
        <<abstract>>
    }
    class GamingPCOrderFactory
    class BudgetPCOrderFactory
    class WorkstationPCOrderFactory
    class ComputerBuilder
    class Computer

    PCConfiguratorFacade --> BuildStrategyContext : configures and delegates
    BuildStrategyContext --> ComputerBuildStrategy : holds interface reference
    ComputerBuildStrategy <|.. GamingBuildStrategy
    ComputerBuildStrategy <|.. BudgetBuildStrategy
    ComputerBuildStrategy <|.. WorkstationBuildStrategy

    GamingBuildStrategy --> GamingPCOrderFactory : uses
    BudgetBuildStrategy --> BudgetPCOrderFactory : uses
    WorkstationBuildStrategy --> WorkstationPCOrderFactory : uses

    GamingPCOrderFactory --> ComputerBuilder : creates
    BudgetPCOrderFactory --> ComputerBuilder : creates
    WorkstationPCOrderFactory --> ComputerBuilder : creates
    ComputerBuilder ..> Computer : builds

    ComponentFactory <|.. IntelNvidiaComponentFactory
    ComponentFactory <|.. AMDComponentFactory
    GamingPCOrderFactory --> ComponentFactory
    BudgetPCOrderFactory --> ComponentFactory
    WorkstationPCOrderFactory --> ComponentFactory
```

The Strategy pattern sits above the existing Factory Method and Builder flow. The facade chooses a strategy, the context delegates to it, and each concrete strategy reuses the correct order factory and builder pipeline for the selected build type.

