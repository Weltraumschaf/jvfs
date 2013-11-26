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

import de.weltraumschaf.jvfs.JvfsCollections;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsMountPoint}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsMountPointTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void normalizePath_nullThrowsException() {
        thrown.expect(NullPointerException.class);
        JvfsMountPoint.normalizePath(null);
    }

    @Test
    public void normalizePath_emptyThrowsExcpetion() {
        thrown.expect(IllegalArgumentException.class);
        JvfsMountPoint.normalizePath("");
    }

    @Test
    public void normalizePath() {
        assertThat(JvfsMountPoint.normalizePath("/foo/bar/baz"), is(equalTo("/foo/bar/baz")));
        assertThat(JvfsMountPoint.normalizePath("foo/bar/baz"), is(equalTo("/foo/bar/baz")));
        assertThat(JvfsMountPoint.normalizePath("/foo/bar/baz/"), is(equalTo("/foo/bar/baz")));
        assertThat(JvfsMountPoint.normalizePath("/"), is(equalTo("/")));
    }

    @Test
    public void isRootFileSystem() {
        assertThat(new JvfsMountPoint("/").isRootFileSystem(), is(true));
        assertThat(new JvfsMountPoint("/foo").isRootFileSystem(), is(false));
        assertThat(new JvfsMountPoint("/foo/bar").isRootFileSystem(), is(false));
        assertThat(new JvfsMountPoint("/foo/bar/baz").isRootFileSystem(), is(false));
    }

    @Test
    public void compareTo() {
        final List<JvfsMountPoint> mounts = JvfsCollections.asList(
                new JvfsMountPoint("/foo"),
                new JvfsMountPoint("/foo/bar"),
                new JvfsMountPoint("/"),
                new JvfsMountPoint("/foo/baz"),
                new JvfsMountPoint("/snafu")
        );
        Collections.sort(mounts);
        assertThat(mounts, contains(
                new JvfsMountPoint("/snafu"),
                new JvfsMountPoint("/foo/baz"),
                new JvfsMountPoint("/foo/bar"),
                new JvfsMountPoint("/foo"),
                new JvfsMountPoint("/")
        ));
    }
}
