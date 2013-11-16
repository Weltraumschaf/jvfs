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

import de.weltraumschaf.jvfs.JvfsFileSystems;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a file's location in a file system.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
class JvfsPath implements Path {

    /**
     * Directory separator.
     */
    static final String DIR_SEP = JvfsFileSystems.DIR_SEP;
    /**
     * Parent directory.
     */
    static final String DIR_UP = "..";
    /**
     * The directory self.
     */
    static final String DIR_THIS = ".";

    /**
     * Internal representation.
     */
    private final String path;

    /**
     * Owning {@link JvfsFileSystem}.
     */
    private final JvfsFileSystem jvfs;

    /**
     * Dedicated constructor.
     *
     * @param path must not be {@literal null}
     * @param fileSystem must not be {@literal null}
     */
    JvfsPath(final String path, final JvfsFileSystem fileSystem) {
        super();
        JvfsAssertions.notNull(path, "path");
        JvfsAssertions.notNull(fileSystem, "fileSystem");
        this.path = path;
        this.jvfs = fileSystem;
    }

    /**
     * Convenience constructor to create root path.
     *
     * Initializes {@link #path} with {@link #DIR_SEP}.
     *
     * @param fileSystem must not be {@literal null}
     */
    JvfsPath(final JvfsFileSystem fileSystem) {
        this(DIR_SEP, fileSystem);
    }

    @Override
    public FileSystem getFileSystem() {
        return jvfs;
    }

    @Override
    public boolean isAbsolute() {
        return this.path.startsWith(DIR_SEP);
    }

    @Override
    public Path getRoot() {
        return this.isAbsolute() ? new JvfsPath(jvfs) : null;
    }

    @Override
    public Path getFileName() {
        // Root and empty String has no file name
        if (path.isEmpty() || path.equals(DIR_SEP)) {
            return null;
        } else {
            final List<String> tokens = tokenize(this);
            return new JvfsPath(tokens.get(tokens.size() - 1), this.jvfs);
        }
    }

    @Override
    public Path getParent() {
        final List<String> tokens = tokenize(this);
        // No parent?
        final int numTokens = tokens.size();

        if (numTokens == 0 || numTokens == 1 && !this.isAbsolute()) {
            return null;
        }

        // Iterate over all but the last token and build a new path
        final StringBuffer sb = new StringBuffer();

        if (this.isAbsolute()) {
            sb.append(DIR_SEP);
        }

        for (int i = 0; i < numTokens - 1; i++) {
            if (i >= 1) {
                sb.append(DIR_SEP);
            }
            sb.append(tokens.get(i));
        }

        final String parentPath = sb.toString();
        return new JvfsPath(parentPath, jvfs);
    }

    @Override
    public int getNameCount() {
        String context = this.path;

        // Kill trailing slashes
        if (context.endsWith(DIR_SEP)) {
            context = context.substring(0, context.length() - 1);
        }

        // Kill preceding slashes
        if (context.startsWith(DIR_SEP)) {
            context = context.substring(1);
        }

        // Root
        if (context.length() == 0) {
            return 0;
        }

        // Else count names by using the separator
        return this.countOccurrences(context, DIR_SEP, 0) + 1;
    }

    /**
     * Returns the number of occurrences of the specified character in the specified {@link String}, starting at the
     * specified offset.
     *
     * @param string must not be {@code null}
     * @param s
     * @param offset
     * @return non negative number
     */
    private int countOccurrences(final String string, final String s, int offset) {
        assert string != null : "String must be specified";

        if ((offset = string.indexOf(s, offset)) == -1) {
            return 0;
        } else {
            return 1 + countOccurrences(string, s, offset + 1);
        }
    }

