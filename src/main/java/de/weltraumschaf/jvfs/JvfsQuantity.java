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

import java.util.Map;

/**
 * Abstracts the quantity a file or a file system may have in bytes.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public final class JvfsQuantity {

    /**
     * Quantity with size 0.
     */
    public static final JvfsQuantity EMPTY = new JvfsQuantity(0L);
    /**
     * Factor for magnitudes.
     */
    private static final long FACTOR = 1024L;
    /**
     * Caches instances by its string representation.
     */
    private static final Map<String, JvfsQuantity> CACHE_BY_STRING = JvfsCollections.newMap();
    /**
     * Caches instances by its value.
     */
    private static final Map<Long, JvfsQuantity> CACHE_BY_LONG = JvfsCollections.newMap();
    /**
     * Number of bytes.
     */
    private final long value;

    /**
     * Dedicated constructor.
     *
     * Use {@link #forValue(java.lang.String)} or {@link #forValue(long)} to get instances.
     *
     * @param value must not be negative
     */
    private JvfsQuantity(final long value) {
        super();
        assert value >= 0 : "value must not be negative";
        this.value = value;
    }

    /**
     * Get the number of bytes.
     *
     * @return non negative
     */
    public long value() {
        return value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsQuantity)) {
            return false;
        }

        final JvfsQuantity other = (JvfsQuantity) obj;
        return value == other.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Factory method to get instances by a string representation.
     *
     * The string representation is either a number (the bytes) or
     * a number an a letter for magnitudes:
     * <ul>
     * <li><kbd>k</kbd> or <kbd>K</kbd> for kilo</li>
     * <li><kbd>m</kbd> or <kbd>M</kbd> for mega</li>
     * <li><kbd>g</kbd> or <kbd>G</kbd> for giga</li>
     * </ul>
     *
     * This method caches instances and returns the same for the same input value.
     *
     * @param quantity must not be {@literal null} or empty
     * @return never {@literal null}
     */
    public static JvfsQuantity forValue(final String quantity) {
        JvfsAssertions.notNull(quantity, "quantity");
        final String trimmedQuantity = quantity.trim();
        JvfsAssertions.notEmpty(trimmedQuantity, "quantity");

        if (!CACHE_BY_STRING.containsKey(trimmedQuantity)) {
            CACHE_BY_STRING.put(trimmedQuantity, forValue(parseQuantity(trimmedQuantity)));
        }

        return CACHE_BY_STRING.get(trimmedQuantity);
    }

    /**
     * Factory method to get instances by number of bytes.
     *
     * This method caches instances and returns the same for the same input value.
     *
     * @param quantity must not be negative
     * @return never {@literal null}
     */
    public static JvfsQuantity forValue(final long quantity) {
        JvfsAssertions.greaterThanEqual(quantity, 0, "quantity");

        if (!CACHE_BY_LONG.containsKey(quantity)) {
            CACHE_BY_LONG.put(quantity, new JvfsQuantity(quantity));
        }

        return CACHE_BY_LONG.get(quantity);
    }

    /**
     * Parses string to long with respecting magnitudes ('k', 'K', 'm', 'M', "g', 'G').
     *
     * @param quantity must not be {@literal null} or empty
     * @return any long
     */
    static long parseQuantity(final String quantity) {
        JvfsAssertions.notNull(quantity, "quantity");
        final String trimmedQuantity = quantity.trim();
        JvfsAssertions.notEmpty(trimmedQuantity, "quantity");

        final char last = trimmedQuantity.toLowerCase().charAt(trimmedQuantity.length() - 1);
        final Magnitude factor = Magnitude.forValue(last);
        final String stringValue;

        if (factor == Magnitude.NONE) {
            stringValue = trimmedQuantity;
        } else {
            stringValue = trimmedQuantity.substring(0, trimmedQuantity.length() - 1);
        }

        final long base = Long.parseLong(stringValue);
        return base * factor.factor;
    }

    /**
     * Magnitudes of bytes.
     */
    private enum Magnitude {
        /**
         * Magnitude of factor 2^1.
         */
        NONE(' ', 1),
        /**
         * Magnitude kilo ('k') of factor 2^10 (1024).
         */
        KILO('k', FACTOR),
        /**
         * Magnitude mega ('m') of factor 2^20 (1048576).
         */
        MEGA('m', FACTOR * FACTOR),
        /**
         * Magnitude giga ('g') of factor 2^30 (1073741824).
         */
        GIGA('g', FACTOR * FACTOR * FACTOR);

        /**
         * The nornmalized (lower case) meta character.
         */
        private final char meta;
        /**
         * Concrete factor.
         */
        private final long factor;

        /**
         * Dedicated constructor.
         *
         * @param meta any character
         * @param factor must not be less than one
         */
        private Magnitude(final char meta, final long factor) {
            assert factor > 0 : "factor must be greater than 0";
            this.meta = meta;
            this.factor = factor;
        }

        /**
         * Finds the proper magnitude for a given meta character.
         *
         * If the character is not a valid meta character {@link #NONE} is returned.
         *
         * @param meta any character
         * @return never {@literal null}, as default {@link #NONE}
         */
        static Magnitude forValue(final char meta) {
            for (final Magnitude f : values()) {
                if (f.meta == meta) {
                    return f;
                }
            }

            return NONE;
        }
    }
}
