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
import de.weltraumschaf.jvfs.JvfsOptions;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * In memory file system.
 *
 * This file system is by default registered for the {@literal jvfs://} protocol.
 * Inspired by implementation of {@literal sun.nio.fs.LinuxFileSystemProvider} and ZipFS demo.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemProvider extends FileSystemProvider {

    /**
     * Logging facility.
     */
    private static final Logger LOG = Logger.getLogger(JvfsFileSystemProvider.class);
    /**
     * Holds the file systems.
     */
    private final JvfsFileSystemTable fstab = new JvfsFileSystemTable();
    /**
     * If true a root file system will be mounted if one is requested and nothing is mounted ({@link #fstab} is empty).
     */
    private final boolean autoMount;

    /**
     * Dedicated constructor.
     */
    public JvfsFileSystemProvider() {
        this(true);
    }

    /**
     * Constructor with auto mount option.
     *
     * @param autoMount {@code true} if a root file system will be auto mounted on first access
     */
    public JvfsFileSystemProvider(final boolean autoMount) {
        super();
        this.autoMount = autoMount;
    }

    @Override
    public String getScheme() {
        return JvfsFileSystems.PROTOCOL_JVFS;
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IOException {
        LOG.debug("Create new file system for " + uri.toString());
        checkUri(uri);
        final JvfsMountPoint mountPount = new JvfsMountPoint(uri.getPath());
        final JvfsFileSystem fs = new JvfsFileSystem(this, JvfsOptions.forValue((Map<String, ?>) env));
        fstab.mount(mountPount, fs);
        return fs;
    }

    @Override
    public FileSystem getFileSystem(final URI uri) {
        LOG.debug("Geting file system for " + uri.toString());
        checkUri(uri);

        try {
            return fstab.get(new JvfsMountPoint(uri.getPath()));
        } catch (final FileSystemNotFoundException e) {
            if (!autoMount) {
                throw e;
            }
            try {
                return newFileSystem(uri, JvfsOptions.DEFAULT.getEnv());
            } catch (IOException ex) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Checks URI against some rules.
     *
     * Rules:
     * <ul>
     * <li>the URI scheme matches {@link #getScheme()}</li>
     * <li>the URI has no authority</li>
     * <li>the path is not {@literal null}</li>
     * <li>the path component is the root directory</li>
     * <li>the URI has no query part</li>
     * <li>the URI has no fragment</li>
     * </ul>
     *
     * Throws {@link IllegalArgumentException} if any rule is not complied.
     *
     * @param uri must not be {@literal null}
     */
    void checkUri(final URI uri) {
        assert null != uri : "uri must not be null";

        if (!uri.getScheme().equalsIgnoreCase(getScheme())) {
            throw new IllegalArgumentException("URI does not match this provider!");
        }

        if (uri.getAuthority() != null) {
            throw new IllegalArgumentException("Authority component present!");
        }

        if (uri.getPath() == null || uri.getPath().isEmpty()) {
            throw new IllegalArgumentException("Path component is undefined!");
        }

        if (uri.getQuery() != null) {
            throw new IllegalArgumentException("Query component present!");
        }

        if (uri.getFragment() != null) {
            throw new IllegalArgumentException("Fragment component present!");
        }
    }

    @Override
    public Path getPath(final URI uri) {
        return new JvfsPath(uri.getPath(), (JvfsFileSystem) getFileSystem(uri));
    }

    @Override
    public FileChannel newFileChannel(
        final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs) throws IOException {
        return toJvfsPath(path).newFileChannel(options, attrs);
    }

    @Override
    public SeekableByteChannel newByteChannel(
        final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs) throws IOException {
        return toJvfsPath(path).newByteChannel(options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(
        final Path dir,
        final DirectoryStream.Filter<? super Path> filter) throws IOException {
        return toJvfsPath(dir).newDirectoryStream(filter);
    }

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {
        toJvfsPath(dir).createDirectory(attrs);
    }

    @Override
    public void delete(final Path path) throws IOException {
        toJvfsPath(path).delete();
    }

    @Override
    public void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
        toJvfsPath(source).copy(toJvfsPath(target), options);
    }

    @Override
    public void move(final Path source, final Path target, final CopyOption... options) throws IOException {
        toJvfsPath(source).move(toJvfsPath(target), options);
    }

    @Override
    public boolean isSameFile(final Path path, final Path path2) throws IOException {
        return toJvfsPath(path).isSameFile(path2);
    }

    @Override
    public boolean isHidden(final Path path) throws IOException {
        return toJvfsPath(path).isHidden();
    }

    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        return toJvfsPath(path).getFileStore();
    }

    @Override
    public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
        toJvfsPath(path).checkAccess(modes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends FileAttributeView> V getFileAttributeView(
            final Path path,
            final Class<V> type,
            final LinkOption... options) {
        return (V) new JvfsFileAttributeView(toJvfsPath(path));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends BasicFileAttributes> A readAttributes(
            final Path path,
            final Class<A> type,
            final LinkOption... options) throws IOException {
        if (type == BasicFileAttributes.class) {
            return (A) toJvfsPath(path).getAttributes();
        }

        return null;
    }

    @Override
    public Map<String, Object> readAttributes(
            final Path path,
            final String attributes,
            final LinkOption... options) throws IOException {
        return toJvfsPath(path).readAttributes(attributes, options);
    }

    @Override
    public void setAttribute(
            final Path path,
            final String attribute,
            final Object value,
            final LinkOption... options) throws IOException {
        toJvfsPath(path).setAttribute(attribute, value, options);
    }

    /**
     * Casts given path to {@link JvfsPath}.
     *
     * Throws a {@link ProviderMismatchException} if given object is not instance of {@link JvfsPath}.
     *
     * @param obj must not be {@literal null}
     * @return never {@literal null}
     */
    static JvfsPath toJvfsPath(final Path obj) {
        JvfsAssertions.notNull(obj, "obj");

        if (!(obj instanceof JvfsPath)) {
            throw new ProviderMismatchException("Given path not of type " + JvfsPath.class.getSimpleName() + "!");
        }

        return (JvfsPath) obj;
    }

}
