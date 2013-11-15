/*
 *  LICENSE
 *
 * "THE BEER-WARE LICENSE" (Revision 43):
 * "Sven Strittmatter" <weltraumschaf@googlemail.com> wrote this file.
 * As long as you retain this notice you can do whatever you want with
 * this stuff. If we meet some day, and you think this stuff is worth it,
 * you can buy me a non alcohol-free beer in return.
 *
 * Copyright (C) 2012 "Sven Strittmatter" <weltraumschaf@googlemail.com>
 */
package de.weltraumschaf.jvfs.impl;

/**
 * Static methods to assert values.
 *
 * If the assertion is not satisfied exceptions will be thrown.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsAssertions {

    /**
     * Format string of error message for {@link #notNull(java.lang.Object, java.lang.String)}.
     */
    private static final String NOT_NULL_MESSAGE = "Parameter '%s' must not be null!";
    /**
     * Format string of error message for {@link #lessThan(int, int, java.lang.String)}.
     */
    private static final String LESS_THAN_MESSAGE = "Paramater '%s' must be less than '%d'!";
    /**
     * Format string of error message for {@link #lessThanEqual(int, int, java.lang.String)}.
     */
    private static final String LESS_THAN_EQUAL_MESSAGE = "Paramater '%s' must be less than or equal '%d'!";
    /**
     * Format string of error message for {@link #greaterThan(int, int, java.lang.String)}.
     */
    private static final String GREATER_THAN_MESSAGE = "Paramater '%s' must be greater than '%d'!";
    /**
     * Format string of error message for {@link #greaterThanEqual(int, int, java.lang.String)}
     * and {@link #greaterThanEqual(long, long, java.lang.String)}.
     */
    private static final String GREATER_THAN_EQUAL_MESSAGE = "Paramater '%s' must be greater than or equal '%d'!";
    /**
     * Format string of error message for {@link #isEqual(java.lang.Object, java.lang.Object, java.lang.String)}.
     */
    private static final String EQUAL_MESSAGE = "Parameter '%s' is not equal to '%s'!";

    /**
     * Hidden for pure static class.
     */
    private JvfsAssertions() {
        super();
        throw new UnsupportedOperationException(); // Avoid reflective instantiation.
    }

    /**
     * Validates that the name is not {@code null} or empty.
     *
     * Throws {@link NullPointerException} if {@code null} or
     * {@link IllegalArgumentException} if empty.
     *
     * @param name may be {@code null}
     */
    private static void validateName(final String name) {
        if (null == name) {
            throw new NullPointerException("Argument name must not be null!");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Argument name must not be empty!");
        }
    }
    /**
     * Asserts that given object is not {@literal null}.
     *
     * Throws{@link NullPointerException} if given object is {@literal nul}.
     *
     * @param o checked if {@literal null}
     * @param name name of checked parameter for error message
     */
    static void notNull(final Object o, final String name) {
        validateName(name);

        if (null == o) {
            throw new NullPointerException(String.format(NOT_NULL_MESSAGE, name));
        }
    }

    static void lessThan(final int checked, final int reference, final String name) {
        validateName(name);

        if (checked >= reference) {
            throw new IllegalArgumentException(String.format(LESS_THAN_MESSAGE, name, reference));
        }
    }

    static void lessThanEqual(int checked, int reference, String name) {
        validateName(name);

        if (checked > reference) {
            throw new IllegalArgumentException(String.format(LESS_THAN_EQUAL_MESSAGE, name, reference));
        }
    }

    static void greaterThan(final int checked, final int reference, final String name) {
        validateName(name);

        if (checked <= reference) {
            throw new IllegalArgumentException(String.format(GREATER_THAN_MESSAGE, name, reference));
        }
    }

    static void greaterThanEqual(final int checked, final int reference, final String name) {
        validateName(name);
        greaterThanEqual((long) checked, (long) reference, name);
    }

    static void greaterThanEqual(final long checked, final long reference, final String name) {
        validateName(name);

        if (checked < reference) {
            throw new IllegalArgumentException(String.format(GREATER_THAN_EQUAL_MESSAGE, name, reference));
        }
    }

    static void isEqual(final Object actual, final Object expected, final String name) {
        validateName(name);

        if (notEqual(actual, expected)) {
            throw new IllegalArgumentException(String.format(EQUAL_MESSAGE, name, expected));
        }
    }

    static boolean equal(final Object a, final Object b) {
        return a == b || (a != null && a.equals(b));
    }

    static boolean notEqual(final Object a, final Object b) {
        return !equal(a, b);
    }
}