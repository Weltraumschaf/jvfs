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

import de.weltraumschaf.jvfs.JvfsAssertions;
import de.weltraumschaf.jvfs.JvfsFileSystems;
import de.weltraumschaf.jvfs.JvfsObject;

/**
 * Immutable representation of a mount point.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsMountPoint implements Comparable<JvfsMountPoint> {

    /**
     * Directory separator as character.
     */
    private static final char DIR_SEP = JvfsFileSystems.DIR_SEP.charAt(0);
    /**
     * The mount point.
     *
     * Normalized by {@link #normalizePath(java.lang.String)}.
     */
    private final String path;

    /**
     * Dedicated constructor.
     *
     * @param path must not be {@code null} or empty
     */
    public JvfsMountPoint(final String path) {
        super();
        this.path = normalizePath(path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsMountPoint)) {
            return false;
        }

        final JvfsMountPoint other = (JvfsMountPoint) obj;
        return JvfsObject.equal(path, other.path);
    }

    @Override
    public int compareTo(final JvfsMountPoint other) {
        return path.compareTo(other.path);
    }

    /**
     * Add leading directory separator if missing and remove trailing one if present.
     *
     * @param path must not be {@code null} or empty
     * @return never {@code null} nor empty
     */
    static String normalizePath(final String path) {
        JvfsAssertions.notEmpty(path, "path");
        final StringBuilder buffer = new StringBuilder();

        if (DIR_SEP != path.charAt(0)) {
            buffer.append(DIR_SEP);
        }

        if (path.length() > 1 && DIR_SEP == path.charAt(path.length() - 1)) {
            buffer.append(path.substring(0, path.length() - 1));
        } else {
            buffer.append(path);
        }

        return buffer.toString();
    }

}
