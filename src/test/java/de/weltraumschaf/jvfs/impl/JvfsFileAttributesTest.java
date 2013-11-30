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

import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for {@link JvfsFileAttributes}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileAttributesTest {

    private static final String PATH = "/foo/bar";

    @Test
    public void lastModifiedTime() {
        final JvfsFileEntry entry = JvfsFileEntry.newFile(PATH);
        final JvfsFileAttributes sut = new JvfsFileAttributes(entry);
        assertThat(
            sut.lastModifiedTime(),
            is(equalTo(FileTime.from(0L, TimeUnit.SECONDS))));
        final long timestamp = 1384538944L;
        entry.setLastModifiedTime(timestamp);
        assertThat(
            sut.lastModifiedTime(),
            is(equalTo(FileTime.from(timestamp, TimeUnit.SECONDS))));
    }

    @Test
    public void lastAccessTime() {
        final JvfsFileEntry entry = JvfsFileEntry.newFile(PATH);
        final JvfsFileAttributes sut = new JvfsFileAttributes(entry);
        assertThat(
            sut.lastAccessTime(),
            is(equalTo(FileTime.from(0L, TimeUnit.SECONDS))));
        final long timestamp = 1384538944L;
        entry.setLastAccessTime(timestamp);
        assertThat(
            sut.lastAccessTime(),
            is(equalTo(FileTime.from(timestamp, TimeUnit.SECONDS))));
    }

    @Test
    public void creationTime() {
        final JvfsFileEntry entry = JvfsFileEntry.newFile(PATH);
        final JvfsFileAttributes sut = new JvfsFileAttributes(entry);
        assertThat(
            sut.creationTime(),
            is(equalTo(FileTime.from(0L, TimeUnit.SECONDS))));
        final long timestamp = 1384538944L;
        entry.setCreationTime(timestamp);
        assertThat(
            sut.creationTime(),
            is(equalTo(FileTime.from(timestamp, TimeUnit.SECONDS))));
    }

    @Test
    public void isRegularFile() {
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newFile(PATH)).isRegularFile(),
            is(true));
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newDir(PATH)).isRegularFile(),
            is(false));
    }

    @Test
    public void isDirectory() {
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newFile(PATH)).isDirectory(),
            is(false));
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newDir(PATH)).isDirectory(),
            is(true));
    }

    @Test
    public void isSymbolicLink() {
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newFile(PATH)).isSymbolicLink(),
            is(false));
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newDir(PATH)).isSymbolicLink(),
            is(false));
    }

    @Test
    public void isOther() {
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newFile(PATH)).isOther(),
            is(false));
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newDir(PATH)).isOther(),
            is(false));
    }

    @Test
    public void size() {
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newFile(PATH)).size(),
            is(0L));
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newDir(PATH)).size(),
            is(-1L));
    }

    @Test
    public void fileKey() {
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newFile(PATH)).fileKey(),
            is(equalTo((Object) PATH)));
        assertThat(
            new JvfsFileAttributes(JvfsFileEntry.newDir(PATH)).fileKey(),
            is(equalTo((Object) PATH)));
    }


}
