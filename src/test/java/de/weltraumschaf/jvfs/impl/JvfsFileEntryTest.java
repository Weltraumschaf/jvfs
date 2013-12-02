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
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsFileEntry}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileEntryTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

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
        assertThat(dir.getParent(), is(nullValue()));
        assertThat(dir.hasChildren(), is(false));
        assertThat(dir.getChildren(), hasSize(0));
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
        assertThat(file.getParent(), is(nullValue()));
        assertThat(file.hasChildren(), is(false));
        assertThat(file.getChildren(), hasSize(0));
    }

    @Test
    public void testToString() {
        assertThat(
            JvfsFileEntry.newFile("foo").toString(),
            is(equalTo("---------- foo")));
        assertThat(
            JvfsFileEntry.newDir("foo").toString(),
            is(equalTo("d--------- foo")));
    }

    @Test
    public void testHashCode() {
        final JvfsFileEntry one = JvfsFileEntry.newFile("foo");
        final JvfsFileEntry two = JvfsFileEntry.newFile("foo");
        final JvfsFileEntry three = JvfsFileEntry.newFile("bar");
        final JvfsFileEntry four = JvfsFileEntry.newDir("foo");

        assertThat(one.hashCode(), is(one.hashCode()));
        assertThat(one.hashCode(), is(two.hashCode()));
        assertThat(two.hashCode(), is(one.hashCode()));
        assertThat(two.hashCode(), is(two.hashCode()));
        // Dir/file does not matter
        assertThat(four.hashCode(), is(one.hashCode()));
        assertThat(four.hashCode(), is(two.hashCode()));
        assertThat(four.hashCode(), is(four.hashCode()));

        assertThat(three.hashCode(), is(three.hashCode()));
        assertThat(three.hashCode(), is(not(one.hashCode())));
        assertThat(three.hashCode(), is(not(two.hashCode())));
        assertThat(three.hashCode(), is(not(four.hashCode())));
    }

    @Test
    public void equals() {
        final JvfsFileEntry one = JvfsFileEntry.newFile("foo");
        final JvfsFileEntry two = JvfsFileEntry.newFile("foo");
        final JvfsFileEntry three = JvfsFileEntry.newFile("bar");
        final JvfsFileEntry four = JvfsFileEntry.newDir("foo");

        assertThat(one.equals(one), is(true));
        assertThat(one.equals(two), is(true));
        assertThat(two.equals(one), is(true));
        assertThat(two.equals(two), is(true));
        // Dir/file does not matter
        assertThat(four.equals(one), is(true));
        assertThat(four.equals(two), is(true));
        assertThat(four.equals(four), is(true));

        assertThat(three.equals(three), is(true));
        assertThat(three.equals(one), is(false));
        assertThat(three.equals(two), is(false));
        assertThat(three.equals(four), is(false));

        //CHECKSTYLE:OFF
        assertThat(one.equals(null), is(false));
        assertThat(one.equals("foobar"), is(false));
        //CHECKSTYLE:ON
    }

    @Test
    public void copy() {
        final JvfsFileEntry root = JvfsFileEntry.newDir("/");
        final JvfsFileEntry original = JvfsFileEntry.newFile("foo");
        original.setParent(root);
        assertThat(original.getParent(), is(sameInstance(root)));
        original.setCreationTime(1L);
        original.setLastModifiedTime(2L);
        original.setLastAccessTime(3L);
        original.setReadable(true);
        original.setWritable(true);
        original.setExecutable(true);
        original.setHidden(true);

        final JvfsFileEntry copy = original.copy();
        assertThat(copy.getParent(), is(nullValue()));
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

    @Test
    public void copy_withNewPath() {
        final JvfsFileEntry root = JvfsFileEntry.newDir("/");
        final JvfsFileEntry original = JvfsFileEntry.newFile("foo");
        original.setParent(root);
        assertThat(original.getParent(), is(sameInstance(root)));
        original.setCreationTime(1L);
        original.setLastModifiedTime(2L);
        original.setLastAccessTime(3L);
        original.setReadable(true);
        original.setWritable(true);
        original.setExecutable(true);
        original.setHidden(true);

        final JvfsFileEntry copy = original.copy("bar");
        assertThat(copy.getParent(), is(nullValue()));
        assertThat(copy, is(not(sameInstance(original))));
        assertThat(copy.getPath(), is(equalTo("bar")));
        assertThat(copy.getCreationTime(), is(1L));
        assertThat(copy.getLastModifiedTime(), is(2L));
        assertThat(copy.getLastAccessTime(), is(3L));
        assertThat(copy.isReadable(), is(true));
        assertThat(copy.isWritable(), is(true));
        assertThat(copy.isExecutable(), is(true));
        assertThat(copy.isHidden(), is(true));
    }

    @Test
    public void addChild_toNonDirThrowsException() {
        thrown.expect(IllegalStateException.class);
        final JvfsFileEntry sut = JvfsFileEntry.newFile("foo");
        sut.addChild(JvfsFileEntry.newFile("foo"));
    }

    @Test
    public void addChild() {
        final JvfsFileEntry sut = JvfsFileEntry.newDir("foo");
        assertThat(sut.hasChildren(), is(false));
        assertThat(sut.getChildren(), hasSize(0));

        final JvfsFileEntry child1 = JvfsFileEntry.newFile("child1");
        sut.addChild(child1);
        assertThat(sut.hasChildren(), is(true));
        assertThat(sut.getChildren(), hasSize(1));
        assertThat(sut.getChildren(), containsInAnyOrder(child1));

        final JvfsFileEntry child2 = JvfsFileEntry.newFile("child2");
        sut.addChild(child2);
        assertThat(sut.hasChildren(), is(true));
        assertThat(sut.getChildren(), hasSize(2));
        assertThat(sut.getChildren(), containsInAnyOrder(child1, child2));

        final JvfsFileEntry child3 = JvfsFileEntry.newFile("child3");
        sut.addChild(child3);
        assertThat(sut.hasChildren(), is(true));
        assertThat(sut.getChildren(), hasSize(3));
        assertThat(sut.getChildren(), containsInAnyOrder(child1, child2, child3));
    }

    @Test
    public void setParent_ifParentIsNotDirectory() {
        thrown.expect(IllegalArgumentException.class);
        final JvfsFileEntry sut = JvfsFileEntry.newFile("foo");
        sut.setParent(JvfsFileEntry.newFile("bar"));
    }

    @Test
    public void setParent() {
        final JvfsFileEntry sut = JvfsFileEntry.newFile("foo");
        assertThat(sut.getParent(), is(nullValue()));
        final JvfsFileEntry parent = JvfsFileEntry.newDir("bar");
        sut.setParent(parent);
        assertThat(sut.getParent(), is(sameInstance(parent)));
    }
}
