# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

No build tool (Maven/Gradle). Compile and run manually from the source root:

```bash
cd src/main/java
javac lexer/*.java
java lexer.Main
```

Compiled `.class` files land alongside `.java` files — that's expected for this project.

## Architecture

Single package `lexer` with four classes:

- **`Lexer`** — stateful engine. Holds `source`, `pos` (cursor), and `line` counter. `tokenize()` is the main loop: dispatches to `readNumber()`, `readIdentifier()`, or `readString()` for multi-char tokens, or emits single-char tokens directly. Keywords are distinguished from identifiers inside `readIdentifier()` via `isKeyword()`.
- **`Token`** — Java record (`type`, `value`, `line`). Immutable, no logic.
- **`TokenType`** — enum of generic categories. Intended to be extended once the target language is defined.
- **`Main`** — thin driver; replace the hardcoded `code` string to test different inputs.

## Extending for a Specific Language

1. Add keywords to `Lexer.isKeyword()` (switch expression).
2. Add new token categories to `TokenType` if needed.
3. Add new dispatch branches in the `tokenize()` while-loop for language-specific constructs (e.g., comments, char literals, multi-char operators like `==`, `!=`).