    @Override
    public Path getName(int index) {
        // Precondition checks handled by subpath implementation.
        return this.subpath(index, index + 1);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        JvfsAssertions.greaterThanEqual(beginIndex, 0, "beginIndex");
        JvfsAssertions.greaterThanEqual(endIndex, 0, "endIndex");
        JvfsAssertions.lessThan(beginIndex, endIndex, "beginIndex");

        final List<String> tokens = tokenize(this);
        final int tokenCount = tokens.size();

        if (beginIndex >= tokenCount) {
            throw new IllegalArgumentException("Invalid begin index " + endIndex + " for " + this.toString()
                    + "; must be between 0 and " + tokenCount + " exclusive");
        }

        if (endIndex > tokenCount) {
            throw new IllegalArgumentException("Invalid end index " + endIndex + " for " + this.toString()
                    + "; must be between 0 and " + tokenCount + " inclusive");
        }

        final StringBuilder buffer = new StringBuilder();

        for (int i = beginIndex; i < endIndex; i++) {
            if (i > beginIndex) {
                buffer.append(DIR_SEP);
            }

            buffer.append(tokens.get(i));
        }

        return this.fromString(buffer.toString());
    }

    /**
     * Creates a new {@link JvfsPath} instance from the specified input {@link String}.
     *
     * @param p must not be {@literal null}
     * @return never {@literal null}
     */
    private Path fromString(final String p) {
        JvfsAssertions.notNull(p, "path");
        return new JvfsPath(p, jvfs);
    }

    @Override
    public boolean startsWith(final Path other) {
        JvfsAssertions.notNull(other, "other");

        if (this.getFileSystem() != other.getFileSystem()) {
            return false;
        }

        final List<String> ourTokens = tokenize(this);
        final List<String> otherTokens = tokenize((JvfsPath) other);

        // Inequal roots
        if (other.isAbsolute() && !this.isAbsolute()) {
            return false;
        }

        // More names in the other Path than we have
        final int otherTokensSize = otherTokens.size();
        if (otherTokensSize > ourTokens.size()) {
            return false;
        }

        // Ensure each of the other name elements match ours
        for (int i = 0; i < otherTokensSize; i++) {
            if (!otherTokens.get(i).equals(ourTokens.get(i))) {
                return false;
            }
        }

        // All conditions met
        return true;
    }

    @Override
    public boolean startsWith(final String other) {
        // Precondition checks
        JvfsAssertions.notNull(other, "other");
        return this.startsWith(this.fromString(other));
    }

    @Override
    public boolean endsWith(final Path other) {
        // Precondition checks
        JvfsAssertions.notNull(other, "other");

        // Unequal FS? (also ensures that we can safely cast to this type later)
        if (this.getFileSystem() != other.getFileSystem()) {
            return false;
        }

        final List<String> ourTokens = tokenize(this);
        final List<String> otherTokens = tokenize((JvfsPath) other);

        // Bigger than us, fails
        final int numOtherTokens = otherTokens.size();
        if (numOtherTokens > ourTokens.size()) {
            return false;
        }

        // Difference in component size
        final int differential = ourTokens.size() - numOtherTokens;
        // Given an absolute? We must have the same number of elements.
        if (other.isAbsolute() && differential != 0) {
            return false;
        }

        // Compare all components
        for (int i = numOtherTokens - 1; i >= 0; i--) {
            if (!ourTokens.get(i + differential).equals(otherTokens.get(i))) {
                // Any tokens don't match, punt
                return false;
            }
        }

        // All conditions met
        return true;
    }

    @Override
    public boolean endsWith(final String other) {
        JvfsAssertions.notNull(other, "other");
        return this.endsWith(this.fromString(other));
    }

    @Override
    public Path normalize() {
        final String normalizedString = JvfsPathUtil.normalize(tokenize(this), this.isAbsolute());
        return new JvfsPath(normalizedString, this.jvfs);
    }

    @Override
    public Path resolve(final Path other) {
        JvfsAssertions.notNull(other, "other");

        if (other.isAbsolute()) {
            return other;
        }

        if (other.toString().length() == 0) {
            return this;
        }

        // Else join other to this
        final StringBuilder sb = new StringBuilder(this.path);
        if (!this.path.endsWith(DIR_SEP)) {
            sb.append(DIR_SEP);
        }
        sb.append(other.toString());

        return new JvfsPath(sb.toString(), this.jvfs);
    }

    @Override
    public Path resolve(final String other) {
        // Delegate
        return this.resolve(this.fromString(other));
    }

    @Override
    public Path resolveSibling(final Path other) {
        JvfsAssertions.notNull(other, "other");
        final Path parent = getParent();
        return (parent == null) ? other : parent.resolve(other);
    }

