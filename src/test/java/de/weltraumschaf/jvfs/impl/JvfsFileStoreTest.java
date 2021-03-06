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
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JvfsFileStore}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileStoreTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON
    private final JvfsOptions options = JvfsOptions.builder().capacity("1k").create();
    private final JvfsFileSystem fs = spy(new JvfsFileSystem(new JvfsFileSystemProvider(), options));
    private final JvfsFileStore sut = new JvfsFileStore(options, fs);

    @Test
    public void name() {
        assertThat(sut.name(), is(equalTo("jvfs")));
    }

    @Test
    public void type() {
        assertThat(sut.type(), is(equalTo("in-memory")));
    }

    @Test
    public void isReadOnly() {
        assertThat(
            new JvfsFileStore(JvfsOptions.DEFAULT, mock(JvfsFileSystem.class)).isReadOnly(),
            is(false));
        assertThat(
            new JvfsFileStore(
                JvfsOptions.builder().readonly(true).create(),
                mock(JvfsFileSystem.class)).isReadOnly(),
            is(true));
    }

    @Test
    public void getTotalSpace() throws IOException {
        assertThat(sut.getTotalSpace(), is(1024L));
        when(fs.getUsedSpace()).thenReturn(50L);
        assertThat(sut.getTotalSpace(), is(1024L));
    }

    @Test
    public void getUsedSpace() {
        assertThat(sut.getUsedSpace(), is(0L));
        when(fs.getUsedSpace()).thenReturn(50L);
        assertThat(sut.getUsedSpace(), is(50L));
    }

    @Test
    public void getUsableSpace() throws IOException {
        assertThat(sut.getUsableSpace(), is(1024L));
        when(fs.getUsedSpace()).thenReturn(50L);
        assertThat(sut.getUsableSpace(), is(974L));
    }

    @Test
    public void getUnallocatedSpace() throws IOException {
        assertThat(sut.getUnallocatedSpace(), is(1024L));
        when(fs.getUsedSpace()).thenReturn(50L);
        assertThat(sut.getUnallocatedSpace(), is(974L));
    }

    @Test
    public void supportsFileAttributeView_byClass() {
        assertThat(sut.supportsFileAttributeView(BasicFileAttributeView.class), is(true));
        assertThat(sut.supportsFileAttributeView(FileAttributeView.class), is(false));
    }

    @Test
    public void supportsFileAttributeView_byName() {
        assertThat(sut.supportsFileAttributeView("basic"), is(true));
        assertThat(sut.supportsFileAttributeView("foobar"), is(false));
        assertThat(sut.supportsFileAttributeView(""), is(false));
        assertThat(sut.supportsFileAttributeView((String) null), is(false));
    }

    @Test
    public void getFileStoreAttributeView() {
        assertThat(sut.getFileStoreAttributeView(null), is(nullValue()));
    }

    @Test
    public void getAttribute() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.getAttribute("foobar");
    }

    @Test
    public void testHashCode() {
        final JvfsFileSystem fs1 = mock(JvfsFileSystem.class);
        final JvfsFileStore sut1 = new JvfsFileStore(JvfsOptions.DEFAULT, fs1);
        final JvfsFileStore sut2 = new JvfsFileStore(JvfsOptions.DEFAULT, fs1);
        final JvfsFileStore sut3 = new JvfsFileStore(JvfsOptions.DEFAULT, mock(JvfsFileSystem.class));

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
        final JvfsFileSystem fs1 = mock(JvfsFileSystem.class);
        final JvfsFileStore sut1 = new JvfsFileStore(JvfsOptions.DEFAULT, fs1);
        final JvfsFileStore sut2 = new JvfsFileStore(JvfsOptions.DEFAULT, fs1);
        final JvfsFileStore sut3 = new JvfsFileStore(JvfsOptions.DEFAULT, mock(JvfsFileSystem.class));

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
        assertThat(sut.toString(), startsWith(
            "JvfsFileStore{options=JvfsOptions{id=, capacity=1024, readonly=false}, fs="));
    }

}
