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

import de.weltraumschaf.jvfs.JvfsObject;
import de.weltraumschaf.jvfs.JvfsOptions;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.Objects;

/**
 * Implementation of the JVFS file store.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsFileStore extends FileStore {

    /**
     * Name of the store.
     */
    private static final String NAME = "jvfs";
    /**
     * Type of the store.
     */
    private static final String TYPE = "in-memory";
    /**
     * Whether the sore is readonly or not.
     */
    private final JvfsOptions options;
    /**
     * Associated file system.
     */
    private final JvfsFileSystem fs;

    /**
     * Dedicated constructor.
     *
     * @param opts {@code true} registers readonly file system
     * @param fs must not be {@code null}
     */
    JvfsFileStore(final JvfsOptions opts, final JvfsFileSystem fs) {
        super();
        assert null != opts : "opts must be defined";
        assert null != fs : "store must be defined";
        this.options = opts;
        this.fs = fs;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String type() {
        return TYPE;
    }


    @Override
    public boolean isReadOnly() {
        return options.isReadonly();
    }

    @Override
    public long getTotalSpace() throws IOException {
        return this.getUsableSpace() + this.getUsedSpace();
    }

    /**
     * Get the used space in bytes.
     *
     * @return non negative
     */
    public long getUsedSpace() {
        return fs.getUsedSpace();
    }

    @Override
    public long getUsableSpace() throws IOException {
        return options.getCapacity().value();
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        return this.getUsableSpace();
    }

    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
        return BasicFileAttributeView.class.equals(type);
    }

    @Override
    public boolean supportsFileAttributeView(final String name) {
        return JvfsFileSystem.FILE_ATTR_VIEW_BASIC.equals(name);
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(final Class<V> type) {
        // XXX: Considder to implement this.
        return null;
    }

    @Override
    public Object getAttribute(final String attribute) throws IOException {
        // XXX: Considder to implement this.
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support attributes.");
    }

    @Override
    public int hashCode() {
        return JvfsObject.hashCode(options, fs);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JvfsFileStore)) {
            return false;
        }

        final JvfsFileStore other = (JvfsFileStore) obj;
        return JvfsObject.equal(options, other.options) && JvfsObject.equal(fs, other.fs);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{"
            + "options=" + Objects.toString(options) + ", "
            + "fs=" + fs.getClass().getSimpleName()
            + '}';
    }


}
