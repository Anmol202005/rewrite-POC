package org.demo.checkstyle.fixer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.demo.checkstyle.model.ViolationPosition;

/**
 * Utility class for fixing lowercase 'l' violations in Java source files.
 * This class provides functionality to fix specific violations at given line and column positions.
 */
public final class LowercaseLongLiteralFixer {
    private static final Logger LOGGER = Logger.getLogger(LowercaseLongLiteralFixer.class.getName());

    private LowercaseLongLiteralFixer() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Fixes lowercase 'l' violations in a Java source file at specified positions.
     * The method reads the file, applies fixes at the given line and column positions,
     * and writes the changes back to the file if any fixes were made.
     *
     * @param filePath Path to the Java source file to fix
     * @param violationPositions Set of line and column positions where violations occur
     * @throws IllegalArgumentException if filePath is null or violationPositions is empty
     * @throws java.io.IOException if there are issues reading or writing the file
     */
    public static void fixLowercaseLongLiterals(Path filePath, Set<ViolationPosition> violationPositions) {
        validateInput(filePath, violationPositions);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File not found: " + filePath.toAbsolutePath());
        }

        boolean fixed = false;
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (ViolationPosition violation : violationPositions) {
                fixed |= fixViolationAtPosition(lines, violation);
            }
            
            if (fixed) {
                Files.write(filePath, lines);
                LOGGER.info("File successfully updated with fixes at specified positions.");
            } else {
                LOGGER.info("No violations fixed at specified positions.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while fixing lowercase long literals", e);
            throw new RuntimeException("Failed to fix violations", e);
        }
    }

    /**
     * Validates the input parameters for the fix operation.
     *
     * @param filePath The file path to validate
     * @param violationPositions The violation positions to validate
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInput(Path filePath, Set<ViolationPosition> violationPositions) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (violationPositions == null || violationPositions.isEmpty()) {
            throw new IllegalArgumentException("Violation positions cannot be null or empty");
        }
    }

    /**
     * Fixes a single violation at the specified position.
     *
     * @param lines The list of lines in the file
     * @param violation The violation position to fix
     * @return true if a fix was applied, false otherwise
     */
    private static boolean fixViolationAtPosition(List<String> lines, ViolationPosition violation) {
        int lineIdx = violation.getLine() - 1;
        int colIdx = violation.getColumn() - 1;
        
        if (lineIdx < 0 || lineIdx >= lines.size()) {
            LOGGER.warning("Invalid line number: " + violation.getLine());
            return false;
        }

        String line = lines.get(lineIdx);
        if (colIdx < 0 || colIdx >= line.length()) {
            LOGGER.warning("Invalid column number: " + violation.getColumn() + " for line " + violation.getLine());
            return false;
        }

        if (line.charAt(colIdx) == 'l') {
            lines.set(lineIdx, line.substring(0, colIdx) + 'L' + line.substring(colIdx + 1));
            LOGGER.info("Fixed lowercase 'l' at line " + violation.getLine() + ", column " + violation.getColumn());
            return true;
        }
        return false;
    }
} 