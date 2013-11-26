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

import java.nio.file.FileSystemAlreadyExistsException;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link JvfsFileSystemTable}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemTableTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON
    private final JvfsFileSystemTable sut = new JvfsFileSystemTable();

    @Test
    public void mount_samePathTwiceThrowsException() {
        assertThat(sut.size(), is(0));
        sut.mount("/foo/bar", mock(JvfsFileSystem.class));
        assertThat(sut.size(), is(1));
        thrown.expect(FileSystemAlreadyExistsException.class);
        sut.mount("/foo/bar", mock(JvfsFileSystem.class));
        assertThat(sut.size(), is(1));
    }

    @Test
    public void mount_sameFsTwiceThrowsException() {
        assertThat(sut.size(), is(0));
        final JvfsFileSystem fs = mock(JvfsFileSystem.class);
        sut.mount("/foo/bar", fs);
        assertThat(sut.size(), is(1));
        thrown.expect(FileSystemAlreadyExistsException.class);
        sut.mount("/foo/baz", fs);
        assertThat(sut.size(), is(1));
    }

    @Test
    public void umount_noneMountedQuietly() {
        assertThat(sut.size(), is(0));
        sut.umount("/foo/bar");
        assertThat(sut.size(), is(0));
    }

    @Test
    public void findMountedFilesystem_throwsExcpetionIfNull() {
        thrown.expect(NullPointerException.class);
        sut.findMountedFilesystem(null);
    }

    @Test
    public void findMountedFilesystem_throwsExcpetionIfEmpty() {
        thrown.expect(IllegalArgumentException.class);
        sut.findMountedFilesystem("");
    }

    @Test @Ignore
    public void findMountedFilesystem() {
        final JvfsFileSystem fs1 = mock(JvfsFileSystem.class);
        sut.mount("/", fs1);
        final JvfsFileSystem fs2 = mock(JvfsFileSystem.class);
        sut.mount("/foo/bar", fs2);
        final JvfsFileSystem fs3 = mock(JvfsFileSystem.class);
        sut.mount("/foo", fs3);
        final JvfsFileSystem fs4 = mock(JvfsFileSystem.class);
        sut.mount("/snafu", fs4);

        assertThat(sut.findMountedFilesystem("/"), is(sameInstance(fs1)));
        assertThat(sut.findMountedFilesystem("/tmp"), is(sameInstance(fs1)));
        assertThat(sut.findMountedFilesystem("/tmp/foo/bar/baz.cfg"), is(sameInstance(fs1)));

        assertThat(sut.findMountedFilesystem("/foo/bar"), is(sameInstance(fs2)));
        assertThat(sut.findMountedFilesystem("/foo/bar/"), is(sameInstance(fs2)));
        assertThat(sut.findMountedFilesystem("/foo/bar/snafu"), is(sameInstance(fs2)));
        assertThat(sut.findMountedFilesystem("/foo/bar/snafu/bla.txt"), is(sameInstance(fs2)));

        assertThat(sut.findMountedFilesystem("/foo/baz/snafu"), is(sameInstance(fs3)));

        assertThat(sut.findMountedFilesystem("/snafu"), is(sameInstance(fs4)));
        assertThat(sut.findMountedFilesystem("/snafu/"), is(sameInstance(fs4)));
        assertThat(sut.findMountedFilesystem("/snafu/foo"), is(sameInstance(fs4)));
        assertThat(sut.findMountedFilesystem("/snafu/bar"), is(sameInstance(fs4)));
        assertThat(sut.findMountedFilesystem("/snafu/baz/bar/foo.txt"), is(sameInstance(fs4)));
    }

}
