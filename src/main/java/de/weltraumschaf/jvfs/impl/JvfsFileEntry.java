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
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holds the administrative data of a file entry in the virtual file system.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsFileEntry {

    /**
     * Absolute path name of the file system entry.
     */
    private final String path;
    /**
     * Whether it is a directory or not.
     */
    private final boolean direcotry;
    /**
     * R/W lock.
     */
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    /**
     * Holds the file data.
     */
    private byte[] content;
    /**
     * Last modification time.
     */
    private long lastModifiedTime;
    /**
     * Last access time.
     */
    private long lastAccessTime;
    /**
     * Creation time.
     */
    private long creationTime;
    /**
     * Whether it is hidden.
     */
    private boolean hidden;
    /**
     * Holds the file permissions.
     */
    private JvfsFilePermissions permissions = new JvfsFilePermissions();
    /**
     * Contains children if it is a directory.
     */
    private Set<JvfsFileEntry> children = JvfsCollections.newSet();
    /**
     * Contains the parent directory, except if it is the root.
     */
    private JvfsFileEntry parent;

    /**
     * Copy constructor.
     *
     * Hidden: Use {@link #copy()} to create a copy of an entry.
     *
     * @param path must not be {@code null} or empty
     * @param src must not be {@literal null}
     */
    private JvfsFileEntry(final String path, final JvfsFileEntry src) {
        this(path, src.direcotry, src.getContent().clone());
        this.lastModifiedTime = src.getLastModifiedTime();
        this.lastAccessTime = src.getLastAccessTime();
        this.creationTime = src.getCreationTime();
        this.permissions = src.permissions.copy();
        this.hidden = src.isHidden();
        this.children = JvfsCollections.newSet(src.children);
    }

    /**
     * Hidden: Use either {@link #newDir(java.lang.String)} or {@link #newFile(java.lang.String)}.
     *
     * @param path must not be {@literal null} or empty
     * @param direcotry {@literal true} if it is a directory, else {@literal false}
     */
    private JvfsFileEntry(final String path, final boolean direcotry) {
        this(path, direcotry, new byte[0]);
    }

    /**
     * Dedicated constructor.
     *
     * @param path must not be {@literal null} or empty
     * @param direcotry {@literal true} if it is a directory, else {@literal false}
     * @param content must not be {@code null}
     * @param parent may be {@code null} for root
     */
    JvfsFileEntry(final String path, final boolean direcotry, final byte[] content) {
        super();
        assert path != null : "path must not be null";
        assert !path.isEmpty() : "path must not be empty";
        assert content != null : "content must not be null";
        this.path = path;
        this.direcotry = direcotry;
        this.content = content;
    }

    /**
     * Creates a new directory entry.
     *
     * @param path must not be {@literal null} or empty
     * @return never {@literal null}
     */
    static JvfsFileEntry newDir(final String path) {
        return new JvfsFileEntry(path, true);
    }

    /**
     * Creates a new file entry.
     *
     * @param path must not be {@literal null} or empty
     * @return never {@literal null}
     */
    static JvfsFileEntry newFile(final String path) {
        return new JvfsFileEntry(path, false);
    }

    /**
     * Creates identical deep copy with a different path.
     *
     * @param newPath must not be {@literal null} or empty
     * @return never {@literal null}
     */
    JvfsFileEntry copy(final String newPath) {
        return new JvfsFileEntry(newPath, this);
    }

    /**
     * Creates identical deep copy.
     *
     * @return never {@literal null}
     */
    JvfsFileEntry copy() {
        return new JvfsFileEntry(this.getPath(), this);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsFileEntry)) {
            return false;
        }

        final JvfsFileEntry other = (JvfsFileEntry) obj;
        return path.equals(other.path);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        if (isDirectory()) {
            buffer.append('d');
        } else {
            buffer.append('-');
        }

        buffer.append(permissions).append(' ').append(path);
        return buffer.toString();
    }

    /**
     * Get the absolute path.
     *
     * @return never {@literal null} or empty
     */
    String getPath() {
        return path;
    }

    /**
     * Whether it is a directory.
     *
     * @return {@literal true} means it is a directory, {@literal false} means file
     */
    boolean isDirectory() {
        return direcotry;
    }

    /**
     * Whether the entry is hidden or not.
     *
     * @return {@literal true} if it is hidden, else {@literal false}
     */
    boolean isHidden() {
        return hidden;
    }

    /**
     * Get last modification time in seconds.
     *
     * @return non negative number
     */
    long getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Set last modification time in seconds.
     *
     * @param timestamp must be non negative
     */
    void setLastModifiedTime(final long timestamp) {
        JvfsAssertions.greaterThanEqual(timestamp, 0, "timestamp");
        this.lastModifiedTime = timestamp;
    }

    /**
     * Get last access time in seconds.
     *
     * @return non negative number
     */
    long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Set last access time in seconds.
     *
     * @param timestamp must be non negative
     */
    void setLastAccessTime(final long timestamp) {
        JvfsAssertions.greaterThanEqual(timestamp, 0, "timestamp");
        this.lastAccessTime = timestamp;
    }

    /**
     * Get last creation in seconds.
     *
     * @return non negative number
     */
    long getCreationTime() {
        return creationTime;
    }

    /**
     * Set creation time in seconds.
     *
     * @param timestamp must be non negative
     */
    void setCreationTime(final long timestamp) {
        JvfsAssertions.greaterThanEqual(timestamp, 0, "timestamp");
        this.creationTime = timestamp;
    }

    /**
     * Whether the file is readable.
     *
     * @return {@literal true} if readable, else {@literal false}
     */
    boolean isReadable() {
        return permissions.ownerRead();
    }

    /**
     * Whether the file is writable.
     *
     * @return {@literal true} if writable, else {@literal false}
     */
    boolean isWritable() {
        return permissions.ownerWrite();
    }

    /**
     * Whether the file is executable.
     *
     * @return {@literal true} if executable, else {@literal false}
     */
    boolean isExecutable() {
        return permissions.ownerExecute();
    }

    /**
     * Set readable attribute.
     *
     * @param readable {@code true} for readable, else {@code false}
     */
    public void setReadable(boolean readable) {
        permissions.ownerRead(readable);
    }

    /**
     * Set writable attribute.
     *
     * @param writable {@code true} for writable, else {@code false}
     */
    public void setWritable(boolean writable) {
        permissions.ownerWrite(writable);
    }

    /**
     * Set executable attribute.
     *
     * @param executable {@code true} for executable, else {@code false}
     */
    public void setExecutable(boolean executable) {
        permissions.ownerExecute(executable);
    }

    /**
     * Set hidden attribute.
     *
     * @param hidden {@code true} for hidden, else {@code false}
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Whether it is a symbolic link.
     *
     * @return {@literal true} if it is a symbolic link, else {@literal false}
     */
    boolean isSymbolicLink() {
        // TODO Implement symbolic links.
        return false;
    }

    /**
     * Get the size of the file content in bytes.
     *
     * @return -1 if it is a directory, else size
     */
    long size() {
        if (this.isDirectory()) {
            return -1L;
        }

        return content.length;
    }

    /**
     * Get the file content.
     *
     * @return never {@literal null}
     */
    byte[] getContent() {
        return content;
    }

    /**
     * Set the file content.
     *
     * @param content must not be {@code null}
     */
    void setContent(final byte[] content) {
        assert content != null : "content must be defined";
        this.content = content;
    }

    /**
     * Acquire write lock.
     */
    void beginWrite() {
        rwlock.writeLock().lock();
    }

    /**
     * Return write lock.
     */
    void endWrite() {
        rwlock.writeLock().unlock();
    }

    /**
     * Acquire read lock.
     */
    void beginRead() {
        rwlock.readLock().lock();
    }

    /**
     * Return read lock.
     */
    void endRead() {
        rwlock.readLock().unlock();
    }

    /**
     * Set file permissions in bulk operation.
     *
     * @param permissions must not be {@code null}
     */
    void setPermissions(final JvfsFilePermissions permissions) {
        JvfsAssertions.notNull(permissions, "permissions");
        this.permissions = permissions;
    }

    /**
     * Get all file permissions as a bulk action.
     *
     * @return never {@code null}
     */
    JvfsFilePermissions getPermissions() {
        return permissions;
    }

    /**
     * Whether a entry has child entries.
     *
     * @return {@literal true} if entry is a directory and children were added, else {@literal false}
     */
    boolean hasChildren() {
        if (isDirectory()) {
            return children.size() > 0;
        }

        return false;
    }

    /**
     * Get the children.
     *
     * @return never {@code null}, always empty if not a directory
     */
    Set<JvfsFileEntry> getChildren() {
        return children;
    }

    /**
     * Add a child entry.
     *
     * Throws an {@link IllegalStateException} if it is not a directory.
     *
     * @param child must not be {@code null}
     */
    void addChild(final JvfsFileEntry child) {
        if (isDirectory()) {
            JvfsAssertions.notNull(child, "child");
            children.add(child);
            return;
        }

        throw new IllegalStateException("Cant add child to non direcotry!");
    }

    /**
     * Whether the entry has a parent.
     *
     * @return {@code true} except for root
     */
    boolean hasParent() {
        return null != parent;
    }

    /**
     * Get the parent entry.
     *
     * @return {@code null} for the root
     */
    JvfsFileEntry getParent() {
        return parent;
    }

    /**
     * Set the parent.
     *
     * @param dir may be {@code null}, must be directory if not {@code null}
     */
    void setParent(final JvfsFileEntry dir) {
        if (dir != null && !dir.isDirectory()) {
            throw new IllegalArgumentException("Parent must be a directory!");
        }

        parent = dir;
    }
}
