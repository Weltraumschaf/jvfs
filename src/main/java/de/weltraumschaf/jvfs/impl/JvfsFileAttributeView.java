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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;

/**
 * Implementation of a basic file attributes view.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsFileAttributeView implements BasicFileAttributeView {

    /**
     * To get attributes from.
     */
    private final JvfsPath path;

    /**
     * Dedicated constructor.
     *
     * @param path must not be {@code null}
     */
    JvfsFileAttributeView(final JvfsPath path) {
        super();
        assert path != null : "path must be specified";
        this.path = path;
    }

    @Override
    public String name() {
        return JvfsFileSystem.FILE_ATTR_VIEW_BASIC;
    }

    @Override
    public BasicFileAttributes readAttributes() throws IOException {
        return path.getAttributes();
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime)
        throws IOException {
        path.setTimes(lastModifiedTime, lastAccessTime, createTime);
    }

    /**
     * Sets the value of a file attribute.
     *
     * @param attribute must not be {@code nul} or empty
     * @param value must not be {@code nul} or empty
     */
    void setAttribute(final String attribute, final Object value) {
        JvfsAssertions.notEmpty(attribute, "attribute");
        JvfsAssertions.notNull(value, "value");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Reads a set of file attributes as a bulk operation.
     *
     * @param attributes must not be {@code nul} or empty
     * @return may be {@code null}
     */
    Map<String, Object> readAttributes(String attributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the path from which the attributes are viewed.
     *
     * @return never {@code null}
     */
    Path getPath() {
        return path;
    }

}
