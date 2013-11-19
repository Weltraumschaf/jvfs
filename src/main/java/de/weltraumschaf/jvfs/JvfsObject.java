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

package de.weltraumschaf.jvfs;

import java.util.Arrays;

/**
 * Helpers for objects.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public final class JvfsObject {

    /**
     * Hidden for pure static class.
     */
    private JvfsObject() {
        super();
        throw new UnsupportedOperationException();
    }

    /**
     * Handy method to calculate hash code.
     *
     * @param objects must not be {@code null}
     * @return any number
     */
    public static int hashCode(final Object ... objects) {
        JvfsAssertions.notNull(objects, "objects");
        return Arrays.hashCode(objects);
    }

    /**
     * Determines if two objects are {@link Object#equals(java.lang.Object) equal} with
     * respect of {@code null} values.
     *
     * @param a may be {@code null}
     * @param b may be {@code null}
     * @return {@code true} if a and b are equal, else {@code false}
     */
    public static boolean equal(final Object a, final Object b) {
        return a == b || a != null && a.equals(b);
    }

    /**
     * Determines if two objects are not {@link Object#equals(java.lang.Object) equal} with
     * respect of {@code null} values.
     *
     * @param a may be {@code null}
     * @param b may be {@code null}
     * @return {@code true} if a and b are not equal, else {@code false}
     */
    public static boolean notEqual(final Object a, final Object b) {
        return !equal(a, b);
    }
}
