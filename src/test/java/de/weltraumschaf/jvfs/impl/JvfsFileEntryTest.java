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

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Ignore;

/**
 * Tests for {@link JvfsFileEntry}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileEntryTest {

    @Test
    public void defaults_dir() {
        final JvfsFileEntry dir = JvfsFileEntry.newDir("foo");
        assertThat(dir.getPath(), is(equalTo("foo")));
        assertThat(dir.isDirectory(), is(true));
        assertThat(dir.isHidden(), is(false));
        assertThat(dir.isReadable(), is(false));
        assertThat(dir.isWritable(), is(false));
        assertThat(dir.isExecutable(), is(false));
        assertThat(dir.getLastModifiedTime(), is(0L));
        assertThat(dir.getLastAccessTime(), is(0L));
        assertThat(dir.getCreationTime(), is(0L));
        assertThat(dir.size(), is(-1L));
        assertThat(dir.getContent(), is(not(nullValue())));
    }

    @Test
    public void defaults_file() {
        final JvfsFileEntry file = JvfsFileEntry.newFile("foo");
        assertThat(file.isDirectory(), is(false));
        assertThat(file.isHidden(), is(false));
        assertThat(file.isReadable(), is(false));
        assertThat(file.isWritable(), is(false));
        assertThat(file.isExecutable(), is(false));
        assertThat(file.getLastModifiedTime(), is(0L));
        assertThat(file.getLastAccessTime(), is(0L));
        assertThat(file.getCreationTime(), is(0L));
        assertThat(file.size(), is(0L));
        assertThat(file.getContent(), is(not(nullValue())));
    }

    @Test
    public void testToString() {
        assertThat(
            JvfsFileEntry.newFile("foo").toString(),
            is(equalTo("foo")));
    }

    @Test @Ignore
    public void testHashCode() {

    }

    @Test @Ignore
    public void equals() {

    }

    @Test
    public void copy() {
        final JvfsFileEntry original = JvfsFileEntry.newFile("foo");
        original.setCreationTime(1L);
        original.setLastModifiedTime(2L);
        original.setLastAccessTime(3L);
        original.setReadable(true);
        original.setWritable(true);
        original.setExecutable(true);
        original.setHidden(true);

        final JvfsFileEntry copy = original.copy();
        assertThat(copy, is(not(sameInstance(original))));
        assertThat(copy.getPath(), is(equalTo("foo")));
        assertThat(copy.getCreationTime(), is(1L));
        assertThat(copy.getLastModifiedTime(), is(2L));
        assertThat(copy.getLastAccessTime(), is(3L));
        assertThat(copy.isReadable(), is(true));
        assertThat(copy.isWritable(), is(true));
        assertThat(copy.isExecutable(), is(true));
        assertThat(copy.isHidden(), is(true));
    }
}
