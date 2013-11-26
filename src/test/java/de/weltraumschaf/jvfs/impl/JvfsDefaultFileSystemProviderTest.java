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

import de.weltraumschaf.jvfs.JvfsCollections;
import java.nio.file.FileSystem;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link JvfsDefaultFileSystemProvider}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsDefaultFileSystemProviderTest {

    @Test
    public void isMounted() {
        final Map<String, FileSystem> fstab = JvfsCollections.newMap();
        final JvfsDefaultFileSystemProvider sut = new JvfsDefaultFileSystemProvider(
            mock(FileSystemProvider.class),
            Collections.unmodifiableMap(fstab),
            mock(FileSystemProvider.class));

        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/snafu", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/snafu/blub", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo/bar", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/tmp/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/tmp", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/", mock(JvfsFileSystem.class))), is(false));

        fstab.put("/foo/bar/baz", mock(FileSystem.class));
        fstab.put("/tmp", mock(FileSystem.class));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/snafu", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/snafu/blub", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo/", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/foo", mock(JvfsFileSystem.class))), is(false));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo/bar", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/", mock(JvfsFileSystem.class))), is(false));

        fstab.clear();
        fstab.put("/", mock(FileSystem.class));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/snafu", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/snafu/blub", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/baz", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/bar", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/foo", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo/bar", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/foo", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp/", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/tmp", mock(JvfsFileSystem.class))), is(true));
        assertThat(sut.isMounted(new JvfsPath("/", mock(JvfsFileSystem.class))), is(true));
    }

}
