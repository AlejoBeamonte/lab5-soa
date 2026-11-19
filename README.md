# Lab 5 Modules & Application Events

Individual starter for Web Engineering 2026–27. Stack matches the group project: **Java 25 LTS**, **Kotlin 2.4.0**, **Spring Boot 4.1.0**, **Gradle 9.6.0**, plus **Spring Modulith 2.1.2** and Spring Integration for this lab.

The assignment, AI rules, and deadline are in [`docs/GUIDE.md`](docs/GUIDE.md). You may add an `AGENTS.md` or a project skill so an agent follows that guide (see AI use there). Fill [`REPORT.md`](REPORT.md) before you submit. Delivery is the Moodle zip only (`docs/GUIDE.md`).

## Run

Java 25 is required (`./gradlew` uses the wrapper). GitHub Codespaces is optional (`docs/GUIDE.md`). Clone this course repository; you do not fork it to submit.

```bash
git clone https://github.com/UNIZAR-30246-WebEngineering/lab5-soa.git
cd lab5-soa
./gradlew check
./gradlew bootRun
```

`./gradlew check` passes on this starter: `IntegrationFlowTests` and `ModularityTests` are `@Disabled`. The assignment is to fix the modules and flows and remove those annotations. `./gradlew bootRun` starts the application and logs every event and message; the wiring is wrong on purpose.

```bash
./gradlew test
./gradlew ktlintCheck
```

## Layout

```
src/main/kotlin/soa/IntegrationApplication.kt
src/main/kotlin/soa/numbers/   NumberData, NumberSources
src/main/kotlin/soa/routing/   NumberEvents, SendNumber, RoutingFlows, internal/Parity
src/main/kotlin/soa/even/      EvenNumberData, EvenFlows
src/main/kotlin/soa/odd/       OddNumberData, OddFlows, SomeService
src/main/resources/application.yml
src/test/kotlin/soa/IntegrationFlowTests.kt
src/test/kotlin/soa/ModularityTests.kt
docs/GUIDE.md
docs/EIP.png
```

Each package under `soa` is a Spring Modulith module. `docs/EIP.png` is the target. You draw the starter as `diagrams/before.png`, fix the modules and flows until they match the target, and enable both test classes.
