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
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Holds the file permissions.
 *
 * Based on a set of {@link PosixFilePermission}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
class JvfsFilePermissions {

    /**
     * Name to identify Posix file permission attribute.
     */
    private static final String NAME = "posix:permissions";

    /**
     * Holds the permissions.
     */
    private final Set<PosixFilePermission> permissions;

    /**
     * Dedicated constructor.
     *
     * @param permissions must not be {@code null}
     */
    public JvfsFilePermissions(final Set<PosixFilePermission> permissions) {
        super();
        JvfsAssertions.notNull(permissions, "permissions");
        this.permissions = permissions;
    }

    /**
     * Has owner read permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean ownerRead() {
        return permissions.contains(PosixFilePermission.OWNER_READ);
    }

    /**
     * Has owner write permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean ownerWrite() {
        return permissions.contains(PosixFilePermission.OWNER_WRITE);
    }

    /**
     * Has owner execute permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean ownerExecute() {
        return permissions.contains(PosixFilePermission.OWNER_EXECUTE);
    }

    /**
     * Has group read permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean groupRead() {
        return permissions.contains(PosixFilePermission.GROUP_READ);
    }

    /**
     * Has group write permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean groupWrite() {
        return permissions.contains(PosixFilePermission.GROUP_WRITE);
    }

    /**
     * Has group execute permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean groupExecute() {
        return permissions.contains(PosixFilePermission.GROUP_EXECUTE);
    }

    /**
     * Has others read permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean othersRead() {
        return permissions.contains(PosixFilePermission.OTHERS_READ);
    }

    /**
     * Has others write permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean othersWrite() {
        return permissions.contains(PosixFilePermission.OTHERS_WRITE);
    }

    /**
     * Has others execute permission.
     *
     * @return {@code true} if permitted, else {@code false}
     */
    boolean othersExecute() {
        return permissions.contains(PosixFilePermission.OTHERS_EXECUTE);
    }

    @Override
    public int hashCode() {
        return permissions.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsFilePermissions)) {
            return false;
        }

        final JvfsFilePermissions other = (JvfsFilePermissions) obj;
        return permissions.equals(other.permissions);
    }

    @Override
    public String toString() {
        return PosixFilePermissions.toString(permissions);
    }

    static JvfsFilePermissions forValue(final FileAttribute<?>... attributes) {
        for (final FileAttribute<?> attribute : attributes) {
            final String name = attribute.name();
            final Object value = attribute.value();

            if (NAME.equals(name) && value instanceof Set) {
                return new JvfsFilePermissions((Set) value);
            }
        }

        return null;
    }

}
