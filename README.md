# Checkstyle Violation Fixer

A Java-based tool for automatically fixing Checkstyle violations in Java source files. Currently, it supports fixing lowercase 'l' in long literals at specific positions.

## Project Structure

The project is organized into the following modules:

```
src/main/java/org/demo/
├── CheckstyleFixPoc.java                 # Main entry point
└── checkstyle/
    ├── model/
    │   └── ViolationPosition.java        # Position representation
    ├── fixer/
    │   └── LowercaseLongLiteralFixer.java # Core fixing logic
    └── recipe/
        └── PositionalLiteralFixer.java   # OpenRewrite-based implementation
```

## Features

- Fixes lowercase 'l' in long literals (e.g., `1000000l` → `1000000L`)
- Supports fixing violations at specific line and column positions
- Provides both direct file manipulation and OpenRewrite-based implementations
- Includes comprehensive error handling and logging
- Validates input parameters and positions

## Usage

1. Build the project using Maven:
   ```bash
   mvn clean compile
   ```

2. Run the program:
   ```bash
   mvn exec:java
   ```

The program will automatically fix violations in the test file (`src/main/java/org/demo/Test.java`).

