package org.demo;

public class Test {

    // Bad Style (lowercase 'l')
    private static final long BAD_STYLE = 1000000l;  // Violation: lowercase 'l'

    // Good Style (uppercase 'L')
    private static final long GOOD_STYLE = 2000000L; // Correct: uppercase 'L'

    // Method with long literals
    public void testMethod() {
        long value1 = 42l; // Violation: lowercase 'l'
        long value2 = 123l; // Correct: uppercase 'L'
    }

    public static void main(String[] args) {
        // Outputting the values
        System.out.println("BAD_STYLE: " + BAD_STYLE);
        System.out.println("GOOD_STYLE: " + GOOD_STYLE);
    }
}
