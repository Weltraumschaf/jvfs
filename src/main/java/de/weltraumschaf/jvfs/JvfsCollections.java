/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.weltraumschaf.jvfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class which infers generic types for collections.
 *
 * @author Sven.Strittmatter
 */
public final class JvfsCollections {

    /**
     * Default size for lists.
     */
    private static final int DEFAULT_LIST_SIZE = 10;

    /**
     * Hidden for pure static factory.
     */
    private JvfsCollections() {
        super();
        throw new UnsupportedOperationException(); // Avoid reflective instantiation.
    }

    /**
     * Create new array list with {@link #DEFAULT_LIST_SIZE size}.
     *
     * @param <T> type of list entries
     * @return never {@literal null}, always new instance
     */
    public static <T> List<T> newList() {
        return newList(DEFAULT_LIST_SIZE);
    }

    /**
     * Create new array list with given size.
     *
     * @param <T> type of list entries
     * @param size must be non negative
     * @return never {@literal null}, always new instance
     */
    public static <T> List<T> newList(final int size) {
        JvfsAssertions.greaterThan(size, -1, "size");
        return new ArrayList<T>(size);
    }

    /**
     * Creates a copy of given list.
     *
     * @param <T> type of list entries
     * @param original must not be {@literal null}
     * @return never {@literal null}
     */
    public static <T> List<T> newList(final List<T> original) {
        JvfsAssertions.notNull(original, "original");
        return new ArrayList<T>(original);
    }

    /**
     * Creates new hash map.
     *
     * @param <K> type of map keys
     * @param <V> type of map values
     * @return never {@literal null}, always new instance
     */
    public static <K, V> Map<K, V> newMap() {
        return new HashMap<K, V>();
    }

    /**
     * Creates new concurrent map.
     *
     * @param <K> type of map keys
     * @param <V> type of map values
     * @return never {@literal null}, always new instance
     */
    public static <K, V> Map<K, V> newConcurrentSortedMap() {
        return new ConcurrentHashMap<K, V>();
    }

    /**
     * Creates new hash set.
     *
     * @param <T> type of set entries
     * @return never {@literal null}, always new instance
     */
    public static <T> Set<T> newSet() {
        return new HashSet<T>();
    }

    /**
     * Converts given varargs into a list.
     *
     * @param <T> type of set entries
     * @param a variable arguments or array
     * @return never {@literal null}
     */
    @SafeVarargs
    public static <T> List<T> asList(T... a) {
        return Arrays.asList(a);
    }
}
