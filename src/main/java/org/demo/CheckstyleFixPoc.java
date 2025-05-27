package org.demo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.demo.checkstyle.fixer.LowercaseLongLiteralFixer;
import org.demo.checkstyle.model.ViolationPosition;

/**
 * Main class for demonstrating Checkstyle violation fixing.
 * This program fixes lowercase 'l' violations in long literals at specified positions.
 */
public class CheckstyleFixPoc {
    private static final Logger LOGGER = Logger.getLogger(CheckstyleFixPoc.class.getName());
    private static final String DEFAULT_TEST_FILE = "src/main/java/org/demo/Test.java";

    public static void main(String[] args) {
        try {
            Path testFile = Paths.get(DEFAULT_TEST_FILE);
            LOGGER.info("Found test file at: " + testFile.toAbsolutePath());

            Set<ViolationPosition> violations = new HashSet<>();
            violations.add(new ViolationPosition(6, 50));
            violations.add(new ViolationPosition(13, 25));

            LowercaseLongLiteralFixer.fixLowercaseLongLiterals(testFile, violations);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while running CheckstyleFixPoc", e);
            System.exit(1);
        }
    }
}