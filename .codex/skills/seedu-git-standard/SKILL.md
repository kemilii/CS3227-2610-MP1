---
name: seedu-git-standard
description: "Apply the SE-EDU Git conventions when creating or reviewing commits and branch names in this project."
---

# Seedu Git Standard

Apply these rules to every commit and branch name in this repository. Use the
SE-EDU [Git conventions](https://se-education.org/guides/conventions/git.html)
as the authoritative source.

## Commit messages

- Write a clear subject line in the imperative mood, capitalize its first
  letter, and do not end it with a period. Keep it preferably within 50
  characters and never over 72 characters. Add a short scope or category only
  when it improves clarity.
- For every non-trivial commit, separate the subject and body with a blank line.
  Wrap the body at 72 characters, use blank lines between paragraphs when
  helpful, and explain what changed and why. Do not spend the body describing
  implementation steps that the diff already shows.
- Structure a non-trivial body around the present situation, why it needs to
  change, what to do, why that approach is appropriate, and any relevant
  additional context. Use bullets when they make the rationale clearer.

Before proposing or creating a commit, inspect the staged diff and ensure the
message accurately describes the complete change. Do not commit or push unless
the user explicitly asks for it.

## Branch names

Use meaningful kebab-case names made from relevant keywords. For issue-related
branches, use `<issueNumber>-<keywords-from-issue-title>`.
