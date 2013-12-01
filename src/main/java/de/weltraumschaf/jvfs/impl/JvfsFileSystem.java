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
import de.weltraumschaf.jvfs.JvfsFileSystems;
import de.weltraumschaf.jvfs.JvfsOptions;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Maintains the file system specific hierarchy.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
class JvfsFileSystem extends FileSystem {

    /**
     * Supported views.
     */
    private static final Set<String> SUPPORTED_ATTRIBUTE_VIEWS = JvfsCollections.newSet();

    static {
        SUPPORTED_ATTRIBUTE_VIEWS.add(JvfsFileAttributeView.BASIC_VIEW_NAME);
    }
    /**
     * Provider which created this {@link JvfsFileSystemProvider}.
     */
    private final JvfsFileSystemProvider provider;
    /**
     * Organizes the file hierarchy.
     *
     * The key is the absolute pathname of the file ({@link JvfsFileEntry#path}).
     */
    private final Map<String, JvfsFileEntry> attic = JvfsCollections.newMap();
    /**
     * List of file stores.
     */
    private final List<FileStore> fileStores;
    /**
     * Whether or not this FS is open.
     *
     * Volatile as we don't need compound operations and thus don't need full sync.
     */
    private volatile boolean open;

    /**
     * Dedicated constructor.
     *
     * @param provider must not be {@literal null}
     * @param options must not be {@literal null}
     */
    JvfsFileSystem(final JvfsFileSystemProvider provider, final JvfsOptions options) {
        super();
        JvfsAssertions.notNull(provider, "provider");
        JvfsAssertions.notNull(options, "options");
        this.provider = provider;
        this.open = true;
        final FileStore store = new JvfsFileStore(options, this);
        final List<FileStore> stores = JvfsCollections.newList(1);
        stores.add(store);
        this.fileStores = Collections.unmodifiableList(stores);
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        this.open = false;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public boolean isReadOnly() {
        return getFileStore().isReadOnly();
    }

    @Override
    public String getSeparator() {
        return JvfsFileSystems.DIR_SEP;
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        this.checkClosed();
        return JvfsCollections.<Path>asList(new JvfsPath(this));
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        this.checkClosed();
        return fileStores;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        this.checkClosed();
        return SUPPORTED_ATTRIBUTE_VIEWS;
    }

    @Override
    public Path getPath(final String first, final String... more) {
        this.checkClosed();

        if (!JvfsPathUtil.isValid(first) || !JvfsPathUtil.isValid(more)) {
            throw new InvalidPathException(first, "Invalid input!");
        }

        JvfsAssertions.notNull(first, "first");
        final String merged = this.merge(first, more);
        return new JvfsPath(merged, this);
    }

    /**
     * Merges the path context with a varargs String sub-contexts, returning the result.
     *
     * @param first must not be {@literal null}
     * @param more must not be {@literal null}
     * @return never {@literal null}
     */
    private String merge(final String first, final String... more) {
        assert first != null : "first must be specified";
        assert more != null : "more must be specified";

        final StringBuilder merged = new StringBuilder();
        merged.append(first);

        for (final String name : more) {
            merged.append(JvfsFileSystems.DIR_SEP);
            merged.append(name);
        }

        return merged.toString();
    }

    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern) {
        return JvfsPathMatcher.newMatcher(syntaxAndPattern);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("JVFS archives do not support a watch services!");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{'
                + "fileStores=" + Objects.toString(fileStores)
                + '}';
    }

    /**
     * Checks if the {@link JvfsFileSystem} is closed, and throws a {@link ClosedFileSystemException} if so.
     */
    private void checkClosed() {
        if (!this.isOpen()) {
            throw new ClosedFileSystemException();
        }
    }

    /**
     * Throws {@link NoSuchFileException} if file does not exist.
     *
     * @param path must not be {@literal null} or empty
     * @throws NoSuchFileException if file does not exists
     */
    private void assertFileExists(final String path) throws NoSuchFileException {
        if (!contains(path)) {
            throw new NoSuchFileException(path);
        }
    }

    /**
     * Get a file entry.
     *
     * @param path must not be {@literal null} or empty
     * @return may be {@literal null} if not exists
     */
    JvfsFileEntry get(final String path) {
        JvfsAssertions.notEmpty(path, "path");
        checkClosed();
        return attic.get(path);
    }

    /**
     * Add a file entry.
     *
     * @param entry must not be {@literal null}
     */
    void add(final JvfsFileEntry entry) {
        JvfsAssertions.notNull(entry, "entry");
        JvfsFileEntry previous = null;

        if (!attic.containsKey(JvfsFileSystems.DIR_SEP)) {
            final JvfsFileEntry root = JvfsFileEntry.newDir(JvfsFileSystems.DIR_SEP);
            root.setPermissions(entry.getPermissions());
            previous = root;
            attic.put(JvfsFileSystems.DIR_SEP, root);
        }

        final StringBuilder buffer = new StringBuilder();
        final List<String> names = JvfsPathUtil.tokenize(entry.getPath());
        names.remove(names.size() - 1);

        for (final String name : names) {
            buffer.append(JvfsFileSystems.DIR_SEP).append(name);

            if (!attic.containsKey(buffer.toString())) {
                final JvfsFileEntry dir = JvfsFileEntry.newDir(buffer.toString());
                dir.setPermissions(entry.getPermissions());
                dir.setParent(previous);
                previous = dir;
                attic.put(buffer.toString(), dir);
            }
        }

        entry.setParent(previous);
        attic.put(entry.getPath(), entry);
    }

    /**
     * Whether the file system contains a file entry.
     *
     * @param path must not be {@literal null} or empty
     * @return {@literal true} if entry exists, else {@literal false}
     */
    boolean contains(final String path) {
        JvfsAssertions.notEmpty(path, "path");
        checkClosed();
        return attic.containsKey(path);
    }

    /**
     * Create new file channel.
     *
     * @param path must not be {@literal null} or empty
     * @param options options specifying how the file is opened
     * @param attrs an optional list of file attributes to set atomically when creating the file
     * @return never {@literal null}
     * @throws IOException if path does not exist
     */
    FileChannel newFileChannel(
            final String path,
            final Set<? extends OpenOption> options,
            final FileAttribute<?>... attrs) throws IOException {
        JvfsAssertions.notEmpty(path, "path");
        // XXX: All checks done by newByteChannel.
        checkClosed();
        final boolean forWrite = options.contains(StandardOpenOption.WRITE)
                || options.contains(StandardOpenOption.APPEND);

        if (forWrite) {
            if (isReadOnly()) {
                throw new ReadOnlyFileSystemException();
            }

            if (contains(path)) {
                if (options.contains(StandardOpenOption.CREATE_NEW)) {
                    throw new FileAlreadyExistsException(path);
                }

                if (get(path).isDirectory()) {
                    throw new FileAlreadyExistsException("directory <" + path + "> exists");
                }
            } else {
                if (!options.contains(StandardOpenOption.CREATE_NEW)) {
                    throw new NoSuchFileException(path);
                }
            }
        } else {
            if (contains(path)) {
                if (get(path).isDirectory()) {
                    throw new NoSuchFileException(path);
                }
            } else {
                throw new NoSuchFileException(path);
            }
        }

        return new JvfsFileChannel(newByteChannel(path, options, attrs));
    }

    /**
     * Create a new byte channel.
     *
     * @param path must not be {@literal null} or empty
     * @param options options specifying how the file is opened
     * @param attrs an optional list of file attributes to set atomically when creating the file
     * @return never {@literal null}
     * @throws IOException on any I/O error
     */
    SeekableByteChannel newByteChannel(
            final String path,
            final Set<? extends OpenOption> options,
            final FileAttribute<?>... attrs) throws IOException {
        checkClosed();

        // Writing?
        if (options.contains(StandardOpenOption.CREATE)
                || options.contains(StandardOpenOption.CREATE_NEW)
                || options.contains(StandardOpenOption.WRITE)) {
            if (contains(path)) {
                if (options.contains(StandardOpenOption.WRITE)) {
                    final JvfsFileEntry entry = get(path);
                    final JvfsSeekableByteChannel channel = new JvfsSeekableByteChannel(entry);

                    if (options.contains(StandardOpenOption.APPEND)) {
                        channel.position(channel.size());
                    }

                    return channel;
                } else {
                    throw new FileAlreadyExistsException(path);
                }
            } else {
                final JvfsFileEntry entry = JvfsFileEntry.newFile(path);
                entry.setPermissions(JvfsFilePermissions.forValue(attrs));
                add(entry);
                return new JvfsSeekableByteChannel(entry);
            }
        }

        if (contains(path)) {
            return new JvfsSeekableByteChannel(get(path));
        }

        throw new NoSuchFileException(path);
    }

    /**
     * check file permissions.
     *
     * @param path must not be {@literal null} or empty
     * @param modes The access modes to check; may have zero elements
     * @throws IOException if path does not exist
     */
    void checkAccess(final String path, final AccessMode... modes) throws IOException {
        checkClosed();
        assertFileExists(path);

        boolean r = false;
        boolean w = false;
        boolean x = false;

        for (AccessMode mode : modes) {
            switch (mode) {
                case READ:
                    r = true;
                    break;
                case WRITE:
                    w = true;
                    break;
                case EXECUTE:
                    x = true;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        final JvfsFileEntry entry = get(path);

        if (r && !entry.isReadable()) {
            throw new AccessDeniedException(path);
        }

        if (w && (isReadOnly() || !entry.isWritable())) {
            throw new AccessDeniedException(path);
        }

        if (x && !entry.isExecutable()) {
            throw new AccessDeniedException(path);
        }
    }

    /**
     * Create a directory.
     *
     * @param path must not be {@literal null} or empty
     * @param attrs an optional list of file attributes to set atomically when creating the directory
     * @throws IOException if path does not exist
     */
    void createDirectory(final String path, final FileAttribute<?>... attrs) throws IOException {
        checkClosed();
        final JvfsFileEntry directory = JvfsFileEntry.newDir(path);
        directory.setPermissions(JvfsFilePermissions.forValue(attrs));
        add(directory);
    }

    /**
     * Deletes a file.
     *
     * @param path must not be {@literal null} or empty
     * @throws IOException if path does not exist
     */
    void delete(final String path) throws IOException {
        checkClosed();
        assertFileExists(path);
        final JvfsFileEntry entry = get(path);

        //CHECKSTYLE:OFF
        if (entry.isDirectory() && false) {
            //CHECKSTYLE:ON
            // TODO Implement check if empty.
            throw new DirectoryNotEmptyException(path);
        }

        attic.remove(path);
    }

    /**
     * Get the file attributes.
     *
     * @param path must not be {@literal null} or empty
     * @return never {@literal null}
     * @throws IOException if path does not exist
     */
    JvfsFileAttributes getFileAttributes(final String path) throws IOException {
        checkClosed();
        assertFileExists(path);
        return new JvfsFileAttributes(get(path));
    }

    /**
     * Set the times.
     *
     * @param path must not be {@literal null} or empty
     * @param mtime not changed if {@literal null}
     * @param atime not changed if {@literal null}
     * @param ctime not changed if {@literal null}
     * @throws IOException if source does not exist
     */
    void setTimes(final String path, final FileTime mtime, final FileTime atime, final FileTime ctime)
        throws IOException {
        checkClosed();
        assertFileExists(path);
        final JvfsFileEntry entry = get(path);

        if (null != mtime) {
            entry.setLastModifiedTime(mtime.to(TimeUnit.SECONDS));
        }

        if (null != atime) {
            entry.setLastAccessTime(atime.to(TimeUnit.SECONDS));
        }

        if (null != ctime) {
            entry.setCreationTime(ctime.to(TimeUnit.SECONDS));
        }
    }

    /**
     * Copy a file from one path to an other one.
     *
     * @param source must not be {@literal null} or empty
     * @param target must not be {@literal null} or empty
     * @param options options specifying how the copy should be done
     * @throws IOException if source does not exist
     */
    void copy(final String source, final String target, final CopyOption... options) throws IOException {
        checkClosed();
        assertFileExists(source);

        if (contains(target)) {
            throw new FileAlreadyExistsException(target);
        }

        add(get(source).copy());
    }

    /**
     * Move a file from one path to an other one.
     *
     * @param source must not be {@literal null} or empty
     * @param target must not be {@literal null} or empty
     * @param options options specifying how the move should be done
     * @throws IOException if source does not exist
     */
    void move(final String source, final String target, final CopyOption... options) throws IOException {
        checkClosed();
        assertFileExists(source);
        JvfsAssertions.notEmpty(target, "target");

        if (contains(target)) {
            throw new FileAlreadyExistsException(target);
        }

        final JvfsFileEntry entry = get(source);
        attic.remove(entry.getPath());
        add(entry);
    }

    /**
     * Get the file store.
     *
     * @return never {@literal null}
     */
    FileStore getFileStore() {
        return fileStores.get(0);
    }

    /**
     * Ask hidden attribute for a path.
     *
     * @param path must not be {@literal null} or empty
     * @return {@literal true} if file is hidden, else {@literal false}
     * @throws IOException if file does not exist
     */
    boolean isHidden(final String path) throws IOException {
        checkClosed();
        assertFileExists(path);
        return get(path).isHidden();
    }

    /**
     * Sums up the sizes of all non directory file entries.
     *
     * @return non negative
     */
    long getUsedSpace() {
        long usedBytes = 0L;

        final Iterator<Map.Entry<String, JvfsFileEntry>> it = attic.entrySet().iterator();

        while (it.hasNext()) {
            final JvfsFileEntry file = it.next().getValue();

            if (file.isDirectory()) {
                break;
            }

            usedBytes += file.size();
        }

        return usedBytes;
    }

}
