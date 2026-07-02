# Grindstone

## Overview

Grindstone is a framework designed to simplify the development of session-based games by separating game logic from presentation and user interaction concerns. It is designed to improve testability by isolating game rules from user interfaces. It also reduces repetitive code across game projects by providing a consistent structure for implementing game logic. As a result, developers can focus more on what makes each game unique: its mechanics and rules.

The framework is intended for games that have a clearly defined beginning and end. Examples include Tic-Tac-Toe, Chess, or Checkers. Persistent worlds or continuously running games, such as Clash of Clans itself, are outside the scope of the framework. However, a Clash of Clans battle could be implemented because it represents a self-contained game with a start and an end.

The primary goal of Grindstone is to allow developers to focus exclusively on implementing game rules and mechanics. Concepts such as user interfaces, rendering, networking, persistence, and platform-specific behavior are intentionally left to external engines. Because of that, games created with Grindstone are not directly playable on their own. Instead, they act as abstract game implementations that can be integrated into different engines. This approach enables the same game logic to be reused across multiple platforms and user interfaces.

For example, a Tic-Tac-Toe implementation created with Grindstone only defines the game rules, board state, player turns, and win conditions. An engine is responsible for deciding how the board is presented to players. A web-based engine may render the board using HTML, CSS, and JavaScript, while a command-line engine may display the board using plain text characters.
