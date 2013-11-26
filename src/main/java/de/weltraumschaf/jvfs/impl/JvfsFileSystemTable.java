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

import de.weltraumschaf.jvfs.JvfsCollections;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.Map;

/**
 * Represents a file system table which maintains mount points and the associated file system.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemTable {

    private final Map<JvfsMountPoint, JvfsFileSystem> fstab = JvfsCollections.newConcurrentSortedMap();

    public void mount(final String path, final JvfsFileSystem fs) {
        final JvfsMountPoint mount = new JvfsMountPoint(path);

        if (fstab.containsKey(path)) {
            throw new FileSystemAlreadyExistsException(path);
        }
    }

    public void umount(final String path) {
        final JvfsMountPoint mount = new JvfsMountPoint(path);
    }

}
