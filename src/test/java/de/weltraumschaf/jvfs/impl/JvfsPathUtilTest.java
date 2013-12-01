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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsPathUtil}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsPathUtilTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void invokeConstructorByReflectionThrowsException() throws Exception {
        thrown.expect(InvocationTargetException.class);
        assertThat(JvfsPathUtil.class.getDeclaredConstructors().length, is(1));
        final Constructor<JvfsPathUtil> ctor = JvfsPathUtil.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
    }

    @Test
    public void normalize() {
        assertThat(
            JvfsPathUtil.normalize(JvfsPathUtil.tokenize("foo/./bar/../baz"), false),
            is(equalTo("foo/baz")));
    }

    @Test
    public void tokenize() {
        assertThat(
            JvfsPathUtil.tokenize("foo/./bar/../baz"),
            contains("foo", ".", "bar", "..", "baz"));
    }

    @Test
    public void isValid_oneNullThrowsException() {
        thrown.expect(NullPointerException.class);
        JvfsPathUtil.isValid((String) null);
    }

    @Test
    public void isValid_onePath() {
        assertThat(JvfsPathUtil.isValid("foobar"), is(true));
        assertThat(JvfsPathUtil.isValid("fooBAR12"), is(true));
        assertThat(JvfsPathUtil.isValid("foo.bar"), is(true));
        assertThat(JvfsPathUtil.isValid("foo-bar"), is(true));
        assertThat(JvfsPathUtil.isValid("foo_bar"), is(true));
        assertThat(JvfsPathUtil.isValid("foo/bar"), is(true));

        assertThat(JvfsPathUtil.isValid("foo+bar"), is(false));
        assertThat(JvfsPathUtil.isValid("foo bar"), is(false));
    }


    @Test
    public void isValid_multiNullThrowsException() {
        thrown.expect(NullPointerException.class);
        JvfsPathUtil.isValid((String[]) null);
    }
}
