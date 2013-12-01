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
     * Contracted name of the {@link java.nio.file.attribute.BasicFileAttributeView}.
     */
    static final String BASIC_VIEW_NAME = "basic";
    /**
     * To get attributes from.
     */
    private final JvfsPath path;

    /**
     * Dedicated constructor.
     *
     * @param path must not be {@literal null}
     */
    JvfsFileAttributeView(final JvfsPath path) {
        super();
        assert path != null : "path must be specified";
        this.path = path;
    }

    @Override
    public String name() {
        return BASIC_VIEW_NAME;
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
     * @param attribute must not be {@literal nul} or empty
     * @param value must not be {@literal nul} or empty
     * @throws IOException IOException if file does not exist
     */
    void setAttribute(final String attribute, final Object value) throws IOException {
        JvfsAssertions.notEmpty(attribute, "attribute");
        JvfsAssertions.notNull(value, "value");

        switch (Names.valueOf(attribute)) {
            case creationTime:
                setTimes(null, null, (FileTime) value);
                break;
            case lastAccessTime:
                setTimes(null, (FileTime) value, null);
                break;
            case lastModifiedTime:
                setTimes((FileTime) value, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Attribute '" + attribute
                    + "' is unknown or read-only attribute!");
        }
    }

    /**
     * Reads a set of file attributes as a bulk operation.
     *
     * Examples:
     * <ul>
     * <li>{@literal "*"}: Read all basic-file-attributes.</li>
     * <li>{@literal "size,lastModifiedTime,lastAccessTime"}:
     * Reads the file size, last modified time, and last access time attributes.</li>
     * </ul>
     *
     * @param attributes must not be {@literal nul} or empty
     * @return never {@literal null}
     * @throws IOException if file does not exist
     */
    Map<String, Object> readAttributes(final String attributes) throws IOException {
        JvfsAssertions.notEmpty(attributes, "attributes");
        final BasicFileAttributes attrs = readAttributes();
        final Map<String, Object> map = JvfsCollections.newMap();

        if ("*".equals(attributes.trim())) {
            for (Names id : Names.values()) {
                if (id == Names._unknown) {
                    continue;
                }

                map.put(id.name(), attribute(id, attrs));
            }
        } else {
            final String[] as = attributes.trim().split(",");

            for (String a : as) {
                map.put(a, attribute(Names.valueOf(a.trim()), attrs));
            }
        }

        return map;
    }

    /**
     * Get a path attribute by its name.
     *
     * @param id must not be {@literal null}
     * @param attrs must not {@literal null}
     * @return {@literal null} if unsupported id given
     */
    Object attribute(final Names id, final BasicFileAttributes attrs) {
        assert id != null : "id must be defined";
        assert attrs != null : "attrs must be defined";

        switch (id) {
            case size:
                return attrs.size();
            case creationTime:
                return attrs.creationTime();
            case lastAccessTime:
                return attrs.lastAccessTime();
            case lastModifiedTime:
                return attrs.lastModifiedTime();
            case isDirectory:
                return attrs.isDirectory();
            case isRegularFile:
                return attrs.isRegularFile();
            case isSymbolicLink:
                return attrs.isSymbolicLink();
            case isOther:
                return attrs.isOther();
            case fileKey:
                return attrs.fileKey();
            default:
                return null;
        }
    }

    /**
     * Get the path from which the attributes are viewed.
     *
     * @return never {@literal null}
     */
    Path getPath() {
        return path;
    }

    /**
     * Names of attributes.
     */
    static enum Names {
        /** Size attribute. */
        size,
        /** Creation time attribute. */
        creationTime,
        /** Last access time attribute. */
        lastAccessTime,
        /** Last modification time attribute. */
        lastModifiedTime,
        /** Is directory attribute. */
        isDirectory,
        /** Is regular file attribute. */
        isRegularFile,
        /** Is symbolic link attribute. */
        isSymbolicLink,
        /** Is other file attribute. */
        isOther,
        /** File key attribute. */
        fileKey,
        /** Only for testing. */
        //CHECKSTYLE:OFF
        _unknown;
        //CHECKSTYLE:ON
    };
}
