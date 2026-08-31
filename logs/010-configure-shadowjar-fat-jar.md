# Task 010 — Configure shadowJar fat JAR

- Task ID: `01a052cb-0e02-7c00-a7e3-51ea3620823d`
- Scope: Explain and apply executable fat-JAR packaging.

## Prompts and interactions

- Asked how to configure `build.gradle` for a Shadow fat JAR and how to build,
  locate, and run it. The response identified the existing Shadow plugin and
  corrected the application entry point to `homehub.HomeHub`.
- Asked to “help me do” it. `build.gradle` was updated with the correct main
  class and `homehub.jar` archive name.

## Outcome and verification

`./gradlew clean shadowJar` produced `build/libs/homehub.jar`, and
`java -jar build/libs/homehub.jar` ran successfully under Java 25. JUnit and
the UI test workflow passed; the complete UI record was appended to
`test/ui-test-plan.md`.
