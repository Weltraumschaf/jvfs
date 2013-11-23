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
     * Factor for magnitudes.
     */
    private static final long FACTOR = 1024L;
    /**
     * Caches instances by its string representation.
     */
    private static final Map<String, JvfsQuantity> CACHE_BY_STRING = JvfsCollections.newHashMap();
    /**
     * Caches instances by its value.
     */
    private static final Map<Long, JvfsQuantity> CACHE_BY_LONG = JvfsCollections.newHashMap();
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
     * @param quantity must not be {@code null} or empty
     * @return never {@code null}
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
     * @return never {@code null}
     */
    public static JvfsQuantity forValue(final long quantity) {
        JvfsAssertions.greaterThanEqual(quantity, 0, "quantity");

        if (!CACHE_BY_LONG.containsKey(quantity)) {
            CACHE_BY_LONG.put(quantity, new JvfsQuantity(quantity));
        }

        return CACHE_BY_LONG.get(quantity);
    }

    static long parseQuantity(final String quantity) {
        JvfsAssertions.notNull(quantity, "quantity");
        final String trimmedQuantity = quantity.trim();
        JvfsAssertions.notEmpty(trimmedQuantity, "quantity");

        final char last = trimmedQuantity.toLowerCase().charAt(trimmedQuantity.length() - 1);
        final Factor factor = Factor.forValue(last);
        final String stringValue;

        if (factor == Factor.NONE) {
            stringValue = trimmedQuantity;
        } else {
            stringValue = trimmedQuantity.substring(0, trimmedQuantity.length() - 1);
        }

        final long base = Long.parseLong(stringValue);
        return base * factor.factor;
    }

    private enum Factor {
        NONE(' ', 1),
        KILO('k', FACTOR),
        MEGA('m', FACTOR * FACTOR),
        GIGA('g', FACTOR * FACTOR * FACTOR);

        private final char meta;
        private final long factor;

        private Factor(final char meta, final long factor) {
            this.meta = meta;
            this.factor = factor;
        }

        static Factor forValue(final char meta) {
            for (final Factor f : values()) {
                if (f.meta == meta) {
                    return f;
                }
            }

            return NONE;
        }
    }
}
