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

import de.weltraumschaf.jvfs.impl.JvfsFileSystemProvider;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
    public static final String PROTOCOL_JVFS = "jvfs";
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
     * Singleton instance.
     */
    private static final JvfsFileSystems INSTANCE = new JvfsFileSystems();
    /**
     * Whether the registered default file system is readonly or not.
     */
    private static boolean readonly;
    /**
     * Mount points for virtual file systems to be hooked in.
     */
    private final Map<String, FileSystem> fstab = JvfsCollections.newMap();

    /**
     * Hidden constructor.
     *
     * Use {@link #getInstance()} to get an instance.
     */
    private JvfsFileSystems() {
        super();
    }

    /**
     * Get the single instance.
     *
     * @return never {@literal null}
     */
    public static JvfsFileSystems getInstance() {
        return INSTANCE;
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
        return new JvfsFileSystemProvider();
    }

    /**
     * Constructs a new {@link URI} with the form: {@literal file:///}.
     *
     * @return never {@literal null}
     */
    public static URI createRootUri() {
        return createUri(DIR_SEP);
    }

    /**
     * Creates an URI for the JVFS protocol.
     *
     * @param path must not be {@literal null} or empty
     * @return never {@literal null}
     */
    public static URI createUri(final String path) {
        final StringBuilder sb = new StringBuilder();
        sb.append(PROTOCOL_JVFS);
        sb.append(URI_PROTOCOL_SUFFIX);
        sb.append(path);
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

    /**
     * Creates Posix file permissions usable for {@link java.nio.file.Files#createFile(java.nio.file.Path,
     * java.nio.file.attribute.FileAttribute...)}.
     *
     * Example:<br/>
     * <code>
     * final Path foo = Files.createFile(uri, JvfsFileSystems.createFileAttribute(
     *       PosixFilePermission.OWNER_READ,
     *       PosixFilePermission.OWNER_WRITE,
     *       PosixFilePermission.OWNER_EXECUTE));
     * </code>
     *
     * @param perms must not be {@code null}
     * @return never {@code nul}
     */
    public static final FileAttribute<Set<PosixFilePermission>> createFileAttribute(
        final PosixFilePermission ... perms) {
        JvfsAssertions.notNull(perms, "perms");
        final Set<PosixFilePermission> permissions = JvfsCollections.newSet();
        permissions.addAll(Arrays.asList(perms));
        return PosixFilePermissions.asFileAttribute(permissions);
    }

    /**
     * Mount a virtual file system.
     *
     * @param path must not be {@literal null} or empty
     */
    public void mount(final String path) {
        JvfsAssertions.notEmpty(path, "path");
        fstab.put(path, FileSystems.getFileSystem(createRootUri()));
    }

    /**
     * Unmount a virtual file system.
     *
     * Throws an {@link IllegalStateException} if not mounted.
     *
     * @param path must not be {@literal null} or empty
     */
    public void umount(final String path) {
        JvfsAssertions.notEmpty(path, "path");

        if (!fstab.containsKey(path)) {
            throw new IllegalStateException(String.format("Path not mounted to JVFS: '%s'!", path));
        }

        fstab.remove(path);
    }

    /**
     * Get map of mount points and file systems.
     *
     * @return never {@literal null}
     */
    public Map<String, FileSystem> getFstab() {
        return Collections.unmodifiableMap(fstab);
    }

}
