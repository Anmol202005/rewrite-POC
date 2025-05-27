package org.demo.checkstyle.model;

/**
 * Represents a position (line and column) of a Checkstyle violation in a source file.
 * Line and column numbers are 1-based (first line/column is 1).
 */
public final class ViolationPosition {
    private final int line;
    private final int column;

    /**
     * Creates a new violation position.
     *
     * @param line The line number (1-based)
     * @param column The column number (1-based)
     * @throws IllegalArgumentException if line or column is less than 1
     */
    public ViolationPosition(int line, int column) {
        if (line < 1) {
            throw new IllegalArgumentException("Line number must be positive");
        }
        if (column < 1) {
            throw new IllegalArgumentException("Column number must be positive");
        }
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ViolationPosition that = (ViolationPosition) obj;
        return line == that.line && column == that.column;
    }

    @Override
    public int hashCode() {
        return 31 * line + column;
    }

    @Override
    public String toString() {
        return String.format("ViolationPosition[line=%d, column=%d]", line, column);
    }
} 