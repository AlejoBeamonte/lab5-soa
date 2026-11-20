# Lab 5 Modules & Application Events -- Project Report

## EIP diagram (before)

![Before diagram](diagrams/before.png)

I manually created the diagram in draw.io. It represents the actual behaviour of the starter before fixing it and is stored in `diagrams/before.png`.

The `numbers` module publishes `NumberData` events: a counter value every 100 ms and a random negative number every second. The counter publisher runs inside a transaction, while the negative publisher initially did not. The events reach the `routing` module through `@ApplicationModuleListener`, then pass through the gateway, `numberChannel`, and the router.

In the starter, `evenChannel` is publish-subscribe and `oddChannel` is an automatically created `DirectChannel`, because it is not declared as a bean. The odd flow contains a filter that checks `Parity.isEven`, even though it receives the numbers that the router considers odd. In addition, `SomeService` is another consumer of `oddChannel`, so the consumers compete for messages. Messages rejected by the filter go to `discardChannel` and end at the `Discard handler`.

The diagram also includes a note showing the initial module dependencies. It records that `routing` uses `OddNumberData` from `odd`, while `odd` uses `Parity` from `routing.internal`. The dotted connectors from the note to the `routing` and `odd` module boundaries make clear that these are module-level dependencies, not message-flow connections. Together, they create a cycle and an internal-package violation, which are fixed later in the implementation.

## What I specified

I specified that all numbers, including negative numbers, had to be published as events and reach the `routing` listener. The router had to send even numbers to `evenChannel` and odd numbers to `oddChannel`.

According to the target diagram, `evenChannel` had to be point-to-point and have one consumer. The even flow had to transform `NumberData` into `EvenNumberData` and run the even handler. `oddChannel` had to be publish-subscribe so that every odd number reached both the `OddFlows` flow and `SomeService`. The odd flow had to transform `NumberData` into `OddNumberData` and run the odd handler without rejecting messages.

I also specified that `routing` must not depend on `odd.OddNumberData`, and that `odd` must not access `routing.internal.Parity`. Modules could use the public API of `numbers`, especially `NumberData`, and communicate through channel names, but they must not create cycles or access internal packages.

The differences to fix were the transaction on the negative publisher, the channel types, the way parity was decided, the incorrect filter, the discard flow, and the module dependencies. I considered them fixed when `./gradlew check` passed, the channels had the expected types, even numbers reached only the even flow, and odd numbers reached both consumers.

## What I changed

1. In `src/main/kotlin/soa/numbers/NumberSources.kt`, I added `@Transactional` to `publishNegative()`. This makes negative events publish inside a transaction, like the counter events, so they can be processed correctly by `@ApplicationModuleListener`.

2. In `src/main/kotlin/soa/routing/RoutingFlows.kt`, I changed `evenChannel` from publish-subscribe to `MessageChannels.direct()` and declared `oddChannel` with `MessageChannels.publishSubscribe()`.

3. In the same file, I replaced the construction of `OddNumberData` as the routing decision with a direct parity check (`m.data % 2 == 0`). This removes the import from the `odd` module in `routing`.

4. In `src/main/kotlin/soa/odd/OddFlows.kt`, I removed the filter based on `Parity`, because the router already separates even and odd numbers. I also removed the `discardChannel` flow, because the target flow does not reject messages.

5. I removed `@Disabled` from `IntegrationFlowTests` and `ModularityTests` to enable the mandatory lab checks. I did not change their assertions or log messages.

## Technical decisions

`evenChannel` is direct because each message only has to be delivered to `EvenFlows`. In contrast, `oddChannel` has two independent consumers: the `OddFlows` flow and `SomeService`. It must therefore be publish-subscribe; a direct channel would make both consumers compete and would prevent every odd number from reaching both of them.

