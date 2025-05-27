package org.demo.checkstyle.recipe;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.demo.checkstyle.model.ViolationPosition;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.marker.Range;

/**
 * A Recipe implementation that fixes lowercase 'l' in long literals at specific positions.
 * This uses OpenRewrite's visitor pattern to traverse and modify Java source code.
 */
public final class PositionalLiteralFixer extends Recipe {
    private static final Logger LOGGER = Logger.getLogger(PositionalLiteralFixer.class.getName());
    private static final Pattern LONG_LITERAL_PATTERN = Pattern.compile("\\d+l$");
    private final Set<ViolationPosition> violationPositions;

    /**
     * Creates a new PositionalLiteralFixer with the specified violation positions.
     *
     * @param violationPositions Set of positions where violations should be fixed
     * @throws IllegalArgumentException if violationPositions is null or empty
     */
    public PositionalLiteralFixer(Set<ViolationPosition> violationPositions) {
        if (violationPositions == null || violationPositions.isEmpty()) {
            throw new IllegalArgumentException("Violation positions cannot be null or empty");
        }
        this.violationPositions = new HashSet<>(violationPositions);
    }

    @Override
    public String getDisplayName() {
        return "Fix lowercase 'l' long literals at specific positions";
    }

    @Override
    public String getDescription() {
        return "Replaces lowercase 'l' with uppercase 'L' in long literals at specified line:column positions.";
    }

    @Override
    public JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {
            @Override
            public J.Literal visitLiteral(J.Literal literal, ExecutionContext ctx) {
                J.Literal l = super.visitLiteral(literal, ctx);
                String valueSource = l.getValueSource();

                if (valueSource != null && LONG_LITERAL_PATTERN.matcher(valueSource).matches()) {
                    Range range = l.getMarkers().findFirst(Range.class).orElse(null);
                    if (range == null) {
                        return l;
                    }

                    for (ViolationPosition violation : violationPositions) {
                        boolean isSameLine = violation.getLine() == range.getStart().getLine();
                        boolean isViolationAtEndOfLiteral = isSameLine &&
                            violation.getColumn() == range.getEnd().getColumn() - 1;

                        if (isViolationAtEndOfLiteral) {
                            LOGGER.info("Fixing violation at " + violation + " for literal: " + valueSource);
                            String fixedValueSource = valueSource.substring(0, valueSource.length() - 1) + "L";
                            return l.withValueSource(fixedValueSource);
                        }
                    }
                }
                return l;
            }
        };
    }
} 