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
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A provider implementation which is registered as default file system.
 *
 * Via {@link JvfsFileSystems} you can mount virtual in memory file systems.
 * This provider will dispatches all paths matching a mounted file system
 * to {@link JvfsFileSystemProvider}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsDefaultFileSystemProvider extends FileSystemProvider {

    /**
     * The default system provider.
     */
    private final FileSystemProvider parent;
    /**
     * Virtual file system.
     */
    private final FileSystemProvider jvfs;
    /**
     * Holds the mount points.
     */
    private final Map<String, FileSystem> fstab;

    /**
     * Constructor used by {@link FileSystems} to create default provider.
     *
     * @param parent is ignored at the moment, necessary that JVM default factory can instantiate
     */
    public JvfsDefaultFileSystemProvider(final FileSystemProvider parent) {
        this(parent, JvfsFileSystems.getInstance().getFstab(), new JvfsFileSystemProvider());
    }

    /**
     * Dedicated constructor.
     *
     * @param parent must not be {@literal null}
     * @param fstab must not be {@literal null}
     * @param jvfs must not be {@literal null}
     */
    JvfsDefaultFileSystemProvider(final FileSystemProvider parent, final Map<String, FileSystem> fstab, final FileSystemProvider jvfs) {
        super();
        JvfsAssertions.notNull(parent, "parent");
        this.parent = parent;
        JvfsAssertions.notNull(fstab, "fstab");
        this.fstab = fstab;
        JvfsAssertions.notNull(jvfs, "jvfs");
        this.jvfs = jvfs;
    }

    @Override
    public String getScheme() {
        return parent.getScheme();
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IOException {
        return parent.newFileSystem(uri, env);
    }

    @Override
    public FileSystem getFileSystem(final URI uri) {
        return parent.getFileSystem(uri);
    }

    @Override
    public Path getPath(final URI uri) {
        return parent.getPath(uri);
    }

    @Override
    public SeekableByteChannel newByteChannel(
        final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {

        if (isMounted(path)) {
            return jvfs.newByteChannel(translate(path), options, attrs);
        }

        return parent.newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(
        final Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {

        if (isMounted(dir)) {
            return jvfs.newDirectoryStream(translate(dir), filter);
        }

        return parent.newDirectoryStream(dir, filter);
    }

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {
        if (isMounted(dir)) {
            jvfs.createDirectory(translate(dir), attrs);
            return;
        }

        parent.createDirectory(dir, attrs);
    }

    @Override
    public void delete(final Path path) throws IOException {
        if (isMounted(path)) {
            jvfs.delete(translate(path));
            return;
        }

        parent.delete(path);
    }

    @Override
    public void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
        if (isMounted(source) && isMounted(target)) {
            jvfs.copy(translate(source), translate(target), options);
            return;
        } else if (!isMounted(source) && !isMounted(target)) {
            parent.copy(source, target, options);
            return;
        }

        throw new UnsupportedOperationException("Copy over different file systems not provided!");
    }

    @Override
    public void move(final Path source, final Path target, final CopyOption... options) throws IOException {
        if (isMounted(source) && isMounted(target)) {
            jvfs.move(translate(source), translate(target), options);
            return;
        } else if (!isMounted(source) && !isMounted(target)) {
            parent.move(source, target, options);
            return;
        }

        throw new UnsupportedOperationException("Move over different file systems not provided!");
    }

    @Override
    public boolean isSameFile(final Path path, final Path path2) throws IOException {
        if (isMounted(path) && isMounted(path)) {
            return jvfs.isSameFile(translate(path), translate(path));
        } else if (!isMounted(path) && !isMounted(path)) {
            return parent.isSameFile(path, path);
        }

        throw new UnsupportedOperationException("Is same file check over different file systems not provided!");
    }

    @Override
    public boolean isHidden(final Path path) throws IOException {
        if (isMounted(path)) {
            return jvfs.isHidden(translate(path));
        }

        return parent.isHidden(path);
    }

    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        if (isMounted(path)) {
            return jvfs.getFileStore(translate(path));
        }

        return parent.getFileStore(path);
    }

    @Override
    public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
        if (isMounted(path)) {
            jvfs.checkAccess(translate(path), modes);
        }

        parent.checkAccess(path, modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(
        final Path path, final Class<V> type, final LinkOption... options) {
        if (isMounted(path)) {
            return jvfs.getFileAttributeView(translate(path), type, options);
        }

        return parent.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(
        final Path path, final Class<A> type, final LinkOption... options) throws IOException {
        if (isMounted(path)) {
            return jvfs.readAttributes(translate(path), type, options);
        }

        return parent.readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(
        final Path path, final String attributes, final LinkOption... options) throws IOException {
        if (isMounted(path)) {
            return jvfs.readAttributes(translate(path), attributes, options);
        }

        return parent.readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(
        final Path path, final String attribute, final Object value, final LinkOption... options) throws IOException {
        if (isMounted(path)) {
            jvfs.setAttribute(translate(path), attribute, value, options);
        }

        parent.setAttribute(path, attribute, value, options);
    }

    /**
     * Checks if the given path is in a mounted directory.
     *
     * @param path must not be {@literal null}
     * @return {@literal true} if the path starts with a mount point, else {@literal false}
     */
    boolean isMounted(final Path path) {
        assert path != null : "path must be defined";

        if (fstab.isEmpty()) {
            return false;
        }

        final String pathName = path.toString();
        final Iterator<Map.Entry<String, FileSystem>>  it = fstab.entrySet().iterator();

        while (it.hasNext()) {
            if (pathName.startsWith(it.next().getKey())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Translates a path to a JVFS path.
     *
     * @param path must not be {@code null}
     * @return never {@code null}
     */
    Path translate(final Path path) {
        return Paths.get(JvfsFileSystems.createUri(path.toString()));
    }

}