    @Override
    public Path resolveSibling(final String other) {
        // Delegate
        return this.resolveSibling(this.fromString(other));
    }

    @Override
    public Path relativize(final Path other) {
        JvfsAssertions.notNull(other, "other");
        if (!(other instanceof JvfsPath)) {
            throw new IllegalArgumentException("Can only relativize paths of type "
                    + JvfsPath.class.getSimpleName());
        }

        // Equal paths, return empty Path
        if (this.equals(other)) {
            return new JvfsPath("", this.jvfs);
        }

        // Recursive relativization
        return relativizeCommonRoot(this, this, other, other, 0);
    }

    /**
     * {@inheritDoc}
     *
     * Unsupported operation!
     */
    @Override
    public WatchKey register(
            final WatchService watcher,
            final WatchEvent.Kind<?>[]  events,
            final WatchEvent.Modifier ... modifiers) throws IOException {
        throw new UnsupportedOperationException("JVFS Paths do not support registration with a watch service!");
    }

    /**
     * {@inheritDoc}
     *
     * Unsupported operation!
     */
    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return this.register(watcher, events, (WatchEvent.Modifier) null);
    }

    @Override
    public URI toUri() {
        String location = JvfsFileSystems.getRootUri().toString();

        // Compose a new URI location, stripping out the extra "/" root idabsolute
        if (isAbsolute()) {
            location += this.toString().substring(1);
        } else {
            location += this.toString();
        }

        return URI.create(location);
    }

    @Override
    public Path toAbsolutePath() {
        // Already absolute?
        if (this.isAbsolute()) {
            return this;
        }

        // Else construct a new absolute path and normalize it
        final Path absolutePath = new JvfsPath(DIR_SEP + this.path, this.jvfs);
        return absolutePath.normalize();
    }

    @Override
    public Path toRealPath(final LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File toFile() {
        // XXX Consider returning own implementation, because File will may be circumvent JVFS.
        return new File(toString());
    }

    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            /**
             * Current position of the iterator.
             */
            private int index;

            @Override
            public boolean hasNext() {
                return index < getNameCount();
            }

            @Override
            public Path next() {
                if (index < getNameCount()) {
                    final Path result = getName(index);
                    index++;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int compareTo(final Path other) {
        JvfsAssertions.notNull(other, "other");
        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return this.path;
    }

    @Override
    public int hashCode() {
        return JvfsObject.hashCode(jvfs.hashCode(), path.hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsPath)) {
            return false;
        }

        final JvfsPath other = (JvfsPath) obj;

        if (this.jvfs != other.jvfs) {
            return false;
        }

        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the components of this path in order from root out.
     *
     * @param path must not be {@literal null}
     * @return never {@literal null} may be empty collection
     */
    static List<String> tokenize(final JvfsPath path) {
        return JvfsPathUtil.tokenize(path.toString());
    }

    /**
     * Relativizes the paths recursively.
     *
     * @param thisOriginal
     * @param thisCurrent
     * @param otherOriginal
     * @param otherCurrent
     * @param backupCount
     * @return never {@code null}
     */
    private static JvfsPath relativizeCommonRoot(final JvfsPath thisOriginal, final Path thisCurrent,
            final Path otherOriginal, Path otherCurrent, final int backupCount) {
        // Preconditions
        assert thisOriginal != null;
        assert thisCurrent != null;
        assert otherOriginal != null;
        assert otherCurrent != null;
        assert backupCount >= 0;

        // Do we yet have a common root?
        if (!otherCurrent.startsWith(thisCurrent)) {
            // Back up until we do
            final Path otherParent = otherCurrent.getParent();
            final JvfsPath thisParent = (JvfsPath) thisCurrent.getParent();

            if (otherParent != null && thisParent != null) {
                return relativizeCommonRoot(thisOriginal, thisParent, otherOriginal, otherParent, backupCount + 1);
            } else {
                throw new IllegalArgumentException("No common components");
            }
        }

        // Common root. Now relativize that.
        final List<String> thisTokens = tokenize(thisOriginal);
        final List<String> otherTokens = tokenize((JvfsPath) otherOriginal);
        final int numOtherTokens = otherTokens.size();
        final int numToTake = otherTokens.size() - thisTokens.size();
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < backupCount; i++) {
            sb.append(DIR_UP);
            sb.append(DIR_SEP);
        }

        final int startCounter = numOtherTokens - numToTake - backupCount;
        final int stopCounter = numOtherTokens - 1;

        for (int i = startCounter; i <= stopCounter; i++) {
            if (i > startCounter) {
                sb.append(DIR_SEP);
            }
            sb.append(otherTokens.get(i));
        }

        return new JvfsPath(sb.toString(), thisOriginal.jvfs);
    }

    /**
     * Create new file channel.
     *
     * @param options
     * @param attrs
     * @return never {@code null}
     * @throws IOException if path does not exist
     */
    FileChannel newFileChannel(final Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
        throws IOException {
        return jvfs.newFileChannel(path, options, attrs);
    }

    /**
     * Create new byte channel.
     *
     * @param options
     * @param attrs
     * @return never {@code null}
     */
    SeekableByteChannel newByteChannel(final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) {
        return jvfs.newByteChannel(path, options, attrs);
    }

    /**
     * Create new directory channel.
     *
     * @param filter
     * @return never {@code null}
     */
    DirectoryStream<Path> newDirectoryStream(final DirectoryStream.Filter<? super Path> filter) {
        return new JvfsDirectoryStream(this, filter);
    }

    /**
     * Create directory.
     *
     * @param attrs
     * @throws IOException if path does not exist
     */
    void createDirectory(final FileAttribute<?>... attrs) throws IOException {
        jvfs.createDirectory(path, attrs);
    }

    /**
     * Delete path.
     *
     * @throws IOException if path does not exist
     */
    void delete() throws IOException {
        jvfs.delete(path);
    }

    /**
     * Copy path.
     *
     * @param target must not be {@code null}
     * @param options
     * @throws IOException if path does not exist
     */
    void copy(final JvfsPath target, final CopyOption... options) throws IOException {
        JvfsAssertions.notNull(target, "target");
        jvfs.copy(path, target.toString(), options);
    }

    /**
     * Move path.
     *
     * @param target must not be {@code null}
     * @param options
     * @throws IOException if path does not exist
     */
    void move(final JvfsPath target, final CopyOption... options) throws IOException {
        JvfsAssertions.notNull(target, "target");
        jvfs.move(path, target.toString(), options);
    }

    boolean isSameFile(final Path other) {
        return equals(other);
    }

    boolean isHidden() throws IOException {
        return jvfs.isHidden(path);
    }

    FileStore getFileStore() {
        return jvfs.getFileStore();
    }

    void checkAccess(final AccessMode... modes) throws IOException {
        jvfs.checkAccess(path, modes);
    }

    void setTimes(final FileTime mtime, final FileTime atime, final FileTime ctime) throws IOException {
        jvfs.setTimes(path, mtime, atime, ctime);
    }

    BasicFileAttributes getAttributes() throws IOException {
        final JvfsFileAttributes attrs = jvfs.getFileAttributes(path);

        if (attrs == null) {
            throw new NoSuchFileException(path);
        }

        return attrs;
    }

    /**
     * Reads a set of file attributes as a bulk operation.
     *
     * @see de.weltraumschaf.jvfs.impl.JvfsFileAttributeView.readAttributes(java.lang.String)
     * @param attributes attributes must not be {@code nul} or empty
     * @param options ignored at the moment
     * @return never {@code null}
     * @throws IOException IOException if file does not exist
     */
    Map<String, Object> readAttributes(final String attributes, final LinkOption... options) throws IOException {
        final JvfsFileAttributeView view = new JvfsFileAttributeView(this);
        return view.readAttributes(attributes);
    }

    /**
     * Sets the value of a file attribute.
     *
     * @param attribute must not be {@code nul} or empty
     * @param value must not be {@code nul} or empty
     * @param options ignored at the moment
     * @throws IOException IOException if file does not exist
     */
    void setAttribute(final String attribute, final Object value, final LinkOption... options) throws IOException {
        final JvfsFileAttributeView view = new JvfsFileAttributeView(this);
        view.setAttribute(attribute, value);
    }
}
