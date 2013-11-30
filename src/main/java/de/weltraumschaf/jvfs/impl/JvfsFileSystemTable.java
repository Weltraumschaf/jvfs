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
import de.weltraumschaf.jvfs.JvfsObject;
import java.io.IOException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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
    /**
     * Mount point of the root file system.
     */
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
        mount(new JvfsMountPoint(path), fs);
    }

    /**
     * Mounts a file system on a path.
     *
     * Given paths will be normalized according to {@link JvfsMountPoint#normalizePath(java.lang.String)}.
     *
     * @param path must not be {@code null}
     * @param fs must not be {@code null}
     */
    public void mount(final JvfsMountPoint path, final JvfsFileSystem fs) {
        JvfsAssertions.notNull(fs, "fs");

        if (fstab.containsKey(path) || fstab.containsValue(fs)) {
            throw new FileSystemAlreadyExistsException(path.toString());
        }

        if (path.isRootFileSystem()) {
            root = path;
        }

        fstab.put(path, fs);
    }

    /**
     * Get the file system associated with a mount point.
     *
     * Throws a {@link FileSystemNotFoundException} if the file system is not mounted.
     *
     * @param path must not be {@code null}
     * @return never {@code null}
     */
    public JvfsFileSystem get(final JvfsMountPoint path) {
        JvfsAssertions.notNull(path, "path");

        if (fstab.containsKey(path)) {
            return fstab.get(path);
        }

        throw new FileSystemNotFoundException(path.toString());
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

    /**
     * Find a mounted file system for given path.
     *
     * Throws an {@link IllegalStateException} if no mount point matches.
     *
     * @param path must not be {@code null} or empty
     * @return never {@code null}
     */
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

    String list() {
        final StringBuilder buffer = new StringBuilder();

        final Iterator<Map.Entry<JvfsMountPoint, JvfsFileSystem>> it = fstab.entrySet().iterator();

        while (it.hasNext()) {
            final Map.Entry<JvfsMountPoint, JvfsFileSystem> entry = it.next();
            buffer.append(entry.getKey().getPath());
            buffer.append("    ");
            buffer.append(entry.getValue().toString());
            buffer.append('\n');
        }

        return buffer.toString();
    }

    /**
     * Get the number of mounted file systems.
     *
     * @return non negative
     */
    int size() {
        return fstab.size();
    }

    @Override
    public int hashCode() {
        return JvfsObject.hashCode(fstab, root);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsFileSystemTable)) {
            return false;
        }

        final JvfsFileSystemTable other = (JvfsFileSystemTable) obj;
        return JvfsObject.equal(fstab, other.fstab) && JvfsObject.equal(root, other.root);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName() + "{");
        boolean first = true;
        final Iterator<Map.Entry<JvfsMountPoint, JvfsFileSystem>> it = fstab.entrySet().iterator();

        while (it.hasNext()) {
            final Map.Entry<JvfsMountPoint, JvfsFileSystem> entry = it.next();

            if (!first) {
                buffer.append(", ");
            }

            buffer.append(entry.getKey()).append("=").append(Objects.toString(entry.getValue()));
            first = false;
        }

        buffer.append(", root=").append(Objects.toString(root)).append('}');
        return buffer.toString();
    }


}
