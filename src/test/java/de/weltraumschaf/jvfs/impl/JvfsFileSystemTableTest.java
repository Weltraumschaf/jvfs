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

import de.weltraumschaf.jvfs.JvfsOptions;
import java.nio.file.FileSystemAlreadyExistsException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
    private final JvfsFileSystemProvider provider = new JvfsFileSystemProvider();

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

    @Test
    public void findMountedFilesystem() {
        final JvfsFileSystem fs1 = createFs("1");
        sut.mount("/", fs1);
        final JvfsFileSystem fs2 = createFs("2");
        sut.mount("/foo/bar", fs2);
        final JvfsFileSystem fs3 = createFs("3");
        sut.mount("/foo", fs3);
        final JvfsFileSystem fs4 = createFs("4");
        sut.mount("/snafu", fs4);

        assertThat(sut.findMountedFilesystem("/"), is(sameInstance(fs1)));
        assertThat(sut.findMountedFilesystem("/tmp"), is(sameInstance(fs1)));
        assertThat(sut.findMountedFilesystem("/tmp/foo/bar/baz.cfg"), is(sameInstance(fs1)));

        assertThat(sut.findMountedFilesystem("/foo/bar"), is(sameInstance(fs2)));
        assertThat(sut.findMountedFilesystem("/foo/bar/"), is(sameInstance(fs2)));
        assertThat(sut.findMountedFilesystem("/foo/bar/snafu"), is(sameInstance(fs2)));
        assertThat(sut.findMountedFilesystem("/foo/bar/snafu/bla.txt"), is(sameInstance(fs2)));

        assertThat(sut.findMountedFilesystem("/foo/baz/snafu"), is(sameInstance(fs3)));

//        assertThat(sut.findMountedFilesystem("/snafu"), is(sameInstance(fs4)));
//        assertThat(sut.findMountedFilesystem("/snafu/"), is(sameInstance(fs4)));
//        assertThat(sut.findMountedFilesystem("/snafu/foo"), is(sameInstance(fs4)));
//        assertThat(sut.findMountedFilesystem("/snafu/bar"), is(sameInstance(fs4)));
//        assertThat(sut.findMountedFilesystem("/snafu/baz/bar/foo.txt"), is(sameInstance(fs4)));
    }

    @Test
    public void testHashCode() {
        final JvfsFileSystemTable sut1 = new JvfsFileSystemTable();
        final JvfsFileSystem fs1 = createFs("1");
        sut1.mount("/foo", fs1);
        final JvfsFileSystemTable sut2 = new JvfsFileSystemTable();
        sut2.mount("/foo", fs1);
        final JvfsFileSystemTable sut3 = new JvfsFileSystemTable();
        sut3.mount("/bar", createFs("2"));

        assertThat(sut1.hashCode(), is(sut1.hashCode()));
        assertThat(sut1.hashCode(), is(sut2.hashCode()));
        assertThat(sut2.hashCode(), is(sut1.hashCode()));
        assertThat(sut2.hashCode(), is(sut2.hashCode()));

        assertThat(sut3.hashCode(), is(sut3.hashCode()));
        assertThat(sut3.hashCode(), is(not(sut2.hashCode())));
        assertThat(sut3.hashCode(), is(not(sut1.hashCode())));

    }

    @Test
    public void equals() {
        final JvfsFileSystemTable sut1 = new JvfsFileSystemTable();
        final JvfsFileSystem fs1 = createFs("1");
        sut1.mount("/foo", fs1);
        final JvfsFileSystemTable sut2 = new JvfsFileSystemTable();
        sut2.mount("/foo", fs1);
        final JvfsFileSystemTable sut3 = new JvfsFileSystemTable();
        sut3.mount("/bar", createFs("2"));

        //CHECKSTYLE:OFF
        assertThat(sut1.equals(null), is(false));
        assertThat(sut1.equals(""), is(false));
        //CHECKSTYLE:ON

        assertThat(sut1.equals(sut1), is(true));
        assertThat(sut1.equals(sut2), is(true));
        assertThat(sut2.equals(sut1), is(true));
        assertThat(sut2.equals(sut2), is(true));

        assertThat(sut3.equals(sut3), is(true));
        assertThat(sut3.equals(sut2), is(false));
        assertThat(sut3.equals(sut1), is(false));
    }

    @Test
    public void testToString() {
        sut.mount("/foo", createFs("/foo"));
        assertThat(sut.toString(), is(equalTo(
            "JvfsFileSystemTable{"
                + "/foo=JvfsFileSystem{"
                    + "fileStores=[JvfsFileStore{"
                        + "options=JvfsOptions{id=/foo, capacity=1048576, readonly=false}, "
                        + "fs=JvfsFileSystem}"
                    + "]"
                + "}, "
                + "root=null"
            + "}"
        )));
        sut.mount("/", createFs("/"));
        assertThat(sut.toString(), is(equalTo(
            "JvfsFileSystemTable{"
                + "/foo=JvfsFileSystem{"
                    + "fileStores=[JvfsFileStore{"
                        + "options=JvfsOptions{id=/foo, capacity=1048576, readonly=false}, "
                        + "fs=JvfsFileSystem}"
                    + "]"
                + "}, "
                + "/=JvfsFileSystem{"
                    + "fileStores=[JvfsFileStore{"
                        + "options=JvfsOptions{id=/, capacity=1048576, readonly=false}, "
                        + "fs=JvfsFileSystem}"
                    + "]"
                + "}, "
                + "root=/"
            + "}"
        )));
    }

    @Test
    public void list() {
        sut.mount("/", createFs("1"));
        assertThat(sut.list(), is(equalTo(
            "/    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=1, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
        )));
        sut.mount("/foo/bar", createFs("2"));
        assertThat(sut.list(), is(equalTo(""
            + "/foo/bar    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=2, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
            + "/    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=1, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
        )));
        sut.mount("/foo", createFs("3"));
        assertThat(sut.list(), is(equalTo(""
            + "/foo/bar    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=2, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
            + "/foo    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=3, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
            + "/    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=1, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
        )));
        sut.mount("/snafu", createFs("4"));
        assertThat(sut.list(), is(equalTo(""
            + "/snafu    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=4, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
            + "/foo/bar    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=2, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
            + "/foo    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=3, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
            + "/    JvfsFileSystem{fileStores=[JvfsFileStore{options=JvfsOptions{id=1, capacity=1048576, readonly=false}, fs=JvfsFileSystem}]}\n"
        )));
    }

    private JvfsFileSystem createFs(final String id) {
        final JvfsOptions options = JvfsOptions.builder().capacity("1M").id(id).create();
        return new JvfsFileSystem(provider, options);
    }
}
