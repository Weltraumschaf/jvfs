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
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JvfsFileAttributeView}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileAttributeViewTest {

    private JvfsFileSystem fs;
    private JvfsPath path;
    private JvfsFileAttributeView sut;

    @Before
    public void setUpMocks() throws IOException {
        fs = mock(JvfsFileSystem.class);
        final String pathname = "/foo/bar";
        when(fs.getFileAttributes(pathname)).thenReturn(new JvfsFileAttributes(JvfsFileEntry.newFile(pathname)));
        path = spy(new JvfsPath(pathname, fs));
        sut = new JvfsFileAttributeView(path);
    }

    @Test
    public void name() {
        assertThat(sut.name(), is(equalTo("basic")));
    }

    @Test
    public void readAttributes() throws IOException {
        sut.readAttributes();
        verify(path, times(1)).getAttributes();
    }

    @Test
    public void setTimes() throws IOException {
        final FileTime lastModifiedTime = FileTime.from(1L, TimeUnit.SECONDS);
        final FileTime lastAccessTime = FileTime.from(2L, TimeUnit.SECONDS);
        final FileTime createTime = FileTime.from(3L, TimeUnit.SECONDS);

        sut.setTimes(lastModifiedTime, lastAccessTime, createTime);
        verify(path, times(1)).setTimes(lastModifiedTime, lastAccessTime, createTime);
    }
}
