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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    private final String pathname = "/foo/bar";
    private final JvfsFileAttributes attributes = new JvfsFileAttributes(JvfsFileEntry.newFile(pathname));
    private final JvfsFileSystem fs = mock(JvfsFileSystem.class);
    private final JvfsPath path = spy(new JvfsPath(pathname, fs));
    private final JvfsFileAttributeView sut = new JvfsFileAttributeView(path);

    @Before
    public void setUpMocks() throws IOException {
        when(fs.getFileAttributes(pathname)).thenReturn(attributes);;
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

    @Test
    public void setAttribute_creationTime() throws IOException {
        assertThat(sut.readAttributes().creationTime(), is(equalTo(FileTime.from(0L, TimeUnit.SECONDS))));
        sut.setAttribute("creationTime", FileTime.from(1L, TimeUnit.SECONDS));
        verify(path, times(1)).setTimes(null, null, FileTime.from(1L, TimeUnit.SECONDS));
    }

    @Test
    public void setAttribute_lastAccessTime() throws IOException {
        assertThat(sut.readAttributes().lastAccessTime(), is(equalTo(FileTime.from(0L, TimeUnit.SECONDS))));
        sut.setAttribute("lastAccessTime", FileTime.from(1L, TimeUnit.SECONDS));
        verify(path, times(1)).setTimes(null, FileTime.from(1L, TimeUnit.SECONDS), null);
    }

    @Test
    public void setAttribute_lastModifiedTime() throws IOException {
        assertThat(sut.readAttributes().lastModifiedTime(), is(equalTo(FileTime.from(0L, TimeUnit.SECONDS))));
        sut.setAttribute("lastModifiedTime", FileTime.from(1L, TimeUnit.SECONDS));
        verify(path, times(1)).setTimes(FileTime.from(1L, TimeUnit.SECONDS), null, null);
    }

    @Test
    public void setAttribute_throwsExceptionForReadonly_size() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Attribute 'size' is unknown or read-only attribute!");
        sut.setAttribute("size", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionForReadonly_isDirectory() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Attribute 'isDirectory' is unknown or read-only attribute!");
        sut.setAttribute("isDirectory", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionForReadonly_isRegularFile() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Attribute 'isRegularFile' is unknown or read-only attribute!");
        sut.setAttribute("isRegularFile", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionForReadonly_isSymbolicLink() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Attribute 'isSymbolicLink' is unknown or read-only attribute!");
        sut.setAttribute("isSymbolicLink", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionForReadonly_isOther() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Attribute 'isOther' is unknown or read-only attribute!");
        sut.setAttribute("isOther", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionForReadonly_fileKey() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Attribute 'fileKey' is unknown or read-only attribute!");
        sut.setAttribute("fileKey", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionIfAttributeNameIsNull() throws IOException {
        thrown.expect(NullPointerException.class);
        sut.setAttribute(null, "foobar");
    }
    @Test
    public void setAttribute_throwsExceptionIfAttributeNameIsEmpty() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        sut.setAttribute("", "foobar");
    }

    @Test
    public void setAttribute_throwsExceptionIfAttributeValueIsNull() throws IOException {
        thrown.expect(NullPointerException.class);
        sut.setAttribute("lastModifiedTime", null);
    }

    @Test
    public void readAttributes_byNames_throwsExceptionIfNamesIsNull() throws IOException {
        thrown.expect(NullPointerException.class);
        sut.readAttributes(null);
    }

    @Test
    public void readAttributes_byNames_throwsExceptionIfNamesIsEmpty() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        sut.readAttributes("");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readAttributes_byNames_wildcard() throws IOException {
        final Map <String, Object> attrs = sut.readAttributes("*");
        assertThat(attrs.size(), is(9));
        assertThat(attrs, allOf(
            hasEntry("lastModifiedTime", (Object) FileTime.from(0L, TimeUnit.SECONDS)),
            hasEntry("fileKey", (Object) "/foo/bar"),
            hasEntry("isDirectory", (Object) false),
            hasEntry("lastAccessTime", (Object) FileTime.from(0L, TimeUnit.SECONDS)),
            hasEntry("isOther", (Object) false),
            hasEntry("isSymbolicLink", (Object) false),
            hasEntry("isRegularFile", (Object) true),
            hasEntry("creationTime", (Object) FileTime.from(0L, TimeUnit.SECONDS)),
            hasEntry("size", (Object) 0L)
        ));
    }
}
