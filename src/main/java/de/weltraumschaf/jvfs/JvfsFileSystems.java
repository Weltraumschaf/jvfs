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
package de.weltraumschaf.jvfs;

import de.weltraumschaf.jvfs.impl.JvfsFileSystemProviderOld;
import java.net.URI;
import java.nio.file.spi.FileSystemProvider;

/**
 * Factory to get virtual file system stuff.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public final class JvfsFileSystems {

    /**
     * OS dependent directory separator.
     */
    public static final String DIR_SEP = System.getProperty("file.separator");
    /**
     * Protocol portion of a {@link URI} to JVFS {@link java.nio.file.FileSystem file systems}.
     */
    public static final String PROTOCOL_JVFS = "file";
    /**
     * The default file system protocol.
     */
    public static final String PROTOCOL_FILE = "file";
    /**
     * Name of property to set default file system provider implementations.
     *
     * @see java.nio.file.FileSystems.DefaultFileSystemHolder#getDefaultProvider()
     */
    public static final String IMPLEMENTATION_PROPERTY_NAME = "java.nio.file.spi.DefaultFileSystemProvider";
    /**
     * Full qualified class name of provider implementation.
     */
    public static final String IMPLEMENTATION_CLASS_NAME = "de.weltraumschaf.jvfs.impl.JvfsFileSystemProviderOld";
    /**
     * Protocol suffix before ID portion of ShrinkWrap {@link URI}s.
     */
    private static final String URI_PROTOCOL_SUFFIX = "://";
    /**
     * Whether the registered default file system is readonly or not.
     */
    private static boolean readonly;

    /**
     * Hidden for pure static class.
     */
    private JvfsFileSystems() {
        super();
        throw new UnsupportedOperationException(); // Avoid reflective instantiation.
    }

    /**
     * Returns writable a new file system provider.
     *
     * @return never {@literal null} always new instance
     */
    public static FileSystemProvider newUnixProvider() {
        return newUnixProvider(false);
    }

    /**
     * Returns a new file system provider.
     *
     * @param flag {@literal true} creates readonly file system
     * @return never {@literal null} always new instance
     */
    public static FileSystemProvider newUnixProvider(final boolean flag) {
        return new JvfsFileSystemProviderOld(flag);
    }

    /**
     * Constructs a new {@link URI} with the form: {@literal file:///}.
     *
     * @return never {@literal null}
     */
    public static URI getRootUri() {
        final StringBuilder sb = new StringBuilder();
        sb.append(PROTOCOL_FILE);
        sb.append(URI_PROTOCOL_SUFFIX);
        sb.append(DIR_SEP);
        return URI.create(sb.toString());
    }

    /**
     * Whether the registered default file system is readonly.
     *
     * @return {@literal true} means that the registered default file system is not writable
     */
    public static boolean isReadonly() {
        return readonly;
    }

    /**
     * Registers writable the JVFS implementation of {@link FileSystemProvider} as default file system.
     */
    public static void registerUnixAsDefault() {
        registerUnixAsDefault(false);
    }

    /**
     * Registers the JVFS implementation of {@link FileSystemProvider} as default file system.
     *
     * @param flag {@literal true} registers readonly file system
     */
    public static void registerUnixAsDefault(final boolean flag) {
        readonly = flag;
        System.setProperty(IMPLEMENTATION_PROPERTY_NAME, IMPLEMENTATION_CLASS_NAME);
    }

    /**
     * Removes the custom implementation.
     */
    public static void unregisterDefault() {
        System.setProperty(IMPLEMENTATION_PROPERTY_NAME, "");
    }

}
