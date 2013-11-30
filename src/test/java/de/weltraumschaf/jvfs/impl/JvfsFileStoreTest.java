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
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;

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
    private final JvfsFileStore sut = new JvfsFileStore(JvfsOptions.DEFAULT, mock(JvfsFileSystem.class));

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

    @Test @Ignore
    public void getTotalSpace() {

    }

    @Test
    public void getUsedSpace() {
        assertThat(sut.getUsedSpace(), is(0L));
    }

    @Test @Ignore
    public void getUsableSpace() {

    }

    @Test @Ignore
    public void getUnallocatedSpace() {

    }

    @Test @Ignore
    public void supportsFileAttributeView() {

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
    @Ignore
    public void testHashCode() {}

    @Test
    @Ignore
    public void equals() {}

    @Test
    @Ignore
    public void testToString() {}

}
