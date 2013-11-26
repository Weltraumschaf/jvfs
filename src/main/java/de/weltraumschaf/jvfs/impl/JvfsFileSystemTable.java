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
import de.weltraumschaf.jvfs.JvfsCollections;
import java.io.IOException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Represents a file system table which maintains mount points and the associated file system.
 *
 * The class is thread safe.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsFileSystemTable {

    /**
     * Logging facility.
     */
    private static final Logger LOG = Logger.getLogger(JvfsFileSystemTable.class);
    /**
     * Holds the mount points with their file system.
     */
    private final Map<JvfsMountPoint, JvfsFileSystem> fstab = JvfsCollections.newConcurrentSortedMap();
    private JvfsMountPoint root;

    /**
     * Mounts a file system on a path.
     *
     * Given paths will be normalized according to {@link JvfsMountPoint#normalizePath(java.lang.String)}.
     *
     * @param path must not be {@code null} or empty
     * @param fs must not be {@code null}
     */
    public void mount(final String path, final JvfsFileSystem fs) {
        final JvfsMountPoint mount = new JvfsMountPoint(path);
        JvfsAssertions.notNull(fs, "fs");

        if (fstab.containsKey(mount) || fstab.containsValue(fs)) {
            throw new FileSystemAlreadyExistsException(path);
        }

        if (mount.isRootFileSystem()) {
            root = mount;
        }

        fstab.put(mount, fs);
    }

    /**
     * Unmounts a file system on a path.
     *
     * Given paths will be normalized according to {@link JvfsMountPoint#normalizePath(java.lang.String)}.
     *
     * @param path must not be {@code null} or empty
     */
    public void umount(final String path) {
        final JvfsFileSystem fs = fstab.remove(new JvfsMountPoint(path));

        if (null != fs) {
            try {
                fs.close();
            } catch (IOException e) {
                LOG.error("Can't unmount fs correct " + path, e);
            }
        }
    }

    public JvfsFileSystem findMountedFilesystem(final String path) {
        JvfsAssertions.notEmpty(path, "path");
        final String normalizedPath = JvfsMountPoint.normalizePath(path);

        if (fstab.isEmpty()) {
            throw new IllegalStateException("No file system mounted!");
        }

        final Iterator<Map.Entry<JvfsMountPoint, JvfsFileSystem>> it = fstab.entrySet().iterator();

        while (it.hasNext()) {
            final Map.Entry<JvfsMountPoint, JvfsFileSystem> entry = it.next();
            final String mountPoint = entry.getKey().getPath();

            if (normalizedPath.startsWith(mountPoint)) {
                return entry.getValue();
            }
        }

        if (root != null) {
            return fstab.get(root);
        }

        throw new IllegalStateException("No file system mounted for " + path);
    }

    /**
     * Get the number of mounted file systems.
     *
     * @return non negative
     */
    int size() {
        return fstab.size();
    }

}
