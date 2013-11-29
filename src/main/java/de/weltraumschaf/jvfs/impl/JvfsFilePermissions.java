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

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
class JvfsFilePermissions {

    private final Set<PosixFilePermission> permissions;

    public JvfsFilePermissions(final Set<PosixFilePermission> permissions) {
        super();
        this.permissions = permissions;
    }

    boolean ownerRead() {
        return permissions.contains(PosixFilePermission.OWNER_READ);
    }

    boolean ownerWrite() {
        return permissions.contains(PosixFilePermission.OWNER_WRITE);
    }

    boolean ownerExecute() {
        return permissions.contains(PosixFilePermission.OWNER_EXECUTE);
    }

    boolean groupRead() {
        return permissions.contains(PosixFilePermission.GROUP_READ);
    }

    boolean groupWrite() {
        return permissions.contains(PosixFilePermission.GROUP_WRITE);
    }

    boolean groupExecute() {
        return permissions.contains(PosixFilePermission.GROUP_EXECUTE);
    }

    boolean othersRead() {
        return permissions.contains(PosixFilePermission.OTHERS_READ);
    }

    boolean othersWrite() {
        return permissions.contains(PosixFilePermission.OTHERS_WRITE);
    }

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
        return permissions.equals(other);
    }

    @Override
    public String toString() {
        return permissions.toString();
    }

    static JvfsFilePermissions forValue(final FileAttribute<?>... attributes) {
        for (final FileAttribute<?> attribute : attributes) {
            if (attribute instanceof Set) {
                final Set permissions = (Set) attribute;

                if (!permissions.isEmpty() && permissions.toArray()[0] instanceof PosixFilePermission) {
                    return new JvfsFilePermissions(permissions);
                }
            }
        }
        return null;
    }
}
