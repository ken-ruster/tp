# Project Portfolio Page

By Antriksh Dhand (@antrikshdhand)

## Overview

I primarily focused on enhancing the gameplay experience and ensuring the robustness of the codebase. I implemented new key commands, refactored existing code for an improved object-oriented design, and explored end-to-end testing strategies.

## Contributions

### Code contributed

Check out my contributions on the [TP Dashboard](https://nus-cs2113-ay2324s1.github.io/tp-dashboard/?search=antrikshdhhttps://nus-cs2113-ay2324s1.github.io/tp-dashboard/?search=antrikshdhand&breakdown=truehttps://nus-cs2113-ay2324s1.github.io/tp-dashboard/?search=antrikshdhand&breakdown=truehttps://nus-cs2113-ay2324s1.github.io/tp-dashboard/?search=antrikshdhand&breakdown=trueand&breakdown=true).

### Enhancements implemented

#### `captured`, `history`, and `stepback` Commands

I implemented the `captured`, `history`, and `stepback` commands, allowing the player access to commands they would generally expect to see in a modern chess game. The `stepback` command posed a unique challenge, requiring a deep dive into move reversal mechanisms. This effort involved significant code refactoring to promote cleaner, more object-oriented structures.

#### Object-Oriented Design Refactoring

To enhance code maintainability and extensibility, I introduced sub-classes such as `CastleMove` and `EnPassantMove`. Additionally, the implementation of the `MoveFactory` class, adopting the Factory method pattern, has streamlined the creation of Move objects. This in turn fed into making the reversal of moves much more cleaner to implement and more robust overall. I also encouraged and administered the use of SLAP in our codebase.

### Contributions to the User Guide (UG)

While not introducing new major sections, I diligently maintained the UG by documenting my new commands and ensuring that information stays relevant and up to date.

### Contributions to the Developer Guide (DG)

#### End-to-End Testing Section

I made substantial progress in establishing end-to-end testing for the CLI. Despite not achieving full completion, I laid the groundwork and documented the process in the DG, providing a valuable resource for future testing efforts. You can see a stub of this code in the DG.

### General Contributions

Regularly contributed to team discussions, providing insights and assistance where needed.

Actively participated in code reviews and offered constructive feedback to enhance overall code quality.