# Reflections on AI-Assisted Software Engineering

## Introduction

I used a large language model (LLM) as an AI-assisted software engineering partner while developing HomeHub. It helped with brainstorming, implementation, refactoring, testing, documentation, and parts of the JavaFX interface. This was not my first time using AI to assist with coding, but it was my first time using project-specific skills such as `test-ui` as part of the development workflow.

My main conclusion is that an LLM is most useful when it is placed inside a disciplined engineering process. It can generate code very quickly, but speed alone does not guarantee correctness. The strongest results came when I gave it clear constraints, expected behavior, and a way to verify the result. The weakest results came when I left important design decisions implicit and accepted a plausible-looking answer too quickly.

## Reflection 1: project ideation and scope control

One of my first prompts was:

Come up with some project ideas that is a **Java desktop** app where its functionality should contain as listed here: [the CS3227 Duke requirements].

The LLM proposed several ideas and mapped them to features such as adding items, listing them, marking them as done, using deadlines, and handling errors. I selected HomeHub because household chores, bills, repairs, and events gave the basic task manager a clear identity.

The more interesting prompt came immediately afterwards:

Can you change the code to fit the homehub idea, do not add any new features ... just change to homehub first

This prompt shows why negative constraints are important in LLM prompting. The assistant could easily have added categories, priorities, overdue warnings, or summaries because those features sounded appropriate for a household app. However, they were not required at that point. By explicitly saying what not to change, I kept the first implementation focused on renaming the application and its user-facing messages.

The engineering lesson was that brainstorming and requirements definition are different activities. AI was useful for generating possibilities, but I had to choose the scope and decide which ideas were suitable for the module. If I had treated every suggestion as a requirement, the project would have become much larger and harder to test.

## Reflection 2: prompting an incremental refactor

For the architecture, I asked the LLM to gradually extract `Ui`, `Storage`, `Parser`, and `TaskList` as instructed in the guideline::

1. Choose a small stand-alone increment.
2. Implement it without changing unrelated behavior.
3. Test it.
4. Commit it with a rationale.
5. Generate a visual diff.

This was much more effective than a broad instruction such as “refactor the code using good object-oriented design”. The work proceeded from UI extraction to storage, task-list ownership, parsing, and finally task-command handling. Each step had a clear purpose, and the commit history showed how the design changed over time.

The process also exposed an important limitation of AI-generated refactoring. One version removed duplicated UI rendering logic, but `showGoodbye()` still wrote directly to standard output. This meant the GUI response collector did not receive the goodbye message even though the CLI appeared to work. The test failure identified the mismatch between the two interfaces, and the code was adjusted while preserving the existing response contract.

This example changed how I think about refactoring. A refactor is not correct just because the classes look cleaner or follow a familiar pattern. It must preserve observable behavior, including behavior in less obvious consumers such as the GUI. The LLM was good at suggesting boundaries, but I was still responsible for deciding whether those boundaries made sense and checking that the old behavior remained intact.

## Reflection 3: creating `test-ui` as an engineering tool

I asked the LLM to create a project-specific `test-ui` skill. The prompt required test cases to be kept in `test/ui-test-plan.md`, actual output to be compared exactly with expected output, cases to run in order, and execution to stop at the first failure. It also required a complete record of console input and output.

This was one of the most valuable uses of the LLM because it generated a repeatable process, not just a one-time test script. It made the test plan a shared contract between the requirements, implementation, and verification. For a command-line application, exact output matters: an extra space can change the displayed task description, and a missing line can make a command unclear to the user.

The skill found defects that were easy to miss during normal development. A leading-whitespace case showed that the command keyword was trimmed but the task description was extracted from the original untrimmed input. The program therefore stored `do spaced task` instead of `spaced task`. Another test showed that Java's default date resolver silently normalized `2026-02-30` instead of rejecting it. Both defects affected correctness and, in the date case, could have changed the user's data without permission.

There were also failures in the testing process itself. One shell harness sent the literal characters `\\n` rather than actual newlines. In another run, Java 17 was used even though the project required Java 25 because the environment was not configured correctly. These experiences made me more careful about test validity. A green result is not enough; I need to check the runtime, inputs, isolation of persistent data, expected-output format, and exit status.

The large reduction in bugs after introducing this workflow was therefore not because the LLM suddenly became perfect. It was because the development loop became more systematic. The skill made regressions visible earlier and made it harder to ignore negative cases.

## Reflection 4: handling ambiguity in date and time requirements

The date/time requirement included an example such as `deadline return book /by 2/12/2019 1800`, while the minimum requirement also described ISO-style input such as `2019-10-15`. These examples do not fully specify whether both formats must be accepted, how ambiguous numeric dates should be interpreted, or whether a date without a time means midnight.

The LLM converted deadline and event values from strings to `LocalDateTime`, updated display formatting, changed persistence, and added validation. This was a good example of AI connecting several parts of a requirement that are easy to overlook: the model, parser, user output, storage format, and tests all need to agree.

At the same time, the assistant initially had to make design choices that the prompt did not settle. The implementation eventually standardized on ISO dates and date-times because they were unambiguous and easier to test. That was a reasonable engineering decision, but it should have been stated explicitly before implementation. Otherwise, an LLM's assumption can quietly become part of the product specification.

The important lesson is to ask the LLM to identify ambiguity before writing code. A stronger prompt would have asked it to list the possible date formats, recommend one, and explain the effect on parsing, display, persistence, and backwards compatibility. This would have made the design decision deliberate rather than something discovered during testing.

## Reflection 5: AI-generated GUI assets and supporting work

AI was useful beyond Java code. When I asked it to implement the GUI, it also generated avatars and related interface pieces that fitted the HomeHub project well. This was impressive because I did not have to create every supporting asset manually before seeing a usable interface. It made the JavaFX work feel more complete and shortened the time between having a command-line prototype and having a recognizable application.

However, generated assets still need a design review. “Fits the project” is a human judgment involving consistency, readability, accessibility, and whether the asset distracts from the product. I treated the generated avatars as starting points and checked that they worked with the conversation layout and HomeHub's tone. This is similar to generated code: the value is high, but the developer remains responsible for integration and quality.


## Conclusion

AI-assisted software engineering made HomeHub faster to explore and improve. I spent one semester to implement a project like this but this time only took few days. I have also used AI for coding before, but using a project-specific skill such as `test-ui` showed me a more mature use of LLMs. The number of bugs dropped substantially once the workflow included structured testing.

The most important lesson is that prompting is part of engineering design, not just a way to ask for code. Good prompts made scope, assumptions, interfaces, and verification explicit. Still, the final responsibility stayed with me. I had to inspect the changes, question unexpected behavior, validate the test environment, and decide whether an AI suggestion actually improved HomeHub.
