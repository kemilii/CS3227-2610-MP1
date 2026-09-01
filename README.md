# HomeHub

HomeHub is a Java desktop household task manager for chores, deadlines, and
events. Moss, its household concierge, helps users keep their task board tidy.

## Prerequisites

- JDK 25
- IntelliJ IDEA (optional, for IDE-based development)

The repository includes the Gradle wrapper, so a separate Gradle installation
is not required.

## Run HomeHub

### Run the release JAR

The latest bundled release is available at `release/homehub.jar`. It includes
the JavaFX libraries needed by the application for supported desktop platforms.

```bash
java -jar release/homehub.jar
```

### Run from source

From the project root, use the Gradle wrapper:

```bash
./gradlew run
```

On Windows, run `gradlew.bat run` instead.

### Run from IntelliJ IDEA

1. Open the project directory as a Gradle project.
2. Configure the project to use JDK 25.
3. Run `homehub.Launcher` from `src/main/java`.

## Build and test

Run the test suite with:

```bash
./gradlew test
```

Build the executable fat JAR with its JavaFX dependencies included:

```bash
./gradlew shadowJar
```

The generated JAR is written to `build/libs/homehub.jar`. To update the
checked-in release artifact on macOS or Linux:

```bash
cp build/libs/homehub.jar release/homehub.jar
```

## Documentation

- [User Guide](docs/README.md) — setup, commands, and examples
- [Developer Guide](docs/DeveloperGuide.md) — architecture and development
- [Reflections](docs/Reflections.md) — project reflections
- [Interaction Logs](logs/README.md) — project interaction records
