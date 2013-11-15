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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link JvfsFileSystemProvider}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemProviderTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON
    private final JvfsFileSystemProvider sut = new JvfsFileSystemProvider();

    @Test
    public void getScheme() {
        assertThat(sut.getScheme(), is(equalTo("file")));
    }

    @Test
    public void newFileSystem_alwaysThrowException() throws IOException, URISyntaxException {
        thrown.expect(FileSystemAlreadyExistsException.class);
        thrown.expectMessage("JVFS is not supposed to create new file systems!");
        sut.newFileSystem(new URI("file:///"), null);
    }

    @Test
    public void getFileSystem_alwaysReturnSameInstance() throws URISyntaxException {
        final FileSystem fs = sut.getFileSystem(new URI("file:///"));
        assertThat(fs, is(not(nullValue())));
        assertThat(fs, is(sameInstance(sut.getFileSystem(new URI("file:///")))));
    }

    @Test
    public void newAsynchronousFileChannel_isUnsupportedOperation() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.newAsynchronousFileChannel(null, null, null, null);
    }

    @Test
    public void newDirectoryStream() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final DirectoryStream.Filter<Path> filter = mock(DirectoryStream.Filter.class);
        sut.newDirectoryStream(path, filter);
        verify(path, times(1)).newDirectoryStream(filter);
    }
}