The router uses the parity of `NumberData` itself. This keeps the decision in the `routing` module and avoids depending on `OddNumberData`, which belongs to the `odd` module. The `OddNumberData` constructor is still used by its own module as part of the translator.

The starter filter checked `Parity.isEven` inside the odd flow. Besides depending on an internal package, it could reject messages. I removed it because the router already guarantees that messages sent to `oddChannel` are odd. For the same reason, I removed `discardChannel` and its handler: the target behaviour does not discard messages.

I kept `SomeService` because it is part of the target behaviour: it receives the original `NumberData` as a second consumer of the odd channel. I also kept the translators because the target requires transforming `NumberData` into `EvenNumberData` or `OddNumberData` before running each handler.

I did not change the test logic or the log messages. I also did not implement any bonus functionality.

## How I verified

To check the original starter, I enabled the two test classes, and ran the tests. `IntegrationFlowTests` completed 8 tests and 6 failed: `the odd channel is publish-subscribe()`, `a NumberData event published by a module starts the flow`, `the even channel is point-to-point()`, `an odd number from the gateway is routed to both odd consumers`, `every odd number from the poller reaches both odd consumers`, and `negative numbers published on a schedule reach the listener`. These failures correspond to the wrong channel types, the competing consumers and filter in the odd flow, and the missing transaction on the negative publisher.

The starter's `ModularityTests` completed 2 tests and 1 failed: `modules only use each other's public API and have no cycles()`, with a Spring Modulith `Violations` error. This corresponds to the `routing → odd` dependency through `OddNumberData` and the `odd → routing.internal` dependency through `Parity`. The documentation-writing test passed.

After implementing the fixes, I ran the tests with both test classes enabled:

```text
./gradlew clean test
```

The result was `BUILD SUCCESSFUL`. I then ran:

```text
./gradlew check
```

This also finished with `BUILD SUCCESSFUL`, including the integration tests, modularity tests, and Kotlin style checks.

For the manual check, I ran `./gradlew bootRun` and reviewed the logs. I confirmed that `evenChannel` is created as a `DirectChannel` and `oddChannel` as a `PublishSubscribeChannel` with two consumers. I also confirmed that even numbers reach the `Even Handler`, odd numbers reach both the `Odd Handler` and `SomeService`, negative numbers reach the listener, and no `REJECT` or `Discard Handler` messages appear.

## AI disclosure

- **Tools / skills:** ChatGPT-5.6 Luna.
- **Purpose:** Receive assistance understanding the guide and EIP patterns, identifying what the starter code actually does before drawing the starter diagram, reviewing the diagram, and checking small implementation changes.
- **Representative prompts:** “Help me understand what the starter code does before I draw its EIP diagram”, “Help me check that my diagram represents the starter correctly”, and “Review this small code change and explain how I can test it”.
- **Affected files/sections:** `diagrams/before.png` (created and manually reviewed by me in draw.io; AI assistance was used to understand the starter code, check the EIP mapping, and suggest minor diagram edits), `src/main/kotlin/soa/numbers/NumberSources.kt`, `src/main/kotlin/soa/routing/RoutingFlows.kt`, `src/main/kotlin/soa/odd/OddFlows.kt`, `src/test/kotlin/soa/IntegrationFlowTests.kt`, `src/test/kotlin/soa/ModularityTests.kt`, and this `REPORT.md`.
- **Validation steps:** I manually reviewed the diagram and the code, ran `./gradlew clean test`, `./gradlew check`, and `./gradlew bootRun`, and checked the logs for even numbers, odd numbers, negative numbers, `Odd Handler`, `Even Handler`, and `SomeService`.
- **Citations:** I did not adapt external snippets. The explanations were based on `docs/GUIDE.md`, the repository code, and the provided target diagram.
- **Human-reviewed:** I manually created the diagram in draw.io and checked that it represented the starter code, reviewed the suggested minor diagram edits, verified every code change, confirmed that the test assertions and log messages were not modified, and performed the final manual verification.
